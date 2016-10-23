package net.binggl.mydms.tags;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import net.binggl.mydms.shared.AbstractStore;

public class TagStore extends AbstractStore<Tag> {
    
	@Inject
	public TagStore(final SessionFactory factory) {
        super(Tag.class, factory);
    }
}