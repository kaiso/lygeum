package io.github.kaiso.lygeum.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.core.entities.Client;
import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.manager.ClientsManager;
import io.github.kaiso.lygeum.core.manager.UsersManager;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;
import io.github.kaiso.lygeum.core.system.GlobalConstants;

@RestController
public class AdminController extends LygeumRestController {

  private UsersManager usersManager;
  private ClientsManager clientsManager;

  @Autowired
  public AdminController(UsersManager usersManager, ClientsManager clientsManager) {
    this.usersManager = usersManager;
    this.clientsManager = clientsManager;
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/roles", method = RequestMethod.GET)
  public ResponseEntity<List<Role>> fetchAllRoles() {
    return ResponseEntity.ok(usersManager.findAllRoles());
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/users", method = RequestMethod.GET)
  public ResponseEntity<List<User>> fetchAllUsers(
      @RequestParam(name = "search", required = false) String search) {
    List<User> users;
    if (StringUtils.hasText(search)) {
      users = usersManager.findUsersByPattern(search);
    } else {
      users = usersManager.findAllUsers();
    }
    return ResponseEntity.ok(users);
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
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("USer successfully deleted");
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/clients", method = RequestMethod.GET)
  public ResponseEntity<List<Client>> fetchAllClients() {
    return ResponseEntity.ok(
        clientsManager
            .findAll()
            .parallelStream()
            .filter(c -> !c.getClientId().equals("ui"))
            .collect(Collectors.toList()));
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/clients", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Client> createClient(@RequestBody Client client) {
    if (client == null) {
      throw new IllegalArgumentException("Client resource can not be null");
    }
    client.setAuthorizedGrantTypes(new HashSet<String>(Arrays.asList("client_credentials")));
    client.setScope(new HashSet<String>(Arrays.asList("lygeum:auth")));
    client.setResourceIds(
        new HashSet<String>(Arrays.asList(GlobalConstants.LYGEUM_RESOURCE_SERVER_ID)));
    return ResponseEntity.of(Optional.of(clientsManager.create(client)));
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/clients/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Client> updateClient(
      @RequestBody Client client, @PathVariable("id") String code) {
    if (client == null) {
      throw new IllegalArgumentException("Client resource can not be null");
    }
    if (StringUtils.isEmpty(code)) {
      throw new IllegalArgumentException("Client code can not be null");
    }
    client.setCode(code);
    client.setAuthorizedGrantTypes(new HashSet<String>(Arrays.asList("client_credentials")));
    client.setScope(new HashSet<String>(Arrays.asList("lygeum:auth")));
    client.setResourceIds(
        new HashSet<String>(Arrays.asList(GlobalConstants.LYGEUM_RESOURCE_SERVER_ID)));
    return ResponseEntity.of(Optional.of(clientsManager.save(client)));
  }

  @RolesAllowed(AuthorizationManager.ROLE_ADMIN)
  @RequestMapping(path = "/admin/clients/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteClient(@PathVariable("id") String code) {

    if (StringUtils.isEmpty(code)) {
      throw new IllegalArgumentException("Client code can not be null");
    }
    clientsManager.deleteByCode(code);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Client successfully deleted");
  }
}
