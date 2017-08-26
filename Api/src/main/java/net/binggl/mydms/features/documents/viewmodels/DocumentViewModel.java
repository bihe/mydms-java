package net.binggl.mydms.features.documents.viewmodels;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.binggl.mydms.features.shared.JsonDateSerializer;

public class DocumentViewModel {

	private String id;
	@NotEmpty
	private String title;
	@NotEmpty
	private String fileName;
	private String alternativeId;
	private String previewLink;
	private double amount;
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date created;
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date modified;
	private List<String> tags = new ArrayList<>();
	private List<String> senders = new ArrayList<>();
	@NotEmpty
	private String uploadFileToken;
	
	public DocumentViewModel() {
		super();
	}
	
	public DocumentViewModel(String id, String title, String fileName, String alternativeId, String previewLink,
			double amount) {
		super();
		this.id = id;
		this.title = title;
		this.fileName = fileName;
		this.alternativeId = alternativeId;
		this.previewLink = previewLink;
		this.amount = amount;
	}

	public DocumentViewModel(String id, String title, String fileName, String alternativeId, String previewLink,
			double amount, Date created, Date modified, List<String> tags, List<String> senders, String uploadFileToken) {
		super();
		this.id = id;
		this.title = title;
		this.fileName = fileName;
		this.alternativeId = alternativeId;
		this.previewLink = previewLink;
		this.amount = amount;
		this.created = created;
		this.modified = modified;
		this.tags = tags;
		this.senders = senders;
		this.uploadFileToken = uploadFileToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
        byte[] fileNameBytes = this.fileName.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeBase64String(fileNameBytes);
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getSenders() {
		return senders;
	}

	public void setSenders(List<String> senders) {
		this.senders = senders;
	}
	
	public String getUploadFileToken() {
		return uploadFileToken;
	}

	public void setUploadFileToken(String uploadFileToken) {
		this.uploadFileToken = uploadFileToken;
	}

	@Override
	public String toString() {
		return "DocumentViewModel [id=" + id + ", title=" + title + "]";
	}
}
