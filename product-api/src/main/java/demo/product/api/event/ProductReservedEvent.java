package demo.product.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class ProductReservedEvent {

  UUID productId;
  UUID orderId;
}
