package net.binggl.mydms.senders;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import com.google.inject.Inject;

import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class SenderHealthCheck extends NamedHealthCheck {

	private final SessionFactory sessionFactory;
	private static final String HealthCheckName = "senders_availability";

	@Inject
	public SenderHealthCheck(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected Result check() throws Exception {
		try (Session session = sessionFactory.openSession()) {
			final Transaction txn = session.beginTransaction();
			try {
				Criteria crit = session.createCriteria(Sender.class);
				if (!anySender(crit))
					return Result.unhealthy("No senders available!");
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

	private boolean anySender(Criteria crit) {
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