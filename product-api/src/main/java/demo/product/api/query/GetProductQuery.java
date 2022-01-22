package demo.product.api.query;

import java.util.UUID;

import lombok.Value;

@Value
public class GetProductQuery {

  UUID productId;
}