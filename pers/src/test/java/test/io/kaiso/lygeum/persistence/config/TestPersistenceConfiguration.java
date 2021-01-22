/**
 * Copyright © Kais OMRI
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.io.kaiso.lygeum.persistence.config;

import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;
import io.github.kaiso.lygeum.persistence.auditing.SpringSecurityAuditorAware;
import io.github.kaiso.lygeum.persistence.config.LygeumBasePersistenceConfig;
import io.github.kaiso.lygeum.persistence.config.LygeumGenericPersistenceConfig;
import io.github.kaiso.lygeum.persistence.service.StorageServiceImpl;
import io.github.kaiso.lygeum.persistence.service.VersionningServiceImpl;
import java.util.Properties;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/** @author Kais OMRI (kaiso) */
@Configuration
@Import({LygeumBasePersistenceConfig.class, LygeumGenericPersistenceConfig.class})
@ComponentScan(
    basePackages = {
      "io.github.kaiso.lygeum.persistence.service",
      "io.github.kaiso.lygeum.persistence.auditing"
    },
    useDefaultFilters = false,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {SpringSecurityAuditorAware.class, StorageServiceImpl.class, VersionningServiceImpl.class}))
public class TestPersistenceConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(TestPersistenceConfiguration.class);

  @Bean
  public ApplicationArguments applicationArguments() {
    return new DefaultApplicationArguments(new String[] {""});
  }

  @Bean
  public SecurityContextHolder securityContextHolder() {
    return new SecurityContextHolder() {

      @Override
      public User getCurrentUser() {
        User user = new User();
        user.setUsername("lygeum-test-user");
        return user;
      }
    };
  }

  @Bean
  @Primary()
  public DataSource dataSource() {
    logger.debug("creating dataSource");
//    return DataSourceBuilder.create()
//        .username("lygeum")
//        .url("jdbc:h2:tcp://localhost:9135/lygeumtestdb;IFEXISTS=FALSE,AUTO_SERVER=TRUE")
//        .type(JdbcDataSource.class)
//        .driverClassName("org.h2.Driver")
//        .build();
    	return
     DataSourceBuilder.create().url("jdbc:h2:mem:lygeumtestdb;;DB_CLOSE_DELAY=-1").type(JdbcDataSource.class)
    		.driverClassName("org.h2.Driver").build();
  }

  @Bean
  @Primary
  public Properties jpaProperties() {
    Properties props = new Properties();
    props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    props.put("hibernate.format_sql", "true");
    props.put("hibernate.show_sql", "true");
    props.put("hibernate.hbm2ddl.auto", "create-drop");
    props.put("hibernate.generate_statistics", "true");
    return props;
  }
}
