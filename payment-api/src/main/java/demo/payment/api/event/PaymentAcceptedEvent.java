package demo.payment.api.event;

import java.util.UUID;

import lombok.Value;

@Value
public class PaymentAcceptedEvent {

  UUID paymentId;
}
