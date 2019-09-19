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
package io.github.kaiso.lygeum.core.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.kaiso.lygeum.core.entities.listener.EntityListener;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class, EntityListener.class })
public class BaseEntity implements Auditable<String, Long, LocalDateTime> {

    @JsonIgnore
    @Id
    @SequenceGenerator(name = "seq", sequenceName = "LGM_SEQUENCE", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    @NaturalId
    private String code;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public Optional<String> getCreatedBy() {
	return Optional.ofNullable(createdBy);
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    public Optional<LocalDateTime> getCreatedDate() {
	return null == createdDate ? Optional.empty() : Optional.of(createdDate);
    }

    public void setCreatedDate(LocalDateTime createdDate) {
	if (createdDate == null) {
	    return;
	}
	this.createdDate = createdDate.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
		.toLocalDateTime();
    }

    public Optional<String> getLastModifiedBy() {
	return Optional.ofNullable(lastModifiedBy);
    }

    public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
    }

    public Optional<LocalDateTime> getLastModifiedDate() {
	return null == lastModifiedDate ? Optional.empty() : Optional.of(lastModifiedDate);
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
	if (lastModifiedDate == null) {
	    return;
	}
	this.lastModifiedDate = lastModifiedDate.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
		.toLocalDateTime();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isNew() {
	return null == getId() && StringUtils.isEmpty(getCode());
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	BaseEntity other = (BaseEntity) obj;
	if (code == null) {
	    if (other.code != null)
		return false;
	} else if (!code.equals(other.code)) {
	    return false;
	}
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id)) {
	    return false;
	}
	return true;
    }

}
