package net.binggl.mydms.features.gdrive.store;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import net.binggl.mydms.config.GoogleConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.gdrive.GDriveRuntimeException;
import net.binggl.mydms.features.gdrive.models.FilesystemCredentialHolder;
import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.shared.crypto.AesEncryption;

public class GDriveFileSystemEncryptionStore implements GDriveCredentialStore {

	private GoogleConfiguration config;
	private static final Logger LOGGER = LoggerFactory.getLogger(GDriveFileSystemEncryptionStore.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final AesEncryption CRYPTO = new AesEncryption();

	@Inject
	public GDriveFileSystemEncryptionStore(MydmsConfiguration config) {
		this.config = config.getApplication().getGoogle();
	}

	@Override
	public void save(String userToken, GDriveCredential credential) {
		try {

			String[] payload = new String[2];

			String crypto0 = CRYPTO.encrypt(config.getEncryptionKey(), credential.getAccessToken());
			String crypto1 = CRYPTO.encrypt(config.getEncryptionKey(), credential.getRefreshToken());

			payload[0] = crypto0;
			payload[1] = crypto1;

			FilesystemCredentialHolder holder = new FilesystemCredentialHolder(payload);

			String payloadString = MAPPER.writeValueAsString(holder);
			String encodedPayloadString = Base64.encodeBase64String(payloadString.getBytes(StandardCharsets.UTF_8));

			FileUtils.writeStringToFile(this.getFilePath(userToken), encodedPayloadString, StandardCharsets.UTF_8);

		} catch (Exception EX) {
			LOGGER.error("Could not persist the credentials for user {}, {}", userToken, EX.getMessage(), EX);
			throw new GDriveRuntimeException("Could not persist the credentials.", EX);
		}
	}

	@Override
	public GDriveCredential load(String userToken) {
		GDriveCredential credentials = null;
		try {

			String filePayload = FileUtils.readFileToString(this.getFilePath(userToken), StandardCharsets.UTF_8);
			byte[] decodedPayload = Base64.decodeBase64(filePayload);
			FilesystemCredentialHolder holder = MAPPER.readValue(decodedPayload, FilesystemCredentialHolder.class);
			credentials = new GDriveCredential();
			credentials.setAccessToken(CRYPTO.decryptAsString(config.getEncryptionKey(), holder.getHolder()[0]));
			credentials.setRefreshToken(CRYPTO.decryptAsString(config.getEncryptionKey(), holder.getHolder()[1]));

		} catch (Exception EX) {
			LOGGER.error("Could not restore the credentials for user {}, {}", userToken, EX.getMessage(), EX);
			throw new GDriveRuntimeException("Could not restore the credentials.", EX);
		}
		return credentials;
	}

	@Override
	public void clearCredentials(String userToken) {
		try {

			File file = this.getFilePath(userToken);
			if(file.exists()) {
				file.delete();
			}

		} catch (Exception EX) {
			LOGGER.error("Could not clear the credentials for user {}, {}", userToken, EX.getMessage(), EX);
			throw new GDriveRuntimeException("Could not clear the credentials.", EX);
		}
	}

	@Override
	public boolean isCredentialAvailable(String userToken) {
		boolean isAvailable = false;
		try {
			File file = this.getFilePath(userToken);
			if(file.exists()) {
				GDriveCredential cred = this.load(userToken);
				if(cred != null)
					isAvailable = true;
			}
		} catch(Exception EX) {
			LOGGER.error("Could not check the credentials for user {}, {}", userToken, EX.getMessage(), EX);
			throw new GDriveRuntimeException("Could not check the credentials.", EX);
		}
		
		return isAvailable;
	}

	private File getFilePath(String userToken) {
		Path filePath = FileSystems.getDefault().getPath(config.getStorePath(), String.format("%s.cred", userToken));
		return filePath.toFile();
	}
}
