package demo.order.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.order.api.StartOrderProcessCommand;
import demo.order.factory.IdFactory;

@RestController
public class OrderController {

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private IdFactory idFactory;

  @GetMapping("/test")
  public Object createOrder() {
    return commandGateway.send(new StartOrderProcessCommand(idFactory.generateOrderId()));
//    return commandGateway.send(new CompleteOrderProcessCommand(randomUUID(), false, false));
//    return commandGateway.send(new OrderConfirmedEvent(randomUUID()));
  }
}
