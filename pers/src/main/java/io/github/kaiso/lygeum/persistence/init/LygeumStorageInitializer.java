/**
   * Copyright © Kais OMRI
   *    
   * Licensed under the Apache License, Version 2.0 (the "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *     http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
   */
package io.github.kaiso.lygeum.persistence.init;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Configuration
public class LygeumStorageInitializer implements ApplicationRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(LygeumStorageInitializer.class);

	private DataSource dataSource;

	@Autowired
	public LygeumStorageInitializer(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.
	 * ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args.containsOption("install")) {
			logger.info("Lygeum started in install mode with (--install) option, will init storage"); 
			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
			database.setDatabaseChangeLogLockTableName("APS_CHANNGESLOCK");
			database.setDatabaseChangeLogTableName("APS_CHANGES");
			Liquibase liquibase = new Liquibase("migration/chliqaps.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update(new Contexts());
			logger.info("Storage initialized");
		}
	}

}
