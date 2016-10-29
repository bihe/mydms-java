package net.binggl.mydms.features.documents;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.AbstractStore;
import net.binggl.mydms.features.shared.OrderBy;
import net.binggl.mydms.features.shared.SortOrder;

public class DocumentStore extends AbstractStore<Document> {
    
	@Inject
	public DocumentStore(final SessionFactory factory) {
        super(Document.class, factory);
    }
	
	
	public List<Document> searchDocuments(Optional<String> title, Optional<Long> tagId,
			Optional<Long> senderId, Optional<Date> dateFrom, Optional<Date> dateUntil,
			Optional<Integer> limit, Optional<Integer> skip, OrderBy ... order) {
     	Criteria criteria = this.currentSession().createCriteria(Document.class);
        
     	if(title.isPresent()) {
     		criteria = criteria.add(Restrictions.like("title", title.get() + "%").ignoreCase());
     	}
     	if(tagId.isPresent()) {
     		criteria = criteria.createAlias("tags", "t");
     		criteria = criteria.add(Restrictions.eq("t.id", tagId.get()));
     	}
     	if(senderId.isPresent()) {
     		criteria = criteria.createAlias("senders", "s");
     		criteria = criteria.add(Restrictions.eq("s.id", senderId.get()));
     	}
     	if(dateFrom.isPresent()) {
     		criteria = criteria.add(Restrictions.ge("created", dateFrom.get()));
     	}
     	if(dateUntil.isPresent()) {
     		criteria = criteria.add(Restrictions.le("created", dateUntil.get()));
     	}
     	if(limit.isPresent()) {
     		criteria = criteria.setMaxResults(limit.get());
     	}
     	if(skip.isPresent()) {
     		criteria = criteria.setFirstResult(skip.get());
     	}
     	
     	for(OrderBy o : order) {
        	if(o.getSort() == SortOrder.Ascending)
        		criteria = criteria.addOrder(Order.asc(o.getField()));
        	else
        		criteria = criteria.addOrder(Order.desc(o.getField()));
        }
     	
     	return list(criteria);
    }
	
	
}