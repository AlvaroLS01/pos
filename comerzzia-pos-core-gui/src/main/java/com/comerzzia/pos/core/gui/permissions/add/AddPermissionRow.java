package com.comerzzia.pos.core.gui.permissions.add;

import com.comerzzia.core.facade.model.Profile;
import com.comerzzia.core.facade.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPermissionRow {
	
	/**
	 * <p>User or group name</p>
	 */
	protected String holder;
	
	/**
	 * <p>Only used if holder is an user</p>
	 */
	protected String holderDes;

	protected User user;
	protected Profile profile;

	public AddPermissionRow(User user) {
		this.user = user;
		this.holder = user.getUserCode();
		this.holderDes = user.getUserDes();
	}

	public AddPermissionRow(Profile profile) {
		this.profile = profile;
		this.holder = profile.getProfileDes();
		this.holderDes = "";
	}

}
