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
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent.NotificationType;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationDobfEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.TextNotificationEvent;

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

	public void addAccessControlNotification(NotificationAccCtrlEvent notifEvent){

		System.out.println("Adding new notification panel");
		if (notifEvent.getNotificationType()==NotificationType.SIMPLE){
			AccessControlPanel panel = new AccessControlPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getEffect());
			panels.add(panel);
			resetPanels();
		}else if (notifEvent.getNotificationType()==NotificationType.TIMED){
			TimedAccessControlPanel panel = new TimedAccessControlPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getEffect());
			panels.add(panel);
			resetPanels();
		}
		requestFocus();
	}
	public void addDobfNotification(NotificationDobfEvent notifEvent) {
		System.out.println("Adding new dobf notification panel");
		if (notifEvent.getNotificationType()==NotificationType.SIMPLE){
			DObfPanel panel = new DObfPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getDataType(), notifEvent.getObfuscationLevel());
			panels.add(panel);
			resetPanels();
		}else if (notifEvent.getNotificationType()==NotificationType.TIMED){
			TimedDObfPanel panel = new TimedDObfPanel(personisHelper, notifEvent.getMessage(), notifEvent.getUuid(), listener, notifEvent.getDataType(), notifEvent.getObfuscationLevel());
			panels.add(panel);
			resetPanels();
		} 
		requestFocus();
	}

	private void addEmptyNotification(){
		TextNotification textNotificationPanel = new TextNotification("Your notifications will appear in this area here. ");
		
		panels.add(textNotificationPanel);
		resetPanels();
		
	}
	
	public void addTextNotification(TextNotificationEvent txtNotifEvent) {
		TextNotification textNotificationPanel = new TextNotification(txtNotifEvent.getMessage());
		panels.add(textNotificationPanel);
		resetPanels();
		
	}


	private void resetPanels() {
		mainPanel.removeAll();
		System.out.println("removed all panels");

		int i = panels.size();
		gbc.gridx = 0;
		for (NotificationPanel nPanel : panels){
			System.out.println("recreating notif list: "+nPanel.getUuid());
			gbc.gridy = i;
			mainPanel.add(nPanel, gbc);
			i--;
		}
		System.out.println("finished adding panels, revalidating mainPanel");

		mainPanel.revalidate();
		revalidate();
		
	}

	public void removeNotification(String uuid){
		System.out.println("Removing notification with uuid "+uuid);
		NotificationPanel panelToBeRemoved = null;
		for (NotificationPanel panel: panels){
			if (panel.getUuid().equalsIgnoreCase(uuid)){
				System.out.println("Found panel for removal");
				panelToBeRemoved = panel;
				break;

			}
		}
		if (null!=panelToBeRemoved){
			if (panels.remove(panelToBeRemoved)){
				System.out.println("panel removed");
				resetPanels();
				System.out.println("reset panels");
			}else{
				System.out.println("panel not removed from panels list");
			}
			
		}

	}

	



}
