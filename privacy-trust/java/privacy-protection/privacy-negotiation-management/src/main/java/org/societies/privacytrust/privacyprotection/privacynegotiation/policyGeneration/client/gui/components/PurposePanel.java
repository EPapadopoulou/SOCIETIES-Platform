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
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		//remove following 4 lines
		purpose +="Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		String label = "<html>"+purpose+"</html>";
		
		JLabel purposeLbl = new JLabel(label);
		GridBagConstraints gbc_jlabel = new GridBagConstraints();
		gbc_jlabel.fill = GridBagConstraints.BOTH;
		gbc_jlabel.gridx = 0;
		gbc_jlabel.gridy = 0;
		add(purposeLbl, gbc_jlabel);
		

		


	}


}
