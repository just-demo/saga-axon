package demo.order.aggregate;

import static demo.order.api.query.OrderStatus.APPROVED;
import static demo.order.api.query.OrderStatus.CREATED;
import static demo.order.api.query.OrderStatus.REJECTED;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import demo.order.api.command.ApproveOrderCommand;
import demo.order.api.command.CreateOrderCommand;
import demo.order.api.command.RejectOrderCommand;
import demo.order.api.query.OrderStatus;
import demo.order.api.event.OrderApprovedEvent;
import demo.order.api.event.OrderCreatedEvent;
import demo.order.api.event.OrderRejectedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor
@Getter
@Aggregate
public class OrderAggregate {

  @AggregateIdentifier
  private UUID orderId;
  private UUID productId;
  private OrderStatus status;
  private String reason;

  @CommandHandler
  public OrderAggregate(CreateOrderCommand command) {
    log.info("Creating order: {}", command);
    apply(new OrderCreatedEvent(command.getOrderId(), command.getProductId()));
  }

  @CommandHandler
  protected void handle(ApproveOrderCommand command) {
    log.info("Approving order: {}", command);
    apply(new OrderApprovedEvent(command.getOrderId()));
  }

  @CommandHandler
  protected void handle(RejectOrderCommand command) {
    log.info("Rejecting order: {}", command);
    apply(new OrderRejectedEvent(command.getOrderId(), command.getReason()));
  }

  @EventSourcingHandler
  protected void on(OrderCreatedEvent event) {
    log.info("Order created: {}", event);
    orderId = event.getOrderId();
    productId = event.getProductId();
    status = CREATED;
  }

  @EventSourcingHandler
  protected void on(OrderApprovedEvent event) {
    log.info("Order approved: {}", event);
    status = APPROVED;
  }

  @EventSourcingHandler
  protected void on(OrderRejectedEvent event) {
    log.info("Order rejected: {}", event);
    status = REJECTED;
    reason = event.getReason();
  }
}
