package demo.order.saga;

import static demo.shipment.api.ShipmentStatus.DELIVERED;

import java.time.Duration;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import demo.order.api.CompleteOrderProcessCommand;
import demo.order.api.OrderConfirmedEvent;
import demo.order.factory.IdFactory;
import demo.payment.api.OrderPaidEvent;
import demo.payment.api.OrderPaymentCancelledEvent;
import demo.payment.api.PayOrderCommand;
import demo.shipment.api.CancelShipmentCommand;
import demo.shipment.api.ShipOrderCommand;
import demo.shipment.api.ShipmentStatusUpdatedEvent;

@Saga
public class OrderSaga {

  private static final String ASSOCIATION_ORDER_ID = "orderId";
  private static final String ASSOCIATION_PAYMENT_ID = "paymentId";
  private static final String ASSOCIATION_SHIPMENT_ID = "shipmentId";
  public static final String ORDER_COMPLETE_DEADLINE = "OrderCompleteDeadline";

  @Autowired
  private transient CommandGateway commandGateway;

  private UUID orderId;
  private UUID shipmentId;
  private String orderDeadlineId;
  private boolean orderPaid;
  private boolean orderDelivered;

  @StartSaga
  @SagaEventHandler(associationProperty = ASSOCIATION_ORDER_ID)
  public void on(OrderConfirmedEvent event, DeadlineManager deadlineManager, IdFactory idFactory) {
    orderId = event.getOrderId();

    //Send a command to paid to get the order paid. Associate this Saga with the payment Id used.
    UUID paymentId = idFactory.generatePaymentId();
    SagaLifecycle.associateWith(ASSOCIATION_PAYMENT_ID, paymentId.toString());
    commandGateway.send(new PayOrderCommand(paymentId));

    //Send a command to logistics to ship the order. Associate this Saga with the shipment Id used.
    UUID shipmentId = idFactory.generateShipmentId();
    SagaLifecycle.associateWith(ASSOCIATION_SHIPMENT_ID, shipmentId.toString());
    commandGateway.send(new ShipOrderCommand(shipmentId));
    this.shipmentId = shipmentId;

    //This order should be completed in 5 days
    orderDeadlineId = deadlineManager.schedule(Duration.ofDays(5), ORDER_COMPLETE_DEADLINE);
  }

  @SagaEventHandler(associationProperty = ASSOCIATION_PAYMENT_ID)
  public void on(OrderPaidEvent event, DeadlineManager deadlineManager) {
    orderPaid = true;
    if (orderDelivered) {
      completeOrderProcess(deadlineManager);
    }
  }

  @SagaEventHandler(associationProperty = ASSOCIATION_PAYMENT_ID)
  public void on(OrderPaymentCancelledEvent event, DeadlineManager deadlineManager) {
    // Cancel the shipment and update the Order
    commandGateway.send(new CancelShipmentCommand(shipmentId));
    completeOrderProcess(deadlineManager);
  }

  @SagaEventHandler(associationProperty = ASSOCIATION_SHIPMENT_ID)
  public void on(ShipmentStatusUpdatedEvent event, DeadlineManager deadlineManager) {
    orderDelivered = DELIVERED.equals(event.getShipmentStatus());
    if (orderPaid && orderDelivered) {
      completeOrderProcess(deadlineManager);
    }
  }

  @DeadlineHandler(deadlineName = ORDER_COMPLETE_DEADLINE)
  @EndSaga
  public void on() {
    commandGateway.send(new CompleteOrderProcessCommand(orderId, orderPaid, orderDelivered));
  }

  private void completeOrderProcess(DeadlineManager deadlineManager) {
    commandGateway.send(new CompleteOrderProcessCommand(orderId, orderPaid, orderDelivered));
    deadlineManager.cancelSchedule(ORDER_COMPLETE_DEADLINE, orderDeadlineId);
    SagaLifecycle.end();
  }
}
