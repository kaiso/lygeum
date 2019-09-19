package io.github.kaiso.lygeum.core.entities.listener;

import javax.persistence.PrePersist;

import org.springframework.util.StringUtils;

import io.github.kaiso.lygeum.core.entities.BaseEntity;
import io.github.kaiso.lygeum.core.util.NaturalCodeGenerator;

public class EntityListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
	if (StringUtils.isEmpty(entity.getCode())) {
	    entity.setCode(NaturalCodeGenerator.getInstance().next());
	}
    }

}
