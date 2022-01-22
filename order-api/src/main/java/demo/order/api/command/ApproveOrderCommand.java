package demo.order.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class ApproveOrderCommand {

  @TargetAggregateIdentifier
  UUID orderId;
}
