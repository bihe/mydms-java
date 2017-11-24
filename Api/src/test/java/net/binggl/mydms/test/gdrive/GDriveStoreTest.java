package net.binggl.mydms.test.gdrive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore;
import net.binggl.mydms.features.gdrive.store.GDriveFileSystemEncryptionStore;

public class GDriveStoreTest {

	@Test
	public void testEncryptionStore() {
		MydmsConfiguration config = new MydmsConfiguration();
		config.getApplication().getGoogle().setEncryptionKey("SECRET_KEY");
		config.getApplication().getGoogle().setStorePath("./target/");

		GDriveCredentialStore store = new GDriveFileSystemEncryptionStore(config);
		GDriveCredential credential = new GDriveCredential();
		credential.setAccessToken("ACCESS_TOKEN");
		credential.setRefreshToken("REFRRESH_TOKEN");

		store.save("USER", credential);

		GDriveCredential loaded = store.load("USER");
		assertNotNull(loaded);
		assertEquals("ACCESS_TOKEN", loaded.getAccessToken());
		assertEquals("REFRRESH_TOKEN", loaded.getRefreshToken());
	}
}
