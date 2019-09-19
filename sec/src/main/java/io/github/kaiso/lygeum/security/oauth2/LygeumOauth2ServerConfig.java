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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import io.github.kaiso.lygeum.core.manager.UsersManager;
import io.github.kaiso.lygeum.core.security.LygeumPasswordEncoder;

/**
 * 
 * @author kaisomri
 *
 */
@Configuration
public class LygeumOauth2ServerConfig {

    private UsersManager usersManager;
    private LygeumPasswordEncoder encoder;

    @Autowired
    public LygeumOauth2ServerConfig(UsersManager usersManager, LygeumPasswordEncoder encoder) {
	super();
	this.usersManager = usersManager;
	this.encoder = encoder;
    }

    @Bean(name = "lygeumServerAuthenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	authProvider.setUserDetailsService(usersManager);
	authProvider.setPasswordEncoder(encoder);
	return new ProviderManager(Arrays.asList(authProvider));
    }

    @Bean
    @Primary
    public AuthorizationServerTokenServices lygeumServerTokenServices() {
	final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
	defaultTokenServices.setTokenStore(new InMemoryTokenStore());
	defaultTokenServices.setSupportRefreshToken(true);
	return defaultTokenServices;
    }

}
