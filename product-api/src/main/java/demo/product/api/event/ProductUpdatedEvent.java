package demo.product.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class ProductUpdatedEvent {

  UUID productId;
  String name;
  long count;
}
