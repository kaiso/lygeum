/**
 * Copyright © Kais OMRI
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.io.kaiso.lygeum.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import io.github.kaiso.lygeum.core.entities.ApplicationEntity;
import io.github.kaiso.lygeum.core.entities.Change;
import io.github.kaiso.lygeum.core.entities.Change.ChangeType;
import io.github.kaiso.lygeum.core.entities.CommitEntity;
import io.github.kaiso.lygeum.core.entities.EnvironmentEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;
import io.github.kaiso.lygeum.core.spi.VersionningService;
import test.io.kaiso.lygeum.persistence.config.BasePersistenceTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/** @author Kais OMRI (kaiso) */
@Rollback(true)
@Transactional
public class VersionningTest extends BasePersistenceTest {

  @Autowired private VersionningService versionningService;

  private static final String AUTHOR = "lygeum";

  @BeforeEach
  public void setUp() {}

  @AfterEach
  public void tearDown() {}

  @Test
  public void should_commit_properties() {

    ApplicationEntity app = new ApplicationEntity();
    app.setDescription("application 1");
    app.setName("APPONE");
    app.setCode("APPCODE");

    EnvironmentEntity env = new EnvironmentEntity();
    env.setName("ENVONE");
    env.setDescription("environment 1");
    env.setCode("ENVCODE");

    PropertyValueEntity v0 = createPropertyValue(app, env, "key0", "value0");
    PropertyValueEntity v1 = createPropertyValue(app, env, "key1", "value1");
    PropertyValueEntity v2 = createPropertyValue(app, env, "key2", "value2");
    versionningService.commit(env.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v1, v2));

