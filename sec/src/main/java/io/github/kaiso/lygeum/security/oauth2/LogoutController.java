package io.github.kaiso.lygeum.security.oauth2;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogoutController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @RequestMapping("/lygeum/auth/logout")
    @ResponseBody
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, HttpServletRequest request) {

	consumerTokenServices.revokeToken(token.replace("Bearer", "").trim());

	new SecurityContextLogoutHandler().logout(request, null, null);

	return ResponseEntity.ok("success");
    }

}
