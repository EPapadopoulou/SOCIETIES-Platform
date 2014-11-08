package ac.hw.personis.notification;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseEvent.Response;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;

import ac.hw.personis.PersonisHelper;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;

public class NotificationPanel extends JPanel {

	private static final String EMPTY = "EMPTY";
	private final String myUuid;
	private PersonisHelper personisHelper;
	private JButton btnAbort;
	private JButton btnCloseMe;

		/**
	 * Create the panel.
	 */
	public NotificationPanel(PersonisHelper helper, String text, String uuid, PanelClosedListener listener) {
			setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.personisHelper = helper;
		this.myUuid = uuid;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{126, 0, 77, 0};
		gridBagLayout.rowHeights = new int[]{32, 23, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		btnCloseMe = new JButton("X");
		btnCloseMe.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!myUuid.equalsIgnoreCase(EMPTY)){
				UserResponseEvent urEvent = new UserResponseEvent(myUuid, Response.CONTINUE);
				
				
				InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_RESPONSE, "", getClass().getName(), urEvent);
				try {
					personisHelper.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		});
		btnCloseMe.addActionListener(listener);
		btnCloseMe.setActionCommand(uuid);
		GridBagConstraints gbc_btnCloseMe = new GridBagConstraints();
		gbc_btnCloseMe.anchor = GridBagConstraints.EAST;
		gbc_btnCloseMe.insets = new Insets(5, 0, 5, 0);
		gbc_btnCloseMe.gridx = 2;
		gbc_btnCloseMe.gridy = 0;
		add(btnCloseMe, gbc_btnCloseMe);
		
		//JLabel lblNotificationText = new JLabel("<html> <span style='width: 200px'>"+text+"</span></html>");
		JLabel lblNotificationText = new JLabel("<html> <p>"+text+"</p></html>");
		GridBagConstraints gbc_lblNotificationText = new GridBagConstraints();
		gbc_lblNotificationText.gridheight = 2;
		gbc_lblNotificationText.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotificationText.gridwidth = 3;
		gbc_lblNotificationText.gridx = 0;
		gbc_lblNotificationText.gridy = 1;
		add(lblNotificationText, gbc_lblNotificationText);
		
		setBounds(0, 0, 280, 136);
		
		btnAbort = new JButton("Abort");
		btnAbort.setActionCommand(uuid);
		btnAbort.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UserResponseEvent urEvent = new UserResponseEvent(myUuid, Response.ABORT);
				
				
				InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_RESPONSE, "", getClass().getName(), urEvent);
				try {
					personisHelper.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnAbort.addActionListener(listener);
		GridBagConstraints gbc_btnAbort = new GridBagConstraints();
		gbc_btnAbort.gridx = 1;
		gbc_btnAbort.gridy = 3;
		add(btnAbort, gbc_btnAbort);

	}

	public String getUuid() {
		return myUuid;
	}
	
	public static NotificationPanel getEmptyNotificationPanel(String text, PanelClosedListener listener){
		NotificationPanel panel = new NotificationPanel(null, text, EMPTY, listener);
		panel.btnAbort.setVisible(false);
		panel.remove(panel.btnAbort);
		panel.btnCloseMe.setVisible(false);
		panel.remove(panel.btnCloseMe);
		
		return panel;
	}
}
