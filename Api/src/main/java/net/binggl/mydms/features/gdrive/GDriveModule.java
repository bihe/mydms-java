package net.binggl.mydms.features.gdrive;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore;
import net.binggl.mydms.features.gdrive.store.GDriveFileSystemEncryptionStore;

public class GDriveModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(GDriveCredentialStore.class).to(GDriveFileSystemEncryptionStore.class).in(Scopes.SINGLETON);
	}

}
