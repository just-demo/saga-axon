package demo.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Override
  public void pay(UUID paymentId, UUID orderId) {
//    throw new IllegalArgumentException("Payment failed just for demo");
  }
}
