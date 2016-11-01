package net.binggl.mydms.config;

import javax.validation.constraints.NotNull;

public class GoogleConfiguration {
	@NotNull
	private String clientId;
	@NotNull
	private String clientSecret;
	@NotNull
	private String redirectUrl;

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

}
