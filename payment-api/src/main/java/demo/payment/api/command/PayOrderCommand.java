package demo.payment.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class PayOrderCommand {

  @TargetAggregateIdentifier
  UUID paymentId;
  UUID orderId;
}
