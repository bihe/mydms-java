package net.binggl.mydms.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class ApplicationConfiguration {

	@NotNull
	private boolean initialData = false;
	@NotNull
	private String uploadPath = "";
	@NotNull
	private Long maxUploadSize = 1000L;
	@NotEmpty
	private List<String> allowedFileTypes;
	@NotNull
	private GoogleConfiguration google = new GoogleConfiguration();

	public boolean isInitialData() {
		return initialData;
	}

	public void setInitialData(boolean initialData) {
		this.initialData = initialData;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public Long getMaxUploadSize() {
		return maxUploadSize;
	}

	public void setMaxUploadSize(Long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public List<String> getAllowedFileTypes() {
		return allowedFileTypes;
	}

	public void setAllowedFileTypes(List<String> allowedFileTypes) {
		this.allowedFileTypes = allowedFileTypes;
	}

	public GoogleConfiguration getGoogle() {
		return google;
	}

	public void setGoogle(GoogleConfiguration google) {
		this.google = google;
	}

}
