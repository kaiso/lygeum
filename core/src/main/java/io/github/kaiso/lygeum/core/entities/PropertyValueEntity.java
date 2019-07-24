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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Entity
@Table(name = "APS_PROPERTY_VALUE")
public class PropertyValueEntity extends BaseEntity {

	private String value;

	private String description;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "env_id", referencedColumnName = "id")
	private EnvironmentEntity environment;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "prop_id", referencedColumnName = "id")
	private PropertyEntity property;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public EnvironmentEntity getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentEntity environment) {
		this.environment = environment;
	}

	public PropertyEntity getProperty() {
		return property;
	}

	public void setProperty(PropertyEntity property) {
		this.property = property;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static PropertyValueEntityBuilder builder() {
		return new PropertyValueEntity.PropertyValueEntityBuilder();
	}

	public static class PropertyValueEntityBuilder {

		private String code;
		private String value;
		private String description;
		private EnvironmentEntity environment;
		private PropertyEntity property;

		public PropertyValueEntityBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public PropertyValueEntityBuilder withValue(String value) {
			this.value = value;
			return this;
		}

		public PropertyValueEntityBuilder withDesciption(String description) {
			this.description = description;
			return this;
		}

		public PropertyValueEntityBuilder withEnvironment(EnvironmentEntity environment) {
			this.environment = environment;
			return this;
		}

		public PropertyValueEntityBuilder withProperty(PropertyEntity property) {
			this.property = property;
			return this;
		}

		public PropertyValueEntity build() {
			PropertyValueEntity e = new PropertyValueEntity();
			e.setCode(code);
			e.setValue(value);
			e.setDescription(description);
			e.setEnvironment(environment);
			e.setProperty(property);
			return e;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PropertyValueEntity other = (PropertyValueEntity) obj;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment)) {
			return false;
		}
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
