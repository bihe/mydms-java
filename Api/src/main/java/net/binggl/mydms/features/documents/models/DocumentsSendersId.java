package net.binggl.mydms.features.documents.models;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.binggl.mydms.features.senders.Sender;

@Embeddable
public class DocumentsSendersId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Document document;
	private Sender sender;

	@JsonIgnore
	@ManyToOne
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@JsonIgnore
	@ManyToOne
	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
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
		DocumentsSendersId other = (DocumentsSendersId) obj;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		return true;
	}

}
