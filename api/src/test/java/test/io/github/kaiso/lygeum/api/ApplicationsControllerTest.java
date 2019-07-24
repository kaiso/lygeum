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
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.kaiso.lygeum.api.ApplicationsController;
import io.github.kaiso.lygeum.api.handler.GlobalControllerExceptionHandler;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
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
public class ApplicationsControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationsControllerTest.class);

	@BeforeAll
	public static void applySpringIntegration() {
		try {
			new FakeBeanFactory();
		} catch (Exception e) {

			logger.error("can not instanciate FakeBeanFactory ", e);
		}
	}

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ApplicationsController(applicationsManager))
				.setControllerAdvice(new GlobalControllerExceptionHandler()).build();
	}

	@Mocked
	private ApplicationsManager applicationsManager;

	private MockMvc mockMvc;

	@Test
	// @WithMockUser(authorities = { "PROD_READ" })
	public void should_return_all_applications() throws Exception {
		List<ApplicationEntity> apps = new ArrayList<>();
		ApplicationEntity entity = new ApplicationEntity("APP01", "PRODUCTION");

		apps.add(entity);
		new Expectations() {
			{
				applicationsManager.findAll();
				result = apps;

			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/applications")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				JSONAssert.assertEquals(PrintUtils.json(apps), result.getResponse().getContentAsString(), false);
			}
		};

	}

	@Test
	@WithMockUser(authorities = { "PRODUCTION_UPDATE" })
	public void should_update_application() throws Exception {
		ApplicationEntity entity = new ApplicationEntity("code01", "production");
		new Expectations() {
			{
				applicationsManager.findByCode(anyString);
				result = Optional.ofNullable(entity);
			}
		};
		String content = PrintUtils.json(entity);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/lygeum/api/applications/" + entity.getCode())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				ApplicationEntity calledEntity;
				applicationsManager.update(calledEntity = withCapture());
				assertEquals(entity, calledEntity);
			}
		};

	}

	@Test
	@WithMockUser(authorities = { "PRODUCTION_UPDATE" })
	public void should_fail_update_when_application_not_found() throws Exception {
		ApplicationEntity entity = new ApplicationEntity("code01", "production");

		new Expectations() {
			{
				applicationsManager.findByCode(anyString);
				result = Optional.ofNullable(null);
			}
		};
		String content = PrintUtils.json(entity);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/lygeum/api/applications/" + entity.getCode())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
			}
		};

	}

	@Test
	@WithMockUser(authorities = { "ALL_APP_CREATE" })
	public void should_create_application() throws Exception {
		ApplicationEntity entity = new ApplicationEntity("code01", "production");
		new Expectations() {
			{
				applicationsManager.create(entity);
				result = entity;
			}
		};
		String content = PrintUtils.json(entity);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lygeum/api/applications/")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				MockMvcResultMatchers.jsonPath("$.code", Matchers.equalTo("code01")).match(result);
				MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("production")).match(result);
			}
		};

	}

	@Test
	@WithMockUser(authorities = { "ALL_APP_DELETE" })
	public void should_delete_application() throws Exception {
		ApplicationEntity entity = new ApplicationEntity("code01", "production");
		new Expectations() {
			{
				applicationsManager.findByCode(anyString);
				result = Optional.ofNullable(entity);
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/lygeum/api/applications/" + entity.getCode())
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
				ApplicationEntity calledEntity;
				applicationsManager.delete(calledEntity = withCapture());
				assertEquals(entity, calledEntity);
			}
		};

	}

	@Test
	@WithMockUser(authorities = { "ALL_APP_DELETE" })
	public void should_fail_delete_when_application_not_found() throws Exception {

		new Expectations() {
			{
				applicationsManager.findByCode(anyString);
				result = Optional.ofNullable(null);
			}
		};
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/lygeum/api/applications/code22")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		PrintUtils.printResponse(result);

		new Verifications() {
			{
				assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());

			}
		};

	}

}
