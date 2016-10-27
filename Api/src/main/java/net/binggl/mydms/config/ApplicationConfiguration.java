package net.binggl.mydms.config;

import javax.validation.constraints.NotNull;

public class ApplicationConfiguration {

	@NotNull
	private boolean initialData = false;

	public boolean isInitialData() {
		return initialData;
	}

	public void setInitialData(boolean initialData) {
		this.initialData = initialData;
	}
}
