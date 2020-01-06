package io.github.kaiso.lygeum.api;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.manager.UsersManager;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;

@RestController
public class AdminController extends LygeumRestController {

	private UsersManager usersManager;

	@Autowired
	public AdminController(UsersManager usersManager) {
		this.usersManager = usersManager;
	}

	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/roles", method = RequestMethod.GET)
	public ResponseEntity<List<Role>> fetchAllRoles() {
		return ResponseEntity.ok(usersManager.findAllRoles());
	}

	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/users", method = RequestMethod.GET)
	public ResponseEntity<List<User>> fetchAllUsers() {
		return ResponseEntity.ok(usersManager.findAllUsers());
	}

	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/users", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		if (user == null) {
			throw new IllegalArgumentException("User resource can not be null");
		}
		return ResponseEntity.of(Optional.of(usersManager.createUser(user)));
	}

	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/users/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") String code) {
		if (user == null) {
			throw new IllegalArgumentException("User resource can not be null");
		}
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("User code can not be null");
		}
		user.setCode(code);
		return ResponseEntity.of(Optional.of(usersManager.saveUser(user)));
	}

	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@PathVariable("id") String code) {

		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("User code can not be null");
		}
		usersManager.deleteUserByCode(code);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Environment successfully deleted");
	}
	
	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/clients", method = RequestMethod.GET)
	public ResponseEntity<List<User>> fetchAllClients() {
		return ResponseEntity.ok(usersManager.findAllUsers());
	}
	
	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/clients", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> createClient(@RequestBody User user) {
		if (user == null) {
			throw new IllegalArgumentException("User resource can not be null");
		}
		return ResponseEntity.of(Optional.of(usersManager.createUser(user)));
	}
	
	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/clients/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> updateClient(@RequestBody User user, @PathVariable("id") String code) {
		if (user == null) {
			throw new IllegalArgumentException("User resource can not be null");
		}
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("User code can not be null");
		}
		user.setCode(code);
		return ResponseEntity.of(Optional.of(usersManager.saveUser(user)));
	}
	
	@RolesAllowed(AuthorizationManager.ROLE_ADMIN)
	@RequestMapping(path = "/admin/clients/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteClient(@PathVariable("id") String code) {
		
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("User code can not be null");
		}
		usersManager.deleteUserByCode(code);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Environment successfully deleted");
	}

}
