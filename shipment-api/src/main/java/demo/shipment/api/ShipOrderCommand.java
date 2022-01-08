package demo.shipment.api;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class ShipOrderCommand {

  @TargetAggregateIdentifier
  private final UUID shipmentId;
}
