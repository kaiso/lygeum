package io.github.kaiso.lygeum.api.resources;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;

public final class PropertyMapper {

	private PropertyMapper() {
		super();
	}

	public static PropertyEntity map(PropertyResource r) {
		return PropertyEntity.builder().withCode(r.getCode()).withKey(r.getKey()).withValue(r.getValue())
				.withApplication(new ApplicationEntity(r.getAppCode(), null))
				.withEnvironment(new EnvironmentEntity(r.getEnvCode(), null)).build();
	}

	public static PropertyEntity map(PropertyResource r, ApplicationEntity app, EnvironmentEntity env) {
		return PropertyEntity.builder().withCode(r.getCode()).withKey(r.getKey()).withValue(r.getValue())
				.withApplication(app).withEnvironment(env).build();
	}

	public static PropertyResource map(PropertyEntity r) {
		return PropertyResource.builder().withCode(r.getCode()).withKey(r.getKey()).withValue(r.getValue()).build();
	}

}
