package demo.order.api;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderConfirmedEvent {

  private final UUID orderId;
}
