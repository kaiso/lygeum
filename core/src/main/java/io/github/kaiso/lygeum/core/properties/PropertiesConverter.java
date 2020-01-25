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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public final class PropertiesConverter {

	private static final JavaPropsMapper javaPropsMapper = new JavaPropsMapper();
	private static final YAMLMapper yamlMapper = new YAMLMapper();
	private static final ObjectMapper mapper = new ObjectMapper();

	private PropertiesConverter() {
		super();
	}

	public static String convertJsonToPropertiesString(Object obj) throws JsonProcessingException {
		return new String(javaPropsMapper.writeValueAsBytes(obj));
	}

	public static String convertJsonToYamlString(Object obj) throws JsonProcessingException {
		return new String(yamlMapper.writeValueAsBytes(obj));
	}

	public static String getPropertiesAsString(Properties prop) throws IOException {
		StringWriter writer = new StringWriter();
		prop.store(writer, "");
		return writer.getBuffer().toString();
	}

	public static Properties convertYamlStringToProperties(String yamlString) throws IOException {
		Object json = yamlMapper.readValue(yamlString, Object.class);
		Properties props = new Properties();
		props.load(new ByteArrayInputStream(convertJsonToPropertiesString(json).getBytes()));
		return props;

	}

	public static Object convertPropertiesMapToJson(Map<String, String> properties) throws IOException {
		return javaPropsMapper.readValue(getPropertiesMapAsString(properties), Object.class);
	}
	
	
	public static String convertPropertiesMapToJsonString(Map<String, String> properties) throws IOException {
		return mapper.writeValueAsString(javaPropsMapper.readValue(getPropertiesMapAsString(properties), Object.class));
	}
	
	

	public static String getPropertiesMapAsString(Map<String, String> properties) {
		StringBuilder sb = new StringBuilder();
		properties.entrySet().stream()
				.sorted((Entry<String, String> e1, Entry<String, String> e2) -> e1.getKey().compareTo(e2.getKey()))
				.forEach(e -> sb.append("\n").append(e.getKey()).append("=").append(e.getValue()));
		return sb.toString();
	}

}
