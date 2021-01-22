package io.github.kaiso.lygeum.persistence.config;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.kaiso.lygeum.persistence.repositories.generic")
@EnableTransactionManagement()
public class LygeumGenericPersistenceConfig {

  private static final Logger logger =
      LoggerFactory.getLogger(LygeumGenericPersistenceConfig.class);

  @PostConstruct
  public void configure() {
    logger.info("Configuring Lygeum Generic persistence layer");
  }
}
