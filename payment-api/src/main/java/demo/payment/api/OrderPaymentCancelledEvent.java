package demo.payment.api;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderPaymentCancelledEvent {

  private final UUID paymentId;
}
