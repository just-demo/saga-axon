package demo.order.api;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class CompleteOrderProcessCommand {

  @TargetAggregateIdentifier
  private final UUID orderId;
  private final boolean orderPaid;
  private final boolean orderDelivered;
}
