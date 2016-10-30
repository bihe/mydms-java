package net.binggl.mydms.features.tags;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.store.AbstractStore;

public class TagStore extends AbstractStore<Tag> {

	private static final String FIELD_NAME = "name";

	@Inject
	public TagStore(final SessionFactory factory) {
		super(Tag.class, factory);
	}

	public List<Tag> searchByName(String search) {
		Criteria criteria = this.currentSession().createCriteria(Tag.class)
				.add(Restrictions.like(FIELD_NAME, search + "%").ignoreCase());
		return list(criteria.addOrder(Order.asc(FIELD_NAME)));
	}

	public Optional<Tag> tagByName(String tagName) {
		Criteria criteria = this.currentSession().createCriteria(Tag.class)
				.add(Restrictions.eq(FIELD_NAME, tagName).ignoreCase());
		return Optional.ofNullable(uniqueResult(criteria));
	}

	public List<Tag> findAll() {
		Criteria criteria = this.currentSession().createCriteria(Tag.class);
		return list(criteria.addOrder(Order.asc(FIELD_NAME)));
	}
}