package net.binggl.mydms.features.tags;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.AbstractStore;

public class TagStore extends AbstractStore<Tag> {
    
	@Inject
	public TagStore(final SessionFactory factory) {
        super(Tag.class, factory);
    }
}