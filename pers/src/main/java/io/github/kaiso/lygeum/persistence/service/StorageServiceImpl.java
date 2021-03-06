/**
 * Copyright © Kais OMRI
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.kaiso.lygeum.persistence.service;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.Client;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;
import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.spi.StorageService;
import io.github.kaiso.lygeum.persistence.repositories.base.ApplicationRepository;
import io.github.kaiso.lygeum.persistence.repositories.base.ClientRepository;
import io.github.kaiso.lygeum.persistence.repositories.base.EnvironmentRepository;
import io.github.kaiso.lygeum.persistence.repositories.base.PropertyRepository;
import io.github.kaiso.lygeum.persistence.repositories.base.RoleRepository;
import io.github.kaiso.lygeum.persistence.repositories.base.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** @author Kais OMRI (kaiso) */
@Service
@Transactional(value = TxType.REQUIRES_NEW)
public class StorageServiceImpl implements StorageService {

  private ApplicationRepository applicationRepository;

  private EnvironmentRepository environmentRepository;

  private PropertyRepository propertyRepository;

  private RoleRepository roleRepository;

  private UserRepository userRepository;

  private ClientRepository clientRepository;


  @Autowired
  public StorageServiceImpl(
      ApplicationRepository applicationRepository,
      EnvironmentRepository environmentRepository,
      PropertyRepository propertyRepository,
      ClientRepository clientRepository,
      RoleRepository roleRepository,
      UserRepository userRepository) {
    this.applicationRepository = applicationRepository;
    this.environmentRepository = environmentRepository;
    this.propertyRepository = propertyRepository;
    this.clientRepository = clientRepository;
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
    ApplicationEntity save = applicationRepository.save(app);
    roleRepository.saveAll(getRolesFor("application", save.getCode(), save.getName()));
    return save;
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
    propertyRepository.deleteAll(
        propertyRepository.findByApplicationCode(app.getCode()).collect(Collectors.toList()));
    roleRepository.deleteRolesByCode(
        getRolesFor("application", app.getCode(), app.getName())
            .stream()
            .map(Role::getCode)
            .collect(Collectors.toList())
            .toArray(new String[2]));
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
    EnvironmentEntity save = environmentRepository.save(env);
    roleRepository.saveAll(getRolesFor("environment", save.getCode(), save.getName()));
    return save;
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
    roleRepository.deleteRolesByCode(
        getRolesFor("environment", env.getCode(), env.getName())
            .stream()
            .map(Role::getCode)
            .collect(Collectors.toList())
            .toArray(new String[2]));
    environmentRepository.delete(env);
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.kaiso.lygeum.core.spi.StorageService#
   * findPropertiesByEnvironmentAndApplication(java.lang.String, java.lang.String)
   */
  @Override
  public Collection<PropertyEntity> findPropertiesByEnvironmentAndApplication(
      String environment, String application) {
    return propertyRepository
        .findByEnvironmentAndApplicationNamed(environment, application)
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
    EnvironmentEntity env =
        environmentRepository
            .findByCode(environment)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "environment can not be found with code " + environment));
    ApplicationEntity app =
        applicationRepository
            .findByCode(application)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "application can not be found with code " + application));
    List<PropertyEntity> list =
        propertyRepository
            .findByApplicationCode(application)
            .map(
                p -> {
                  if (props.containsKey(p.getName())) {
                    AtomicBoolean found = new AtomicBoolean(false);
                    p.getValues()
                        .forEach(
                            pv -> {
                              if (pv.getEnvironment().getCode().equals(env.getCode())) {
                                pv.setValue(props.get(p.getName()));
                                found.set(true);
                              }
                            });
                    if (!found.get()) {
                      p.getValues()
                          .add(
                              PropertyValueEntity.builder()
                                  .withProperty(p)
                                  .withEnvironment(env)
                                  .withValue(props.get(p.getName()))
                                  .build());
                    }
                    props.remove(p.getName());
                  } else {
                    p.getValues().removeIf(v -> v.getEnvironment().equals(env));
                  }
                  return p;
                })
            .collect(Collectors.toList());

    props.forEach(
        (k, v) ->
            list.add(
                PropertyEntity.builder()
                    .withName(k)
                    .withValue(v)
                    .withApplication(app)
                    .withEnvironment(env)
                    .build()));
    propertyRepository.saveAll(list);
    cleanUpProperties(list);
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
    List<PropertyEntity> other = new ArrayList<>();
    List<PropertyEntity> updated =
        props
            .parallelStream()
            .map(
                p -> {
                  PropertyValueEntity newValue = p.getValues().iterator().next();
                  Optional<PropertyEntity> existing =
                      propertyRepository.findByNameAndApplicationCode(
                          p.getName(), p.getApplication().getCode());
                  PropertyEntity result;
                  if (existing.isPresent()) {
                    PropertyEntity existingProperty = existing.get();
                    if (StringUtils.isEmpty(p.getCode())) {
                      // no code specified so update the existing with the new value
                      result = updateValue(newValue, existingProperty);
                    } else {
                      if (p.getCode().equals(existingProperty.getCode())) {
                        // same code so it is an update
                        result = updateValue(newValue, existingProperty);
                      } else {
                        // different codes so it is a rename
                        // remove value from renamed property
                        PropertyEntity renamed =
                            propertyRepository
                                .findByCodeEager(p.getCode())
                                .orElseThrow(
                                    () ->
                                        new EntityNotFoundException(
                                            "Can not find property with code " + p.getCode()));
                        renamed
                            .getValues()
                            .removeIf(
                                v ->
                                    v.getEnvironment()
                                        .getCode()
                                        .equals(newValue.getEnvironment().getCode()));
                        other.add(renamed);
                        // put value in the existing property
                        result = updateValue(newValue, existingProperty);
                      }
                    }
                  } else {
                    if (!StringUtils.isEmpty(p.getCode())) {
                      // the code is specified so it is a rename
                      // remove value from renamed property
                      PropertyEntity renamed =
                          propertyRepository
                              .findByCodeEager(p.getCode())
                              .orElseThrow(
                                  () ->
                                      new EntityNotFoundException(
                                          "Can not find property with code " + p.getCode()));
                      renamed
                          .getValues()
                          .removeIf(
                              v ->
                                  v.getEnvironment()
                                      .getCode()
                                      .equals(newValue.getEnvironment().getCode()));
                      other.add(renamed);
                      // new property
                      p.setCode(null);
                    }
                    result = p;
                  }
                  return result;
                })
            .collect(Collectors.toList());
    updated.addAll(other);
    propertyRepository.saveAll(updated);
    cleanUpProperties(updated);
  }

  private PropertyEntity updateValue(PropertyValueEntity newValue, PropertyEntity property) {
    AtomicBoolean found = new AtomicBoolean(false);
    property
        .getValues()
        .forEach(
            pv -> {
              if (pv.getEnvironment().getCode().equals(newValue.getEnvironment().getCode())) {
                found.set(true);
                pv.setValue(newValue.getValue());
              }
            });
    if (!found.get()) {
      newValue.setProperty(property);
      property.getValues().add(newValue);
    }
    return property;
  }

  private void cleanUpProperties(List<PropertyEntity> props) {

    props.forEach(
        p -> {
          propertyRepository
              .findByNameAndApplicationCode(p.getName(), p.getApplication().getCode())
              .ifPresent(
                  pr -> {
                    if (pr.getValues().isEmpty()) {
                      propertyRepository.delete(pr);
                    }
                  });
        });
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.kaiso.lygeum.core.spi.StorageService#findClientById(java.lang.
   * String)
   */
  @Override
  public Optional<Client> findClientById(String clientId) {
    return clientRepository.findByCode(clientId);
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
  public void deleteProperty( PropertyEntity prop, String envCode) {
    
    prop.getValues().removeIf(pv -> pv.getEnvironment().getCode().equals(envCode));
    if (prop.getValues().isEmpty()) {
      propertyRepository.delete(prop);
    } else {
      propertyRepository.save(prop);
    }
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
      List<String> codes =
          user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList());
      user.setRoles(
          roleRepository
              .streamAll()
              .filter(r -> codes.contains(r.getCode()))
              .collect(Collectors.toList()));
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

  @Override
  public void deleteUserByCode(String code) {
    userRepository.delete(
        userRepository
            .findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Can not find user with code " + code)));
  }

  @Override
  public List<Client> findAllClients() {
    return clientRepository.findAll();
  }

  @Override
  public void deleteClientByCode(String code) {
    clientRepository.delete(
        clientRepository
            .findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Can not find user with code " + code)));
  }

  @Override
  public Client saveClient(Client client) {
    if (!CollectionUtils.isEmpty(client.getRoles())) {
      List<String> codes =
          client.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList());
      client.setRoles(
          roleRepository
              .streamAll()
              .filter(r -> codes.contains(r.getCode()))
              .collect(Collectors.toList()));
    }
    return clientRepository.save(client);
  }

  private List<Role> getRolesFor(String type, String code, String name) {
    return Arrays.asList(
        Role.builder()
            .withCode(code + "_" + AuthorizationAction.READ)
            .withForceNew(true)
            .withName("Read " + type + " " + name)
            .build(),
        Role.builder()
            .withCode(code + "_" + AuthorizationAction.UPDATE)
            .withForceNew(true)
            .withName("Update " + type + " " + name)
            .build());
  }

  @Override
  public Optional<ApplicationEntity> findApplicationByNameOrCode(String application) {
    return applicationRepository.findByNameOrCode(application, application);
  }

  @Override
  public Optional<EnvironmentEntity> findEnvironmentByNameOrCode(String environment) {
    return environmentRepository.findByNameOrCode(environment, environment);
  }

  @Override
  public List<User> findUsersByPattern(String search) {
    return userRepository.findUsersByPattern(search);
  }

}
