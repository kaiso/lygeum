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
package io.github.kaiso.lygeum.persistence.repositories;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import io.github.kaiso.lygeum.core.entities.User;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Repository
public interface UserRepository
	extends PagingAndSortingRepository<User, Long>, BaseJpaRepository<User> {

    @Query("SELECT p FROM User p")
    Stream<User> streamAll();
    
    Optional<User> findByUsername(String username);

}
