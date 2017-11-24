package net.binggl.mydms.features.upload;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import net.binggl.mydms.features.shared.store.AbstractHibernateStore;
import net.binggl.mydms.features.upload.models.UploadItem;

public class UploadStore extends AbstractHibernateStore<UploadItem> {

	@Inject
	public UploadStore(final SessionFactory factory) {
		super(UploadItem.class, factory);
	}
}