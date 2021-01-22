package io.github.kaiso.lygeum.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.kaiso.lygeum.api.resources.OperationResult;
import io.github.kaiso.lygeum.api.resources.OperationResult.OperationResultCode;
import io.github.kaiso.lygeum.api.resources.PropertyMapper;
import io.github.kaiso.lygeum.api.resources.PropertyResource;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.Change;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.manager.PropertiesManager;
import io.github.kaiso.lygeum.core.properties.PropertiesConverter;
import io.github.kaiso.lygeum.core.properties.PropertiesLayout;
import io.github.kaiso.lygeum.core.properties.PropertiesMediaType;
import io.github.kaiso.lygeum.core.properties.exception.PropertiesConvertionException;
import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;

@RestController
public class PropertiesController extends LygeumRestController {

  private PropertiesManager propertiesManager;
  private ApplicationsManager applicationsManager;
  private EnvironmentsManager environmentsManager;

  @Autowired
  public PropertiesController(
      PropertiesManager propertiesManager,
      ApplicationsManager applicationsManager,
      EnvironmentsManager environmentsManager) {
    this.propertiesManager = propertiesManager;
    this.applicationsManager = applicationsManager;
    this.environmentsManager = environmentsManager;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/properties", produces = "application/json")
  public ResponseEntity<Collection<PropertyResource>> properties(
      @RequestParam(name = "env", required = true) String environment,
      @RequestParam(name = "app", required = true) String application) {
    AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.READ);
    List<PropertyResource> result =
        propertiesManager
            .findPropertiesByEnvironmentAndApplication(environment, application)
            .parallelStream()
            .map(PropertyMapper::map)
            .collect(Collectors.toList());
    return ResponseEntity.ok(result);
  }

