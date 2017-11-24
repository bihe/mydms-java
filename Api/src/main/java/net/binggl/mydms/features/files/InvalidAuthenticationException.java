package net.binggl.mydms.features.files;

public class InvalidAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidAuthenticationException(String message) {
		super(message);
	}
	
	public InvalidAuthenticationException(Throwable EX) {
		super(EX);
	}
	
	public InvalidAuthenticationException(String message, Throwable EX) {
		super(message, EX);
	}
}
