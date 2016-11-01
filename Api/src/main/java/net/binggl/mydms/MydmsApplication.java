package net.binggl.mydms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.server.session.SessionHandler;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.DocumentConfig;
import net.binggl.mydms.features.senders.SenderConfig;
import net.binggl.mydms.features.tags.TagConfig;
import net.binggl.mydms.hibernate.MydmsHibernateBundle;
import net.binggl.mydms.hibernate.MydmsHibernateModule;
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

		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
				bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

		bootstrap.addBundle(new AssetsBundle("/assets/", "/static"));
		bootstrap.addBundle(new MultiPartBundle());

		bootstrap.addBundle(new MigrationsBundle<MydmsConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(MydmsConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});

		this.initHibernateBundel();
		// register hbn bundle before guice to make sure factory initialized
		// before guice context start
		bootstrap.addBundle(hibernate);

		bootstrap.addBundle(GuiceBundle.builder().enableAutoConfig(APP_BASE_PACKAGE).searchCommands()
				.modules(new MydmsHibernateModule(hibernate)).build());
	}

	@Override
	public void run(MydmsConfiguration configuration, Environment environment) {
		
		environment.servlets().setSessionHandler(new SessionHandler());
		//environment.jersey().register(HttpSessionProvider.class);
	}

	private void initHibernateBundel() {
		this.hibernate = new MydmsHibernateBundle(getMappedEntities());
	}

	private Class<?>[] getMappedEntities() {
		List<Class<?>> persistenceEntities = new ArrayList<>();

		// specify the available entities of the different modules/features
		Collections.addAll(persistenceEntities, TagConfig.MappedEntities);
		Collections.addAll(persistenceEntities, SenderConfig.MappedEntities);
		Collections.addAll(persistenceEntities, DocumentConfig.MappedEntities);

		Class<?>[] entities = persistenceEntities.toArray(new Class<?>[0]);
		return entities;
	}
}
