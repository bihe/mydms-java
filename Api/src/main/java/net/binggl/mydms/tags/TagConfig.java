package net.binggl.mydms.tags;

import io.dropwizard.setup.Environment;
import net.binggl.mydms.MydmsConfiguration;
import net.binggl.mydms.bootstrap.MydmsHibernateBundle;

public class TagConfig {

	private static final String HealthCheckName = "health.tags";
	
	public static Class<?>[] MappedEntities = new Class<?>[]{
		Tag.class
	};
	
	
	public static void setup(MydmsConfiguration configuration, Environment environment, MydmsHibernateBundle hibernateBundle) {
		final TagHealthCheck healthCheck = new TagHealthCheck(hibernateBundle.getSessionFactory());
		environment.healthChecks().register(HealthCheckName, healthCheck);
	}
}
