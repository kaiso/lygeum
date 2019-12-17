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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Entity
@Table(name = "LGM_PROPERTY")
@NamedQuery(name = "PropertyEntity.findByEnvironmentAndApplicationNamed", query = "select p from PropertyEntity p left join fetch p.values q left join q.environment e on e.code=?1 where p.application.code=?2")
public class PropertyEntity extends BaseEntity {
    
	@Column(name = "name", unique = true)
	private String name;

	private String description;

	@JsonIgnore
	@OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
	private Set<PropertyValueEntity> values;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "app_id", referencedColumnName = "id")
	private ApplicationEntity application;

	/**
	 * @param key the key to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return getValues().isEmpty() ? null : getValues().iterator().next().getValue();
	}

	public void setValue(String value) {
		getValues().add(PropertyValueEntity.builder().withProperty(this).withValue(value).build());
	}

	public Set<PropertyValueEntity> getValues() {
		if (values == null) {
			values = new HashSet<>();
		}
		return values;
	}

	public void setValues(Set<PropertyValueEntity> environment) {
		this.values = environment;
	}

	public ApplicationEntity getApplication() {
		return application;
	}

	public void setApplication(ApplicationEntity application) {
		this.application = application;
	}

	public static PropertyEntityBuilder builder() {
		return new PropertyEntity.PropertyEntityBuilder();
	}

	public static class PropertyEntityBuilder {

		private String code;
		private String description;
		private String name;
		private String value;
		private EnvironmentEntity environment;
		private ApplicationEntity application;

		public PropertyEntityBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public PropertyEntityBuilder withDesciption(String description) {
			this.description = description;
			return this;
		}

		public PropertyEntityBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public PropertyEntityBuilder withValue(String value) {
			this.value = value;
			return this;
		}

		public PropertyEntityBuilder withEnvironment(EnvironmentEntity env) {
			this.environment = env;
			return this;
		}

		public PropertyEntityBuilder withApplication(ApplicationEntity application) {
			this.application = application;
			return this;
		}

		public PropertyEntity build() {
			PropertyEntity e = new PropertyEntity();
			e.setCode(code);
			e.setName(name);
			e.setDescription(description);
			Set<PropertyValueEntity> values = new HashSet<>();
			if (StringUtils.hasText(value)) {
				if (environment == null || !StringUtils.hasText(environment.getCode())) {
					throw new IllegalArgumentException(
							"when specifying value for a property, environment must be provided");
				}
				values.add(PropertyValueEntity.builder().withEnvironment(environment).withProperty(e).withValue(value)
						.build());
			}
			e.setValues(values);
			e.setApplication(application);
			return e;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyEntity other = (PropertyEntity) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application)) {
			return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
