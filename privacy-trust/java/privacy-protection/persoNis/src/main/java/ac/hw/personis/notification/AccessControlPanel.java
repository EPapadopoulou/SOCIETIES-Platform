package ac.hw.personis.notification;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseAccCtrlEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;

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
		System.out.println("Bounds: "+getBounds().x+","+getBounds().y);
		GridBagLayout gridBagLayout = new GridBagLayout();
		//gridBagLayout.columnWidths = new int[]{101, 133, 100, 0};
		//gridBagLayout.rowHeights = new int[]{98, 28, 0};
		//gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		//gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		//JLabel lblNotificationText = new JLabel("<html> <span style='width: 200px'>"+text+"</span></html>");
		JLabel lblNotificationText = new JLabel("<html> <p>"+text+"</p></html>");
		lblNotificationText.setPreferredSize(new Dimension(300, 100));
		GridBagConstraints gbc_lblNotificationText = new GridBagConstraints();
		gbc_lblNotificationText.fill = GridBagConstraints.BOTH;
		gbc_lblNotificationText.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotificationText.gridwidth = 3;
		gbc_lblNotificationText.gridx = 0;
		gbc_lblNotificationText.gridy = 0;
		add(lblNotificationText, gbc_lblNotificationText);



		btnBlockAccess = new JButton("Block Access");
		btnBlockAccess.setActionCommand(uuid);
		btnBlockAccess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UserResponseAccCtrlEvent urEvent = new UserResponseAccCtrlEvent(myUuid, PrivacyOutcomeConstantsBean.BLOCK, userClicked);


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
		GridBagConstraints gbc_btnBlockAccess = new GridBagConstraints();
		gbc_btnBlockAccess.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBlockAccess.insets = new Insets(0, 0, 0, 5);
		gbc_btnBlockAccess.gridx = 0;
		gbc_btnBlockAccess.gridy = 1;
		add(btnBlockAccess, gbc_btnBlockAccess);
		//setBounds(0, 0, 350, 200);
		btnAllowAccess = new JButton("Allow Access");
		btnAllowAccess.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!myUuid.equalsIgnoreCase(EMPTY)){
					UserResponseAccCtrlEvent urEvent = new UserResponseAccCtrlEvent(myUuid, PrivacyOutcomeConstantsBean.ALLOW, userClicked);


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
		GridBagConstraints gbc_btnAllowAccess = new GridBagConstraints();
		gbc_btnAllowAccess.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnAllowAccess.gridx = 2;
		gbc_btnAllowAccess.gridy = 1;
		add(btnAllowAccess, gbc_btnAllowAccess);

	}

	public String getUuid() {
		return myUuid;
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
