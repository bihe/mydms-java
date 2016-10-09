package net.binggl.mydms.boot;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.SessionFactoryHealthCheck;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.Example.ExampleHealthCheck;
import net.binggl.mydms.Example.ExampleResource;
import net.binggl.mydms.Example.FooDao;
import net.binggl.mydms.Example.PersonDAO;
import net.binggl.mydms.tags.TagConfiguration;

public class MydmsApplication extends Application<MydmsConfiguration> {
	
	private final MydmsHibernateBundle hibernateBundle =
	    new MydmsHibernateBundle(Database.MappedEntities) {
	        @Override
	        public DataSourceFactory getDataSourceFactory(MydmsConfiguration configuration) {
	            return configuration.getDataSourceFactory();
	        }
	    };
	
	public static void main(String[] args) throws Exception {
		new MydmsApplication().run(args);
	}

	@Override
	public String getName() {
		return "mydms";
	}

	@Override
	public void initialize(Bootstrap<MydmsConfiguration> bootstrap) {
		
		bootstrap.addBundle(new AssetsBundle("/assets/", "/static"));
        bootstrap.addBundle(new MigrationsBundle<MydmsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(MydmsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(MydmsConfiguration configuration, Environment environment) {

		final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
		final FooDao fooDao = new FooDao(hibernateBundle.getSessionFactory());
		
		final ExampleResource resource = new ExampleResource(configuration.getTemplate(),
				configuration.getDefaultName(), dao, fooDao);
		environment.jersey().register(resource);

		final ExampleHealthCheck healthCheck = new ExampleHealthCheck(configuration.getTemplate());
		environment.healthChecks().register("template", healthCheck);
		
		
		
		final SessionFactoryHealthCheck databaseHealthCheck = new SessionFactoryHealthCheck(hibernateBundle.getSessionFactory(), "SELECT 1");
		environment.healthChecks().register("database", databaseHealthCheck);
		
		
		TagConfiguration.setup(configuration, environment, hibernateBundle);
	}
}