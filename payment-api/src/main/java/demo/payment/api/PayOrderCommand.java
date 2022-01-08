package demo.payment.api;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class PayOrderCommand {

  @TargetAggregateIdentifier
  private final UUID paymentId;
}
