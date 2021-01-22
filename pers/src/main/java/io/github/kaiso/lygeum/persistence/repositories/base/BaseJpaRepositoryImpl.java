package io.github.kaiso.lygeum.persistence.repositories.base;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.EntityInformation;

import io.github.kaiso.lygeum.core.entities.BaseEntity;
import io.github.kaiso.lygeum.core.entities.PropertyEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;

public class BaseJpaRepositoryImpl<T extends BaseEntity> extends SimpleJpaRepository<T, Long>
		implements BaseJpaRepository<T> {

	private EntityInformation<T, ?> baseEntityInformation;
	private EntityManager baseEm;

	public BaseJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.baseEm = entityManager;
		this.baseEntityInformation = entityInformation;
	}

	@Override
	public <S extends T> S save(S entity) {
		if (entity.isNew()) {
			baseEm.persist(entity);
			return entity;
		} else {
			if (entity.getId() == null) {
				T exitingEntity = findByCode(entity.getCode()).orElseThrow(
						() -> new EntityNotFoundException("entity with code " + entity.getCode() + "was  not found"));
				entity.setId(exitingEntity.getId());
				entity.setCreatedBy(exitingEntity.getCreatedBy().orElse(null));
				entity.setCreatedDate(exitingEntity.getCreatedDate().orElse(null));
				if (entity instanceof PropertyEntity) {
					((PropertyEntity) entity).getValues().forEach(p -> {
						for (PropertyValueEntity v : ((PropertyEntity) exitingEntity).getValues()) {
							if (v.getEnvironment().equals(p.getEnvironment())) {
								p.setCode(v.getCode());
								p.setId(v.getId());
								p.setCreatedBy(v.getCreatedBy().orElse(null));
								p.setCreatedDate(v.getCreatedDate().orElse(null));
							}
						}
					});
				}
			}
			return baseEm.merge(entity);
		}
	}

	@Override
	public Optional<T> findByCode(String code) {
		return baseEm.unwrap(Session.class).bySimpleNaturalId(getDomainClass()).loadOptional(code);
	}

}
