package net.binggl.mydms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.bootstrap.MydmsHibernateBundle;
import net.binggl.mydms.bootstrap.MydmsHibernateModule;
import net.binggl.mydms.tags.TagConfig;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public final class MydmsApplication extends Application<MydmsConfiguration> {
	
	private static final String APP_BASE_PACKAGE = "net.binggl.mydms";
	
	private MydmsHibernateBundle hibernate = null;
	
	
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
		
		this.initHibernateBundel();
        // register hbn bundle before guice to make sure factory initialized before guice context start
        bootstrap.addBundle(hibernate);
        
        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(APP_BASE_PACKAGE)
                .modules(new MydmsHibernateModule(hibernate))
                .build());
	}

	@Override
	public void run(MydmsConfiguration configuration, Environment environment) {

//		final SessionFactoryHealthCheck databaseHealthCheck = new SessionFactoryHealthCheck(hibernateBundle.getSessionFactory(), "SELECT 1");
//		environment.healthChecks().register("database", databaseHealthCheck);
//		
//		
		TagConfig.setup(configuration, environment, this.hibernate);
	}
	
	
	
	
	private void initHibernateBundel() {
		this.hibernate = new MydmsHibernateBundle(getMappedEntities());
	}
	
	private Class<?>[] getMappedEntities() {
		List<Class<?>> persistenceEntities = new ArrayList<>(); 
		
		// specify the available entities of the different modules/features
		Collections.addAll(persistenceEntities, TagConfig.MappedEntities);
		
		Class<?>[] entities = persistenceEntities.toArray(new Class<?>[0]);
		return entities;
	}
	
}