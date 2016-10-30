package net.binggl.mydms.features.documents.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.shared.JsonDateSerializer;
import net.binggl.mydms.features.tags.Tag;

@Entity
@Table(name = "DOCUMENTS")
public class Document {

	@Id
	@Column(name = "id")
	private UUID id;
	@Column(name = "title", nullable = false)
	@NotEmpty
	private String title;
	@Column(name = "filename", nullable = false)
	@NotEmpty
	private String fileName;
	@Column(name = "alternativeid")
	private String alternativeId;
	@Column(name = "previewlink")
	private String previewLink;
	@Column(name = "amount")
	private double amount;
	@Column(name = "created")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date created;
	@Column(name = "modified")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date modified;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "DOCUMENTS_TO_TAGS", joinColumns = {
			@JoinColumn(name = "document_id", referencedColumnName = "id", nullable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = true) })
	private List<Tag> tags = new ArrayList<>();

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "DOCUMENTS_TO_SENDERS", joinColumns = {
			@JoinColumn(name = "document_id", referencedColumnName = "id", nullable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = true) })
	private List<Sender> senders = new ArrayList<>();

	public Document() {
		super();
	}

	public Document(UUID id, String title, String fileName, String alternativeId, String previewLink, double amount,
			Date created, Date modified) {
		super();
		this.id = id;
		this.title = title;
		this.fileName = fileName;
		this.alternativeId = alternativeId;
		this.previewLink = previewLink;
		this.amount = amount;
		this.created = created;
		this.modified = modified;
	}

	public Document(String title, String fileName, String alternativeId, String previewLink, double amount) {
		super();
		this.id = UUID.randomUUID();
		this.title = title;
		this.fileName = fileName;
		this.alternativeId = alternativeId;
		this.previewLink = previewLink;
		this.amount = amount;
		this.created = new Date();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAlternativeId() {
		return alternativeId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = alternativeId;
	}

	public String getPreviewLink() {
		return previewLink;
	}

	public void setPreviewLink(String previewLink) {
		this.previewLink = previewLink;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Sender> getSenders() {
		return senders;
	}

	public void setSenders(List<Sender> senders) {
		this.senders = senders;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", title=" + title + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alternativeId == null) ? 0 : alternativeId.hashCode());
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((modified == null) ? 0 : modified.hashCode());
		result = prime * result + ((previewLink == null) ? 0 : previewLink.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Document other = (Document) obj;
		if (alternativeId == null) {
			if (other.alternativeId != null)
				return false;
		} else if (!alternativeId.equals(other.alternativeId))
			return false;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		if (previewLink == null) {
			if (other.previewLink != null)
				return false;
		} else if (!previewLink.equals(other.previewLink))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}