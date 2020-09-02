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
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseBodySnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.epages.restdocs.apispec.ConstrainedFields;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

import io.github.kaiso.lygeum.api.ApplicationsController;
import io.github.kaiso.lygeum.api.handler.GlobalControllerExceptionHandler;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.springframework.FakeBeanFactory;
import test.io.github.kaiso.lygeum.api.config.ApiTestConfig;
import test.io.github.kaiso.lygeum.api.util.PrintUtils;

/** @author Kais OMRI (kaiso) */
@ExtendWith(RestDocumentationExtension.class)
@SpringJUnitConfig(classes = {ApiTestConfig.class})
@AutoConfigureRestDocs
public class ApplicationsControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationsControllerTest.class);

  @Autowired private HttpMessageConverters converters;

  private ConstrainedFields fields = new ConstrainedFields(ApplicationEntity.class);

  @BeforeAll
  public static void applySpringIntegration() {
    try {
      new FakeBeanFactory();
    } catch (Exception e) {

      logger.error("can not instanciate FakeBeanFactory ", e);
    }
  }

  @BeforeEach
  public void setup(RestDocumentationContextProvider restDocumentation) {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new ApplicationsController(applicationsManager))
            .setMessageConverters(
                converters.getConverters().toArray(new HttpMessageConverter<?>[1]))
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .setControllerAdvice(new GlobalControllerExceptionHandler())
            .build();
  }

  @Mocked private ApplicationsManager applicationsManager;

  private MockMvc mockMvc;

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "APP01_READ"})
  public void should_return_all_applications() throws Exception {
    List<ApplicationEntity> apps = new ArrayList<>();
    ApplicationEntity entity = createApp();

    apps.add(entity);
    new Expectations() {
      {
        applicationsManager.findAll();
        result = apps;
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.get("/lygeum/api/applications")
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andDo(
                document(
                    "applications-get",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Get all applications")
                            .description("")
                            .responseFields(
                                fieldWithPath("[].code").description("The application code."),
                                fieldWithPath("[].createdBy").description("The creator."),
                                fieldWithPath("[].createdDate").description("The creation date."),
                                fieldWithPath("[].lastModifiedBy")
                                    .description("The last modifier."),
                                fieldWithPath("[].lastModifiedDate")
                                    .description("The date of the last modification."),
                                fieldWithPath("[].name").description("The application name."),
                                fieldWithPath("[].description")
                                    .description("The application description."))
                            .requestHeaders(
                                headerWithName("accept")
                                    .optional()
                                    .description("accept header: application/json"))
                            .tag("Applications")
                            .build())))
            .andReturn();

    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
        JSONAssert.assertEquals(
            PrintUtils.json(apps.toArray()), result.getResponse().getContentAsString(), false);
      }
    };
  }

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "APP01_UPDATE"})
  public void should_update_application() throws Exception {
    ApplicationEntity entity = createApp();
    new Expectations() {
      {
        applicationsManager.findByCode(anyString);
        result = Optional.ofNullable(entity);
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.put("/lygeum/api/applications/{code}", entity.getCode())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andDo(
                document(
                    "applications-update",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Update an application")
                            .description("")
                            .pathParameters(
                                parameterWithName("code").description("The application code"))
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("application/json"))
                            .tag("Applications")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
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
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "PRODUCTION_UPDATE"})
  public void should_fail_update_when_application_not_found() throws Exception {
    ApplicationEntity entity = new ApplicationEntity("code01", "production");

    new Expectations() {
      {
        applicationsManager.findByCode(anyString);
        result = Optional.ofNullable(null);
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/lygeum/api/applications/" + entity.getCode())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
      }
    };
  }

  @Test
  @WithMockUser(authorities = {AuthorizationManager.ROLE_PREFIX + "ALL_APP_CREATE"})
  public void should_create_application() throws Exception {
    ApplicationEntity entity = createApp();
    new Expectations() {
      {
        applicationsManager.create(entity);
        result = entity;
      }
    };
    String content = PrintUtils.json(entity);
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.post("/lygeum/api/applications/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andDo(
                document(
                    "applications-create",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Create an application")
                            .description("")
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("application/json"))
                            .tag("Applications")
                            .build())))
            .andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is2xxSuccessful());
        MockMvcResultMatchers.jsonPath("$.code", Matchers.equalTo("APP01")).match(result);
        MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("NGINX")).match(result);
      }
    };
  }

  @Test
  @WithMockUser(authorities = {"ROLE_ALL_APP_DELETE"})
  public void should_delete_application() throws Exception {
    ApplicationEntity entity = new ApplicationEntity("code01", "production");
    new Expectations() {
      {
        applicationsManager.findByCode(anyString);
        result = Optional.ofNullable(entity);
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.delete("/lygeum/api/applications/{code}", entity.getCode())
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is2xxSuccessful())
            .andDo(
                document(
                    "applications-delete",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Delete an application")
                            .description("")
                            .pathParameters(
                                parameterWithName("code").description("The application code"))
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("application/json"))
                            .tag("Applications")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build())))
            .andReturn();
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
  @WithMockUser(authorities = {"ROLE_ALL_APP_DELETE"})
  public void should_fail_delete_when_application_not_found() throws Exception {

    new Expectations() {
      {
        applicationsManager.findByCode(anyString);
        result = Optional.ofNullable(null);
      }
    };
    RequestBuilder requestBuilder =
        RestDocumentationRequestBuilders.delete("/lygeum/api/applications/{code}", "code22")
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result =
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().is4xxClientError())
            .andDo(
                document(
                    "applications-delete-failure",
                    resource(
                        ResourceSnippetParameters.builder()
                            .summary("Delete an application")
                            .description("")
                            .pathParameters(
                                parameterWithName("code").description("The application code"))
                            .requestHeaders(
                                headerWithName("Content-type")
                                    .optional()
                                    .description("application/json"))
                            .tag("Applications")
                            .responseFields(
                                fieldWithPath("code").description("The reason code."),
                                fieldWithPath("message").description("The message."))
                            .build()),
                    responseBody(
                        Map.of(
                            "applications-delete-response",
                            "The response of application delete request"))))
            .andReturn();
    PrintUtils.printResponse(result);

    new Verifications() {
      {
        assertTrue(HttpStatus.valueOf(result.getResponse().getStatus()).is4xxClientError());
      }
    };
  }

  private ApplicationEntity createApp() {
    ApplicationEntity entity = new ApplicationEntity("APP01", "NGINX");
    entity.setCreatedBy("lygeum");
    entity.setCreatedDate(LocalDateTime.now());
    entity.setDescription("The nginx server");
    entity.setLastModifiedBy("lygeum");
    entity.setLastModifiedDate(LocalDateTime.now());
    return entity;
  }
}
