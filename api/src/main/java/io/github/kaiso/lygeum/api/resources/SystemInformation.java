package io.github.kaiso.lygeum.api.resources;

public class SystemInformation {

	private String jvm;
	private String database;
	private String version;

	public String getJvm() {
		return jvm;
	}

	public String getDatabase() {
		return database;
	}

	public String getVersion() {
		return version;
	}

	private SystemInformation(Builder builder) {
		this.jvm = builder.jvm;
		this.database = builder.database;
		this.version = builder.version;
	}

	/**
	 * Creates builder to build {@link SystemInformation}.
	 * 
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link SystemInformation}.
	 */
	public static final class Builder {
		private String jvm;
		private String database;
		private String version;

		private Builder() {
		}

		public Builder withJvm(String jvm) {
			this.jvm = jvm;
			return this;
		}

		public Builder withDatabase(String database) {
			this.database = database;
			return this;
		}

		public Builder withVersion(String version) {
			this.version = version;
			return this;
		}

		public SystemInformation build() {
			return new SystemInformation(this);
		}
	}

}
