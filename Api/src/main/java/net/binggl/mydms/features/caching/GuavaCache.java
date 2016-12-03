package net.binggl.mydms.features.caching;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

public class GuavaCache implements Cache {
	
	private com.google.common.cache.Cache<String, Object> cache = CacheBuilder.newBuilder()
		    .maximumSize(100)
		    .expireAfterWrite(3600, TimeUnit.SECONDS)
		    .build(); 
	
	

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> get(String key) {
		Optional<T> entry = Optional.empty();
		if(cache.asMap().containsKey(key)) {
			Object value = cache.asMap().get(key);
			entry = (Optional<T>) Optional.of(value);
		}
		return entry;
	}

	@Override
	public <T> void put(String key, T item) {
		cache.asMap().put(key, item);

	}

	@Override
	public void delete(String key) {
		cache.invalidate(key);
	}
}
