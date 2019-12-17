package io.github.kaiso.lygeum.security.context;

import org.springframework.stereotype.Service;

import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.security.SecurityContextHolder;

@Service
public class SecurityContextHolderImpl implements SecurityContextHolder {

    @Override
    public User getCurrentUser() {
	return (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
		.getPrincipal();
    }

}
