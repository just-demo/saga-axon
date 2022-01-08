package demo.shipment.api;

import java.util.UUID;

import lombok.Data;

@Data
public class ShipmentStatusUpdatedEvent {

  private final UUID shipmentId;
  private final ShipmentStatus shipmentStatus;
}
