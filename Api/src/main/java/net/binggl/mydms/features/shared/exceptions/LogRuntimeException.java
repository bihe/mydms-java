package net.binggl.mydms.features.shared.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * throw this exception if you just can't handle the error, or there is no know
 * resolution available
 * http://googletesting.blogspot.co.at/2009/09/checked-exceptions-i-love-you-but-you.html
 * @author henrik
 */
public class LogRuntimeException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(LogRuntimeException.class);
	private static final long serialVersionUID = 1L;

	public LogRuntimeException() {
		logger.error("Got an exception");
	}

	public LogRuntimeException(String message) {
		super(message);
		logger.error("Got an exception, message {}", message);
	}

	public LogRuntimeException(Throwable cause) {
		super(cause);
		logger.error("Got exception, cause {}", cause);
	}
	
	public LogRuntimeException(Exception exception) {
		super(exception);
		logger.error("Got exception {}, stack {}", exception.getMessage(), exception);
	}

	public LogRuntimeException(String message, Throwable cause) {
		super(message, cause);
		logger.error("Got exception, message: {}, cause: {}", message, cause);
	}

	public LogRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		logger.error("Got exception, message: {}, cause: {}", message, cause);
	}
}