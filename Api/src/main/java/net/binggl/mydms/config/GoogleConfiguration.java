package net.binggl.mydms.config;

import javax.validation.constraints.NotNull;

public class GoogleConfiguration {
	@NotNull
	private String clientId;
	@NotNull
	private String clientSecret;
	@NotNull
	private String redirectUrl;
	@NotNull
	private String encryptionKey;
	@NotNull
	private String storePath;
	@NotNull
	private String successUrl;
	@NotNull
	private String parentDrivePath;

	public GoogleConfiguration() {
		super();
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getParentDrivePath() {
		return parentDrivePath;
	}

	public void setParentDrivePath(String parentDrivePath) {
		this.parentDrivePath = parentDrivePath;
	}

}
