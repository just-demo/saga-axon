package demo.order.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class RejectOrderCommand {

  @TargetAggregateIdentifier
  UUID orderId;
  String reason;
}
