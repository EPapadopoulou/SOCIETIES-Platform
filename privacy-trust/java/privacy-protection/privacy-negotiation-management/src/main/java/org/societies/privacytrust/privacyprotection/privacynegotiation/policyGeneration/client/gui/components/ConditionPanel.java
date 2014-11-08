package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.alee.laf.separator.WebSeparator;

public class ConditionPanel extends JPanel {
	private JComboBox comboBox;
	private Condition requestedCondition;
	private Condition suggestedCondition;
	private boolean firstRound;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private JLabel lblIcon;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ConditionPanel(Condition requestedCondition, Condition suggestedCondition, boolean firstRound) {
		this.firstRound = firstRound;
		this.requestedCondition = requestedCondition;
		this.suggestedCondition = suggestedCondition;
		this.firstRound = firstRound;
		setLayout(null);
		setBounds(0, 0, 650, 25);

		JLabel lblNewLabel = new JLabel(ConditionConstantsFriendly.getFriendlyName(requestedCondition.getConditionConstant()));
		lblNewLabel.setBounds(2, 0, 380, 20);
		add(lblNewLabel);

		comboBox = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));
		comboBox.setBounds(390, 0, 220, 20);
		add(comboBox);


		
		comboBox.setSelectedItem(requestedCondition.getValue());
		logging.debug("I set the selectedItem to: "+requestedCondition.getValue()+" and the combobox is displaying: "+comboBox.getSelectedItem().toString());
		comboBox.setEnabled(firstRound);
		
		WebSeparator webSeparator = new WebSeparator(0);
		webSeparator.setDrawSideLines(false);
		webSeparator.setBounds(0, 22, 650, 3);
		add(webSeparator);
		
		lblIcon = new JLabel();
		if (!firstRound){
			if (requestedCondition.getValue().equalsIgnoreCase(suggestedCondition.getValue())){
				lblIcon.setIcon(createCheckImageIcon());
			}else{
				lblIcon.setIcon(createWarningImageIcon());
			}
		}
		lblIcon.setBounds(625, 1, 23, 23);
		add(lblIcon);

	}
	
	private ImageIcon createWarningImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/warning.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	private ImageIcon createCheckImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/check.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	private ImageIcon createLeftArrowImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/left_arrow_galazio_23.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	public void applyPersonalisation() {
		if (this.suggestedCondition!=null){
			String suggestedConditionValue = "";
			if (this.requestedCondition.getConditionConstant().equals(ConditionConstants.DATA_RETENTION)){
				try {
					suggestedConditionValue = PrivacyConditionsConstantValues.getBetterDataRetention(requestedCondition.getValue(), suggestedCondition.getValue());
					comboBox.setSelectedItem(suggestedConditionValue);
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (this.requestedCondition.getConditionConstant().equals(ConditionConstants.SHARE_WITH_3RD_PARTIES)){
				
				try {
					suggestedConditionValue = PrivacyConditionsConstantValues.getBetterSharedValue(requestedCondition.getValue(), suggestedCondition.getValue());
					comboBox.setSelectedItem(suggestedConditionValue);
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				suggestedConditionValue = PrivacyConditionsConstantValues.getBetterConditionValue(requestedCondition.getConditionConstant());
				comboBox.setSelectedItem(suggestedConditionValue);
				
			}
			if (!requestedCondition.getValue().equalsIgnoreCase(suggestedConditionValue)){
				this.lblIcon.setIcon(this.createLeftArrowImageIcon());
			}
		}
	}

	public void resetChanges(){

		comboBox.setSelectedItem(this.requestedCondition.getValue());
		this.lblIcon.setIcon(null);

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
