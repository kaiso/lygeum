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
package io.github.kaiso.lygeum.persistence.repositories.generic;

import io.github.kaiso.lygeum.core.entities.CommitPropertyEntity;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** @author Kais OMRI (kaiso) */
@Repository
public interface CommitPropertyRepository
    extends PagingAndSortingRepository<CommitPropertyEntity, String> {

  @Query(
      "from CommitPropertyEntity p inner join fetch p.commit c where p.envCode = :env and p.appCode = :app order by c.date asc")
  Stream<CommitPropertyEntity> findByEnvCodeAndAppCode(
      @Param("env") String environment, @Param("app") String application);

  Optional<CommitPropertyEntity> findTopByEnvCodeAndAppCodeOrderByCommitDateAsc(
      String envCode, String appCode);

  @Query(
      "select count ( distinct c.id ) from CommitPropertyEntity p inner join p.commit c where p.envCode = :env and p.appCode = :app")
  long countCommitsByEnvCodeAndAppCode(
      @Param("env") String environment, @Param("app") String application);

  @Modifying
  void deleteByCommitIdAndEnvCodeAndAppCode(
      @Param("commitId") String commit,
      @Param("env") String environment,
      @Param("app") String application);
}
