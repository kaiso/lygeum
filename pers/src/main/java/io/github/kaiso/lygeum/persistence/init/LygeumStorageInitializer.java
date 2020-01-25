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
import org.springframework.boot.ApplicationArguments;

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
public class LygeumStorageInitializer {

    private static final Logger logger = LoggerFactory.getLogger(LygeumStorageInitializer.class);

    private DataSource dataSource;

    @SuppressWarnings("unused")
	private ApplicationArguments applicationArguments;

    public LygeumStorageInitializer(DataSource dataSource, ApplicationArguments applicationArguments) {
	super();
	this.dataSource = dataSource;
	this.applicationArguments = applicationArguments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.
     * ApplicationArguments)
     */
    public void run() throws Exception {
	//if (applicationArguments.containsOption("install")) {
	  //  logger.info("Lygeum started in install mode with (--install) option, will init storage");
	    Database database = DatabaseFactory.getInstance()
		    .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
	    database.setDatabaseChangeLogLockTableName("LGM_CHANNGESLOCK");
	    database.setDatabaseChangeLogTableName("LGM_CHANGES");
	    Liquibase liquibase = new Liquibase("migration/chliqaps.xml", new ClassLoaderResourceAccessor(), database);
	    liquibase.update(new Contexts());
	    logger.info("Storage initialized");
	//}
    }

}
