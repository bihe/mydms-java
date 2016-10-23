package net.binggl.mydms.senders;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import net.binggl.mydms.shared.AbstractStore;

public class SenderStore extends AbstractStore<Sender> {
    
	@Inject
	public SenderStore(final SessionFactory factory) {
        super(Sender.class, factory);
    }
}