package io.github.kaiso.lygeum.core.spi;

import io.github.kaiso.lygeum.core.entities.Change;
import io.github.kaiso.lygeum.core.entities.CommitEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;
import java.util.List;

public interface VersionningService {

  CommitEntity commit(String envCode, String appCode, String author, List<PropertyValueEntity> entities);

  List<Change> findChangesByEnvAndApp(String env, String app);

  void keepOnlyLastbyEnvAndApp(int keepCount, String env, String app);
}
