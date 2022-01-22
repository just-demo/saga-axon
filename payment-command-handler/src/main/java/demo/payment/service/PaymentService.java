package demo.payment.service;

import java.util.UUID;

public interface PaymentService {

  void pay(UUID paymentId, UUID orderId);
}
