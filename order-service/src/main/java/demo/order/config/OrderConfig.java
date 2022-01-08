package demo.order.config;

import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.messaging.ScopeAwareProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

  @Bean
  public ScopeAwareProvider scopeAwareProvider(org.axonframework.config.Configuration configuration) {
    return new ConfigurationScopeAwareProvider(configuration);
  }

  @Bean
  public DeadlineManager deadlineManager(ScopeAwareProvider scopeAwareProvider) {
    return SimpleDeadlineManager.builder()
        .scopeAwareProvider(scopeAwareProvider)
        .build();
  }

}
