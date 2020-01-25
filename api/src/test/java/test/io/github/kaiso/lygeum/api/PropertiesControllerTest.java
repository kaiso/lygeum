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
package test.io.github.kaiso.lygeum.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.kaiso.lygeum.api.PropertiesController;
import io.github.kaiso.lygeum.api.handler.GlobalControllerExceptionHandler;
import io.github.kaiso.lygeum.api.resources.PropertyResource;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.manager.PropertiesManager;
import io.github.kaiso.lygeum.core.manager.impl.PropertiesManagerImpl;
import io.github.kaiso.lygeum.core.properties.PropertiesMediaType;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;
import io.github.kaiso.lygeum.core.spi.StorageService;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.springframework.FakeBeanFactory;
import test.io.github.kaiso.lygeum.api.util.PrintUtils;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@SpringJUnitConfig
public class PropertiesControllerTest {

	private PropertiesManager propertiesManager;

	@Mocked
	private StorageService storageService;

	private MockMvc mockMvc;

	@Mocked
	private EnvironmentsManager environmentsManager;

	@Mocked
	private ApplicationsManager applicationsManager;

	@BeforeAll
	public static void applySpringIntegration() {
		new FakeBeanFactory();
	}

	@BeforeEach
	public void setup() {
		propertiesManager = new PropertiesManagerImpl(storageService);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new PropertiesController(propertiesManager, applicationsManager, environmentsManager))
				.setControllerAdvice(new GlobalControllerExceptionHandler()).build();
	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_READ",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_READ" })
	public void should_return_properties() throws Exception {
		new Expectations() {
			{
				propertiesManager.findPropertiesByEnvironmentAndApplication(anyString, anyString);
				Collection<PropertyEntity> res = new ArrayList<>();
				res.add(PropertyEntity.builder().withName("author").withValue("Kais OMRI")
						.withEnvironment(new EnvironmentEntity("prod", "production")).build());
				result = res;
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/properties")
				.accept(MediaType.APPLICATION_JSON).param("env", "prod").param("app", "application");

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				String expected = "[{\"code\":null,\"name\":\"author\",\"value\":\"Kais OMRI\"}]";
				JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_UPDATE",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_UPDATE" })
	public void should_update_properties() throws Exception {
		new Expectations() {
			{
				environmentsManager.findByCode(anyString);
				result = Optional.of(new EnvironmentEntity("env", null));
				applicationsManager.findByCode(anyString);
				result = Optional.of(new ApplicationEntity("application", null));
			}
		};
		PropertyResource r = PropertyResource.builder().withCode("code01").withName("app.name").withValue("lygeum")
				.build();
		List<PropertyResource> list = new ArrayList<>();
		list.add(r);
		String content = PrintUtils.json(list);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lygeum/api/properties")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)
				.param("env", "prod").param("app", "application");

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				List<PropertyEntity> calledList;
				propertiesManager.updateProperties(calledList = withCapture());
				assertEquals(calledList.get(0).getCode(), r.getCode());
				assertEquals(calledList.get(0).getApplication().getCode(), "application");
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_READ",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_READ" })
	public void should_download_properties_layout() throws Exception {
		new Expectations() {
			{
				Collection<PropertyEntity> res = new ArrayList<>();
				EnvironmentEntity env = new EnvironmentEntity("prod", "production");
				environmentsManager.findByNameOrCode(anyString);
				result = Optional.of(env);
				applicationsManager.findByNameOrCode(anyString);
				result = Optional.of(new ApplicationEntity("application", null));
				propertiesManager.findPropertiesByEnvironmentAndApplication(anyString, anyString);
				res.add(PropertyEntity.builder().withName("author").withValue("Kais OMRI").withEnvironment(env)
						.build());
				result = res;
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/properties/download")
				.accept(MediaType.APPLICATION_JSON).param("env", "prod").param("app", "application")
				.param("layout", "properties");

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				assertTrue(result.getResponse().getContentType().equals("text/x-java-properties"));
				String content = result.getResponse().getContentAsString().replaceAll("#.*", "");
				assertEquals(content.trim(), "author=Kais OMRI");
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_READ",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_READ" })
	public void should_download_yaml_layout() throws Exception {
		new Expectations() {
			{
				EnvironmentEntity env = new EnvironmentEntity("prod", "production");
				environmentsManager.findByNameOrCode(anyString);
				result = Optional.of(env);
				applicationsManager.findByNameOrCode(anyString);
				result = Optional.of(new ApplicationEntity("application", null));
				propertiesManager.findPropertiesByEnvironmentAndApplication(anyString, anyString);
				Collection<PropertyEntity> res = new ArrayList<>();
				res.add(PropertyEntity.builder().withName("author").withValue("Kais OMRI")
						.withEnvironment(env).build());
				res.add(PropertyEntity.builder().withName("launch.date").withValue("2018")
						.withEnvironment(env).build());
				result = res;
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/properties/download")
				.accept(MediaType.APPLICATION_JSON).param("env", "prod").param("app", "application")
				.param("layout", "yaml");

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				assertTrue(result.getResponse().getContentType().equals("application/yaml"));
				System.out.println(result.getResponse().getContentAsString().replaceAll("#.*", ""));
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_READ",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_READ" })
	public void should_download_error_on_invalid_layout() throws Exception {
		new Expectations() {
			{
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/properties/download")
				.accept(MediaType.APPLICATION_JSON).param("env", "prod").param("app", "application")
				.param("layout", "invalidtype");

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_UPDATE",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_UPDATE" })
	public void should_upload_error_on_invalid_mime() throws Exception {
		new Expectations() {
			{
			}
		};

		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.properties", "application/pdf",
				"app=lygeum\nauthor=Kais OMRI".getBytes());
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/lygeum/api/properties/upload")
				.file(multipartFile).param("env", "prod").param("app", "application")).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_UPDATE",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_UPDATE" })
	public void should_upload_properties_layout() throws Exception {
		new Expectations() {
			{
				propertiesManager.storeProperties(anyString, anyString, withNotNull());
			}
		};

		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.properties",
				PropertiesMediaType.PROPERTIES.value(), "app=lygeum\nauthor=Kais OMRI ééù=".getBytes());
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/lygeum/api/properties/upload")
				.file(multipartFile).param("env", "prod").param("app", "application")).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
			}
		};

	}

	@Test
	@WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_UPDATE",
			AuthorizationManager.ROLE_PREFIX + "APPLICATION_UPDATE" })
	public void should_upload_yaml_layout() throws Exception {

		new Expectations() {
			{
			}
		};
		String content = "---\n" + "author: \"Kais OMRI\"\n" + "app:\n" + "  ?\n" + "  : \"LYGEUM\"\n"
				+ "  signature: \"SIG\"\n" + "  name: \"LYGEUM\"";
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.yaml", PropertiesMediaType.YAML.value(),
				content.getBytes());
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.multipart("/lygeum/api/properties/upload")
				.file(multipartFile).param("env", "prod").param("app", "application")).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				String env;
				Map<String, String> map;
				propertiesManager.storeProperties(env = withCapture(), anyString, map = withCapture());
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				assertEquals(env, "prod");
				assertEquals(map.size(), 4);
			}
		};

	}

}
