package ac.hw.personis.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationDobfEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationEvent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.notification.NotificationsPanel;

public class NotificationsListener extends EventListener{
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private PersonisHelper personisHelper;
	private NotificationsPanel notifPanel;

	public NotificationsListener(PersonisHelper personisHelper, NotificationsPanel notifPanel){
		this.personisHelper = personisHelper;
		this.notifPanel = notifPanel;
		String[] eventTypes = new String[]{
				EventTypes.PERSONIS_NOTIFICATION_REQUEST,
				EventTypes.PERSONIS_NOTIFICATION_DOBF_REQUEST
				};
		try{
		this.personisHelper.getEventMgr().subscribeInternalEvent(this, eventTypes, null);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	@Override
	public void handleInternalEvent(InternalEvent event) {
		logging.debug("Received event: {}", event.geteventType());
		if (event.geteventType().equalsIgnoreCase(EventTypes.PERSONIS_NOTIFICATION_REQUEST)){
			if (event.geteventInfo() instanceof NotificationEvent){
				NotificationEvent notifEvent = (NotificationEvent) event.geteventInfo();
				notifPanel.addAccessControlNotification(notifEvent);	
			}else{
				logging.debug("Received event of unknown Class {}", event.geteventInfo());
			}

		}else if (event.geteventType().equalsIgnoreCase(EventTypes.PERSONIS_NOTIFICATION_DOBF_REQUEST)){
			if (event.geteventInfo() instanceof NotificationDobfEvent){
				NotificationDobfEvent notifEvent = (NotificationDobfEvent) event.geteventInfo();
				notifPanel.addDobfNotification(notifEvent);
			}else{
				logging.debug("Received event of unknown Class {}", event.geteventInfo());
			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}

}
