
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
package io.github.kaiso.lygeum.security.oauth2;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;

/**
 * 
 * @author kaisomri
 *
 */
public class LygeumOauth2ServerTokenEndpointFilter extends TokenEndpointAuthenticationFilter {

	public LygeumOauth2ServerTokenEndpointFilter(AuthenticationManager authenticationManager,
			OAuth2RequestFactory oAuth2RequestFactory) {
		super(authenticationManager, oAuth2RequestFactory);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		if (((HttpServletRequest) req).getMethod().equals(HttpMethod.OPTIONS.name())) {
			return;
		} else {
			super.doFilter(req, res, chain);
		}
	}

}
