package demo.order.handler;


import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.LockAwareAggregate;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.inspection.AnnotatedAggregate;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import demo.order.aggregate.OrderAggregate;
import demo.order.api.query.GetOrderQuery;
import demo.order.api.query.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class OrderQueryHandler {

  private final Repository<OrderAggregate> orderRepository;

  @QueryHandler
  public Order handle(GetOrderQuery query) {
    log.info("Getting order: {}", query);
    OrderAggregate order = getWrappedAggregate(orderRepository.load(query.getOrderId().toString()));
    return new Order(
        order.getOrderId(),
        order.getProductId(),
        order.getStatus(),
        order.getReason());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> T getWrappedAggregate(Aggregate<T> aggregate) {
    return ((AnnotatedAggregate<T>) ((LockAwareAggregate) aggregate).getWrappedAggregate()).getAggregateRoot();
  }
}
