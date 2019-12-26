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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.kaiso.lygeum.persistence.init.LygeumStorageInitializer;
import io.github.kaiso.lygeum.persistence.repositories.BaseJpaRepositoryImpl;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.kaiso.lygeum.persistence.repositories", repositoryBaseClass = BaseJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement()
public class LygeumPersistenceConfig {

    private static final Logger logger = LoggerFactory.getLogger(LygeumPersistenceConfig.class);

    private ApplicationArguments applicationArguments;

    @Autowired
    public LygeumPersistenceConfig(ApplicationArguments applicationArguments) {
	super();
	this.applicationArguments = applicationArguments;
    }

    @PostConstruct
    public void configure() {
	logger.info("Configuring Lygeum persistence layer");
    }

    @Bean
    public DataSource dataSource() throws Exception {
	String home = System.getProperty("user.home");
	JdbcDataSource dataSource = DataSourceBuilder.create().username("lygeum").password("")
		.url("jdbc:h2:file:" + home + "/.lygeum/lygeumdb;AUTO_SERVER=TRUE").type(JdbcDataSource.class)
		.driverClassName("org.h2.Driver").build();
	new LygeumStorageInitializer(dataSource, applicationArguments).run();
	return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
	    Properties jpaProperties) {

	HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	vendorAdapter.setGenerateDdl(false);
	LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	factory.setJpaVendorAdapter(vendorAdapter);
	factory.setPackagesToScan("io.github.kaiso.lygeum.core.entities");
	factory.setDataSource(dataSource);
	factory.setJpaProperties(jpaProperties);
	StringBuilder sb = new StringBuilder();
	sb.append("PersistenceUnitInfo [\n\t")
		.append("provider: ")
		.append(vendorAdapter.getPersistenceProvider())
		.append("\n\t")
		.append("datasource: ")
		.append(dataSource)
		.append("\n\t")
		.append("classloader: ")
		.append(factory.getBeanClassLoader())
		.append("\n\t]");
	logger.info("Lygeum persistence info:\n{}", sb);
	return factory;
    }

    @Bean
    public Properties jpaProperties() {
	Properties props = new Properties();
	props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
	props.put("hibernate.showSql", "true");
	props.put("hibernate.hbm2ddl.auto", "update");
	return props;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

	JpaTransactionManager txManager = new JpaTransactionManager();
	txManager.setEntityManagerFactory(entityManagerFactory);
	return txManager;
    }

}
