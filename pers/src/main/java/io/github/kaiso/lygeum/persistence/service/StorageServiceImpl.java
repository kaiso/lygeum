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
package io.github.kaiso.lygeum.persistence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.Client;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.spi.StorageService;
import io.github.kaiso.lygeum.persistence.repositories.ApplicationRepository;
import io.github.kaiso.lygeum.persistence.repositories.EnvironmentRepository;
import io.github.kaiso.lygeum.persistence.repositories.PropertyRepository;
import io.github.kaiso.lygeum.persistence.repositories.PropertyValueRepository;
import io.github.kaiso.lygeum.persistence.repositories.RoleRepository;
import io.github.kaiso.lygeum.persistence.repositories.UserRepository;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
@Transactional(value = TxType.REQUIRED)
public class StorageServiceImpl implements StorageService {

    private ApplicationRepository applicationRepository;

    private EnvironmentRepository environmentRepository;

    private PropertyRepository propertyRepository;

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private PropertyValueRepository propertyValueRepository;

    @Autowired
    public StorageServiceImpl(ApplicationRepository applicationRepository, EnvironmentRepository environmentRepository,
	    PropertyRepository propertyRepository, PropertyValueRepository propertyValueRepository,
	    RoleRepository roleRepository, UserRepository userRepository) {
	this.applicationRepository = applicationRepository;
	this.environmentRepository = environmentRepository;
	this.propertyRepository = propertyRepository;
	this.propertyValueRepository = propertyValueRepository;
	this.roleRepository = roleRepository;
	this.userRepository = userRepository;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.kaiso.lygeum.core.spi.StorageService#findAllApplications()
     */
    @Override
    public List<ApplicationEntity> findAllApplications() {
	return applicationRepository.streamAll().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#findApplicationByCode(java.
     * lang.String)
     */
    @Override
    public Optional<ApplicationEntity> findApplicationByCode(String code) {
	return applicationRepository.findByCode(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#updateApplication(io.github.
     * kaiso.lygeum.core.entities.ApplicationEntity)
     */
    @Override
    public void updateApplication(ApplicationEntity app) {
	applicationRepository.save(app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#createApplication(io.github.
     * kaiso.lygeum.core.entities.ApplicationEntity)
     */
    @Override
    public ApplicationEntity createApplication(ApplicationEntity app) {
	return applicationRepository.save(app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#deleteApplication(io.github.
     * kaiso.lygeum.core.entities.ApplicationEntity)
     */
    @Override
    public void deleteApplication(ApplicationEntity app) {
	propertyRepository
		.deleteAll(propertyRepository.findByApplicationCode(app.getCode()).collect(Collectors.toList()));
	applicationRepository.delete(app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.kaiso.lygeum.core.spi.StorageService#findAllEnvironments()
     */
    @Override
    public List<EnvironmentEntity> findAllEnvironments() {
	return environmentRepository.streamAll().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#findEnvironmentByCode(java.
     * lang.String)
     */
    @Override
    public Optional<EnvironmentEntity> findEnvironmentByCode(String code) {
	return environmentRepository.findByCode(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#updateEnvironment(io.github.
     * kaiso.lygeum.core.entities.EnvironmentEntity)
     */
    @Override
    public void updateEnvironment(EnvironmentEntity env) {
	environmentRepository.save(env);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#createEnvironment(io.github.
     * kaiso.lygeum.core.entities.EnvironmentEntity)
     */
    @Override
    public EnvironmentEntity createEnvironment(EnvironmentEntity env) {
	return environmentRepository.save(env);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#deleteEnvironment(io.github.
     * kaiso.lygeum.core.entities.EnvironmentEntity)
     */
    @Override
    public void deleteEnvironment(EnvironmentEntity env) {
	environmentRepository.delete(env);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.kaiso.lygeum.core.spi.StorageService#
     * findPropertiesByEnvironmentAndApplication(java.lang.String, java.lang.String)
     */
    @Override
    public Collection<PropertyEntity> findPropertiesByEnvironmentAndApplication(String environment,
	    String application) {
	return propertyRepository.findByEnvironmentAndApplicationNamed(environment, application)
		.map(p -> {
		    p.getValues().removeIf(v -> !environment.equals(v.getEnvironment().getCode()));
		    return p;
		})
		.collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#storeProperties(java.lang.
     * String, java.lang.String, java.util.Map)
     */
    @Override
    public void storeProperties(String environment, String application, Map<String, String> props) {
	List<PropertyEntity> list = new ArrayList<>();
	EnvironmentEntity env = environmentRepository.findByCode(environment).orElseThrow(
		() -> new IllegalArgumentException("environment can not be found with code " + environment));
	ApplicationEntity app = applicationRepository.findByCode(application).orElseThrow(
		() -> new IllegalArgumentException("application can not be found with code " + application));

	list.addAll(propertyRepository.findByEnvironmentAndApplicationNamed(environment, application).map(p -> {
	    if (props.containsKey(p.getKey())) {
		p.getValues().forEach(v -> {
		    if (v.getEnvironment().equals(env)) {
			v.setValue(props.get(p.getKey()));
		    }
		});
		props.remove(p.getKey());
	    } else {
		p.getValues().forEach(v -> {
		    if (v.getEnvironment().equals(env)) {
			v.setValue(null);
		    }
		});
	    }
	    return p;
	}).collect(Collectors.toList()));

	props.forEach((k, v) -> list.add(
		PropertyEntity.builder().withKey(k).withValue(v).withApplication(app).withEnvironment(env).build()));
	propertyRepository.saveAll(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#updateProperties(java.lang.
     * String, java.lang.String, java.util.List)
     */
    @Override
    public void updateProperties(List<PropertyEntity> props) {
	propertyRepository.saveAll(props);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.github.kaiso.lygeum.core.spi.StorageService#findClientById(java.lang.
     * String)
     */
    @Override
    public Optional<Client> findClientById(String clientId) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.kaiso.lygeum.core.spi.StorageService#findUserByUsername(java.lang.
     * String)
     */
    @Override
    public Optional<User> findUserByUsername(String username) {
	return userRepository.findByUsername(username);
    }

    @Override
    public void deleteProperty(String code) {
	propertyRepository.delete(propertyRepository.findByCode(code)
		.orElseThrow(() -> new EntityNotFoundException("Can not find property with code " + code)));

    }

    @Override
    public Optional<PropertyEntity> findPropertyByCode(String code) {
	return propertyRepository.findByCode(code);
    }

    @Override
    public List<Role> findAllRoles() {
	return roleRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
	if (!CollectionUtils.isEmpty(user.getRoles())) {
	    List<String> codes = user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList());
	    user.setRoles(
		    roleRepository.streamAll().filter(r -> codes.contains(r.getCode())).collect(Collectors.toList()));
	}
	return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
	return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserByCode(String code) {
	return userRepository.findByCode(code);
    }

}
