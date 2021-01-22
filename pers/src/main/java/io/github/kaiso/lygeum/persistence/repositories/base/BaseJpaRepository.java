package io.github.kaiso.lygeum.persistence.repositories.base;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.github.kaiso.lygeum.core.entities.BaseEntity;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

	Optional<T> findByCode(String code);

}
