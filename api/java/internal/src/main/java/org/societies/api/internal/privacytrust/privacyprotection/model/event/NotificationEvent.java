package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

public class NotificationEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String uuid;
	private final String message;

	public NotificationEvent(String uuid, String message){
		this.uuid = uuid;
		this.message = message;
		
	}

	public String getMessage() {
		return message;
	}

	public String getUuid() {
		return uuid;
	}
}
