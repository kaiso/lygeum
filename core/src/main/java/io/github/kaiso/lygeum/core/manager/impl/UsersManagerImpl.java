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
package io.github.kaiso.lygeum.core.manager.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.github.kaiso.lygeum.core.entities.Role;
import io.github.kaiso.lygeum.core.entities.User;
import io.github.kaiso.lygeum.core.manager.UsersManager;
import io.github.kaiso.lygeum.core.security.LygeumPasswordEncoder;
import io.github.kaiso.lygeum.core.spi.StorageService;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@Service
public class UsersManagerImpl implements UsersManager {

    private StorageService storageService;

    private LygeumPasswordEncoder passwordEncoder;

    @Autowired
    public UsersManagerImpl(StorageService storageService, LygeumPasswordEncoder passwordEncoder) {
	this.storageService = storageService;
	this.passwordEncoder = passwordEncoder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
	return storageService.findUserByUsername(username)
		.orElseThrow(() -> new UsernameNotFoundException("user not found with name " + username));
    }

    @Override
    public List<Role> findAllRoles() {
	return storageService.findAllRoles();
    }

    @Override
    public User createUser(User user) {
	return storageService.saveUser(user);
    }

    @Override
    public User saveUser(User user) {
	User existingUser = findUserByCode(user.getCode())
		.orElseThrow(() -> new IllegalArgumentException("User can not be found with code" + user.getCode()));
	
	user.setCreatedBy(existingUser.getCreatedBy().get());
	user.setCreatedDate(existingUser.getCreatedDate().get());
	user.setId(existingUser.getId());
	if (!StringUtils.isEmpty(user.getPassword())) {
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	}

	return storageService.saveUser(user);
    }

    @Override
    public List<User> findAllUsers() {
	return storageService.findAllUsers();
    }

    @Override
    public Optional<User> findUserByCode(String code) {
	return storageService.findUserByCode(code);
    }

    @Override
    public void deleteUserByCode(String code) {
         storageService.deleteUserByCode(code);	
    }

}
