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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kaiso.lygeum.core.security.AuthorizationAction;
import io.github.kaiso.lygeum.core.security.AuthorizationManager;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Entity
@Table(name = "LGM_CLIENT")
public class Client extends BaseEntity implements ClientDetails {

	private static final long serialVersionUID = 5255223827480202386L;
	public static final ObjectMapper mapper = new ObjectMapper();

	private String name;

	@Column(name = "resource_ids")
	private String resourceIds;

	@Column(name = "client_secret")
	private String clientSecret;

	private String scope;

	@Column(name = "authorized_grant_types")
	private String authorizedGrantTypes;

	@Column(name = "web_server_redirect_uri")
	private String webServerRedirectUri;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "LGM_CLIENT_ROLE", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles = new ArrayList<>();

	@Column(name = "access_token_validity")
	private Integer accessTokenValidity;

	@Column(name = "refresh_token_validity")
	private Integer refreshTokenValidity;

	@Column(name = "additional_information")
	private String additionalInformation;

	private String autoapprove;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#getClientId()
	 */
	@Override
	public String getClientId() {
		return getCode();
	}

	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getResourceIds()
	 */
	@Override
	public Set<String> getResourceIds() {
		Set<String> resourceIdsList = new HashSet<>();
		if (resourceIds != null) {
			for (String scope : resourceIds.split(",")) {
				resourceIdsList.add(scope);
			}
		}
		return resourceIdsList;
	}
	
	public void setResourceIds(Set<String> uris) {
		if (uris != null) {
			resourceIds = uris.stream().collect(Collectors.joining(","));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#isSecretRequired()
	 */
	@Override
	public boolean isSecretRequired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getClientSecret()
	 */
	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#isScoped()
	 */
	@Override
	public boolean isScoped() {
		return scope != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#getScope()
	 */
	@Override
	public Set<String> getScope() {
		Set<String> scopes = new HashSet<>();
		if (scope != null) {
			for (String scope : scope.split(",")) {
				scopes.add(scope);
			}
		}
		return scopes;
	}

	public void setScope(Set<String> scopes) {
		if (scopes != null) {
			scope = scopes.stream().collect(Collectors.joining(","));
		}
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
		if (authorizedGrantTypes != null) {
			for (String type : authorizedGrantTypes.split(",")) {
				grantTypes.add(type);
			}
		}
		return grantTypes;
	}

	public void setAuthorizedGrantTypes(Set<String> authorizedTypes) {
		if (authorizedTypes != null) {
			authorizedGrantTypes = authorizedTypes.stream().collect(Collectors.joining(","));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getRegisteredRedirectUri()
	 */
	@Override
	public Set<String> getRegisteredRedirectUri() {
		Set<String> uris = new HashSet<>();
		if (webServerRedirectUri != null) {
			for (String uri : webServerRedirectUri.split(",")) {
				uris.add(uri);
			}
		}
		return uris;
	}

	public void setRegisteredRedirectUri(Set<String> uris) {
		if (uris != null) {
			webServerRedirectUri = uris.stream().collect(Collectors.joining(","));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.oauth2.provider.ClientDetails#getAuthorities()
	 */
	@Override
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
		roles.stream().forEach(r -> {
			authorities.add(new SimpleGrantedAuthority(AuthorizationManager.ROLE_PREFIX + r.getCode()));
			if (r.getCode().endsWith(AuthorizationAction.UPDATE.toString())) {
				authorities.add(new SimpleGrantedAuthority(AuthorizationManager.ROLE_PREFIX
						+ r.getCode().substring(0, r.getCode().length() - 6) + AuthorizationAction.READ.toString()));
			}
		});
		return authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getAccessTokenValiditySeconds()
	 */
	@Override
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValidity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getRefreshTokenValiditySeconds()
	 */
	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValidity;
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
		return autoapprove != null && autoapprove.contains(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetails#
	 * getAdditionalInformation()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAdditionalInformation() {
		try {
			return additionalInformation != null ? mapper.readValue(additionalInformation, Map.class) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
