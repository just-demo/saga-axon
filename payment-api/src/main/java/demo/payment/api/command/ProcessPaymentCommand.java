package demo.payment.api.command;

import java.math.BigDecimal;
import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class ProcessPaymentCommand {

  @TargetAggregateIdentifier
  UUID paymentId;
  BigDecimal price;
}
