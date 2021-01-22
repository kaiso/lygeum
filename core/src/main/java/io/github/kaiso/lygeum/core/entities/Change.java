package io.github.kaiso.lygeum.core.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Change {

  private String commit;
  private String author;
  private LocalDateTime date;
  private List<ChangeRecord> changes = new ArrayList<>();

  private Change(Builder builder) {
    this.commit = builder.commit;
    this.author = builder.author;
    this.date = builder.date;
  }

  public String getCommit() {
    return commit;
  }

  public String getAuthor() {
    return author;
  }

  public LocalDateTime getDate() {
  return date;}

  public void addRecord(ChangeRecord r) {
    this.changes.add(r);
  }

  public List<ChangeRecord> getChanges() {
    return changes;
  }

  public static class ChangeRecord {
    private String key;
    private String value;
    private ChangeType type;

    private ChangeRecord(Builder builder) {
      this.key = builder.key;
      this.value = builder.value;
      this.type = builder.type;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    public ChangeType getType() {
      return type;
    }

    /**
     * Creates builder to build {@link ChangeRecord}.
     *
     * @return created builder
     */
    public static Builder builder() {
      return new Builder();
    }
    /** Builder to build {@link ChangeRecord}. */
    public static final class Builder {
      private String key;
      private String value;
      private ChangeType type;

      private Builder() {}

      public Builder withKey(String key) {
        this.key = key;
        return this;
      }

      public Builder withValue(String value) {
        this.value = value;
        return this;
      }

      public Builder withType(ChangeType type) {
        this.type = type;
        return this;
      }

      public ChangeRecord build() {
        return new ChangeRecord(this);
      }
    }

    @Override
    public String toString() {
      return "ChangeRecord [key=" + key + ", value=" + value + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ChangeRecord other = (ChangeRecord) obj;
      if (key == null) {
        if (other.key != null) return false;
      } else if (!key.equals(other.key)) return false;
      if (type != other.type) return false;
      if (value == null) {
        if (other.value != null) return false;
      } else if (!value.equals(other.value)) return false;
      return true;
    }
  }

  public static enum ChangeType {
    ADDED,
    DELETED,
    UPDATED,
  }

  /**
   * Creates builder to build {@link Change}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder to build {@link Change}. */
  public static final class Builder {
    private String commit;
    private String author;
    private LocalDateTime date;

    private Builder() {}

    public Builder withCommit(String commit) {
      this.commit = commit;
      return this;
    }

    public Builder withAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder withDate(LocalDateTime date) {
      this.date = date;
      return this;
    }

    public Change build() {
      return new Change(this);
    }
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Commit ");
    sb.append(commit);
    sb.append(" by ");
    sb.append(author);
    sb.append(" at ");
    sb.append(date);
    if (changes != null) {

      sb.append("\nchanges= {\n\t");
      sb.append(changes.stream().map(Object::toString).collect(Collectors.joining("\n\t")));
      sb.append("\n\t}");
    }
    ;

    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((author == null) ? 0 : author.hashCode());
    result = prime * result + ((changes == null) ? 0 : changes.hashCode());
    result = prime * result + ((commit == null) ? 0 : commit.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Change other = (Change) obj;
    if (author == null) {
      if (other.author != null) return false;
    } else if (!author.equals(other.author)) return false;
    if (changes == null) {
      if (other.changes != null) return false;
    } else if (!changes.equals(other.changes)) return false;
    if (commit == null) {
      if (other.commit != null) return false;
    } else if (!commit.equals(other.commit)) return false;
    if (date == null) {
      if (other.date != null) return false;
    } else if (!date.equals(other.date)) return false;
    return true;
  }
}
