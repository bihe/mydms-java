package net.binggl.mydms.features.senders;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.AbstractStore;

public class SenderStore extends AbstractStore<Sender> {
    
	private static final String FIELD_NAME = "name";
	
	@Inject
	public SenderStore(final SessionFactory factory) {
        super(Sender.class, factory);
    }
	
	public List<Sender> searchByName(String search) {
     	Criteria criteria = this.currentSession()
     			.createCriteria(Sender.class).add(Restrictions.like(FIELD_NAME, search + "%").ignoreCase());
        return list(criteria.addOrder(Order.asc(FIELD_NAME)));
    }
}