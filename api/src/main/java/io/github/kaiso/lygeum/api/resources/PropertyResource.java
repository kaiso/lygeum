package io.github.kaiso.lygeum.api.resources;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author kom3806
 *
 */
public class PropertyResource {

	private String code;
	@NotBlank
	private String key;
	private String description;
	private String value;

	@JsonIgnore
	private String appCode;
	@JsonIgnore
	private String envCode;

	public PropertyResource() {
		super();
	}

	private PropertyResource(String code, String key, String description, String value, String appCode,
			String envCode) {
		super();
		this.code = code;
		this.key = key;
		this.description = description;
		this.value = value;
		this.appCode = appCode;
		this.envCode = envCode;
	}

	public String getCode() {
		return code;
	}

	public String getAppCode() {
		return appCode;
	}

	public String getEnvCode() {
		return envCode;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public void setEnvCode(String envCode) {
		this.envCode = envCode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static PropertyResourceBuilder builder() {
		return new PropertyResourceBuilder();
	}

	public static class PropertyResourceBuilder {

		private String code;
		private String key;
		private String description;
		private String value;
		private String appCode;
		private String envCode;

		public PropertyResourceBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public PropertyResourceBuilder withAppCode(String appCode) {
			this.appCode = appCode;
			return this;
		}

		public PropertyResourceBuilder withEnvCode(String envCode) {
			this.envCode = envCode;
			return this;
		}

		public PropertyResourceBuilder withKey(String key) {
			this.key = key;
			return this;
		}

		public PropertyResourceBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public PropertyResourceBuilder withValue(String value) {
			this.value = value;
			return this;
		}

		public PropertyResource build() {
			return new PropertyResource(code, key, description, value, appCode, envCode);
		}

	}

}
