package net.binggl.mydms.features.security.models;

/**
 * @see http://stackoverflow.com/questions/604424/lookup-enum-by-string-value
 */
public enum Role {
	None("none"), User("user"), Admin("admin");

	private String text;

	Role(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static Role fromString(String text) {
		try {
			if (text != null && !"".equals(text)) {
				for (Role b : Role.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
		} catch(Exception EX) {
			
		}
		return Role.None;
	}
}