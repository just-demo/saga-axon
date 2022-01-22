package demo.order.controller;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.order.api.command.CreateOrderCommand;
import demo.order.api.query.GetOrderQuery;
import demo.order.api.query.Order;
import demo.order.model.CreateOrderModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

  private final CommandGateway commandGateway;
  private final QueryGateway queryGateway;
  private final EventStore eventStore;


  @PostMapping
  public CompletableFuture<UUID> create(@RequestBody CreateOrderModel model) {
    return commandGateway.send(new CreateOrderCommand(
        randomUUID(),
        model.getProductId()));
  }

  @GetMapping("/{orderId}")
  public CompletableFuture<Order> get(@PathVariable UUID orderId) {
    return queryGateway.query(new GetOrderQuery(orderId), Order.class);
  }

  @GetMapping("/{orderId}/events")
  public List<DomainEventMessage<?>> getEvents(@PathVariable UUID orderId) {
    return eventStore.readEvents(orderId.toString())
        .asStream()
        .collect(toList());
  }
}
