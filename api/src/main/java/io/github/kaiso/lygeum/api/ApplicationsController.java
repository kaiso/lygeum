package io.github.kaiso.lygeum.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.security.AuthrorizationManager;

@RestController
public class ApplicationsController extends LygeumRestController {

	private ApplicationsManager applicationsManager;

	@Autowired
	public ApplicationsController(ApplicationsManager applicationsManager) {
		this.applicationsManager = applicationsManager;
	}

	@RequestMapping(path = "/applications", method = RequestMethod.GET)
	public ResponseEntity<List<ApplicationEntity>> fetchAllApplications() {
		return ResponseEntity.ok(applicationsManager.findAll());
	}

	@RequestMapping(path = "/applications/{code}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateApplication(@RequestBody(required = true) ApplicationEntity app,
			@PathVariable(required = true, name = "code") String code) {
		ApplicationEntity application = applicationsManager.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Application not found with code: " + code));
		AuthrorizationManager.preAuthorize(null, application.getName(), AuthorizationAction.UPDATE);

		applicationsManager.update(app);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Application successfully updated");
	}

	@RequestMapping(path = "/applications", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApplicationEntity> createApplication(@RequestBody(required = true) ApplicationEntity app) {
		AuthrorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_APP_CREATE);

		ApplicationEntity application = applicationsManager.create(app);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(application);
	}

	@RequestMapping(path = "/applications/{code}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteApplication(@PathVariable(required = true, name = "code") String code) {
		ApplicationEntity application = applicationsManager.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Application not found with code: " + code));
		AuthrorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_APP_DELETE);

		applicationsManager.delete(application);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Application successfully deleted");
	}

}
