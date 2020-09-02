package io.github.kaiso.lygeum.api.resources;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;

import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;

public final class PropertyMapper {

  private PropertyMapper() {
    super();
  }

  public static PropertyEntity map(PropertyResource r) {
    return PropertyEntity.builder()
        .withCode(r.getCode())
        .withName(r.getName())
        .withValue(r.getValue())
        .withApplication(new ApplicationEntity(r.getAppCode(), null))
        .withEnvironment(new EnvironmentEntity(r.getEnvCode(), null))
        .build();
  }

  public static PropertyEntity map(
      PropertyResource r, ApplicationEntity app, EnvironmentEntity env) {
    return PropertyEntity.builder()
        .withCode(r.getCode())
        .withName(r.getName())
        .withValue(r.getValue())
        .withApplication(app)
        .withEnvironment(env)
        .build();
  }

  public static PropertyResource map(PropertyEntity r) {
    return PropertyResource.builder()
        .withCode(r.getCode())
        .withName(r.getName())
        .withValue(r.getValue())
        .withLastModifiedInformation(mapLastModifiedInformation(r))
        .build();
  }

  private static Entry<String, LocalDateTime> mapLastModifiedInformation(PropertyEntity r) {
    String user = r.getLastModifiedBy().orElse("");
    LocalDateTime date = r.getLastModifiedDate().isPresent() ? r.getLastModifiedDate().get() : null;

    if (!r.getValues().isEmpty()) {
      PropertyValueEntity value = r.getValues().iterator().next();
      if (value.getLastModifiedDate().isPresent()
          && (date != null || value.getLastModifiedDate().get().isAfter(date))) {
        date = value.getLastModifiedDate().get();
        user = value.getLastModifiedBy().orElse("");
      }
    }

    return new AbstractMap.SimpleEntry<>(user,date);
  }
}
