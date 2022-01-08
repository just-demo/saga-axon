package demo.payment.aggregate;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import demo.payment.api.OrderPaidEvent;
import demo.payment.api.PayOrderCommand;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aggregate
public class PaymentAggregate {

  @AggregateIdentifier
  private UUID paymentId;
  private boolean paid;

  public PaymentAggregate() {
    log.info("Instantiating {}", getClass().getSimpleName());
  }

  @CommandHandler
  public PaymentAggregate(PayOrderCommand command) {
    AggregateLifecycle.apply(new OrderPaidEvent(command.getPaymentId()));
  }

  @EventSourcingHandler
  protected void on(OrderPaidEvent event) {
    // TODO: how to use this state? can we get rid of the aggregate?
    paymentId = event.getPaymentId();
    paid = true;
  }
}
