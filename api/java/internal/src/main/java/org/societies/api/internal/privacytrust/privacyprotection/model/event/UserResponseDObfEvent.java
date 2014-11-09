package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

public class UserResponseDObfEvent implements Serializable {

	private final String uuid;
	private final Integer obfuscationLevel;
	private final boolean userClicked;
	
	public UserResponseDObfEvent(final String uuid, Integer obfuscationLevel, boolean userClicked){
		this.uuid = uuid;
		this.obfuscationLevel = obfuscationLevel;
		this.userClicked = userClicked;
		
	}

	public String getUuid() {
		return uuid;
	}

	public Integer getObfuscationLevel() {
		return obfuscationLevel;
	}

	public boolean isUserClicked() {
		return userClicked;
	}
}
