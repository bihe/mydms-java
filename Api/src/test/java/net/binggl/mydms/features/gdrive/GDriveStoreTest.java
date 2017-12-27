package net.binggl.mydms.features.gdrive;

import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore;
import net.binggl.mydms.features.gdrive.store.GDriveFileSystemEncryptionStore;
import org.junit.Assert;
import org.junit.Test;

public class GDriveStoreTest {

    @Test
    public void testEncryptionStore() {
        GDriveCredentialStore store = new GDriveFileSystemEncryptionStore("SECRET_KEY", "./target/");
        GDriveCredential credential = new GDriveCredential();
        credential.setAccessToken("ACCESS_TOKEN");
        credential.setRefreshToken("REFRRESH_TOKEN");

        store.save("USER", credential);

        GDriveCredential loaded = store.load("USER");
        Assert.assertNotNull(loaded);
        Assert.assertEquals("ACCESS_TOKEN", loaded.getAccessToken());
        Assert.assertEquals("REFRRESH_TOKEN", loaded.getRefreshToken());
    }
}
