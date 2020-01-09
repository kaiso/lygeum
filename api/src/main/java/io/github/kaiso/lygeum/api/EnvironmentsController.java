package io.github.kaiso.lygeum.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.manager.EnvironmentsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;

@RestController
public class EnvironmentsController extends LygeumRestController {

	private EnvironmentsManager environmentsManager;

	@Autowired
	public EnvironmentsController(EnvironmentsManager environmentsManager) {
		this.environmentsManager = environmentsManager;
	}

	@RequestMapping(path = "/environments", method = RequestMethod.GET)
	public ResponseEntity<List<EnvironmentEntity>> fetchAllEnvironments() {
		return ResponseEntity.ok(environmentsManager.findAll().stream().filter(e -> {
			try {
				AuthorizationManager.preAuthorize(null, e.getCode(), AuthorizationAction.READ);
				return true;
			} catch (Exception e1) {
				return false;
			}
		}).collect(Collectors.toList()));
	}

	@RequestMapping(path = "/environments/{code}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateEnvironment(@RequestBody(required = true) EnvironmentEntity env,
			@PathVariable(required = true, name = "code") String code) {
		EnvironmentEntity environment = environmentsManager.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Environment not found with code: " + code));
		AuthorizationManager.preAuthorize(null, environment.getName(), AuthorizationAction.UPDATE);

		environmentsManager.update(env);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Environment successfully updated");
	}

	@RequestMapping(path = "/environments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnvironmentEntity> createEnvironment(@RequestBody(required = true) EnvironmentEntity env) {
		AuthorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_ENV_CREATE);

		EnvironmentEntity environment = environmentsManager.create(env);

		return ResponseEntity.status(HttpStatus.CREATED).body(environment);
	}

	@RequestMapping(path = "/environments/{code}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteEnvironment(@PathVariable(required = true, name = "code") String code) {
		EnvironmentEntity environment = environmentsManager.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Environment not found with code: " + code));
		AuthorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_ENV_DELETE);

		environmentsManager.delete(environment);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Environment successfully deleted");
	}

}
