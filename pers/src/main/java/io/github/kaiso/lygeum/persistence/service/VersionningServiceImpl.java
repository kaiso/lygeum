package io.github.kaiso.lygeum.persistence.service;

import io.github.kaiso.lygeum.core.entities.Change;
import io.github.kaiso.lygeum.core.entities.Change.ChangeRecord;
import io.github.kaiso.lygeum.core.entities.Change.ChangeType;
import io.github.kaiso.lygeum.core.entities.CommitEntity;
import io.github.kaiso.lygeum.core.entities.CommitPropertyEntity;
import io.github.kaiso.lygeum.core.entities.PropertyValueEntity;
import io.github.kaiso.lygeum.core.spi.VersionningService;
import io.github.kaiso.lygeum.core.util.NaturalCodeGenerator;
import io.github.kaiso.lygeum.persistence.repositories.generic.CommitPropertyRepository;
import io.github.kaiso.lygeum.persistence.repositories.generic.CommitRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional(value = TxType.REQUIRED)
public class VersionningServiceImpl implements VersionningService {

  private CommitPropertyRepository commitPropertyRepository;

  private CommitRepository commitRepository;

  private static final Logger logger = LoggerFactory.getLogger(VersionningServiceImpl.class);

  @Autowired
  public VersionningServiceImpl(
      CommitPropertyRepository commitPropertyRepository, CommitRepository commitRepository) {
    super();
    this.commitPropertyRepository = commitPropertyRepository;
    this.commitRepository = commitRepository;
  }

  @Override
  public CommitEntity commit(
      String env, String app, String author, List<PropertyValueEntity> entities) {

    logger.debug("Commiting changes to env {} and app {} by author {}", env, app, author);

    CommitEntity commit =
        commitRepository.save(
            CommitEntity.builder()
                .withAuthor(author)
                .withDate(LocalDateTime.now())
                .withId(NaturalCodeGenerator.getInstance().generateObjectId())
                .build());

    commitPropertyRepository.saveAll(
        entities.stream().map(e -> map(e, commit)).collect(Collectors.toList()));
    return commit;
  }

  @Override
  public void keepOnlyLastbyEnvAndApp(int keepCount, String env, String app) {
    long count = commitPropertyRepository.countCommitsByEnvCodeAndAppCode(env, app);
    int maxKeptCommits = keepCount > 5 ? keepCount : 5;
    if (maxKeptCommits < count) {
      Optional<CommitPropertyEntity> commit =
          commitPropertyRepository.findTopByEnvCodeAndAppCodeOrderByCommitDateAsc(env, app);
      commit.ifPresent(
          c ->
              commitPropertyRepository.deleteByCommitIdAndEnvCodeAndAppCode(
                  c.getCommit().getId(), env, app));
    }
  }

  /* public List<Change> findChangesByEnvAndApp(String env, String app) {
    Stream<CommitPropertyEntity> entities =
        commitPropertyRepository.findByEnvCodeAndAppCode(env, app);
    List<Change> changes = new ArrayList<>();
    List<CommitPropertyEntity> current = new ArrayList<CommitPropertyEntity>();
    List<CommitPropertyEntity> previous = new ArrayList<CommitPropertyEntity>();
    AtomicReference<String> currentCommit = new AtomicReference<String>();
    AtomicReference<String> previousCommit = new AtomicReference<String>();
    entities.forEach(
        e -> {
          if (previousCommit.get() == null) {
            previousCommit.set(e.getCommit().getId());
          }

          if (e.getCommit().getId().equals(previousCommit.get())) {
            previous.add(e);
            return;
          }

          if (currentCommit.get() == null) {
            currentCommit.set(e.getCommit().getId());
          }

          if (e.getCommit().getId().equals(currentCommit.get())) {
            current.add(e);
            return;
          }

          changes.add(createChange(previous, current));
          previous.clear();
          previous.addAll(current);
          current.clear();
          currentCommit.set(e.getCommit().getId());
          current.add(e);
        });
    if (!current.isEmpty()) {
      changes.add(createChange(previous, current));
    }
    return changes;
  }*/

  public List<Change> findChangesByEnvAndApp(String env, String app) {
    Stream<CommitPropertyEntity> entities =
        commitPropertyRepository.findByEnvCodeAndAppCode(env, app);
    List<Change> changes = new ArrayList<>();
    List<CommitPropertyEntity> previous = new ArrayList<CommitPropertyEntity>();
    AtomicReference<String> previousCommit = new AtomicReference<String>();

    entities
        .collect(
            Collectors.groupingBy(p -> Map.entry(p.getCommit().getId(), p.getCommit().getDate())))
        .entrySet().stream()
        .sorted((a, b) -> a.getKey().getValue().isAfter(b.getKey().getValue()) ? 1 : -1)
        .forEach(
            e -> {
              if (previousCommit.get() == null) {
                previousCommit.set(e.getKey().getKey());
                previous.addAll(e.getValue());
              } else {
                changes.add(createChange(previous, e.getValue()));
                previous.clear();
                previous.addAll(e.getValue());
                previousCommit.set(e.getKey().getKey());
              }
            });

    return changes;
  }

  private Change createChange(
      List<CommitPropertyEntity> previous, List<CommitPropertyEntity> current) {

    Change c =
        Change.builder()
            .withAuthor(current.get(0).getCommit().getAuthor())
            .withCommit(current.get(0).getCommit().getId())
            .withDate(current.get(0).getCommit().getDate())
            .build();

    List<String> previousKeys = new ArrayList<>();

    for (CommitPropertyEntity previousProp : previous) {
      CommitPropertyEntity currentProp = null;
      previousKeys.add(previousProp.getName());
      for (CommitPropertyEntity it : current) {
        if (previousProp.getName().equals(it.getName())) {
          currentProp = it;
          break;
        }
      }

      if (currentProp == null) {
        c.addRecord(
            ChangeRecord.builder()
                .withKey(previousProp.getName())
                .withValue(previousProp.getValue())
                .withType(ChangeType.DELETED)
                .build());
      } else if (currentProp.getValue().trim().equals(previousProp.getValue().trim())) {
        continue;
      } else {
        c.addRecord(
            ChangeRecord.builder()
                .withKey(currentProp.getName())
                .withValue(currentProp.getValue())
                .withType(ChangeType.UPDATED)
                .build());
      }
    }

    c.getChanges()
        .addAll(
            current
                .stream()
                .filter(a -> !previousKeys.contains(a.getName()))
                .map(
                    n ->
                        ChangeRecord.builder()
                            .withKey(n.getName())
                            .withValue(n.getValue())
                            .withType(ChangeType.ADDED)
                            .build())
                .collect(Collectors.toList()));

    return c;
  }

  private CommitPropertyEntity map(PropertyValueEntity v, CommitEntity c) {
    return CommitPropertyEntity.builder()
        .withId(NaturalCodeGenerator.getInstance().generateObjectId())
        .withCommit(c)
        .withAppCode(v.getProperty().getApplication().getCode())
        .withEnvCode(v.getEnvironment().getCode())
        .withName(v.getProperty().getName())
        .withValue(v.getValue())
        .build();
  }
}
