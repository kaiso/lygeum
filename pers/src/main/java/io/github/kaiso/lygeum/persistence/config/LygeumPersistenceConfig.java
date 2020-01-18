package io.github.kaiso.lygeum.persistence.config;

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
import org.springframework.core.env.Environment;
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
import io.github.kaiso.lygeum.persistence.repositories.BaseJpaRepositoryImpl;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.kaiso.lygeum.persistence.repositories", repositoryBaseClass = BaseJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement()
public class LygeumPersistenceConfig {

	private static final Logger logger = LoggerFactory.getLogger(LygeumPersistenceConfig.class);

	private ApplicationArguments applicationArguments;

	private Environment environment;

	private Properties jpaProperties;

	@Autowired
	public LygeumPersistenceConfig(ApplicationArguments applicationArguments, Environment environment) {
		super();
		this.applicationArguments = applicationArguments;
		this.environment = environment;
	}

	@PostConstruct
	public void configure() {
		logger.info("Configuring Lygeum persistence layer");
	}

	@Bean
	public DataSource dataSource() throws Exception {
		DataSource dataSource;
		String dbVendor = environment.getProperty("lygeum.db.vendor", "h2").toLowerCase();

		switch (dbVendor) {
		case "h2":
			dataSource = createH2DataSource();
			initJpaProperties("org.hibernate.dialect.H2Dialect");
			break;
		case "postgres":
			dataSource = createPostgresDataSource();
			initJpaProperties("org.hibernate.dialect.PostgreSQL82Dialect");
			break;
		default:
			throw new BootstrapMethodError(
					"Failed to initialize Lygeum datasource, db vendor must be among(h2, postgres)");
		}
		new LygeumStorageInitializer(dataSource, applicationArguments).run();
		return dataSource;
	}

	private DataSource createPostgresDataSource() {
		HikariConfig configuration = new HikariConfig();
		configuration.setPoolName("lygeum-db-pool");
		configuration.setMaximumPoolSize(20);
		configuration.setMinimumIdle(5);
		String host = environment.getRequiredProperty("lygeum.db.host");
		String port = environment.getProperty("lygeum.db.port", "5432");
		String database = environment.getProperty("lygeum.db.database", "lygeum");
		configuration.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
		configuration.setUsername(environment.getProperty("lygeum.db.user", "postgres"));
		configuration.setPassword(environment.getProperty("lygeum.db.password", "postgres"));
		return new HikariDataSource(configuration);
	}

	private DataSource createH2DataSource() {
		String home = System.getProperty("user.home");
		DataSource dataSource = DataSourceBuilder.create().username("lygeum").password("")
				.url("jdbc:h2:file:" + home + "/.lygeum/lygeumdb;AUTO_SERVER=TRUE").type(JdbcDataSource.class)
				.driverClassName("org.h2.Driver").build();
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("io.github.kaiso.lygeum.core.entities");
		factory.setDataSource(dataSource);
		factory.setJpaProperties(jpaProperties);
		StringBuilder sb = new StringBuilder();
		sb.append("PersistenceUnitInfo [\n\t").append("provider: ").append(vendorAdapter.getPersistenceProvider())
				.append("\n\t").append("datasource: ").append(dataSource).append("\n\t").append("classloader: ")
				.append(factory.getBeanClassLoader()).append("\n\t]");
		logger.info("Lygeum persistence info:\n{}", sb);
		return factory;
	}

	private Properties initJpaProperties(String dialect) {
		jpaProperties = new Properties();
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

}
