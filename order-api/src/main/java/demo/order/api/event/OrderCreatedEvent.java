package demo.order.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class OrderCreatedEvent {

  UUID orderId;
  UUID productId;
}
