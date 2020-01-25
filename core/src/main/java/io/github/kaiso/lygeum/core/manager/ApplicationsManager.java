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
package io.github.kaiso.lygeum.core.manager;

import java.util.List;
import java.util.Optional;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public interface ApplicationsManager {

	/**
	 * 
	 */
	List<ApplicationEntity> findAll();

	/**
	 * @param code
	 */
	Optional<ApplicationEntity> findByCode(String code);

	/**
	 * @param env
	 */
	void update(ApplicationEntity env);

	/**
	 * @param env
	 */
	ApplicationEntity create(ApplicationEntity env);

	/**
	 * @param env
	 */
	void delete(ApplicationEntity env);
	

	Optional<ApplicationEntity> findByNameOrCode(String application);

}
