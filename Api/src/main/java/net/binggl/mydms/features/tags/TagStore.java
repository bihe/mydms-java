package net.binggl.mydms.features.tags;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.AbstractStore;

public class TagStore extends AbstractStore<Tag> {
    
	private static final String FIELD_NAME = "name";
		
	@Inject
	public TagStore(final SessionFactory factory) {
        super(Tag.class, factory);
    }
	
	public List<Tag> searchByName(String search) {
     	Criteria criteria = this.currentSession()
     			.createCriteria(Tag.class).add(Restrictions.like(FIELD_NAME, search + "%").ignoreCase());
        return list(criteria.addOrder(Order.asc(FIELD_NAME)));
    }
}