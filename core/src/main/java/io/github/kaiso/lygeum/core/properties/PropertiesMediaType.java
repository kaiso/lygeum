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
package io.github.kaiso.lygeum.core.properties;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public enum PropertiesMediaType {

	YAML("text/x-yaml"), PROPERTIES("text/x-java-properties"), UNKNOWN("unknown");

	private String value;

	private PropertiesMediaType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static PropertiesMediaType fromValue(String val) {
		for (PropertiesMediaType type : values()) {
			if (type.value().equals(val)) {
				return type;
			}
		}
		return UNKNOWN;
	}

}
