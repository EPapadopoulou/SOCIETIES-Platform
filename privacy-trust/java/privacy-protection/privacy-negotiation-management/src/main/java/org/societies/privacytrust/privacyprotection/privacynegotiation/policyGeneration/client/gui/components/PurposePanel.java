package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class PurposePanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public PurposePanel(String purpose) {

		String label = "<html>"+purpose+"</html>";
		setLayout(null);

		setBounds(0,0, 645, 26);
		JLabel purposeLbl = new JLabel(label);
		purposeLbl.setBounds(0, 0, 640, 25);

		add(purposeLbl);
		

		


	}


}
