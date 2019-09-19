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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.manager.PropertiesManager;
import io.github.kaiso.lygeum.core.spi.StorageService;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
public class PropertiesManagerImpl implements PropertiesManager {

	private StorageService storageService;

	@Autowired
	public PropertiesManagerImpl(StorageService storageService) {
		this.storageService = storageService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.kaiso.lygeum.core.manager.PropertiesManager#
	 * findPropertiesByEnvironmentAndApplication(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<PropertyEntity> findPropertiesByEnvironmentAndApplication(String environment, String application) {
		return storageService.findPropertiesByEnvironmentAndApplication(environment, application);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.PropertiesManager#storeProperties(java.
	 * lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void storeProperties(String environment, String application, Map<String, String> props) {
		storageService.storeProperties(environment, application, props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.kaiso.lygeum.core.manager.PropertiesManager#updateProperties(java.
	 * lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void updateProperties( List<PropertyEntity> props) {
		storageService.updateProperties(props);
	}

	@Override
	public void delete(String code) {
		storageService.deleteProperty(code);
	}

	@Override
	public Optional<PropertyEntity> findByCode(String code) {
	    return storageService.findPropertyByCode(code);
	}

}
