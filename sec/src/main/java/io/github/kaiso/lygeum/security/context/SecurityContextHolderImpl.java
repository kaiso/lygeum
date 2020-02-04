package io.github.kaiso.lygeum.security.context;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;

@Service
public class SecurityContextHolderImpl implements SecurityContextHolder {

	@Override
	public User getCurrentUser() {
		User user;
		OAuth2Authentication authentication = (OAuth2Authentication) org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		if (authentication.getUserAuthentication() != null) {
			user = (User) authentication.getPrincipal();
		} else {
			user = new User();
			user.setUsername(authentication.getOAuth2Request().getClientId());
		}
		return user;
	}

}