  @RequestMapping(path = "/properties/{code}", method = RequestMethod.DELETE)
  public ResponseEntity<OperationResult> deleteProperty(
      @PathVariable(required = true, name = "code") String code,
      @RequestParam(name = "env", required = true) String environment) {
    PropertyEntity p =
        propertiesManager
            .findByCode(code)
            .orElseThrow(
                () -> new IllegalArgumentException("Property not found with code: " + code));
    EnvironmentEntity env =
        environmentsManager
            .findByNameOrCode(environment)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Environment not found with code: " + environment));
    AuthorizationManager.preAuthorize(
        p.getApplication().getCode(), env.getCode(), AuthorizationAction.UPDATE);

    propertiesManager.delete(code, env);

    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            OperationResult.builder()
                .withCode(OperationResultCode.SUCCESS)
                .withMessage("Property " + code + " successfully deleted")
                .build());
  }

  @RequestMapping(
      method = RequestMethod.POST,
      path = "/properties",
      produces = "application/json",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OperationResult> updateProperties(
      @RequestParam(name = "env", required = true) String environment,
      @RequestParam(name = "app", required = true) String application,
      @RequestBody(required = true) @Valid List<PropertyResource> properties) {
    AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.UPDATE);
    EnvironmentEntity env =
        environmentsManager
            .findByNameOrCode(environment)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Environment not found with code: " + environment));
    ApplicationEntity app =
        applicationsManager
            .findByNameOrCode(application)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Application not found with code: " + application));
    propertiesManager.updateProperties(
        env.getCode(),
        app.getCode(),
        properties
            .parallelStream()
            .map(p -> PropertyMapper.map(p, app, env))
            .collect(Collectors.toList()));
    return ResponseEntity.ok(
        OperationResult.builder()
            .withCode(OperationResultCode.SUCCESS)
            .withMessage("Properties successfully updated")
            .build());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/properties/download")
  public ResponseEntity<byte[]> download(
      @RequestParam(name = "env", required = true) String environment,
      @RequestParam(name = "app", required = true) String application,
      @RequestParam(name = "layout", required = true) String layout) {
    EnvironmentEntity env =
        environmentsManager
            .findByNameOrCode(environment)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Environment not found with code: " + environment));
    ApplicationEntity app =
        applicationsManager
            .findByNameOrCode(application)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Application not found with code: " + application));
    AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.READ);
    Map<String, String> properties =
        propertiesManager
            .findPropertiesByEnvironmentAndApplication(env.getCode(), app.getCode())
            .stream()
            .collect(
                HashMap::new,
                (m, v) -> m.put(v.getName(), Optional.ofNullable(v.getValue()).orElse("")),
                HashMap::putAll);
    String result;
    String contentDisposition;
    String mediaType;
    try {

      if (PropertiesLayout.PROPERTIES.toString().equalsIgnoreCase(layout)) {
        result = PropertiesConverter.getPropertiesMapAsString(properties);
        contentDisposition = "attachment; filename=config.properties";
        result = addStandardComments(env, app, result);
        mediaType = "text/x-java-properties";
      } else if (PropertiesLayout.YAML.toString().equalsIgnoreCase(layout)) {
        Object obj = PropertiesConverter.convertPropertiesMapToJson(properties);
        result = PropertiesConverter.convertJsonToYamlString(obj);
        result = addStandardComments(env, app, result);
        contentDisposition = "attachment; filename=config.yaml";
        mediaType = "application/yaml";
      } else if (PropertiesLayout.JSON.toString().equalsIgnoreCase(layout)) {
        result = PropertiesConverter.convertPropertiesMapToJsonString(properties);
        contentDisposition = "attachment; filename=config.json";
        mediaType = "application/json";
      } else {
        throw new IllegalArgumentException("invalid layout, accepted values are [PROPERTIES,YAML]");
      }
    } catch (IOException e) {
      throw new PropertiesConvertionException("failed to convert persisted properties", e);
    }
    byte[] output;
    output = result.getBytes(StandardCharsets.UTF_8);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("charset", StandardCharsets.UTF_8.name());
    responseHeaders.set("Content-type", mediaType);
    responseHeaders.setContentLength(output.length);
    responseHeaders.set("Content-disposition", contentDisposition);

    return new ResponseEntity<>(output, responseHeaders, HttpStatus.OK);
  }

  private String addStandardComments(EnvironmentEntity env, ApplicationEntity app, String result) {
    result = "# environment: " + env.getName() + ", application: " + app.getName() + "\n" + result;
    result =
        "# date: "
            + ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
            + "\n"
            + result;
    return result;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/properties/upload")
  public ResponseEntity<OperationResult> upload(
      @RequestParam(name = "env", required = true) String environment,
      @RequestParam(name = "app", required = true) String application,
      @RequestBody MultipartFile file) {
    AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.UPDATE);
    PropertiesMediaType mimeType = PropertiesMediaType.fromValue(file.getContentType());

    if (PropertiesMediaType.UNKNOWN.equals(mimeType)) {
      throw new IllegalArgumentException("invalid mime type: " + file.getContentType());
    }

    try {
      Properties props;
      if (PropertiesMediaType.PROPERTIES.equals(mimeType)) {
        props = new Properties();
        props.load(new ByteArrayInputStream(file.getBytes()));
      } else {
        // yaml
        String yamlString = new String(file.getBytes(), StandardCharsets.UTF_8);
        props = PropertiesConverter.convertYamlStringToProperties(yamlString);
      }

      if (props.isEmpty()) {
        throw new IllegalArgumentException("file is empty");
      }

      Map<String, String> propertiesMap = new HashMap<>();
      for (Entry<Object, Object> elem : props.entrySet()) {
        propertiesMap.put((String) elem.getKey(), (String) elem.getValue());
      }
      propertiesManager.storeProperties(environment, application, propertiesMap);

    } catch (JsonParseException | JsonMappingException e) {
      throw new IllegalArgumentException("invalid file format");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("invalid file encoding, accepted: UTF-8");
    } catch (IOException e) {
      throw new IllegalArgumentException("unable to read file, invalid content");
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(
          "invalid element type in file, keys and values must be of type String");
    }

    return ResponseEntity.ok(
        OperationResult.builder()
            .withCode(OperationResultCode.SUCCESS)
            .withMessage("File successfully submitted")
            .build());
  }
  
  
  @RequestMapping(method = RequestMethod.GET, path = "/properties/commits", produces = "application/json")
  public ResponseEntity<Collection<Change>> loadCommitHistory(
      @RequestParam(name = "env", required = true) String environment,
      @RequestParam(name = "app", required = true) String application) {
    AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.READ);
        
    return ResponseEntity.ok(propertiesManager
        .loadCommitHistory(environment, application));
  }
}
