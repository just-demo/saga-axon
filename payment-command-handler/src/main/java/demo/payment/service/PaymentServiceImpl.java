package demo.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Override
  public void pay(UUID paymentId, BigDecimal price) {
//    throw new IllegalArgumentException("Payment failed just for demo");
  }
}
