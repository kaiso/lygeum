package io.github.kaiso.lygeum.api.resources;

public class OperationResult {

	public enum OperationResultCode {
		SUCCESS("0_SUCCESS"), VALIDATION_FAILURE(
				"1_VALIDATION_FAILURE"), INTERNAL_ERROR("2_INTERNAL_ERROR");

		private String code;

		OperationResultCode(String code) {
			this.code = code;
		}

		public String code() {
			return this.code;
		}
		
	}

	private String message;
	private String code;

	private OperationResult(Builder builder) {
		this.message = builder.message;
		this.code = builder.code;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	/**
	 * Creates builder to build {@link OperationResult}.
	 * 
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link OperationResult}.
	 */
	public static final class Builder {
		private String message;
		private String code;
		private Builder() {
		}
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}
		public Builder withCode(OperationResultCode code) {
			this.code = code.code();
			return this;
		}
		public OperationResult build() {
			return new OperationResult(this);
		}
	}

}
