package net.binggl.mydms.features.gdrive;

import java.io.IOException;
import java.util.Collections;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.inject.Inject;

import net.binggl.mydms.config.GoogleConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;

public class GDriveClient {

	private AuthorizationCodeFlow authFlow;
	private GoogleConfiguration config;

	@Inject
	public GDriveClient(MydmsConfiguration configuration) {
		this.config = configuration.getApplication().getGoogle();

		this.authFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				JacksonFactory.getDefaultInstance(), this.config.getClientId(), this.config.getClientSecret(),
				Collections.singleton(DriveScopes.DRIVE_FILE)).setAccessType("offline").setApprovalPrompt("force")
						.build();
	}

	public String getRedirectUrl() {

		return this.authFlow.newAuthorizationUrl().setRedirectUri(this.config.getRedirectUrl()).setState("random")
				.build();
	}

	public Credential handleResponse(String code) throws IOException {
		TokenResponse response = this.authFlow.newTokenRequest(code).setRedirectUri(this.config.getRedirectUrl())
				.execute();

		return this.authFlow.createAndStoreCredential(response, "abc");
	}
}
