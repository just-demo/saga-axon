package demo.product.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import demo.product.api.command.CancelProductReservationCommand;
import demo.product.api.command.CreateProductCommand;
import demo.product.api.command.DeleteProductCommand;
import demo.product.api.command.ReserveProductCommand;
import demo.product.api.command.UpdateProductCommand;
import demo.product.api.event.ProductCreatedEvent;
import demo.product.api.event.ProductDeletedEvent;
import demo.product.api.event.ProductReservationCancelledEvent;
import demo.product.api.event.ProductReservationRejectedEvent;
import demo.product.api.event.ProductReservedEvent;
import demo.product.api.event.ProductUpdatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor
@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
public class ProductAggregate {

  @AggregateIdentifier
  private UUID productId;
  private String name;
  private long count;

  @CommandHandler
  public ProductAggregate(CreateProductCommand command) {
    log.info("Creating product: {}", command);
    apply(new ProductCreatedEvent(
        command.getProductId(),
        command.getName(),
        command.getCount()));
  }

  @CommandHandler
  protected void handle(UpdateProductCommand command) {
    log.info("Updating product: {}", command);
    // TODO: should notify sagas in progress as well
    apply(new ProductUpdatedEvent(
        command.getProductId(),
        command.getName(),
        command.getCount()));
  }

  @CommandHandler
  protected void handle(DeleteProductCommand command) {
    log.info("Deleting product: {}", command);
    // TODO: should notify sagas in progress as well
    apply(new ProductDeletedEvent(
        command.getProductId()));
  }

  @CommandHandler
  protected void handle(ReserveProductCommand command) {
    log.info("Reserving product: {}", command);
    if (count > 0) {
      apply(new ProductReservedEvent(
          command.getProductId(),
          command.getOrderId()));
    } else {
      apply(new ProductReservationRejectedEvent(
          command.getProductId(),
          command.getOrderId(),
          "Product not available"));
    }
  }

  @CommandHandler
  protected void handle(CancelProductReservationCommand command) {
    log.info("Cancelling product reservation: {}", command);
    apply(new ProductReservationCancelledEvent(
        command.getProductId(),
        command.getOrderId()));
  }

  @EventSourcingHandler
  protected void on(ProductCreatedEvent event) {
    log.info("Product created: {}", event);
    productId = event.getProductId();
    name = event.getName();
    count = event.getCount();
  }

  @EventSourcingHandler
  protected void on(ProductUpdatedEvent event) {
    log.info("Product updated: {}", event);
    name = event.getName();
    count = event.getCount();
  }

  @EventSourcingHandler
  protected void on(ProductDeletedEvent event) {
    log.info("Product deleted: {}", event);
    markDeleted();
  }

  @EventSourcingHandler
  protected void on(ProductReservedEvent event) {
    log.info("Product reserved: {}", event);
    count -= 1;
  }

  @EventSourcingHandler
  protected void on(ProductReservationCancelledEvent event) {
    log.info("Product reservation cancelled: {}", event);
    count += 1;
  }

  @EventSourcingHandler
  public void on(ProductReservationRejectedEvent event) {
    log.info("Product reservation rejected: {}", event);
  }
}
