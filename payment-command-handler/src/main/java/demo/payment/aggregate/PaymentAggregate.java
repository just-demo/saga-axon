package demo.payment.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import demo.payment.api.command.PayOrderCommand;
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
  private UUID orderId;
  private boolean paid;
  private String reason;

  @CommandHandler
  public PaymentAggregate(PayOrderCommand command, PaymentService paymentService) {
    log.info("Paying order: {}", command);
    try {
      paymentService.pay(
          command.getPaymentId(),
          command.getOrderId());
      apply(new PaymentAcceptedEvent(
          command.getPaymentId(),
          command.getOrderId()));
    } catch (Exception e) {
      apply(new PaymentRejectedEvent(
          command.getPaymentId(),
          command.getOrderId(),
          e.getMessage()));
    }
  }

  @EventSourcingHandler
  protected void on(PaymentAcceptedEvent event) {
    log.info("Payment accepted: {}", event);
    paymentId = event.getPaymentId();
    orderId = event.getOrderId();
    paid = true;
  }

  @EventSourcingHandler
  protected void on(PaymentRejectedEvent event) {
    log.info("Payment rejected: {}", event);
    paymentId = event.getPaymentId();
    orderId = event.getOrderId();
    paid = false;
    reason = event.getReason();
  }
}
