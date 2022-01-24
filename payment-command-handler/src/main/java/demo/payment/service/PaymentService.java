package demo.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

  void pay(UUID paymentId, BigDecimal price);
}
