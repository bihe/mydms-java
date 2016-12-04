package net.binggl.mydms.features.security.models;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class User implements Principal, Serializable {

	private static final long serialVersionUID = 8604775569255873511L;

	private final String userId;
	private final String userName;
	private final String displayName;
	private final String email;
	private final List<Claim> claims;
	
	private User(final String userId, final String userName, final String displayName, final String email, final List<Claim> claims) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.displayName = displayName;
		this.email = email;
		this.claims = claims;
	}
	
	public String getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getEmail() {
		return email;
	}
	public List<Claim> getClaims() {
		return claims;
	}
	
	@Override
	public String getName() {
		return String.format("%s (%s)", this.userName, this.userId);
	}
	
	public static class UserBuilder {
		
		private String userId;
		private String userName;
		private String displayName;
		private String email;
		private List<Claim> claims;
		
		public UserBuilder() {
			this.claims = new ArrayList<>();
		}
		
		public UserBuilder userId(final String userId) {
			this.userId = userId;
			return this;
		}
		
		public UserBuilder userName(final String userName) {
			this.userName = userName;
			return this;
		}
		
		public UserBuilder displayName(final String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public UserBuilder email(final String email) {
			this.email = email;
			return this;
		}
		
		public UserBuilder claims(final List<Claim> claimList) {
			if(claimList != null)
				this.claims = claimList;
			return this;
		}

		public User build() {
			return new User(userId, userName, displayName, email, claims);
		}
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", displayName=" + displayName + ", email="
				+ email + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((claims == null) ? 0 : claims.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
}