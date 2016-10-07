package net.binggl.mydms.boot;

import com.google.common.collect.ImmutableList;

import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;

public abstract class MydmsHibernateBundle extends HibernateBundle<MydmsConfiguration> {

	protected MydmsHibernateBundle(Class<?>[] entities) {
		super(ImmutableList.<Class<?>>builder().add(entities).build(),
	             new SessionFactoryFactory());
	}

}
