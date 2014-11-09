package ac.hw.personis.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ac.hw.personis.notification.NotificationsPanel;

public class NotificationPanelClosedListener implements ActionListener{

	private NotificationsPanel notifPanel;
	public NotificationPanelClosedListener(NotificationsPanel notifPanel) {
		this.notifPanel = notifPanel;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String uuid = e.getActionCommand();
		notifPanel.removeNotification(uuid);
		
	}

}
