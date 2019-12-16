package io.github.kaiso.lygeum.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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

import io.github.kaiso.lygeum.api.resources.PropertyMapper;
import io.github.kaiso.lygeum.api.resources.PropertyResource;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
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
	public PropertiesController(PropertiesManager propertiesManager, ApplicationsManager applicationsManager,
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
		List<PropertyResource> result = propertiesManager.findPropertiesByEnvironmentAndApplication(environment, application)
				.parallelStream().map(PropertyMapper::map).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping(path = "/properties/{code}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteProperty(@PathVariable(required = true, name = "code") String code) {
	        PropertyEntity p =  propertiesManager.findByCode(code)
	        	.orElseThrow(() -> new IllegalArgumentException("Property not found with code: " + code));
		AuthorizationManager.preAuthorize(p.getApplication().getCode(), null, AuthorizationAction.UPDATE);

		propertiesManager.delete(code);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Property successfully deleted");
	}


	@RequestMapping(method = RequestMethod.POST, path = "/properties", produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateProperties(@RequestParam(name = "env", required = true) String environment,
			@RequestParam(name = "app", required = true) String application,
			@RequestBody(required = true) @Valid List<PropertyResource> properties) {
		AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.UPDATE);
		EnvironmentEntity env = environmentsManager.findByCode(environment)
				.orElseThrow(() -> new IllegalArgumentException("Environment not found with code: " + environment));
		ApplicationEntity app = applicationsManager.findByCode(application)
				.orElseThrow(() -> new IllegalArgumentException("Application not found with code: " + application));
		propertiesManager.updateProperties(
				properties.parallelStream().map(p -> PropertyMapper.map(p, app, env)).collect(Collectors.toList()));
		return ResponseEntity.ok("Properties successfully updated");
	}

	@RequestMapping(method = RequestMethod.GET, path = "/properties/download")
	public ResponseEntity<byte[]> download(@RequestParam(name = "env", required = true) String environment,
			@RequestParam(name = "app", required = true) String application,
			@RequestParam(name = "layout", required = true) String layout) {
		AuthorizationManager.preAuthorize(application, environment, AuthorizationAction.READ);
		Map<String, String> properties = propertiesManager
				.findPropertiesByEnvironmentAndApplication(environment, application).stream()
				.collect(HashMap::new, (m,v)->m.put(v.getName(), Optional.ofNullable(v.getValue()).orElse("")), HashMap::putAll);
		String result;
		String contentDisposition;
		try {
			Object obj = PropertiesConverter.convertPropertiesMapToJson(properties);
			if (PropertiesLayout.PROPERTIES.toString().equalsIgnoreCase(layout)) {
				result = PropertiesConverter.convertJsonToPropertiesString(obj);
				contentDisposition = "attachment; filename=config.properties";
			} else if (PropertiesLayout.YAML.toString().equalsIgnoreCase(layout)) {
				result = PropertiesConverter.convertJsonToYamlString(obj);
				contentDisposition = "attachment; filename=config.yaml";
			} else {
				throw new IllegalArgumentException("invalid layout, accepted values are [PROPERTIES,YAML]");
			}
		} catch (IOException e) {
			throw new PropertiesConvertionException("failed to convert persisted properties", e);
		}
		byte[] output;
		try {
			output = result.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new PropertiesConvertionException("Unsupported encoding utf-8", e);
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("charset", "utf-8");
		responseHeaders.setContentType(MediaType.valueOf("application/octet-stream"));
		responseHeaders.setContentLength(output.length);
		responseHeaders.set("Content-disposition", contentDisposition);

		return new ResponseEntity<>(output, responseHeaders, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST, path = "/properties/upload")
	public ResponseEntity<String> upload(@RequestParam(name = "env", required = true) String environment,
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
				String yamlString = new String(file.getBytes(), "utf-8");
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
			throw new IllegalArgumentException("invalid element type in file, keys and values must be of type String");
		}

		return ResponseEntity.ok("file successfully uploaded");
	}

}
