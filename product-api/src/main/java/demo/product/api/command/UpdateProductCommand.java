package demo.product.api.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class UpdateProductCommand {

  @TargetAggregateIdentifier
  UUID productId;
  String name;
  long count;
}
