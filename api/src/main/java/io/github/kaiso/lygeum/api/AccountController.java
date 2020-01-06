package io.github.kaiso.lygeum.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.api.resources.PasswordResource;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.manager.UsersManager;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;
import io.github.kaiso.lygeum.core.system.GlobalConstants;

@RestController
public class AccountController extends LygeumRestController {

	private SecurityContextHolder securityContextHolder;
	private UsersManager usersManager;

	@Autowired
	private AccountController(SecurityContextHolder securityContextHolder, UsersManager usersManager) {
		super();
		this.securityContextHolder = securityContextHolder;
		this.usersManager = usersManager;
	}

	@RequestMapping(path = "/accounts/me", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> getMe() {
		return ResponseEntity.of(Optional.of(securityContextHolder.getCurrentUser()));
	}

	@RequestMapping(path = "/accounts/{code}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateAccount(@RequestBody User user, @PathVariable String code) {

		if (!securityContextHolder.getCurrentUser().getCode().equals(code)) {
			throw new IllegalArgumentException(
					"can not update user code {}, the user to update must correspond to connected user");
		}

		if (code.equals(GlobalConstants.LYGEUM_ADMIN_CODE)) {
			user.setUsername(GlobalConstants.LYGEUM_ADMIN_USERNAME);
		}
		
		user.setPassword(null);

		usersManager.saveUser(user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User successfully updated");
	}
	
	
	@RequestMapping(path = "/accounts/{code}/password", method = RequestMethod.PUT)
	public ResponseEntity<String> updatePassword(@RequestBody PasswordResource password, @PathVariable String code) {
		
		if (!securityContextHolder.getCurrentUser().getCode().equals(code)) {
			throw new IllegalArgumentException(
					"can not update user code {}, the user to update must correspond to connected user");
		}
		
		if(password.getNewPassword() == null || !password.getNewPassword().equals(password.getConfirmPassword())) {
			throw new IllegalArgumentException("Password mismatch");
		}
		User user = securityContextHolder.getCurrentUser();
		user.setPassword(password.getNewPassword());
		usersManager.saveUser(user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User successfully updated");
	}

}
