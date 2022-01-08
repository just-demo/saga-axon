package demo.order.controller;

import static java.util.UUID.randomUUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.order.api.CompleteOrderProcessCommand;

@RestController
public class OrderController {

  @Autowired
  private CommandGateway commandGateway;

  @GetMapping("/test")
  public Object createOrder() {
    return commandGateway.send(new CompleteOrderProcessCommand(randomUUID(), false, false));
  }
}
