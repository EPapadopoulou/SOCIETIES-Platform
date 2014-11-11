package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;
import java.util.UUID;

public class TextNotificationEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String message;
	private final String uuid;
	
	public TextNotificationEvent(final String message){
		this.message = message;
		this.uuid = UUID.randomUUID().toString();
		
	}

	public String getMessage() {
		return message;
	}

	public String getUuid() {
		return uuid;
	}
	
}
