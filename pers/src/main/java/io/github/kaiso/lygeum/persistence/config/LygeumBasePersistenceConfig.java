package io.github.kaiso.lygeum.persistence.config;

import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.kaiso.lygeum.persistence.init.LygeumStorageInitializer;
import io.github.kaiso.lygeum.persistence.repositories.base.BaseJpaRepositoryImpl;

@Configuration
@EnableJpaRepositories(
    basePackages = "io.github.kaiso.lygeum.persistence.repositories.base",
    repositoryBaseClass = BaseJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement()
public class LygeumBasePersistenceConfig {

  private static final Logger logger = LoggerFactory.getLogger(LygeumBasePersistenceConfig.class);

  private ApplicationArguments applicationArguments;

  private String dialect;

  @Autowired
  public LygeumBasePersistenceConfig(ApplicationArguments applicationArguments) {
    super();
    this.applicationArguments = applicationArguments;
  }

  @PostConstruct
  public void configure() {
    logger.info("Configuring Lygeum persistence layer");
  }

  @Bean
  public DataSource dataSource() throws Exception {
    DataSource dataSource;
    String dbVendor = getApplicationArgument("db-vendor", "h2").get();
    switch (dbVendor) {
      case "h2":
        dataSource = createH2DataSource();
        dialect = "org.hibernate.dialect.H2Dialect";
        break;
      case "postgres":
        dataSource = createPostgresDataSource();
        dialect = "org.hibernate.dialect.PostgreSQL82Dialect";
        break;
      default:
        throw new BootstrapMethodError(
            "Failed to initialize Lygeum datasource, db vendor must be among(h2, postgres)");
    }

    System.setProperty("lygeum.db.vendor", dbVendor);

    new LygeumStorageInitializer(dataSource, applicationArguments).run();
    return dataSource;
  }

  private DataSource createPostgresDataSource() {
    HikariConfig configuration = new HikariConfig();
    configuration.setPoolName("lygeum-db-pool");
    configuration.setMaximumPoolSize(20);
    configuration.setMinimumIdle(5);
    String host =
        getApplicationArgument("db-host", null)
            .orElseThrow(() -> new IllegalStateException("missing lygeum.db.host argument"));
    System.setProperty("lygeum.db.host", host);
    String port = getApplicationArgument("db-port", "5432").get();
    String database = getApplicationArgument("db-database", "lygeum").get();
    configuration.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
    configuration.setSchema(getApplicationArgument("db-schema", "public").get());
    configuration.setUsername(getApplicationArgument("db-user", "postgres").get());
    configuration.setPassword(getApplicationArgument("db-password", "postgres").get());
    return new HikariDataSource(configuration);
  }

  private DataSource createH2DataSource() {
    System.setProperty("lygeum.db.host", "embedded");
    String home = System.getProperty("user.home");
    return DataSourceBuilder.create()
        .username("lygeum")
        .password("")
        .url("jdbc:h2:file:" + home + "/.lygeum/lygeumdb;AUTO_SERVER=TRUE")
        .type(JdbcDataSource.class)
        .driverClassName("org.h2.Driver")
        .build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, Properties jpaProperties) {

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(false);
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("io.github.kaiso.lygeum.core.entities");
    factory.setDataSource(dataSource);
    factory.setJpaProperties(jpaProperties);
    return factory;
  }

  @Bean
  @DependsOn("dataSource")
  public Properties jpaProperties() {
    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.dialect", dialect);
    jpaProperties.put("hibernate.showSql", "true");
    jpaProperties.put("hibernate.hbm2ddl.auto", "validate");
    return jpaProperties;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

  private Optional<String> getApplicationArgument(String key, String defaultValue) {
    if (applicationArguments.containsOption(key)) {
      return Optional.of(applicationArguments.getOptionValues(key).get(0));
    } else {
      return Optional.ofNullable(defaultValue);
    }
  }
}
