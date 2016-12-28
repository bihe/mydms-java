package net.binggl.mydms.features.startpage;

import net.binggl.mydms.features.security.models.User;

public class UserInfo extends User {

	private static final long serialVersionUID = 1L;

	public UserInfo() {
		super("","","","",null);
	}
	
	public UserInfo(User user) {
		super(user.getUserId(),user.getUserName(),user.getDisplayName(),user.getEmail(),null);
	}
}
