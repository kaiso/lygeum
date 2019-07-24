package io.github.kaiso.lygeum.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String writeObjectAsJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}
