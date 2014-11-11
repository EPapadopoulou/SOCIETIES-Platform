package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;

public class NotificationAccCtrlEvent implements Serializable{

	/**
	 * 
	 */
	public enum NotificationType {TIMED, SIMPLE};
	
	private static final long serialVersionUID = 1L;
	private final String uuid;
	private final String message;
	private final NotificationType notificationType;
	private final PrivacyOutcomeConstantsBean effect;
	
	public NotificationAccCtrlEvent(String uuid, String message, NotificationType notifType, PrivacyOutcomeConstantsBean effect){
		this.uuid = uuid;
		this.message = message;
		this.notificationType = notifType;
		this.effect = effect;
		
	}

	public String getMessage() {
		return message;
	}

	public String getUuid() {
		return uuid;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public PrivacyOutcomeConstantsBean getEffect() {
		return effect;
	}
}
