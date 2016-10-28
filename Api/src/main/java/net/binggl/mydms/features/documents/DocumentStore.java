package net.binggl.mydms.features.documents;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.AbstractStore;

public class DocumentStore extends AbstractStore<Document> {
    
	@Inject
	public DocumentStore(final SessionFactory factory) {
        super(Document.class, factory);
    }
}