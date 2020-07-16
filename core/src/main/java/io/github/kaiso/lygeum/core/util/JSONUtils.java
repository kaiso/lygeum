package io.github.kaiso.lygeum.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kaiso.lygeum.core.context.ApplicationContextProvider;

public class JSONUtils {

	private JSONUtils() {
		super();
	}

	public static String writeObjectAsJson(Object obj) {
		try {
			return ApplicationContextProvider.getBean(ObjectMapper.class).writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}
