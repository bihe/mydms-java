package net.binggl.mydms.features.documents.models;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.binggl.mydms.features.tags.Tag;

@Entity
@Table(name = "DOCUMENTS_TO_TAGS")
@AssociationOverrides({
	@AssociationOverride(name = "pk.document",
		joinColumns = @JoinColumn(name = "DOCUMENT_ID")),
	@AssociationOverride(name = "pk.tag",
		joinColumns = @JoinColumn(name = "TAG_ID")) })
public class DocumentsTags {

	private DocumentsTagsId pk = new DocumentsTagsId();

	public DocumentsTags() {
	}
	
	public DocumentsTags(Document document, Tag tag) {
		this.setDocument(document);
		this.setTag(tag);
	}

	@EmbeddedId
	public DocumentsTagsId getPk() {
		return pk;
	}

	public void setPk(DocumentsTagsId pk) {
		this.pk = pk;
	}

	@Transient
	public Document getDocument() {
		return this.pk.getDocument();
	}

	public void setDocument(Document document) {
		this.pk.setDocument(document);
	}

	@Transient
	public Tag getTag() {
		return this.pk.getTag();
	}

	public void setTag(Tag tag) {
		this.pk.setTag(tag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentsTags other = (DocumentsTags) obj;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}

}
