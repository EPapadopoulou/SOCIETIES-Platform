package ac.hw.personis.notification;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class TextNotification extends NotificationPanel {

	private final String uuid;
	private final String message;

	/**
	 * Create the panel.
	 */
	public TextNotification(String message) {
		this.message = message;
		this.uuid = UUID.randomUUID().toString();
		
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		System.out.println("Bounds: "+getBounds().x+","+getBounds().y);
		GridBagLayout gridBagLayout = new GridBagLayout();
		//gridBagLayout.columnWidths = new int[]{101, 133, 100, 0};
		//gridBagLayout.rowHeights = new int[]{98, 28, 0};
		//gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		//gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		//JLabel lblNotificationText = new JLabel("<html> <span style='width: 200px'>"+text+"</span></html>");
		JLabel lblNotificationText = new JLabel("<html> <p>"+message+"</p></html>");
		lblNotificationText.setPreferredSize(new Dimension(300, 100));
		GridBagConstraints gbc_lblNotificationText = new GridBagConstraints();
		gbc_lblNotificationText.fill = GridBagConstraints.BOTH;
		gbc_lblNotificationText.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotificationText.gridwidth = 3;
		gbc_lblNotificationText.gridx = 0;
		gbc_lblNotificationText.gridy = 0;
		add(lblNotificationText, gbc_lblNotificationText);
	}

	@Override
	public String getUuid() {
		return this.uuid;
	}

}
