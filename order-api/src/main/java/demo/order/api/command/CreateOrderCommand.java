package demo.order.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CreateOrderCommand {

  @TargetAggregateIdentifier
  UUID orderId;
  UUID productId;
}
