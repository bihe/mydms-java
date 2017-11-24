package net.binggl.mydms.features.security.models;

import java.io.Serializable;

public class Claim implements Serializable {

	private static final long serialVersionUID = 8188311924048692724L;

	private String name;
	private String url;
	private Role role;

	public Claim(String name, String url, Role role) {
		super();
		this.name = name;
		this.url = url;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
