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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import io.github.kaiso.lygeum.core.util.JSONUtils;

/**
 * 
 * @author kaisomri
 *
 */
public class LygeumOauth2ServerClientAuthenticationFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(LygeumOauth2ServerClientAuthenticationFilter.class);
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private OAuth2RequestFactory oAuth2RequestFactory;
	private ClientDetailsService clientDetailsService;

	public LygeumOauth2ServerClientAuthenticationFilter(OAuth2RequestFactory oAuth2RequestFactory,
			ClientDetailsService clientDetailsService) {
		super();
		this.oAuth2RequestFactory = oAuth2RequestFactory;
		this.clientDetailsService = clientDetailsService;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("init {}", getClass().getName());
		Assert.state(oAuth2RequestFactory != null, "OAuth2RequestFactory must be provided");
		Assert.state(clientDetailsService != null, "ClientDetailsService must be provided");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (((HttpServletRequest) request).getMethod().equals(HttpMethod.OPTIONS.name())) {
			SecurityContextHolder.getContext()
					.setAuthentication(createAnonymousAuthentication((HttpServletRequest) request));
			chain.doFilter(request, response);
			return;
		}

		AuthorizationRequest authorizationRequest;
		ClientDetails clientDetails;
		try {
			authorizationRequest = Optional.ofNullable(extractCredentials((HttpServletRequest) request)).orElseThrow();
			clientDetails = authenticate(authorizationRequest);
		} catch (ClientRegistrationException | NoSuchElementException e) {
			((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write(JSONUtils
					.writeObjectAsJson(OAuth2Exception.create(OAuth2Exception.INVALID_CLIENT, e.getMessage())));
			return;
		}

		authorizationRequest.setScope(getScope((HttpServletRequest) request));
		authorizationRequest.setClientId(clientDetails.getClientId());
		authorizationRequest.setAuthorities(clientDetails.getAuthorities());
		authorizationRequest.setApproved(true);

		OAuth2Request storedOAuth2Request = oAuth2RequestFactory.createOAuth2Request(authorizationRequest);

		OAuth2Authentication authentication = new OAuth2Authentication(storedOAuth2Request, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);

	}

	private ClientDetails authenticate(AuthorizationRequest authorizationRequest) {
		return Optional.ofNullable(clientDetailsService.loadClientByClientId(authorizationRequest.getClientId()))
				.orElseThrow();
	}

	protected Authentication createAnonymousAuthentication(HttpServletRequest request) {
		AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("key", "anonymousUser",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
		auth.setDetails(authenticationDetailsSource.buildDetails(request));

		return auth;
	}

	protected AuthorizationRequest extractCredentials(HttpServletRequest request) {
		String clientId = request.getParameter("client_id");
		if (clientId != null) {
			Map<String, String> map = getSingleValueMap(request);
			map.put(OAuth2Utils.CLIENT_ID, clientId);
			return oAuth2RequestFactory.createAuthorizationRequest(map);
		}
		return null;
	}

	private Set<String> getScope(HttpServletRequest request) {
		return OAuth2Utils.parseParameterList(request.getParameter("scope"));
	}

	private Map<String, String> getSingleValueMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> parameters = request.getParameterMap();
		for (String key : parameters.keySet()) {
			String[] values = parameters.get(key);
			map.put(key, values != null && values.length > 0 ? values[0] : null);
		}
		return map;
	}

	@Override
	public void destroy() {
		logger.debug("destroy {}", getClass().getName());
	}

}
