package demo.payment.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import demo.payment.api.command.ProcessPaymentCommand;
import demo.payment.api.event.PaymentAcceptedEvent;
import demo.payment.api.event.PaymentRejectedEvent;
import demo.payment.service.PaymentService;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor
@Aggregate
public class PaymentAggregate {

  @AggregateIdentifier
  private UUID paymentId;
  private boolean paid;
  private String reason;

  @CommandHandler
  public PaymentAggregate(ProcessPaymentCommand command, PaymentService paymentService) {
    log.info("Processing payment: {}", command);
    try {
      paymentService.pay(command.getPaymentId(), command.getPrice());
      apply(new PaymentAcceptedEvent(command.getPaymentId()));
    } catch (Exception e) {
      apply(new PaymentRejectedEvent(command.getPaymentId(), e.getMessage()));
    }
  }

  @EventSourcingHandler
  protected void on(PaymentAcceptedEvent event) {
    log.info("Payment accepted: {}", event);
    paymentId = event.getPaymentId();
    paid = true;
  }

  @EventSourcingHandler
  protected void on(PaymentRejectedEvent event) {
    log.info("Payment rejected: {}", event);
    paymentId = event.getPaymentId();
    paid = false;
    reason = event.getReason();
  }
}
