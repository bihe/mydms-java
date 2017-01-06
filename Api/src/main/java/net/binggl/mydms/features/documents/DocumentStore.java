package net.binggl.mydms.features.documents;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

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

		boolean hasAlias = false;
		
		if (title.isPresent()) {
			
			criteria = criteria.createAlias("tags", "tags"); 
			criteria = criteria.createAlias("senders", "senders");
			
			hasAlias = true;
			
			Disjunction or = Restrictions.disjunction();
			or.add(Restrictions.like("title", "%" + title.get() + "%").ignoreCase());
			or.add(Restrictions.like("tags.name", "%" + title.get() + "%").ignoreCase());
			or.add(Restrictions.like("senders.name", "%" + title.get() + "%").ignoreCase());
			
			criteria = criteria.add(or);
			
			criteria = criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		}
		if (tagId.isPresent()) {
			if(!hasAlias)
				criteria = criteria.createAlias("tags", "tags", JoinType.INNER_JOIN);
			criteria = criteria.add(Restrictions.eq("tags.id", tagId.get()));
		}
		if (senderId.isPresent()) {
			if(!hasAlias)
				criteria = criteria.createAlias("senders", "senders");
			criteria = criteria.add(Restrictions.eq("senders.id", senderId.get()));
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