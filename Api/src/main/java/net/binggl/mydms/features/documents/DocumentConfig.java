package net.binggl.mydms.features.documents;

import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.models.DocumentsSenders;
import net.binggl.mydms.features.documents.models.DocumentsTags;

public class DocumentConfig {

	public static Class<?>[] MappedEntities = new Class<?>[] { Document.class, DocumentsTags.class, DocumentsSenders.class };
}
