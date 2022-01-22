package demo.product.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class ProductCreatedEvent {

  UUID productId;
  String name;
  long count;
}
