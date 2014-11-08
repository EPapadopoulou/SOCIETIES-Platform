package ac.hw.personis.notification;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.personis.PersonisHelper;

public class NotificationsPanel extends JPanel {
	private Logger logging = LoggerFactory.getLogger(this.getClass());


	private GridBagConstraints gbc;
	private JPanel mainPanel;

	List<NotificationPanel> panels;
	private PersonisHelper personisHelper;


	private PanelClosedListener listener;

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


		listener = new PanelClosedListener(this);
		addEmptyNotification();

	}

	public void addNotification(String uuid, String text){
		logging.debug("Adding new notification panel");
		NotificationPanel panel = new NotificationPanel(personisHelper, text, uuid, listener);
		panels.add(panel);
		resetPanels();
	}

	private void addEmptyNotification(){
		NotificationPanel panel = NotificationPanel.getEmptyNotificationPanel("Your notifications will appear in this area here. ", listener);
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
