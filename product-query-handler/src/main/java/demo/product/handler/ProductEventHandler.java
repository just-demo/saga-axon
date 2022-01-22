package demo.product.handler;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import demo.product.api.event.ProductCreatedEvent;
import demo.product.api.event.ProductDeletedEvent;
import demo.product.api.event.ProductReservationCancelledEvent;
import demo.product.api.event.ProductReservationRejectedEvent;
import demo.product.api.event.ProductReservedEvent;
import demo.product.api.event.ProductUpdatedEvent;
import demo.product.repository.ProductEntity;
import demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class ProductEventHandler {

  private final ProductRepository productRepository;

  @EventHandler
  public void on(ProductCreatedEvent event) {
    log.info("Replicating product created: {}", event);
    ProductEntity product = new ProductEntity(event.getProductId(), event.getName(), event.getCount());
    productRepository.save(product);
  }

  @EventHandler
  public void on(ProductUpdatedEvent event) {
    log.info("Replicating product updated: {}", event);
    ProductEntity product = productRepository.findById(event.getProductId()).orElseThrow();
    product.setName(event.getName());
    product.setCount(event.getCount());
    productRepository.save(product);
  }

  @EventHandler
  public void on(ProductDeletedEvent event) {
    log.info("Replicating product deleted: {}", event);
    productRepository.deleteById(event.getProductId());
  }

  @EventHandler
  public void on(ProductReservedEvent event) {
    log.info("Replicating product reserved: {}", event);
    ProductEntity product = productRepository.findById(event.getProductId()).orElseThrow();
    product.setCount(product.getCount() - 1);
    productRepository.save(product);
  }

  @EventHandler
  public void on(ProductReservationCancelledEvent event) {
    log.info("Replicating product reservation cancelled: {}", event);
    ProductEntity product = productRepository.findById(event.getProductId()).orElseThrow();
    product.setCount(product.getCount() + 1);
    productRepository.save(product);
  }

  @EventHandler
  public void on(ProductReservationRejectedEvent event) {
    log.info("Replicating product reservation rejected: {}", event);
  }
}
