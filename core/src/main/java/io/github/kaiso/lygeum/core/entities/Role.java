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
import javax.persistence.Table;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Entity
@Table(name = "LGM_ROLE")
public class Role extends BaseEntity {

	private String name;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static RoleBuilder builder() {
		return new Role.RoleBuilder();
	}

	public static class RoleBuilder {
		private String code;
		private String name;
		private String description;
		private boolean forceNew = false;

		public RoleBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public RoleBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public RoleBuilder withDescription(String description) {
			this.description = description;
			return this;
		}
		
		public RoleBuilder withForceNew(boolean forceNew) {
			this.forceNew = forceNew;
			return this;
		}

		public Role build() {
			Role role = new Role();
			role.setCode(code.toUpperCase());
			role.setName(name);
			role.setDescription(description);
			role.setForceNew(forceNew);
			return role;
		}
	}

}
