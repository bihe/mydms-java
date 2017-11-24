package net.binggl.mydms.features.files;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class FileServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FileService.class).to(GDriveFileService.class).in(Scopes.SINGLETON);
	}
}
