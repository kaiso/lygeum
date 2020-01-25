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

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.spi.StorageService;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
public class ApplicationsManagerImpl implements ApplicationsManager {

	private StorageService storageService;

	@Autowired
	public ApplicationsManagerImpl(StorageService storageService) {
		this.storageService = storageService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.kaiso.lygeum.core.manager.ApplicationsManager#findAll()
	 */
	@Override
	public List<ApplicationEntity> findAll() {
		
		return storageService.findAllApplications();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.ApplicationsManager#findByCode(java.lang
	 * .String)
	 */
	@Override
	public Optional<ApplicationEntity> findByCode(String code) {
		
		return storageService.findApplicationByCode(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.ApplicationsManager#update(io.github.
	 * kaiso.lygeum.core.entities.ApplicationEntity)
	 */
	@Override
	public void update(ApplicationEntity app) {
       storageService.updateApplication(app);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.ApplicationsManager#create(io.github.
	 * kaiso.lygeum.core.entities.ApplicationEntity)
	 */
	@Override
	public ApplicationEntity create(ApplicationEntity app) {
		return storageService.createApplication(app);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.ApplicationsManager#delete(io.github.
	 * kaiso.lygeum.core.entities.ApplicationEntity)
	 */
	@Override
	public void delete(ApplicationEntity app) {
	  storageService.deleteApplication(app);	
	}

	@Override
	public Optional<ApplicationEntity> findByNameOrCode(String application) {
		return storageService.findApplicationByNameOrCode(application);
	}

}
