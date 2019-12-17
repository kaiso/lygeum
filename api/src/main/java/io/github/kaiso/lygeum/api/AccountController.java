package io.github.kaiso.lygeum.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;

@RestController
public class AccountController extends LygeumRestController {

    private SecurityContextHolder securityContextHolder;

    @Autowired
    private AccountController(SecurityContextHolder securityContextHolder) {
	super();
	this.securityContextHolder = securityContextHolder;
    }

    @RequestMapping(path = "/account/me", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getMe() {
	return ResponseEntity.of(Optional.of(securityContextHolder.getCurrentUser()));
    }

}
