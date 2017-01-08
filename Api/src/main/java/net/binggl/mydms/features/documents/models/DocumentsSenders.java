package net.binggl.mydms.features.documents.models;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.binggl.mydms.features.senders.Sender;

@Entity
@Table(name = "DOCUMENTS_TO_SENDERS")
@AssociationOverrides({
	@AssociationOverride(name = "pk.document",
		joinColumns = @JoinColumn(name = "DOCUMENT_ID")),
	@AssociationOverride(name = "pk.sender",
		joinColumns = @JoinColumn(name = "SENDER_ID")) })
public class DocumentsSenders {

	private DocumentsSendersId pk = new DocumentsSendersId();

	public DocumentsSenders() {
	}
	
	public DocumentsSenders(Document document, Sender sender) {
		this.setDocument(document);
		this.setSender(sender);
	}

	@EmbeddedId
	public DocumentsSendersId getPk() {
		return pk;
	}

	public void setPk(DocumentsSendersId pk) {
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
	public Sender getSender() {
		return this.pk.getSender();
	}

	public void setSender(Sender sender) {
		this.pk.setSender(sender);
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
		DocumentsSenders other = (DocumentsSenders) obj;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}

}
