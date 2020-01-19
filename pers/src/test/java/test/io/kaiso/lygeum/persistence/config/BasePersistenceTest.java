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
package test.io.kaiso.lygeum.persistence.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestPersistenceConfiguration.class })
public class BasePersistenceTest {

	private static Server server;

	@BeforeAll
	public static void init() throws SQLException {
		server = Server.createTcpServer("-tcpPort", "9135", "-tcpAllowOthers", "-baseDir", "~/lygeum").start();
	}

	@AfterAll
	public static void destroy() {
		server.stop();
	}

}
