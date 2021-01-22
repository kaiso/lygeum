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
package io.github.kaiso.lygeum.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/** @author kaiso */
@Entity
@Table(name = "LGM_COMMIT_PROPERTY")
public class CommitPropertyEntity {

  @Id private String id;

  @JoinColumn(name = "commit_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private CommitEntity commit;

  @Column(name = "appcode")
  private String appCode;

  @Column(name = "envcode")
  private String envCode;

  private String name;

  @Column(name = "pvalue")
  private String value;

  public CommitPropertyEntity() {
    super();
  }

  private CommitPropertyEntity(Builder builder) {
    this.id = builder.id;
    this.commit = builder.commit;
    this.appCode = builder.appCode;
    this.envCode = builder.envCode;
    this.name = builder.name;
    this.value = builder.value;
  }

  public String getId() {
    return id;
  }

  public CommitEntity getCommit() {
    return commit;
  }

  public String getAppCode() {
    return appCode;
  }

  public String getEnvCode() {
    return envCode;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  /**
   * Creates builder to build {@link CommitPropertyEntity}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder to build {@link CommitPropertyEntity}. */
  public static final class Builder {
    private String id;
    private CommitEntity commit;
    private String appCode;
    private String envCode;
    private String name;
    private String value;

    private Builder() {}

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withCommit(CommitEntity commit) {
      this.commit = commit;
      return this;
    }

    public Builder withAppCode(String appCode) {
      this.appCode = appCode;
      return this;
    }

    public Builder withEnvCode(String envCode) {
      this.envCode = envCode;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withValue(String value) {
      this.value = value;
      return this;
    }

    public CommitPropertyEntity build() {
      return new CommitPropertyEntity(this);
    }
  }
}
