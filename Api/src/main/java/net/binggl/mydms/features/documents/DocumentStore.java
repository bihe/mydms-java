package net.binggl.mydms.features.documents;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.shared.store.AbstractHibernateStore;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;

public class DocumentStore extends AbstractHibernateStore<Document> {

	@Inject
	public DocumentStore(final SessionFactory factory) {
		super(Document.class, factory);
	}

	public List<Document> searchDocuments(Optional<String> title, Optional<Long> tagId, Optional<Long> senderId,
			Optional<Date> dateFrom, Optional<Date> dateUntil, Optional<Integer> limit, Optional<Integer> skip,
			OrderBy... order) {
		Criteria criteria = this.currentSession().createCriteria(Document.class);

		if (title.isPresent()) {
			criteria = criteria.createAlias("tags", "tags"); 
			criteria = criteria.createAlias("senders", "senders");
			
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.like("title", "%" + title.get() + "%").ignoreCase());
			or.add(Restrictions.like("tags.name", "%" + title.get() + "%").ignoreCase());
			or.add(Restrictions.like("senders.name", "%" + title.get() + "%").ignoreCase());
			
			criteria = criteria.add(or);
		}
		if (tagId.isPresent()) {
			criteria = criteria.createAlias("tags", "t");
			criteria = criteria.add(Restrictions.eq("t.id", tagId.get()));
		}
		if (senderId.isPresent()) {
			criteria = criteria.createAlias("senders", "s");
			criteria = criteria.add(Restrictions.eq("s.id", senderId.get()));
		}
		if (dateFrom.isPresent()) {
			criteria = criteria.add(Restrictions.ge("created", dateFrom.get()));
		}
		if (dateUntil.isPresent()) {
			criteria = criteria.add(Restrictions.le("created", dateUntil.get()));
		}
		if (limit.isPresent()) {
			criteria = criteria.setMaxResults(limit.get());
		}
		if (skip.isPresent()) {
			criteria = criteria.setFirstResult(skip.get());
		}

		for (OrderBy o : order) {
			if (o.getSort() == SortOrder.Ascending)
				criteria = criteria.addOrder(Order.asc(o.getField()));
			else
				criteria = criteria.addOrder(Order.desc(o.getField()));
		}

		return list(criteria);
	}
	
	public Optional<Document> findByAlternativeId(String alternativeId) {
		Criteria criteria = this.currentSession().createCriteria(Document.class)
				.add(Restrictions.eq("alternativeId", alternativeId).ignoreCase());
		return Optional.ofNullable(uniqueResult(criteria));
	}

}