package org.societies.privacytrust.privacyprotection.privacypolicy.gui.components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.privacytrust.privacyprotection.api.policy.BooleanRange;
import org.societies.privacytrust.privacyprotection.api.policy.DataRetentionRange;
import org.societies.privacytrust.privacyprotection.api.policy.ShareDataRange;

public class ConditionPanel extends JPanel {
	private JComboBox comboBox;
	private Condition requestedCondition;
	private JPanel panel;
	private JLabel lblAcceptableRange;
	private JLabel lblBetween;
	private JComboBox cmb_Between2;
	private JLabel lblAnd;
	private JComboBox cmb_Between1;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create the panel.
	 */
	public ConditionPanel(Condition requestedCondition) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		this.requestedCondition = requestedCondition;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{55, 374, 0};
		gridBagLayout.rowHeights = new int[]{30, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel(ConditionConstantsFriendly.getFriendlyName(requestedCondition.getConditionConstant()));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		comboBox = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboBox, gbc_comboBox);

		//if ((requestedCondition.getConditionConstant().equals(ConditionConstants.DATA_RETENTION)) || (requestedCondition.getConditionConstant().equals(ConditionConstants.SHARE_WITH_3RD_PARTIES))){
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(5, 50, 5, 0);
		gbc_panel.gridwidth = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
		lblAcceptableRange = new JLabel("Accepted Range:");
		panel.add(lblAcceptableRange);
		panel.setLayout(boxLayout);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		lblBetween = new JLabel("Between");
		panel.add(lblBetween);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		cmb_Between1 = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));
		panel.add(cmb_Between1);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		lblAnd = new JLabel("and");
		panel.add(lblAnd);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		cmb_Between2 = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));
		cmb_Between2.setSelectedItem(requestedCondition.getValue());
		panel.add(cmb_Between2);
		//}

		comboBox.setSelectedItem(requestedCondition.getValue());
		logging.debug("Setup of ranges, requestedCondition value: "+requestedCondition.getValue()+" combobox displays: "+comboBox.getSelectedItem().toString());



	}




	public Condition getCondition(){

		Condition con = new Condition();
		con.setConditionConstant(requestedCondition.getConditionConstant());
		con.setValue((String) this.comboBox.getSelectedItem());
		return con;
	}

	public  BooleanRange getBooleanRange(){
		try {
			//JOptionPane.showMessageDialog(this, "returning new boolean range: "+cmb_Between1.getSelectedItem()+" - "+cmb_Between2.getSelectedItem());
			return new BooleanRange(cmb_Between1.getSelectedItem().toString(), cmb_Between2.getSelectedItem().toString(), this.requestedCondition.getConditionConstant());

		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ShareDataRange getShareDataRange(){
		try {
			return new ShareDataRange(cmb_Between1.getSelectedItem().toString(), cmb_Between2.getSelectedItem().toString());
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public DataRetentionRange getDataRetentionRange(){
		try {
			return new DataRetentionRange(cmb_Between1.getSelectedItem().toString(), cmb_Between2.getSelectedItem().toString());
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
