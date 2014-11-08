package ac.hw.personis.notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelClosedListener implements ActionListener{

	private NotificationsPanel notifPanel;
	public PanelClosedListener(NotificationsPanel notifPanel) {
		this.notifPanel = notifPanel;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String uuid = e.getActionCommand();
		notifPanel.removeNotification(uuid);
		
	}

}
