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
package io.github.kaiso.lygeum.core.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import io.github.kaiso.lygeum.core.security.AuthorizationAction;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Entity
@Table(name="APS_CLIENT")
public class Client extends BaseEntity implements ClientDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5255223827480202386L;
	private String secret;
	private String scopeList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#getClientId()
	 */
	@Override
	public String getClientId() {
		return getCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getResourceIds()
	 */
	@Override
	public Set<String> getResourceIds() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#isSecretRequired()
	 */
	@Override
	public boolean isSecretRequired() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getClientSecret()
	 */
	@Override
	public String getClientSecret() {
		return secret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#isScoped()
	 */
	@Override
	public boolean isScoped() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#getScope()
	 */
	@Override
	public Set<String> getScope() {
		Set<String> scopes = new HashSet<>();
		if (scopeList != null) {
			for (String scope : scopeList.split(",")) {
				scopes.add(scope);
			}
		}
		return scopes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getAuthorizedGrantTypes()
	 */
	@Override
	public Set<String> getAuthorizedGrantTypes() {
		Set<String> grantTypes = new HashSet<>();
		grantTypes.add("passoword");
		grantTypes.add("client_secret");
		return grantTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getRegisteredRedirectUri()
	 */
	@Override
	public Set<String> getRegisteredRedirectUri() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getAuthorities()
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority(AuthorizationAction.ALL_PROPS_READ.name()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getAccessTokenValiditySeconds()
	 */
	@Override
	public Integer getAccessTokenValiditySeconds() {
		return Integer.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getRefreshTokenValiditySeconds()
	 */
	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return Integer.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#isAutoApprove(java
	 * .lang.String)
	 */
	@Override
	public boolean isAutoApprove(String scope) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getAdditionalInformation()
	 */
	@Override
	public Map<String, Object> getAdditionalInformation() {
		return null;
	}

}
