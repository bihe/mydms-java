package net.binggl.mydms.features.gdrive.models;

import java.io.Serializable;

public class FilesystemCredentialHolder implements Serializable {

	private static final long serialVersionUID = -8396652276729864414L;

	private String[] holder = new String[2];

	public FilesystemCredentialHolder() {
		super();
	}

	public FilesystemCredentialHolder(String[] holder) {
		super();
		this.holder = holder;
	}

	public String[] getHolder() {
		return holder;
	}
}
