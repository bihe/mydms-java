package net.binggl.mydms.features.documents;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.shared.JsonJodaDateTimeSerializer;
import net.binggl.mydms.features.tags.Tag;

@Entity
@Table(name = "document")
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "fileName", nullable = false)
	private String fileName;
	private String alternativeId;
	private String previewLink;
	private double amount;
	@JsonSerialize(using = JsonJodaDateTimeSerializer.class)
	private DateTime created;
	@JsonSerialize(using = JsonJodaDateTimeSerializer.class)
	private DateTime modified;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "documents_to_tags", joinColumns = {
			@JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "id", nullable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "TAG_ID", referencedColumnName = "id", nullable = true) })
	public List<Tag> tags = new ArrayList<>();

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "documents_to_senders", joinColumns = {
			@JoinColumn(name = "DOCUMENT_ID", referencedColumnName = "id", nullable = true) }, inverseJoinColumns = {
					@JoinColumn(name = "SENDER_ID", referencedColumnName = "id", nullable = true) })
	public List<Sender> senders = new ArrayList<>();

	public Document() {
		super();
	}

	public Document(long id, String title, String fileName, String alternativeId, String previewLink, double amount,
			DateTime created, DateTime modified) {
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getModified() {
		return modified;
	}

	public void setModified(DateTime modified) {
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