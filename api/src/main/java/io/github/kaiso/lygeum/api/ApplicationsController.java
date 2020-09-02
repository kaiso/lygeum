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

import io.github.kaiso.lygeum.api.resources.OperationResult;
import io.github.kaiso.lygeum.api.resources.OperationResult.OperationResultCode;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.manager.ApplicationsManager;
import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;

@RestController
public class ApplicationsController extends LygeumRestController {

  private ApplicationsManager applicationsManager;

  @Autowired
  public ApplicationsController(ApplicationsManager applicationsManager) {
    this.applicationsManager = applicationsManager;
  }

  @RequestMapping(path = "/applications", method = RequestMethod.GET)
  public ResponseEntity<List<ApplicationEntity>> fetchAllApplications() {
    return ResponseEntity.ok(
        applicationsManager
            .findAll()
            .stream()
            .filter(
                e -> {
                  try {
                    AuthorizationManager.preAuthorize(e.getCode(), null, AuthorizationAction.READ);
                    return true;
                  } catch (Exception e1) {
                    return false;
                  }
                })
            .collect(Collectors.toList()));
  }

  @RequestMapping(path = "/applications/{code}", method = RequestMethod.PUT)
  public ResponseEntity<OperationResult> updateApplication(
      @RequestBody(required = true) ApplicationEntity app,
      @PathVariable(required = true, name = "code") String code) {
    ApplicationEntity application =
        applicationsManager
            .findByCode(code)
            .orElseThrow(
                () -> new IllegalArgumentException("Application not found with code: " + code));
    AuthorizationManager.preAuthorize(application.getCode(), null, AuthorizationAction.UPDATE);

    applicationsManager.update(app);

    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            OperationResult.builder()
                .withCode(OperationResultCode.SUCCESS)
                .withMessage("Application successfully updated")
                .build());
  }

  @RequestMapping(
      path = "/applications",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApplicationEntity> createApplication(
      @RequestBody(required = true) ApplicationEntity app) {
    AuthorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_APP_CREATE);

    ApplicationEntity application = applicationsManager.create(app);

    return ResponseEntity.status(HttpStatus.CREATED).body(application);
  }

  @RequestMapping(path = "/applications/{code}", method = RequestMethod.DELETE)
  public ResponseEntity<OperationResult> deleteApplication(
      @PathVariable(required = true, name = "code") String code) {
    ApplicationEntity application =
        applicationsManager
            .findByCode(code)
            .orElseThrow(
                () -> new IllegalArgumentException("Application not found with code: " + code));
    AuthorizationManager.preAuthorize(null, null, AuthorizationAction.ALL_APP_DELETE);

    applicationsManager.delete(application);

    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            OperationResult.builder()
                .withCode(OperationResultCode.SUCCESS)
                .withMessage("Application successfully deleted")
                .build());
  }
}
