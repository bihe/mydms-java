package net.binggl.mydms.test.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import net.binggl.mydms.features.caching.Cache;
import net.binggl.mydms.features.caching.GuavaCache;
import net.binggl.mydms.features.shared.models.ActionResult;
import net.binggl.mydms.features.shared.models.SimpleResult;

public class CacheTest {

	private static Cache cache;
	
	@BeforeClass
	public static void beforeTest() {
		cache = new GuavaCache();
	}
	
	@Test
	public void cacheTests() {
		SimpleResult result = new SimpleResult("Message", ActionResult.Created);
		
		cache.put("KEY", result);
		
		Optional<SimpleResult> fromCache = cache.get("KEY");
		assertTrue(fromCache.isPresent());
		assertEquals(fromCache.get().getMessage(), "Message");
		assertEquals(fromCache.get().getResult(), ActionResult.Created);
		
		cache.delete("KEY");
		
		fromCache = cache.get("KEY");
		assertFalse(fromCache.isPresent());
	}
}
