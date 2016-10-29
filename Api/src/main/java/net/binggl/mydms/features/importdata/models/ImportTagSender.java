package net.binggl.mydms.features.importdata.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportTagSender {
	@JsonProperty(value="_id")
	private String id;
	private String name;
	@JsonProperty(value="__v")
	private String version;
	private DateTime created;

	public ImportTagSender() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}