    PropertyValueEntity v3 = createPropertyValue(app, env, "key1", "value12");
    PropertyValueEntity v4 = createPropertyValue(app, env, "key3", "value3");
    versionningService.commit(env.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v4));

    PropertyValueEntity v5 = createPropertyValue(app, env, "key4", "value4");
    PropertyValueEntity v6 = createPropertyValue(app, env, "key3", "value31");
    versionningService.commit(env.getCode(), app.getCode(), AUTHOR, Arrays.asList(v5, v6));

    List<Change> changes = versionningService.findChangesByEnvAndApp(env.getCode(), app.getCode());

    assertThat(changes.size()).isEqualTo(2);
    assertThat(changes)
        .anySatisfy(
            a ->
                assertThat(a.getChanges())
                    .extracting("key", "value", "type")
                    .contains(
                        tuple("key1", "value12", ChangeType.UPDATED),
                        tuple("key2", "value2", ChangeType.DELETED),
                        tuple("key3", "value3", ChangeType.ADDED)))
        .anySatisfy(
            a ->
                assertThat(a.getChanges())
                    .extracting("key", "value", "type")
                    .contains(
                        tuple("key0", "value0", ChangeType.DELETED),
                        tuple("key1", "value12", ChangeType.DELETED),
                        tuple("key3", "value31", ChangeType.UPDATED),
                        tuple("key4", "value4", ChangeType.ADDED)));

    changes.forEach(
        e -> {
          System.out.println(e.toString());
        });
  }

  @Test
  public void should_keep_atleast_5_commits() {

    ApplicationEntity app = new ApplicationEntity();
    app.setDescription("application 1");
    app.setName("APPONE");
    app.setCode("APPCODE");

    EnvironmentEntity env = new EnvironmentEntity();
    env.setName("ENVONE");
    env.setDescription("environment 1");
    env.setCode("ENVCODE");

    EnvironmentEntity env2 = new EnvironmentEntity();
    env2.setName("ENVTWO");
    env2.setDescription("environment 2");
    env2.setCode("ENV2CODE");

    PropertyValueEntity v0 = createPropertyValue(app, env, "key0", "value0");
    PropertyValueEntity v1 = createPropertyValue(app, env, "key1", "value1");
    PropertyValueEntity v2 = createPropertyValue(app, env, "key2", "value2");
    versionningService.commit(env.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v1, v2));

    PropertyValueEntity v3 = createPropertyValue(app, env2, "key1", "value12");
    PropertyValueEntity v4 = createPropertyValue(app, env2, "key3", "value3");
    versionningService.commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v4));

    PropertyValueEntity v5 = createPropertyValue(app, env2, "key3", "value4");
    versionningService.commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v5));

    PropertyValueEntity v6 = createPropertyValue(app, env2, "key3", "value5");
    versionningService.commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v6));

    PropertyValueEntity v7 = createPropertyValue(app, env2, "key3", "value6");
    versionningService.commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v7));

    List<Change> changes1 = versionningService.findChangesByEnvAndApp(env.getCode(), app.getCode());

    versionningService.keepOnlyLastbyEnvAndApp(3, env.getCode(), app.getCode());

    List<Change> changes2 = versionningService.findChangesByEnvAndApp(env.getCode(), app.getCode());

    assertThat(changes1.size()).isEqualTo(changes2.size());
    assertThat(changes1).containsAll(changes2);
  }

  @Test
  public void should_keeplastbyEnvAndApp() {

    ApplicationEntity app = new ApplicationEntity();
    app.setDescription("application 1");
    app.setName("APPONE");
    app.setCode("APPCODE");

    EnvironmentEntity env = new EnvironmentEntity();
    env.setName("ENVONE");
    env.setDescription("environment 1");
    env.setCode("ENVCODE");

    EnvironmentEntity env2 = new EnvironmentEntity();
    env2.setName("ENVTWO");
    env2.setDescription("environment 2");
    env2.setCode("ENV2CODE");

    List<String> remainingCommitIds = new ArrayList<>();

    PropertyValueEntity v0 = createPropertyValue(app, env, "key0", "value0");
    PropertyValueEntity v1 = createPropertyValue(app, env, "key1", "value1");
    PropertyValueEntity v2 = createPropertyValue(app, env, "key2", "value2");
    CommitEntity firstCommit =
        versionningService.commit(env.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v1, v2));

    PropertyValueEntity v3 = createPropertyValue(app, env2, "key1", "value12");
    PropertyValueEntity v4 = createPropertyValue(app, env2, "key3", "value3");
    remainingCommitIds.add(
        versionningService
            .commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v4))
            .getId());

    PropertyValueEntity v5 = createPropertyValue(app, env2, "key3", "value4");
    remainingCommitIds.add(
        versionningService
            .commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v5))
            .getId());

    PropertyValueEntity v6 = createPropertyValue(app, env2, "key3", "value5");
    remainingCommitIds.add(
        versionningService
            .commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v6))
            .getId());

    PropertyValueEntity v7 = createPropertyValue(app, env2, "key3", "value6");
    remainingCommitIds.add(
        versionningService
            .commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v7))
            .getId());

    PropertyValueEntity v8 = createPropertyValue(app, env2, "key3", "value6");
    remainingCommitIds.add(
        versionningService
            .commit(env2.getCode(), app.getCode(), AUTHOR, Arrays.asList(v0, v3, v8))
            .getId());

    List<Change> changes1 = versionningService.findChangesByEnvAndApp(env.getCode(), app.getCode());

    versionningService.keepOnlyLastbyEnvAndApp(3, env.getCode(), app.getCode());

    List<Change> changes2 = versionningService.findChangesByEnvAndApp(env.getCode(), app.getCode());

    assertThat(changes1.size()).isEqualTo(changes2.size() + 1);
    assertThat(changes1).containsAll(changes2);
    assertThat(changes2)
        .extracting("commit")
        .allMatch(c -> !c.equals(firstCommit.getId()) && remainingCommitIds.contains(c));
  }

  private PropertyValueEntity createPropertyValue(
      ApplicationEntity app, EnvironmentEntity env, String name, String value) {
    PropertyValueEntity v =
        PropertyValueEntity.builder()
            .withEnvironment(env)
            .withProperty(PropertyEntity.builder().withApplication(app).withName(name).build())
            .withValue(value)
            .build();
    return v;
  }
}
