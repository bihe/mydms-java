package net.binggl.mydms.features.shared.models;

public class SimpleResult {
	private String message;
	private ActionResult result;
	
	public SimpleResult(String message, ActionResult result) {
		super();
		this.message = message;
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ActionResult getResult() {
		return result;
	}
	public void setResult(ActionResult result) {
		this.result = result;
	}	
}