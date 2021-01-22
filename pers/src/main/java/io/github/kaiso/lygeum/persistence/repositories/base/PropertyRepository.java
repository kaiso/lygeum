/**
* Copyright © Kais OMRI
*    
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.github.kaiso.lygeum.persistence.repositories.base;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.github.kaiso.lygeum.core.entities.PropertyEntity;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Repository
public interface PropertyRepository
		extends PagingAndSortingRepository<PropertyEntity, Long>, BaseJpaRepository<PropertyEntity> {

	Stream<PropertyEntity> findByApplicationCode(String code);

	Stream<PropertyEntity> findByEnvironmentAndApplicationNamed(String environment, String application);
	
	@Query(value = "select p from PropertyEntity p inner join p.application a on a.code=?2 left join fetch p.values q left join q.environment e where p.name=?1 ")
	Optional<PropertyEntity> findByNameAndApplicationCode(String name, String applicationCode);

	@Query(value = "select p from PropertyEntity p inner join fetch p.application a left join fetch p.values q inner join q.environment e where p.code=?1 ")
	Optional<PropertyEntity> findByCodeEager(String code);

}
