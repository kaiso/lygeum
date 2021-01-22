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

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.kaiso.lygeum.core.entities.Role;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, BaseJpaRepository<Role> {

	@Query("SELECT p FROM Role p")
	Stream<Role> streamAll();

	@Modifying
	@Query(value = "DELETE FROM Role p WHERE p.code IN (:codes)")
	void deleteRolesByCode(@Param("codes") String... codes);

}
