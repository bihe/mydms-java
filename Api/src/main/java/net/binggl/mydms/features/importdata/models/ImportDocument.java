package net.binggl.mydms.features.importdata.models;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportDocument {
	@JsonProperty(value = "_id")
	private String id;
	private String title;
	private String fileName;
	@JsonProperty(value = "__v")
	private String version;
	private String state;
	private DateTime created;
	private DateTime modified;
	private double amount;
	private String previewLink;
	private String alternativeId;
	private List<ImportTagSender> tags;
	private List<ImportTagSender> senders;

	public ImportDocument() {
		super();
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPreviewLink() {
		return previewLink;
	}

	public void setPreviewLink(String previewLink) {
		this.previewLink = previewLink;
	}

	public String getAlternativeId() {
		return alternativeId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = alternativeId;
	}

	public List<ImportTagSender> getTags() {
		return tags;
	}

	public void setTags(List<ImportTagSender> tags) {
		this.tags = tags;
	}

	public List<ImportTagSender> getSenders() {
		return senders;
	}

	public void setSenders(List<ImportTagSender> senders) {
		this.senders = senders;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
