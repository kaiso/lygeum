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

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 
 * @author kaisomri
 *
 */
@Configuration
@EnableAuthorizationServer
public class LygeumOauth2ServerAuthorizationServer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("lygeumServerAuthenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("lygeumServerTokenServices")
	private AuthorizationServerTokenServices tokenServices;
	
	@Autowired
	private ClientDetailsService clientDetailsService;
	

	private AuthorizationServerEndpointsConfigurer endpoints;


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.and();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.pathMapping("/oauth/token", "/lygeum/auth/access_token");
		endpoints.pathMapping("/oauth/authorize", "/lygeum/auth/authorize");
		endpoints.pathMapping("/oauth/error", "/lygeum/auth/error");
		endpoints.pathMapping("/oauth/check_token", "/lygeum/auth/check_token");
		endpoints.pathMapping("/oauth/token_key", "/lygeum/auth/token_key");
		endpoints.authenticationManager(authenticationManager).tokenServices(tokenServices);
		this.endpoints = endpoints;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenEndpointAuthenticationFilters(Arrays.asList(
				new LygeumOauth2ServerClientAuthenticationFilter(this.endpoints.getOAuth2RequestFactory(),
						this.endpoints.getClientDetailsService()),
				new LygeumOauth2ServerTokenEndpointFilter(authenticationManager,
						this.endpoints.getOAuth2RequestFactory())));
	}

}
