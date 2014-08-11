package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConditionPanel extends JPanel {
	private JComboBox comboBox;
	private Condition requestedCondition;
	private Condition suggestedCondition;
	private boolean firstRound;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ConditionPanel(Condition requestedCondition, Condition suggestedCondition, boolean firstRound) {
		this.requestedCondition = requestedCondition;
		this.suggestedCondition = suggestedCondition;
		this.firstRound = firstRound;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{55, 374, 0};
		gridBagLayout.rowHeights = new int[]{30, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel(ConditionConstantsFriendly.getFriendlyName(requestedCondition.getConditionConstant()));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		comboBox = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboBox, gbc_comboBox);


		comboBox.setSelectedItem(requestedCondition.getValue());
		comboBox.setEnabled(firstRound);


	}

	public void applyPersonalisation() {
		if (this.suggestedCondition!=null){

			if (this.requestedCondition.getConditionConstant().equals(ConditionConstants.DATA_RETENTION)){
				try {
					comboBox.setSelectedItem(PrivacyConditionsConstantValues.getBetterDataRetention(requestedCondition.getValue(), suggestedCondition.getValue()));
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (this.requestedCondition.getConditionConstant().equals(ConditionConstants.SHARE_WITH_3RD_PARTIES)){
				
				try {
					comboBox.setSelectedItem(PrivacyConditionsConstantValues.getBetterSharedValue(requestedCondition.getValue(), suggestedCondition.getValue()));
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				comboBox.setSelectedItem(PrivacyConditionsConstantValues.getBetterConditionValue(requestedCondition.getConditionConstant()));
				
			}

		}
	}

	public void resetChanges(){

		comboBox.setSelectedItem(this.requestedCondition.getValue());

	}

	public Condition getCondition(){

		Condition con = new Condition();
		con.setConditionConstant(requestedCondition.getConditionConstant());
		con.setValue((String) this.comboBox.getSelectedItem());

		return con;
	}

	public boolean match() {

		return requestedCondition.getValue().equalsIgnoreCase(this.getCondition().getValue());
		/*		Condition userCondition = this.getCondition();
		//System.out.println("IsSelected: "+(userCondition==null)+" (null==requestedCondition.getValue() || \"\"==requestedCondition.getValue() : "+(null==requestedCondition.getValue() || ""==requestedCondition.getValue()));
		//if condition doesn't exist in policy and user has not added it then return true
		if ((null==requestedCondition.getValue() || ""==requestedCondition.getValue()) && null==userCondition){

			return true;
		}
		//if condition doesn't exist in policy but user has selected it, then return false
		if ((null==requestedCondition.getValue() || ""==requestedCondition.getValue()) && null!=userCondition){

			return false;
		}

		//if condition exists in policy but user has deselected it return false
		if ((null!=requestedCondition.getValue() || ""!=requestedCondition.getValue()) && null==userCondition){

			return false;
		}

		//if condition condition exists in policy but user has changed the value, return false, else return true

		boolean equal = ConditionUtils.equal(requestedCondition, userCondition);

		return equal;
		 */	}


}
