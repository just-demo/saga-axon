package demo.order.aggregate;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import demo.order.api.OrderConfirmedEvent;
import demo.order.api.StartOrderProcessCommand;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aggregate
public class OrderAggregate {

  @AggregateIdentifier
  private UUID orderId;
  private boolean confirmed;

  public OrderAggregate() {
    log.info("Instantiating {}", getClass().getSimpleName());
  }

  @CommandHandler
  public OrderAggregate(StartOrderProcessCommand command) {
    AggregateLifecycle.apply(new OrderConfirmedEvent(command.getOrderId()));
  }

  @EventSourcingHandler
  protected void on(OrderConfirmedEvent event) {
    // TODO: how to use this state? can we get rid of the aggregate?
    orderId = event.getOrderId();
    confirmed = true;
  }
}
