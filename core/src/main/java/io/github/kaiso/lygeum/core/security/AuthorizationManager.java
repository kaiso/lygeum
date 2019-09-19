/**
   * Copyright © Kais OMRI
   *    
   * Licensed under the Apache License, Version 2.0 (the "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *     http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
   */
package io.github.kaiso.lygeum.core.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.kaiso.lygeum.core.security.exception.ResourceAccessDeniedException;
import io.github.kaiso.lygeum.core.security.exception.UnauthorizedException;

/**
 * 
 * @author Kais OMRI (kaiso)
 *
 */
public final class AuthorizationManager {

    public static final String ROLE_ADMIN = "LYGEUM_ADMIN";
    public static final String ROLE_OPERATOR = "LYGEUM_OPERATOR";
    public static final String ROLE_PREFIX = "ROLE_";

    private AuthorizationManager() {
	super();
    }

    public static void preAuthorize(String application, String environment, AuthorizationAction action) {
	if (SecurityContextHolder.getContext().getAuthentication() == null) {
	    throw new UnauthorizedException("No valid authentication found");
	}
	Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
		.getAuthorities();
	if (authorities.contains(new SimpleGrantedAuthority(AuthorizationManager.ROLE_PREFIX + ROLE_ADMIN))
		|| authorities.contains(new SimpleGrantedAuthority(AuthorizationManager.ROLE_PREFIX + ROLE_OPERATOR))
		|| authorities
			.contains(new SimpleGrantedAuthority(AuthorizationManager.ROLE_PREFIX + action.toString()))) {
	    return;
	}

	if ((action == AuthorizationAction.READ || action == AuthorizationAction.UPDATE)
		&& environment != null
		&& authorities.contains(new SimpleGrantedAuthority(
			AuthorizationManager.ROLE_PREFIX + environment.toUpperCase() + "_" + action))
		&& application != null
		&& authorities.contains(new SimpleGrantedAuthority(
			AuthorizationManager.ROLE_PREFIX + application.toUpperCase() + "_" + action))) {
	    return;
	}

	throw new ResourceAccessDeniedException();

    }

}
