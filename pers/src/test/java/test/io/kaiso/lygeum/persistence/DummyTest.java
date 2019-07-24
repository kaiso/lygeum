package test.io.kaiso.lygeum.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class DummyTest {

	@Test
	public void testObject() throws IOException {
		System.out.println("success");
		String yaml = new String(Files
				.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("user-details.yml").getPath())));
		System.out.println(convertYamlToJson(yaml));

		System.out.println("properties");
		System.out.println(convertJavaPropsToJson(new String(Files
				.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("car.properties").getPath())))));
	}

	String convertYamlToJson(String yaml) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		Object obj = yamlReader.readValue(yaml, Object.class);

		convertJsonToProperties(obj);
		ObjectMapper jsonWriter = new ObjectMapper();
		return jsonWriter.writeValueAsString(obj);
	}

	String convertJavaPropsToJson(String props) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper yamlReader = new ObjectMapper(new JavaPropsFactory());
		Object obj = yamlReader.readValue(props, Object.class);

		ObjectMapper jsonWriter = new ObjectMapper();

		convertJsonToYaml(obj);
		return jsonWriter.writeValueAsString(obj);
	}

	private void convertJsonToProperties(Object obj) throws JsonProcessingException {
		JavaPropsMapper mapper = new JavaPropsMapper();

		String propsmapped = new String(mapper.writeValueAsBytes(obj));

		System.out.println("json to properties");
		System.out.println(propsmapped);
	}

	private void convertJsonToYaml(Object obj) throws JsonProcessingException {
		YAMLMapper mapper = new YAMLMapper();

		String propsmapped = new String(mapper.writeValueAsBytes(obj));

		System.out.println("json to yaml");
		System.out.println(propsmapped);
	}

	@Test
	public void testJsonCompare() throws JsonParseException, IOException {

		String json1="  { \"password\" :\"ABC\",\"user\":\"XYZ\"}";
		String json2="  { \"user\":\"XYZ\", \"password\" :\"ABC\"    "
				+ "      }";

		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.setCodec(new ObjectMapper());
		JsonParser parser1 = jsonFactory.createParser(json1);
		JsonParser parser2 = jsonFactory.createParser(json2);
		TreeNode node1 = parser1.readValueAsTree();
		TreeNode node2 = parser2.readValueAsTree();
		Assertions.assertEquals(node1,node2);
		
	}

}
