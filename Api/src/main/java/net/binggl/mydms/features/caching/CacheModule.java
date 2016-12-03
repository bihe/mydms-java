package net.binggl.mydms.features.caching;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CacheModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Cache.class).to(GuavaCache.class).in(Scopes.SINGLETON);
	}

}
