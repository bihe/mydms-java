package net.binggl.mydms.tags;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import com.google.inject.Inject;

import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class TagHealthCheck extends NamedHealthCheck {

	private final SessionFactory sessionFactory;
	private static final String HealthCheckName = "tags_availability";

	@Inject
	public TagHealthCheck(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected Result check() throws Exception {
		try (Session session = sessionFactory.openSession()) {
			final Transaction txn = session.beginTransaction();
			try {
				Criteria crit = session.createCriteria(Tag.class);
				if (!anyTag(crit))
					return Result.unhealthy("No tags available!");
				txn.commit();
			} catch (Exception e) {
				if (txn.getStatus().canRollback()) {
					txn.rollback();
				}
				throw e;
			}
		}
		return Result.healthy();
	}

	private boolean anyTag(Criteria crit) {
		boolean anyTagAvailable = false;
		Criteria criteria = crit.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		anyTagAvailable = (count != null && count > 0);
		return anyTagAvailable;
	}

	@Override
	public String getName() {
		return HealthCheckName;
	}
}