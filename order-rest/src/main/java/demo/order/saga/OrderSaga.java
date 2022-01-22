package demo.order.saga;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import demo.order.api.command.ApproveOrderCommand;
import demo.order.api.command.RejectOrderCommand;
import demo.order.api.event.OrderApprovedEvent;
import demo.order.api.event.OrderCreatedEvent;
import demo.order.api.event.OrderRejectedEvent;
import demo.payment.api.command.PayOrderCommand;
import demo.payment.api.event.PaymentAcceptedEvent;
import demo.payment.api.event.PaymentRejectedEvent;
import demo.product.api.command.CancelProductReservationCommand;
import demo.product.api.command.ReserveProductCommand;
import demo.product.api.event.ProductReservationCancelledEvent;
import demo.product.api.event.ProductReservationRejectedEvent;
import demo.product.api.event.ProductReservedEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Saga
public class OrderSaga {

  // It could be also injected as handler method parameter
  @Autowired
  private transient CommandGateway commandGateway;

  private UUID orderId;
  private UUID productId;
  private String reason;

  @StartSaga
  @SagaEventHandler(associationProperty = "orderId")
  public void on(OrderCreatedEvent event, CommandGateway commandGateway2) {
    log.info("Order created: {}", event);
    log.info("Saga started: {}", event);
    orderId = event.getOrderId();
    productId = event.getProductId();
    commandGateway.send(new ReserveProductCommand(productId, orderId));
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void on(ProductReservedEvent event) {
    log.info("Product reserved: {}", event);
    commandGateway.send(new PayOrderCommand(randomUUID(), orderId));
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void on(PaymentAcceptedEvent event) {
    log.info("Payment accepted: {}", event);
    commandGateway.send(new ApproveOrderCommand(orderId));
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void on(ProductReservationRejectedEvent event) {
    log.info("Product reservation rejected: {}", event);
    reason = event.getReason();
    commandGateway.send(new RejectOrderCommand(orderId, reason));
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void on(PaymentRejectedEvent event) {
    log.info("Payment rejected: {}", event);
    reason = event.getReason();
    commandGateway.send(new CancelProductReservationCommand(productId, orderId));
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void on(ProductReservationCancelledEvent event) {
    log.info("Product reservation cancelled: {}", event);
    commandGateway.send(new RejectOrderCommand(orderId, reason));
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "orderId")
  public void on(OrderApprovedEvent event) {
    log.info("Order approved: {}", event);
    log.info("Saga ended");
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "orderId")
  public void on(OrderRejectedEvent event) {
    log.info("Order rejected: {}", event);
    reason = event.getReason();
    log.info("Saga ended");
  }
}
