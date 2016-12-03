package net.binggl.mydms.features.caching;

import java.util.Optional;

public interface Cache {

	<T> Optional<T> get(String key);
	
	<T> void put(String key, T item);
	
	void delete(String key);
}
