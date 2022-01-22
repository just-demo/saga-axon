package demo.product.controller;

import static java.util.UUID.randomUUID;
import static org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.product.api.command.CreateProductCommand;
import demo.product.api.command.DeleteProductCommand;
import demo.product.api.command.UpdateProductCommand;
import demo.product.api.query.GetProductQuery;
import demo.product.api.query.GetProductsQuery;
import demo.product.api.query.Product;
import demo.product.model.CreateProductModel;
import demo.product.model.UpdateProductModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

  private final CommandGateway commandGateway;
  private final QueryGateway queryGateway;

  @GetMapping
  public CompletableFuture<List<Product>> getAll() {
    return queryGateway.query(new GetProductsQuery(), multipleInstancesOf(Product.class));
  }

  @PostMapping
  public CompletableFuture<UUID> create(@RequestBody CreateProductModel model) {
    return commandGateway.send(new CreateProductCommand(
        randomUUID(),
        model.getName(),
        model.getCount()));
  }

  @GetMapping("/{productId}")
  public CompletableFuture<Product> get(@PathVariable UUID productId) {
    return queryGateway.query(new GetProductQuery(productId), Product.class);
  }

  @PutMapping("/{productId}")
  public CompletableFuture<Void> update(@PathVariable UUID productId, @RequestBody UpdateProductModel model) {
    return commandGateway.send(new UpdateProductCommand(
        productId,
        model.getName(),
        model.getCount()));
  }

  @DeleteMapping("/{productId}")
  public CompletableFuture<Void> delete(@PathVariable UUID productId) {
    return commandGateway.send(new DeleteProductCommand(productId));
  }
}
