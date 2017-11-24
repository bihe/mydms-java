package net.binggl.mydms.features.upload.models;

import net.binggl.mydms.features.shared.models.ActionResult;
import net.binggl.mydms.features.shared.models.SimpleResult;

public class UploadResult extends SimpleResult {

	private String token;

	public UploadResult(String token, String message, ActionResult result) {
		super(message, result);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
