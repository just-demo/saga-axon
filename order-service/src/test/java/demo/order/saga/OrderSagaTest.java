package demo.order.saga;

import static demo.order.saga.OrderSaga.ORDER_COMPLETE_DEADLINE;
import static demo.shipment.api.ShipmentStatus.DELIVERED;
import static demo.shipment.api.ShipmentStatus.SHIPPED;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.axonframework.deadline.DeadlineMessage;
import org.axonframework.test.saga.SagaTestFixture;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import demo.order.api.CompleteOrderProcessCommand;
import demo.order.api.OrderConfirmedEvent;
import demo.order.factory.IdFactory;
import demo.payment.api.OrderPaidEvent;
import demo.payment.api.OrderPaymentCancelledEvent;
import demo.payment.api.PayOrderCommand;
import demo.shipment.api.CancelShipmentCommand;
import demo.shipment.api.ShipOrderCommand;
import demo.shipment.api.ShipmentStatusUpdatedEvent;

public class OrderSagaTest {

  @Mock
  private IdFactory idFactoryMock = mock(IdFactory.class);
  private UUID orderId;
  private UUID paymentId;
  private UUID shipmentId;

  private SagaTestFixture<OrderSaga> sagaTestFixture;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    orderId = randomUUID();
    paymentId = randomUUID();
    shipmentId = randomUUID();
    when(idFactoryMock.generateOrderId()).thenReturn(orderId);
    when(idFactoryMock.generatePaymentId()).thenReturn(paymentId);
    when(idFactoryMock.generateShipmentId()).thenReturn(shipmentId);
    sagaTestFixture = new SagaTestFixture<>(OrderSaga.class);
    sagaTestFixture.registerResource(idFactoryMock);
  }

  @Test
  void onOrderConfirmedTest() {
    sagaTestFixture.givenNoPriorActivity()
        .whenPublishingA(new OrderConfirmedEvent(orderId))
        .expectDispatchedCommands(new PayOrderCommand(paymentId), new ShipOrderCommand(shipmentId))
        .expectScheduledDeadlineWithName(Duration.ofDays(5), ORDER_COMPLETE_DEADLINE)
        .expectActiveSagas(1);
  }

  @Test
  void onOrderPaidAndNotDeliveredTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .whenPublishingA(new OrderPaidEvent(paymentId))
        .expectActiveSagas(1);
  }

  @Test
  void onOrderPaidAndDeliveredTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .andThenAPublished(new ShipmentStatusUpdatedEvent(shipmentId, DELIVERED))
        .whenPublishingA(new OrderPaidEvent(paymentId))
        .expectDispatchedCommands(new CompleteOrderProcessCommand(orderId, true, true))
        .expectNoScheduledDeadlineWithName(Duration.ofDays(5), ORDER_COMPLETE_DEADLINE)
        .expectActiveSagas(0);
  }

  @Test
  void onOrderPaymentCancelledTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .whenPublishingA(new OrderPaymentCancelledEvent(paymentId))
        .expectDispatchedCommands(new CancelShipmentCommand(shipmentId),
            new CompleteOrderProcessCommand(orderId, false, false))
        .expectNoScheduledDeadlineWithName(Duration.ofDays(5), ORDER_COMPLETE_DEADLINE)
        .expectActiveSagas(0);
  }

  @Test
  void onShipmentDeliveredAndPaidTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .andThenAPublished(new OrderPaidEvent(paymentId))
        .andThenAPublished(new ShipmentStatusUpdatedEvent(shipmentId, SHIPPED))
        .whenPublishingA(new ShipmentStatusUpdatedEvent(shipmentId, DELIVERED))
        .expectDispatchedCommands(new CompleteOrderProcessCommand(orderId, true, true))
        .expectNoScheduledDeadlineWithName(Duration.ofDays(5), ORDER_COMPLETE_DEADLINE)
        .expectActiveSagas(0);
  }

  @Test
  void onShipmentDeliveredAndNotPaidTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .andThenAPublished(new ShipmentStatusUpdatedEvent(shipmentId, SHIPPED))
        .whenPublishingA(new ShipmentStatusUpdatedEvent(shipmentId, DELIVERED))
        .expectActiveSagas(1);
  }

  @Test
  void onShipmentInTransitTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .andThenAPublished(new OrderPaidEvent(paymentId))
        .whenPublishingA(new ShipmentStatusUpdatedEvent(shipmentId, SHIPPED))
        .expectActiveSagas(1);
  }

  @Test
  void onOrderCompleteDeadlineTest() {
    sagaTestFixture.givenAPublished(new OrderConfirmedEvent(orderId))
        .whenTimeElapses(Duration.ofDays(5))
        .expectNoScheduledDeadlines()
        .expectDeadlinesMetMatching(orderCompleteDeadline())
        .expectDispatchedCommands(new CompleteOrderProcessCommand(orderId, false, false))
        .expectActiveSagas(0);
  }

  protected static TypeSafeMatcher<List<DeadlineMessage<?>>> orderCompleteDeadline() {
    return new TypeSafeMatcher<>() {
      @Override
      protected boolean matchesSafely(List<DeadlineMessage<?>> messages) {
        return messages.stream().allMatch(message ->
            message.getDeadlineName().equals(ORDER_COMPLETE_DEADLINE) && message.getPayload() == null);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("no matching deadline found");
      }
    };
  }
}
