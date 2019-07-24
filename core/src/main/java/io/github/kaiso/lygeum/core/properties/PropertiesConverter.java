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
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public final class PropertiesConverter {

	private PropertiesConverter() {
		super();
	}

	public static String convertJsonToPropertiesString(Object obj) throws JsonProcessingException {
		JavaPropsMapper mapper = new JavaPropsMapper();
		return new String(mapper.writeValueAsBytes(obj));
	}

	public static String convertJsonToYamlString(Object obj) throws JsonProcessingException {
		YAMLMapper mapper = new YAMLMapper();
		return new String(mapper.writeValueAsBytes(obj));

	}

	public static String getPropertiesAsString(Properties prop) throws IOException {
		StringWriter writer = new StringWriter();
		prop.store(writer, "");
		return writer.getBuffer().toString();
	}

	public static Properties convertYamlStringToProperties(String yamlString) throws IOException {
		ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		Object json = yamlReader.readValue(yamlString, Object.class);
		Properties props = new Properties();
		props.load(new ByteArrayInputStream(convertJsonToPropertiesString(json).getBytes()));
		return props;

	}

	public static Object convertPropertiesMapToJson(Map<String, String> properties) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new JavaPropsFactory());
		Properties props = new Properties();
		props.putAll(properties);
		return objectMapper.readValue(getPropertiesAsString(props), Object.class);
	}

}
