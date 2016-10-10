package net.binggl.mydms.bootstrap;

import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;

/**
 * Guice module for {@link SessionFactory} binding.
 *
 * @author Vyacheslav Rusakov
 * @since 12.06.2016
 */
public class MydmsHibernateModule extends AbstractModule {

    private final MydmsHibernateBundle hbnBundle;

    public MydmsHibernateModule(MydmsHibernateBundle hbnBundle) {
        this.hbnBundle = hbnBundle;
    }

    @Override
    protected void configure() {
        // if hibernate bundle was registered before guice, then at this point it's run method
        // will be already called and so its safe to get session factory instance
        bind(SessionFactory.class).toInstance(hbnBundle.getSessionFactory());
    }
}