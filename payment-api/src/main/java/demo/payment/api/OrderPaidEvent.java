package demo.payment.api;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderPaidEvent {

  private final UUID paymentId;
}
