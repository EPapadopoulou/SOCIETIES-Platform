package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent.NotificationType;

public class NotificationDobfEvent implements Serializable{

	private static final long serialVersionUID = 1L;
	private final String uuid;
	private final String message;
	private final NotificationType notificationType;
	private final Integer obfuscationLevel;
	private final String dataType;
	
	public NotificationDobfEvent(final String uuid, final String message, final NotificationType notificationType, final Integer obfuscationLevel, String dataType){
		this.uuid = uuid;
		this.message = message;
		this.notificationType = notificationType;
		this.obfuscationLevel = obfuscationLevel;
		this.dataType = dataType;
		
	}

	public String getUuid() {
		return uuid;
	}

	public String getMessage() {
		return message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public Integer getObfuscationLevel() {
		return obfuscationLevel;
	}

	public String getDataType() {
		return dataType;
	}


}
