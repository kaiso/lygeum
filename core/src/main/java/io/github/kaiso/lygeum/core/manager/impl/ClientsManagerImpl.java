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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import io.github.kaiso.lygeum.core.entities.Client;
import io.github.kaiso.lygeum.core.manager.ClientsManager;
import io.github.kaiso.lygeum.core.spi.StorageService;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
@Primary
public class ClientsManagerImpl implements ClientsManager {

	private StorageService storageService;

	@Autowired
	public ClientsManagerImpl(StorageService storageService) {
		this.storageService = storageService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetailsService#
	 * loadClientByClientId(java.lang.String)
	 */
	@Override
	public Client loadClientByClientId(String clientId) throws ClientRegistrationException {
		return storageService.findClientById(clientId)
				.orElseThrow(() -> new ClientRegistrationException("client not found with id" + clientId));
	}

	@Override
	public List<Client> findAll() {
		return storageService.findAllClients();
	}

	@Override
	public void deleteByCode(String code) {
		storageService.deleteClientByCode(code);
	}

	@Override
	public Client create(Client client) {
		return storageService.saveClient(client);
	}

	@Override
	public Client save(Client client) {
		return storageService.saveClient(client);
	}

}
