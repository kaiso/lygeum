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
package io.github.kaiso.lygeum.core.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public interface UsersManager extends UserDetailsService{

    
    public List<Role> findAllRoles();

    public User saveUser(User user);

    public List<User> findAllUsers();

    public Optional<User> findUserByCode(String code);

    public User createUser(User user);

    public void deleteUserByCode(String code);

    public List<User> findUsersByPattern(String search);
}
