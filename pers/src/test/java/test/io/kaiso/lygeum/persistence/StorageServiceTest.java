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
package test.io.kaiso.lygeum.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.spi.StorageService;
import test.io.kaiso.lygeum.persistence.config.BasePersistenceTest;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public class StorageServiceTest extends BasePersistenceTest {

	@Autowired
	private StorageService storageService;

	private ApplicationEntity createdApp;
	private EnvironmentEntity createdEnv;

	@BeforeEach
	public void setUp() {
		ApplicationEntity app = new ApplicationEntity();
		app.setDescription("application 1");
		app.setName("APPONE");
		createdApp = storageService.createApplication(app);
		EnvironmentEntity env = new EnvironmentEntity();
		env.setName("ENVONE");
		env.setDescription("environment 1");
		createdEnv = storageService.createEnvironment(env);
		Map<String, String> props = new HashMap<>();
		props.put("key", "value");
		storageService.storeProperties(createdEnv.getCode(), createdApp.getCode(), props);
	}

	@AfterEach
	public void tearDown() {
		storageService.findAllApplications().stream().forEach(app -> {
			storageService.deleteApplication(app);
		});
		storageService.findAllEnvironments().stream().forEach(env -> {
			storageService.deleteEnvironment(env);
		});
	}

	@Test
	public void should_find_all_applications() {
		List<ApplicationEntity> apps = storageService.findAllApplications();
		assertEquals(1, apps.size());
	}

	@Test
	public void should_find_application_code() {
		Optional<ApplicationEntity> app = storageService.findApplicationByCode(createdApp.getCode());
		assertTrue(app.isPresent());
	}

	@Test
	public void should_delete_application() {
		Optional<ApplicationEntity> app = storageService.findApplicationByCode(createdApp.getCode());
		assertTrue(app.isPresent());
		storageService.deleteApplication(app.get());
		app = storageService.findApplicationByCode(createdApp.getCode());
		assertFalse(app.isPresent());
	}

	@Test
	public void should_update_application() {
		Optional<ApplicationEntity> app = storageService.findApplicationByCode(createdApp.getCode());
		app.get().setName("new_name");
		storageService.updateApplication(app.get());
		app = storageService.findApplicationByCode(createdApp.getCode());
		assertTrue(app.isPresent());
		assertEquals("new_name", app.get().getName());
	}

	@Test
	@Transactional
	public void should_update_application_using_natural_id() {
		ApplicationEntity tmpApp = new ApplicationEntity();
		tmpApp.setName("new_name");
		tmpApp.setCode(createdApp.getCode());
		storageService.updateApplication(tmpApp);
		Optional<ApplicationEntity> app = storageService.findApplicationByCode(createdApp.getCode());
		assertTrue(app.isPresent());
		assertEquals("new_name", app.get().getName());
	}

	@Test
	public void should_find_properties_without_values() {
		EnvironmentEntity env = new EnvironmentEntity();
		env.setName("ENVONE");
		env.setDescription("environment 1");
		EnvironmentEntity newEnv = storageService.createEnvironment(env);
		Map<String, String> props = new HashMap<>();
		props.put("key-1", null);
		props.put("key-2", "value-2");
		storageService.storeProperties(newEnv.getCode(), createdApp.getCode(), props);
		Collection<PropertyEntity> result = storageService
				.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(3, result.size());
		assertEquals("value",
				result.stream().filter(p -> p.getName().equals("key")).collect(Collectors.toList()).get(0).getValue());
		assertEquals(null, result.stream().filter(p -> p.getName().equals("key-1")).collect(Collectors.toList()).get(0)
				.getValue());
		assertEquals(null, result.stream().filter(p -> p.getName().equals("key-2")).collect(Collectors.toList()).get(0)
				.getValue());
	}

	@Test
	public void should_store_properties() {
		Collection<PropertyEntity> result = storageService
				.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(1, result.size());
		assertEquals("key", result.stream().collect(Collectors.toList()).get(0).getName());
		assertEquals("value",
				result.stream().collect(Collectors.toList()).get(0).getValues().iterator().next().getValue());
		Map<String, String> props = new HashMap<>();
		props.put("key-spec", "value-spec");
		storageService.storeProperties(createdEnv.getCode(), createdApp.getCode(), props);
		result = storageService.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(2, result.size());
		List<String> keyvaluecouples = result.stream().map(p -> p.getName() + p.getValue())
				.collect(Collectors.toList());
		assertTrue(keyvaluecouples.containsAll(Arrays.asList("keynull", "key-specvalue-spec")));
	}

	@Test
	public void should_store_properties_isolated_environments() {

		Collection<PropertyEntity> result = storageService
				.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(1, result.size());
		assertEquals("key", result.stream().collect(Collectors.toList()).get(0).getName());
		assertEquals("value",
				result.stream().collect(Collectors.toList()).get(0).getValues().iterator().next().getValue());

		EnvironmentEntity env = new EnvironmentEntity();
		env.setName("ENVONE");
		env.setDescription("environment 1");
		EnvironmentEntity newEnv = storageService.createEnvironment(env);
		Map<String, String> props = new HashMap<>();
		props.put("key", "value-spec");
		storageService.storeProperties(newEnv.getCode(), createdApp.getCode(), props);

		// old env does not change
		result = storageService.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(1, result.size());
		assertEquals("key", result.stream().collect(Collectors.toList()).get(0).getName());
		assertEquals("value",
				result.stream().collect(Collectors.toList()).get(0).getValues().iterator().next().getValue());

		// new env with different value
		result = storageService.findPropertiesByEnvironmentAndApplication(newEnv.getCode(), createdApp.getCode());
		assertEquals(1, result.size());
		assertEquals("key", result.stream().collect(Collectors.toList()).get(0).getName());
		assertEquals("value-spec",
				result.stream().collect(Collectors.toList()).get(0).getValues().iterator().next().getValue());

	}

	@Test
	public void should_update_properties() {
		Collection<PropertyEntity> result = storageService
				.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());

		PropertyEntity toUpdate = PropertyEntity.builder().withName("new-key").withValue("new-value")
				.withApplication(createdApp).withCode(result.iterator().next().getCode())
				.withDesciption("update description").withEnvironment(createdEnv).build();

		storageService.updateProperties(Collections.singletonList(toUpdate));
		result = storageService.findPropertiesByEnvironmentAndApplication(createdEnv.getCode(), createdApp.getCode());
		assertEquals(1, result.size());
		assertEquals("new-key", result.stream().collect(Collectors.toList()).get(0).getName());
		assertEquals("new-value",
				result.stream().collect(Collectors.toList()).get(0).getValues().iterator().next().getValue());
	}
}
