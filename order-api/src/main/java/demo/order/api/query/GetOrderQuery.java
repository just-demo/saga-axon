package demo.order.api.query;

import java.util.UUID;

import lombok.Value;

@Value
public class GetOrderQuery {

  UUID orderId;
}