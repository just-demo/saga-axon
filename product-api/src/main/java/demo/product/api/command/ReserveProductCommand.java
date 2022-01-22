package demo.product.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class ReserveProductCommand {

  @TargetAggregateIdentifier
  UUID productId;
  UUID orderId;
}
