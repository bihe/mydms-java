package net.binggl.mydms.features.gdrive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.inject.Inject;

import net.binggl.mydms.config.GoogleConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;

public class GDriveClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(GDriveClient.class);
	private static final String APPLICATION_NAME = "mydms-java app";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static boolean INIT = false;
	private static HttpTransport HTTP_TRANSPORT;

	private AuthorizationCodeFlow authFlow;
	private GoogleConfiguration config;
	private Drive driveService = null;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			INIT = true;
		} catch (Throwable t) {
			LOGGER.error("Could not create a trusted HTTP Transport {}", t.getMessage(), t);
		}
	}

	@Inject
	public GDriveClient(MydmsConfiguration configuration) {
		this.config = configuration.getApplication().getGoogle();

		this.authFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				JacksonFactory.getDefaultInstance(), this.config.getClientId(), this.config.getClientSecret(),
				Collections.singleton(DriveScopes.DRIVE_FILE)).setAccessType("offline").setApprovalPrompt("force")
						.build();
	}

	public String getRedirectUrl(String correlationToken) {
		checkInit();
		String url = null;
		try {
			url = this.authFlow.newAuthorizationUrl().setRedirectUri(this.config.getRedirectUrl()).setState(correlationToken)
					.build();
		} catch (Exception EX) {
			LOGGER.error("Could not get a redirect URL {}", EX.getMessage(), EX);
			throw new GDriveRuntimeException(EX);
		}
		return url;
	}

	public Credential getCredentials(String authorizationCode) {
		checkInit();
		Credential credentials = null;
		try {
			TokenResponse response = this.authFlow.newTokenRequest(authorizationCode)
					.setRedirectUri(this.config.getRedirectUrl()).execute();
			credentials = this.authFlow.createAndStoreCredential(response, "abc");

		} catch (Exception EX) {
			LOGGER.error("Could not get OAUTH credentials {}", EX.getMessage(), EX);
			throw new GDriveRuntimeException(EX);
		}
		return credentials;
	}

	public List<File> getFiles(Credential credentials) {
		this.setupDrive(credentials);
		List<File> files = new ArrayList<>();
		try {
			FileList list = this.driveService.files().list().setPageSize(10).execute();
			if (list != null) {
				files = list.getFiles();
			}

		} catch (Exception EX) {
			LOGGER.error("Could not list files {}", EX.getMessage(), EX);
			throw new GDriveRuntimeException(EX);
		}
		return files;
	}

	private void setupDrive(Credential credentials) {
		if (driveService == null) {
			LOGGER.debug("Will setup drive service!");
			this.driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
					.setApplicationName(APPLICATION_NAME).build();
		}
	}

	private static void checkInit() throws RuntimeException {
		if (!INIT) {
			throw new IllegalStateException("The drive application was not initialized properyl!");
		}
	}
}
