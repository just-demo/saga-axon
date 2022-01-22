package demo.product.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class ProductReservationRejectedEvent {

  UUID productId;
  UUID orderId;
  String reason;
}
