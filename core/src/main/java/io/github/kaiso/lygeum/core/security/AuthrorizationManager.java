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
public final class AuthrorizationManager {

	public static final String ROLE_ADMIN = "LYGEUM_ADMIN";
	public static final String ROLE_ALL_PREFIX = "ALL";

	private AuthrorizationManager() {
		super();
	}

	public static void preAuthorize(String application, String environment, AuthorizationAction action) {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			throw new UnauthorizedException("No valid authentication found");
		}
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		if (authorities.contains(new SimpleGrantedAuthority(ROLE_ADMIN))
				|| action.toString().startsWith(ROLE_ALL_PREFIX)
						&& authorities.contains(new SimpleGrantedAuthority(action.toString()))
				|| (environment != null && authorities
						.contains(new SimpleGrantedAuthority(environment.toUpperCase() + "_" + action)))) {
			return;
		}
		throw new ResourceAccessDeniedException();

	}

}
