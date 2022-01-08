package demo.order.api;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class StartOrderProcessCommand {

  @TargetAggregateIdentifier
  private final UUID orderId;

}
