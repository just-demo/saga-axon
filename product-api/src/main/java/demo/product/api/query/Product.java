package demo.product.api.query;

import java.util.UUID;

import lombok.Value;

@Value
public class Product {

  UUID id;
  String name;
  long count;
}
