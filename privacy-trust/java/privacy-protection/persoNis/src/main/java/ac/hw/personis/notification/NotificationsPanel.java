package ac.hw.personis.notification;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationDobfEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationEvent.NotificationType;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationPanelClosedListener;

public class NotificationsPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private Logger logging = LoggerFactory.getLogger(this.getClass());


	private GridBagConstraints gbc;
	private JPanel mainPanel;

	List<NotificationPanel> panels;
	private PersonisHelper personisHelper;


	private NotificationPanelClosedListener listener;

	/**
	 * Create the panel.
	 */
	public NotificationsPanel(PersonisHelper personisHelper) {
		this.personisHelper = personisHelper;
		panels = new ArrayList<NotificationPanel>();
		setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		JLabel label = new JLabel(" Notifications ");

		topPanel.add(label);
		add(topPanel, BorderLayout.NORTH);

		GridBagLayout layout = new GridBagLayout();
		mainPanel = new JPanel();
		mainPanel.setLayout(layout);


		gbc = new GridBagConstraints();



		add(new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		//add(buttonPanel, BorderLayout.SOUTH);


		listener = new NotificationPanelClosedListener(this);
		addEmptyNotification();

	}

	public void addAccessControlNotification(NotificationEvent notifEvent){

		logging.debug("Adding new notification panel");
		if (notifEvent.getNotificationType()==NotificationType.SIMPLE){
			AccessControlPanel panel = new AccessControlPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getEffect());
			panels.add(panel);
			resetPanels();
		}else if (notifEvent.getNotificationType()==NotificationType.TIMED){
			TimedAccessControlPanel panel = new TimedAccessControlPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getEffect());
			panels.add(panel);
			resetPanels();
		}
	}
	public void addDobfNotification(NotificationDobfEvent notifEvent) {
		logging.debug("Adding new dobf notification panel");
		if (notifEvent.getNotificationType()==NotificationType.SIMPLE){
			DObfPanel panel = new DObfPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getDataType(), notifEvent.getObfuscationLevel());
			panels.add(panel);
			resetPanels();
		}else if (notifEvent.getNotificationType()==NotificationType.TIMED){
			TimedDObfPanel panel = new TimedDObfPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getDataType(), notifEvent.getObfuscationLevel());
			panels.add(panel);
			resetPanels();
		} 
		
	}

	private void addEmptyNotification(){
		AccessControlPanel panel = AccessControlPanel.getEmptyNotificationPanel("Your notifications will appear in this area here. ", listener);
		panels.add(panel);
		resetPanels();
	}

	private void resetPanels() {
		mainPanel.removeAll();
		logging.debug("removed all panels");

		int i = panels.size();
		gbc.gridx = 0;
		for (NotificationPanel nPanel : panels){
			logging.debug("recreating notif list: "+nPanel.getUuid());
			gbc.gridy = i;
			mainPanel.add(nPanel, gbc);
			i--;
		}
		logging.debug("finished adding panels, revalidating mainPanel");

		mainPanel.revalidate();
	}

	public void removeNotification(String uuid){
		logging.debug("Removing notification with uuid {}", uuid);
		NotificationPanel panelToBeRemoved = null;
		for (NotificationPanel panel: panels){
			if (panel.getUuid().equalsIgnoreCase(uuid)){
				logging.debug("Found panel for removal");
				panelToBeRemoved = panel;
				break;

			}
		}
		if (null!=panelToBeRemoved){
			if (panels.remove(panelToBeRemoved)){
				logging.debug("panel removed");
				resetPanels();
				logging.debug("reset panels");
			}else{
				logging.debug("panel not removed from panels list");
			}
			
		}

	}




}
