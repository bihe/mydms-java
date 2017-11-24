package net.binggl.mydms.features.senders;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.store.AbstractHibernateStore;

public class SenderStore extends AbstractHibernateStore<Sender> {

	private static final String FIELD_NAME = "name";

	@Inject
	public SenderStore(final SessionFactory factory) {
		super(Sender.class, factory);
	}

	public List<Sender> searchByName(String search) {
		Criteria criteria = this.currentSession().createCriteria(Sender.class)
				.add(Restrictions.like(FIELD_NAME, search + "%").ignoreCase());
		return list(criteria.addOrder(Order.asc(FIELD_NAME)));
	}

	public Optional<Sender> senderByName(String tagName) {
		Criteria criteria = this.currentSession().createCriteria(Sender.class)
				.add(Restrictions.eq(FIELD_NAME, tagName).ignoreCase());
		return Optional.ofNullable(uniqueResult(criteria));
	}

	public List<Sender> findAll() {
		Criteria criteria = this.currentSession().createCriteria(Sender.class);
		return list(criteria.addOrder(Order.asc(FIELD_NAME)));
	}
}