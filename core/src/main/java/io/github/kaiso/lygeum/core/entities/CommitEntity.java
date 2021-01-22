package io.github.kaiso.lygeum.core.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LGM_COMMIT")
public class CommitEntity {

  @Id private String id;

  @Column(name = "created_at")
  private LocalDateTime date;

  private String author;

  private String message;

  public CommitEntity() {
    super();
  }

  private CommitEntity(Builder builder) {
    this.id = builder.id;
    this.date = builder.date;
    this.author = builder.author;
    this.message = builder.commitMsg;
  }

  public String getId() {
    return id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getAuthor() {
    return author;
  }

  public String getMessage() {
    return message;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates builder to build {@link CommitEntity}.
   *
   * @return created builder public static Builder builder(){return new Builder();}
   *     <p>/** Builder to build {@link CommitEntity}.
   */
  public static final class Builder {
    private String id;
    private LocalDateTime date;
    private String author;
    private String commitMsg;

    private Builder() {}

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withDate(LocalDateTime date) {
      this.date = date;
      return this;
    }

    public Builder withAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder withCommitMsg(String commitMsg) {
      this.commitMsg = commitMsg;
      return this;
    }

    public CommitEntity build() {
      return new CommitEntity(this);
    }
  }
}
