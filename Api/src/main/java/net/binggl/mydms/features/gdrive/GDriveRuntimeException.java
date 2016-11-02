package net.binggl.mydms.features.gdrive;

public class GDriveRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GDriveRuntimeException(Throwable EX) {
		super(EX);
	}
	
	public GDriveRuntimeException(String message, Throwable EX) {
		super(message, EX);
	}
}
