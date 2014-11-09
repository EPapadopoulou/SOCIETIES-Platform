package ac.hw.personis.notification;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationPanelClosedListener;

public class AccessControlPanel extends NotificationPanel {

	private static final String EMPTY = "EMPTY";
	private final String myUuid;
	private PersonisHelper personisHelper;
	private JButton btnBlockAccess;
	private JButton btnAllowAccess;
	private PrivacyOutcomeConstantsBean preferenceEffect;
	private boolean userClicked = true;
	/**
	 * Create the panel.
	 */
	public AccessControlPanel(PersonisHelper helper, String text, String uuid, NotificationPanelClosedListener listener, PrivacyOutcomeConstantsBean preferenceEffect) {
		this.preferenceEffect = preferenceEffect;
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.personisHelper = helper;
		this.myUuid = uuid;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{126, 0, 77, 0};
		gridBagLayout.rowHeights = new int[]{32, 23, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		btnAllowAccess = new JButton("Allow Access");
		btnAllowAccess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!myUuid.equalsIgnoreCase(EMPTY)){
					UserResponseEvent urEvent = new UserResponseEvent(myUuid, PrivacyOutcomeConstantsBean.ALLOW, userClicked);


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
		btnAllowAccess.addActionListener(listener);
		btnAllowAccess.setActionCommand(uuid);
		GridBagConstraints gbc_btnCloseMe = new GridBagConstraints();
		gbc_btnCloseMe.anchor = GridBagConstraints.EAST;
		gbc_btnCloseMe.insets = new Insets(5, 0, 5, 0);
		gbc_btnCloseMe.gridx = 2;
		gbc_btnCloseMe.gridy = 2;
		add(btnAllowAccess, gbc_btnCloseMe);

		//JLabel lblNotificationText = new JLabel("<html> <span style='width: 200px'>"+text+"</span></html>");
		JLabel lblNotificationText = new JLabel("<html> <p>"+text+"</p></html>");
		GridBagConstraints gbc_lblNotificationText = new GridBagConstraints();
		gbc_lblNotificationText.gridheight = 2;
		gbc_lblNotificationText.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotificationText.gridwidth = 3;
		gbc_lblNotificationText.gridx = 0;
		gbc_lblNotificationText.gridy = 0;
		add(lblNotificationText, gbc_lblNotificationText);

		setBounds(0, 0, 280, 136);

		btnBlockAccess = new JButton("Block Access");
		btnBlockAccess.setActionCommand(uuid);
		btnBlockAccess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UserResponseEvent urEvent = new UserResponseEvent(myUuid, PrivacyOutcomeConstantsBean.BLOCK, userClicked);


				InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_RESPONSE, "", getClass().getName(), urEvent);
				try {
					personisHelper.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnBlockAccess.addActionListener(listener);
		GridBagConstraints gbc_btnAbort = new GridBagConstraints();
		gbc_btnAbort.anchor = GridBagConstraints.WEST;
		gbc_btnAbort.gridx = 0;
		gbc_btnAbort.gridy = 2;
		add(btnBlockAccess, gbc_btnAbort);

	}

	public String getUuid() {
		return myUuid;
	}

	public static AccessControlPanel getEmptyNotificationPanel(String text, NotificationPanelClosedListener listener){
		AccessControlPanel panel = new AccessControlPanel(null, text, EMPTY, listener, null);
		panel.btnBlockAccess.setVisible(false);
		panel.remove(panel.btnBlockAccess);
		panel.btnAllowAccess.setVisible(false);
		panel.remove(panel.btnAllowAccess);

		return panel;
	}
	
	public void closeMe(){
		if (preferenceEffect==PrivacyOutcomeConstantsBean.ALLOW){
			this.btnAllowAccess.doClick();
		}else if (preferenceEffect==PrivacyOutcomeConstantsBean.BLOCK){
			this.btnBlockAccess.doClick();
		}
	}


	public void setUserClicked(boolean userClicked) {
		this.userClicked = userClicked;
	}
}
