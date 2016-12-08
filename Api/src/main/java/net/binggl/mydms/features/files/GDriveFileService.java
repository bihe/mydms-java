package net.binggl.mydms.features.files;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.binggl.mydms.application.Globals;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.gdrive.client.GDriveClient;
import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.gdrive.models.GDriveItem;
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore;

public class GDriveFileService implements FileService, Globals {

	private static final Logger LOGGER = LoggerFactory.getLogger(GDriveFileService.class);

	private GDriveClient client;
	private GDriveCredentialStore store;
	private MydmsConfiguration configuration;

	@Inject
	public GDriveFileService(GDriveClient client, GDriveCredentialStore store, MydmsConfiguration configuration) {
		this.client = client;
		this.store = store;
		this.configuration = configuration;
	}

	@Override
	public boolean saveFile(FileItem file) {
		boolean result = false;
		try {
			if (!this.store.isCredentialAvailable(USER_TOKEN)) {
				throw new InvalidAuthenticationException("No google credentials are available/account is not linked!");
			}

			String folderName = file.getFolderName();

			Optional<GDriveItem> folder = this.client.getFolder(cred(), folderName,
					configuration.getApplication().getGoogle().getParentDrivePath());

			if (!folder.isPresent()) {
				LOGGER.debug("Create new folder: " + folderName);

				GDriveItem item = this.client.createFolder(cred(), folderName,
						configuration.getApplication().getGoogle().getParentDrivePath());
				LOGGER.debug("Created item {}", item);
				folder = Optional.of(item);
			}

			GDriveItem item = this.client.saveItem(cred(), file.getFileName(), file.getMimeType(),
					file.getPayload(), folder.get().getId());

			LOGGER.debug("Uploaded file {}", item);
			
			result = true;

			
		} catch (Exception EX) {
			LOGGER.error("Could not save file in backend: {}!", EX.getMessage(), EX);
			throw new RuntimeException(EX);
		}
		
		return result;
	}

	private GDriveCredential cred() {
		GDriveCredential credentials = this.store.load(USER_TOKEN);
		LOGGER.debug("Got credentials: {}", credentials);
		return credentials;
	}

}
