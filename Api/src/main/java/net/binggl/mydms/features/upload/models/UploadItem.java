package net.binggl.mydms.features.upload.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.binggl.mydms.features.shared.JsonDateSerializer;

@Entity
@Table(name = "UPLOADS")
public class UploadItem {

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "filename", nullable = false)
	@NotEmpty
	private String fileName;
	
	@Column(name = "mimetype", nullable = false)
	@NotEmpty
	private String mimeType;
	
	@Column(name = "created")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date created;

	public UploadItem() {
		super();
	}

	public UploadItem(String id, String fileName, String mimeType) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.created = new Date();
	}

	public UploadItem(String id, String fileName, String mimeType, Date created) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.created = created;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
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
		UploadItem other = (UploadItem) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UploadItem [id=" + id + ", fileName=" + fileName + "]";
	}
}
