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
package io.github.kaiso.lygeum.core.manager.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.spi.StorageService;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
public class EnvironmentsManagerImpl implements EnvironmentsManager {

	private StorageService storageService;

	@Autowired
	public EnvironmentsManagerImpl(StorageService storageService) {
		this.storageService = storageService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.kaiso.lygeum.core.manager.EnvironmentsManager#findAll()
	 */
	@Override
	public List<EnvironmentEntity> findAll() {
		return storageService.findAllEnvironments();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.EnvironmentsManager#findByCode(java.lang
	 * .String)
	 */
	@Override
	public Optional<EnvironmentEntity> findByCode(String code) {
		return storageService.findEnvironmentByCode(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.EnvironmentsManager#update(io.github.
	 * kaiso.lygeum.core.entities.EnvironmentEntity)
	 */
	@Override
	public void update(EnvironmentEntity env) {
		storageService.updateEnvironment(env);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.EnvironmentsManager#create(io.github.
	 * kaiso.lygeum.core.entities.EnvironmentEntity)
	 */
	@Override
	public EnvironmentEntity create(EnvironmentEntity env) {
		return storageService.createEnvironment(env);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.EnvironmentsManager#delete(io.github.
	 * kaiso.lygeum.core.entities.EnvironmentEntity)
	 */
	@Override
	public void delete(EnvironmentEntity env) {
		storageService.deleteEnvironment(env);
	}

}
