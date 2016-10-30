package net.binggl.mydms.hibernate;

import com.google.common.collect.ImmutableList;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import net.binggl.mydms.config.MydmsConfiguration;

public class MydmsHibernateBundle extends HibernateBundle<MydmsConfiguration> {

	public MydmsHibernateBundle(Class<?>[] entities) {
		super(ImmutableList.<Class<?>>builder().add(entities).build(), new SessionFactoryFactory());
	}

	@Override
	public PooledDataSourceFactory getDataSourceFactory(MydmsConfiguration configuration) {
		return configuration.getDataSourceFactory();
	}
}
