package org.vidge;

public class VidgeException extends Exception {

	private static final long serialVersionUID = -2237830481811463805L;

	public VidgeException() {
		super();
	}

	public VidgeException(String message, Throwable cause) {
		super(message, cause);
	}

	public VidgeException(String message) {
		super(message);
	}

	public VidgeException(Throwable cause) {
		super(cause);
	}
}
