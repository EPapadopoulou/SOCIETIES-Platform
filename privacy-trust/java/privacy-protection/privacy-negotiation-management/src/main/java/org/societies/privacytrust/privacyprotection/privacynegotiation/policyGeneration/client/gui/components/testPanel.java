package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import javax.swing.JPanel;
import javax.swing.JLabel;

public class testPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public testPanel() {
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(24, 11, 693, 365);
		add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(0, 0, 610, 25);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setBounds(0, 25, 610, 25);
		panel.add(lblNewLabel_1);
		
		JLabel label = new JLabel("New label");
		label.setBounds(0, 50, 610, 25);
		panel.add(label);

	}

}
