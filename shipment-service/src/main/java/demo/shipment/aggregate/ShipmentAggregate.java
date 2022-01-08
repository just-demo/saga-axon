package demo.shipment.aggregate;

import static demo.shipment.api.ShipmentStatus.SHIPPED;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import demo.shipment.api.ShipOrderCommand;
import demo.shipment.api.ShipmentStatus;
import demo.shipment.api.ShipmentStatusUpdatedEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aggregate
public class ShipmentAggregate {

  @AggregateIdentifier
  private UUID shipmentId;
  private ShipmentStatus status;

  public ShipmentAggregate() {
    log.info("Instantiating {}", getClass().getSimpleName());
  }

  @CommandHandler
  public ShipmentAggregate(ShipOrderCommand command) {
    AggregateLifecycle.apply(new ShipmentStatusUpdatedEvent(command.getShipmentId(), SHIPPED));
  }

  @EventSourcingHandler
  protected void on(ShipmentStatusUpdatedEvent event) {
    // TODO: how to use this state? can we get rid of the aggregate?
    // TODO: try more statuses and therefore commands/events!!!
    shipmentId = event.getShipmentId();
    status = event.getShipmentStatus();
  }
}
