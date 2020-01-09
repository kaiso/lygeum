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

import io.github.kaiso.lygeum.api.EnvironmentsController;
import io.github.kaiso.lygeum.api.handler.GlobalControllerExceptionHandler;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;
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
public class EnvironmentsControllerTest {

    @BeforeAll
    public static void applySpringIntegration() {
	new FakeBeanFactory();
    }

    @BeforeEach
    public void setup() {
	mockMvc = MockMvcBuilders.standaloneSetup(new EnvironmentsController(environmentsManager))
		.setControllerAdvice(new GlobalControllerExceptionHandler()).build();
    }

    @Mocked
    private EnvironmentsManager environmentsManager;

    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PROD_READ" })
    public void should_return_all_environments() throws Exception {
	List<EnvironmentEntity> envs = new ArrayList<>();
	EnvironmentEntity entity = new EnvironmentEntity("ENV01", "PRODUCTION");
	envs.add(entity);
	new Expectations() {
	    {
		environmentsManager.findAll();
		result = envs;

	    }
	};
	RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/lygeum/api/environments")
		.accept(MediaType.APPLICATION_JSON);

	MvcResult result = mockMvc.perform(requestBuilder).andReturn();
	PrintUtils.printResponse(result);

	new Verifications() {
	    {
		assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
		JSONAssert.assertEquals(PrintUtils.json(envs), result.getResponse().getContentAsString(), false);
	    }
	};

    }

    @Test
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PRODUCTION_UPDATE" })
    public void should_update_environment() throws Exception {
	EnvironmentEntity entity = new EnvironmentEntity("code01", "production");
	new Expectations() {
	    {
		environmentsManager.findByCode(anyString);
		result = Optional.ofNullable(entity);
	    }
	};
	String content = PrintUtils.json(entity);
	RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/lygeum/api/environments/" + entity.getCode())
		.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content);

	MvcResult result = mockMvc.perform(requestBuilder).andReturn();
	PrintUtils.printResponse(result);

	new Verifications() {
	    {
		assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
		EnvironmentEntity calledEntity;
		environmentsManager.update(calledEntity = withCapture());
		assertEquals(entity, calledEntity);
	    }
	};

    }

    @Test
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "PRODUCTION_UPDATE" })
    public void should_fail_update_when_environment_not_found() throws Exception {
	EnvironmentEntity entity = new EnvironmentEntity("code01", "production");
	new Expectations() {
	    {
		environmentsManager.findByCode(anyString);
		result = Optional.ofNullable(null);
	    }
	};
	String content = PrintUtils.json(entity);
	RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/lygeum/api/environments/" + entity.getCode())
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
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "ALL_ENV_CREATE" })
    public void should_create_environment() throws Exception {
	EnvironmentEntity entity = new EnvironmentEntity("code01", "production");
	new Expectations() {
	    {
		environmentsManager.create(entity);
		result = entity;
	    }
	};
	String content = PrintUtils.json(entity);
	RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/lygeum/api/environments/")
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
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "ALL_ENV_DELETE" })
    public void should_delete_environment() throws Exception {
	EnvironmentEntity entity = new EnvironmentEntity("code01", "production");
	new Expectations() {
	    {
		environmentsManager.findByCode(anyString);
		result = Optional.ofNullable(entity);
	    }
	};
	RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/lygeum/api/environments/" + entity.getCode())
		.accept(MediaType.APPLICATION_JSON);

	MvcResult result = mockMvc.perform(requestBuilder).andReturn();
	PrintUtils.printResponse(result);

	new Verifications() {
	    {
		assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
		EnvironmentEntity calledEntity;
		environmentsManager.delete(calledEntity = withCapture());
		assertEquals(entity, calledEntity);
	    }
	};

    }

    @Test
    @WithMockUser(authorities = { AuthorizationManager.ROLE_PREFIX + "ALL_ENV_DELETE" })
    public void should_fail_delete_when_environment_not_found() throws Exception {

	new Expectations() {
	    {
		environmentsManager.findByCode(anyString);
		result = Optional.ofNullable(null);
	    }
	};
	RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/lygeum/api/environments/code22")
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
