package io.github.kaiso.lygeum.persistence.repositories.generic;

import io.github.kaiso.lygeum.core.entities.CommitEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends PagingAndSortingRepository<CommitEntity, String> {}
