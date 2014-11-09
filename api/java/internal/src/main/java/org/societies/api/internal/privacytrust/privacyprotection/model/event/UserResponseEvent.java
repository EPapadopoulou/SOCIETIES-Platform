package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;

public class UserResponseEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String uuid;
	private final PrivacyOutcomeConstantsBean effect;
	private final boolean userClicked;

	public UserResponseEvent(String uuid, PrivacyOutcomeConstantsBean effect, boolean userClicked){
		this.uuid = uuid;
		this.effect = effect;
		this.userClicked = userClicked;
		
	}
	public String getUuid() {
		return uuid;
	}
	public PrivacyOutcomeConstantsBean getEffect() {
		return effect;
	}
	public boolean isUserClicked() {
		return userClicked;
	}

	
}
