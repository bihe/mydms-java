package net.binggl.mydms.application;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class MydmsException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	private boolean isBrowserRequest = false;

	public MydmsException(String message, Status status) {
		super(message, status);
	}

	public MydmsException(String message, int status) {
		super(message, status);
	}

	public boolean isBrowserRequest() {
		return isBrowserRequest;
	}

	public MydmsException browserRequest(boolean isBrowserRequest) {
		this.isBrowserRequest = isBrowserRequest;
		return this;
	}
}
