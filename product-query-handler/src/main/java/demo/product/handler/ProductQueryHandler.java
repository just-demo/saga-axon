package demo.product.handler;


import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import demo.product.api.query.GetProductQuery;
import demo.product.api.query.GetProductsQuery;
import demo.product.api.query.Product;
import demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class ProductQueryHandler {

  private final ProductRepository productRepository;

  @QueryHandler
  public List<Product> handle(GetProductsQuery query) {
    log.info("Getting products: {}", query);
    return stream(productRepository.findAll().spliterator(), false)
        .map(product -> new Product(product.getId(), product.getName(), product.getCount()))
        .collect(toList());
  }

  @QueryHandler
  public Product handle(GetProductQuery query) {
    log.info("Getting product: {}", query);
    return productRepository.findById(query.getProductId())
        .map(product -> new Product(product.getId(), product.getName(), product.getCount()))
        .orElseThrow();
  }
}
