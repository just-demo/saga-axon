package demo.order.api.query;

import java.util.UUID;

import lombok.Value;

@Value
public class Order {

  UUID orderId;
  UUID productId;
  OrderStatus status;
  String reason;
}
