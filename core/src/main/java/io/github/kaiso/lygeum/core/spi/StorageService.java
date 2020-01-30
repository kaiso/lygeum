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
package io.github.kaiso.lygeum.core.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.Client;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public interface StorageService {

	/**
	 * @return
	 */
	List<ApplicationEntity> findAllApplications();

	/**
	 * @param code
	 * @return
	 */
	Optional<ApplicationEntity> findApplicationByCode(String code);

	/**
	 * @param app
	 */
	void updateApplication(ApplicationEntity app);

	/**
	 * @param app
	 * @return
	 */
	ApplicationEntity createApplication(ApplicationEntity app);

	/**
	 * @param app
	 */
	void deleteApplication(ApplicationEntity app);

	/**
	 * @return
	 */
	List<EnvironmentEntity> findAllEnvironments();

	/**
	 * @param code
	 * @return
	 */
	Optional<EnvironmentEntity> findEnvironmentByCode(String code);

	/**
	 * @param env
	 */
	void updateEnvironment(EnvironmentEntity env);

	/**
	 * @param env
	 * @return
	 */
	EnvironmentEntity createEnvironment(EnvironmentEntity env);

	/**
	 * @param env
	 */
	void deleteEnvironment(EnvironmentEntity env);

	/**
	 * @param environment
	 * @param application
	 * @return
	 */
	Collection<PropertyEntity> findPropertiesByEnvironmentAndApplication(String environment, String application);

	/**
	 * @param environment
	 * @param application
	 * @param props
	 */
	void storeProperties(String environment, String application, Map<String, String> props);

	/**
	 * @param environment
	 * @param application
	 * @param props
	 */
	void updateProperties(List<PropertyEntity> props);

	/**
	 * @param clientId
	 * @return
	 */
	Optional<Client> findClientById(String clientId);

	/**
	 * @param username
	 * @return
	 */
	Optional<User> findUserByUsername(String username);

	void deleteProperty(String code, EnvironmentEntity env);

	Optional<PropertyEntity> findPropertyByCode(String code);

	List<Role> findAllRoles();

	User saveUser(User user);

	List<User> findAllUsers();

	Optional<User> findUserByCode(String code);

	void deleteUserByCode(String code);

	List<Client> findAllClients();

	void deleteClientByCode(String code);

	Client saveClient(Client client);

	Optional<ApplicationEntity> findApplicationByNameOrCode(String application);

	Optional<EnvironmentEntity> findEnvironmentByNameOrCode(String environment);

}
