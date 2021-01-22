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
package io.github.kaiso.lygeum.core.manager.impl;

import io.github.kaiso.lygeum.core.entities.Change;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.manager.PropertiesManager;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;
import io.github.kaiso.lygeum.core.spi.StorageService;
import io.github.kaiso.lygeum.core.spi.VersionningService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author Kais OMRI (kaiso) */
@Service
public class PropertiesManagerImpl implements PropertiesManager {

  private static final Logger logger = LoggerFactory.getLogger(PropertiesManagerImpl.class);

  private StorageService storageService;
  private VersionningService versionningService;
  private SecurityContextHolder securityContextHolder;

  @Autowired
  public PropertiesManagerImpl(
      StorageService storageService,
      VersionningService versionningService,
      SecurityContextHolder securityContextHolder) {
    this.storageService = storageService;
    this.versionningService = versionningService;
    this.securityContextHolder = securityContextHolder;
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.kaiso.lygeum.core.manager.PropertiesManager#
   * findPropertiesByEnvironmentAndApplication(java.lang.String, java.lang.String)
   */
  @Override
  public Collection<PropertyEntity> findPropertiesByEnvironmentAndApplication(
      String environment, String application) {
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
    commit(environment, application);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * io.github.kaiso.lygeum.core.manager.PropertiesManager#updateProperties(java.
   * lang.String, java.lang.String, java.util.List)
   */
  @Override
  public void updateProperties(String environment, String application, List<PropertyEntity> props) {
    storageService.updateProperties(props);
    commit(environment, application);
  }

  @Override
  public void delete(String code, EnvironmentEntity env) {
    PropertyEntity prop =
        storageService
            .findPropertyByCode(code)
            .orElseThrow(
                () -> new EntityNotFoundException("Can not find property with code " + code));
    storageService.deleteProperty(prop, env.getCode());
    commit(env.getCode(), prop.getApplication().getCode());
  }

  @Override
  public Optional<PropertyEntity> findByCode(String code) {
    return storageService.findPropertyByCode(code);
  }

  @Override
  public Collection<Change> loadCommitHistory(String environment, String application) {
    return versionningService.findChangesByEnvAndApp(environment, application);
  }

  private void commit(String env, String app) {
    String username = securityContextHolder.getCurrentUser().getUsername();
    CompletableFuture.runAsync(
        () -> {
          try {
            versionningService.commit(
                env,
                app,
                username,
                storageService
                    .findPropertiesByEnvironmentAndApplication(env, app)
                    .parallelStream()
                    .flatMap(e -> e.getValues().stream())
                    .collect(Collectors.toList()));
            
            versionningService.keepOnlyLastbyEnvAndApp(5, env, app);
          } catch (Exception e) {
            logger.warn("failed to commit change to version history ", e);
          }
        });
  }
}
