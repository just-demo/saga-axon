package demo.order.factory;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class IdFactory {

  public UUID generateOrderId() {
    return randomUUID();
  }

  public UUID generatePaymentId() {
    return randomUUID();
  }

  public UUID generateShipmentId() {
    return randomUUID();
  }
}
