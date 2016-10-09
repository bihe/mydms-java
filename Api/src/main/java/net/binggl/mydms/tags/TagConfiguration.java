package net.binggl.mydms.tags;

import io.dropwizard.setup.Environment;
import net.binggl.mydms.boot.MydmsConfiguration;
import net.binggl.mydms.boot.MydmsHibernateBundle;

public class TagConfiguration {
	
	public static void setup(MydmsConfiguration configuration, Environment environment, MydmsHibernateBundle hibernateBundle) {

		final TagDao dao = new TagDao(hibernateBundle.getSessionFactory());
		
		final TagResource resource = new TagResource(dao);
		environment.jersey().register(resource);

		final TagHealthCheck healthCheck = new TagHealthCheck(hibernateBundle.getSessionFactory());
		environment.healthChecks().register("tags", healthCheck);
	}
}
