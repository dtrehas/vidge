package org.vidge.status;


public class PropertyStatus {

	private final FormStatus status;
	private final String message;

	public PropertyStatus(FormStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public PropertyStatus(boolean valid, String helpMessage) {
		this(valid ? FormStatus.VALID : FormStatus.INVALID, helpMessage);
	}

	public FormStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public boolean isValid() {
		return status.equals(FormStatus.VALID);
	}
}