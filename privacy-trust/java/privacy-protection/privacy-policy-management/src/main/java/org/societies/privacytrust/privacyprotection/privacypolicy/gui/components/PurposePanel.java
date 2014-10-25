package org.societies.privacytrust.privacyprotection.privacypolicy.gui.components;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class PurposePanel extends JPanel {

	private JTextArea textArea;

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
		
		textArea = new JTextArea(purpose);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 0;
		add(textArea, gbc_textArea);
		

		


	}

	public String getPurpose() {
		
		return this.textArea.getText();
		
	}


}
