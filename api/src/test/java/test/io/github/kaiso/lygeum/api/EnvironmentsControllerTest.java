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
package test.io.github.kaiso.lygeum.api;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import io.github.kaiso.lygeum.api.EnvironmentsController;
import io.github.kaiso.lygeum.api.handler.GlobalControllerExceptionHandler;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.springframework.FakeBeanFactory;
import test.io.github.kaiso.lygeum.api.config.ApiTestConfig;
import test.io.github.kaiso.lygeum.api.util.PrintUtils;

/** @author Kais OMRI (kaiso) */
@ExtendWith({RestDocumentationExtension.class})
@SpringJUnitConfig(classes = {ApiTestConfig.class})
@AutoConfigureRestDocs
public class EnvironmentsControllerTest {
    
    @Autowired
    private HttpMessageConverters converters;

  @BeforeAll
  public static void applySpringIntegration() {
    new FakeBeanFactory();
  }

  @BeforeEach
  public void setup(RestDocumentationContextProvider restDocumentation) {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new EnvironmentsController(environmentsManager))
            .apply(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withResponseDefaults(prettyPrint()))
            .setMessageConverters(converters.getConverters().toArray(new HttpMessageConverter<?>[1]))
            .setControllerAdvice(new GlobalControllerExceptionHandler())
            .build();
  }

  @Mocked private EnvironmentsManager environmentsManager;

  private MockMvc mockMvc;

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ENV01_READ"})
  public void should_return_all_environments() throws Exception {
    List<EnvironmentEntity> envs = new ArrayList<>();
    EnvironmentEntity entity = createEnvironment("ENV01", "PRODUCTION");
    envs.add(entity);
    new Expectations() {
      {
        environmentsManager.findAll();
        result = envs;
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.get("/lygeum/api/environments")
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andDo(
                document(
                    "environments-get",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Get all environments")
                            .description("")
                            .responseFields(
                                fieldWithPath("[].code").description("The environment code."),
                                fieldWithPath("[].createdBy").description("The creator."),
                                fieldWithPath("[].createdDate").description("The creation date."),
                                fieldWithPath("[].lastModifiedBy")
                                    .description("The last modifier."),
                                fieldWithPath("[].lastModifiedDate")
                                    .description("The date of the last modification."),
                                fieldWithPath("[].name").description("The environment name."),
                                fieldWithPath("[].description")
                                    .description("The environment description."))
                            .requestHeaders(
                                headerWithName("accept")
                                    .optional()
                                    .description("accept header: application/json"))
                            .tag("Environments")
                            .build())))
            .andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
        JSONAssert.assertEquals(
            PrintUtils.json(envs), result.getResponse().getContentAsString(), false);
      }
    };
  }

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ENV11_UPDATE"})
  public void should_update_environment() throws Exception {
    EnvironmentEntity entity = createEnvironment("env11", "production");
    new Expectations() {
      {
        environmentsManager.findByCode(anyString);
        result = Optional.ofNullable(entity);
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.put("/lygeum/api/environments/{code}", entity.getCode())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is2xxSuccessful())
            .andDo(
                document(
                    "environments-update",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Update an environment")
                            .description("")
                            .tag("Environments")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
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
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "PRODUCTION_UPDATE"})
  public void should_fail_update_when_environment_not_found() throws Exception {
    EnvironmentEntity entity = createEnvironment("env_3", "production");
    new Expectations() {
      {
        environmentsManager.findByCode(anyString);
        result = Optional.ofNullable(null);
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.put("/lygeum/api/environments/{code}", entity.getCode())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is4xxClientError())
            .andDo(
                document(
                    "environments-update-failure",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Update an environment")
                            .description("")
                            .tag("Environments")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
      }
    };
  }

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ALL_ENV_CREATE"})
  public void should_create_environment() throws Exception {
    EnvironmentEntity entity = createEnvironment("code01", "production");
    new Expectations() {
      {
        environmentsManager.create(entity);
        result = entity;
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.post("/lygeum/api/environments/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andDo(
                document(
                    "environments-create",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Create an environment")
                            .description("")
                            .responseFields(
                                fieldWithPath("code").description("The environment code."),
                                fieldWithPath("createdBy").description("The creator."),
                                fieldWithPath("createdDate").description("The creation date."),
                                fieldWithPath("lastModifiedBy").description("The last modifier."),
                                fieldWithPath("lastModifiedDate")
                                    .description("The date of the last modification."),
                                fieldWithPath("name").description("The environment name."),
                                fieldWithPath("description")
                                    .description("The environment description."))
                            .requestHeaders(
                                headerWithName("accept")
                                    .optional()
                                    .description("accept header: application/json"))
                            .tag("Environments")
                            .build())))
            .andReturn();

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
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ALL_ENV_DELETE"})
  public void should_delete_environment() throws Exception {
    EnvironmentEntity entity = createEnvironment("code01", "production");
    new Expectations() {
      {
        environmentsManager.findByCode(anyString);
        result = Optional.ofNullable(entity);
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.delete("/lygeum/api/environments/{code}", entity.getCode())
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is2xxSuccessful())
            .andDo(
                document(
                    "environments-delete",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Delete an environment")
                            .description("")
                            .pathParameters(
                                parameterWithName("code").description("The environment code"))
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("environment/json"))
                            .tag("Environments")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
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
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ALL_ENV_DELETE"})
  public void should_fail_delete_when_environment_not_found() throws Exception {

    new Expectations() {
      {
        environmentsManager.findByCode(anyString);
        result = Optional.ofNullable(null);
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.delete("/lygeum/api/environments/{code}", "code22")
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is4xxClientError())
            .andDo(
                document(
                    "environments-delete-failure",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Delete an environment")
                            .description("")
                            .pathParameters(
                                parameterWithName("code").description("The environment code"))
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("environment/json"))
                            .tag("Environments")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
      }
    };
  }

  private EnvironmentEntity createEnvironment(String code, String name) {
    EnvironmentEntity environmentEntity = new EnvironmentEntity(code, name);
    environmentEntity.setDescription("The production environment");
    environmentEntity.setCreatedBy("Lygeum");
    environmentEntity.setLastModifiedBy("Lygeum");
    environmentEntity.setCreatedDate(LocalDateTime.now());
    environmentEntity.setLastModifiedDate(LocalDateTime.now());
    return environmentEntity;
  }
}
