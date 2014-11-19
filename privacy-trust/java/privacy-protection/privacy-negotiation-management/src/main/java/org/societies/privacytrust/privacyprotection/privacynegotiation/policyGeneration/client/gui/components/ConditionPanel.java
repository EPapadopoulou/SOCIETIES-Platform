package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

import com.alee.laf.separator.WebSeparator;

public class ConditionPanel extends JPanel {
	private JComboBox comboBox;
	private Condition requestedCondition;
	private Condition suggestedCondition;
	private boolean firstRound;
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
		lblNewLabel.setToolTipText(getToolTipText());
		lblNewLabel.setBounds(2, 0, 429, 20);
		add(lblNewLabel);

		comboBox = new JComboBox(PrivacyConditionsConstantValues.getValues(requestedCondition.getConditionConstant()));
		comboBox.setBounds(441, 0, 180, 20);
		add(comboBox);


		
		comboBox.setSelectedItem(requestedCondition.getValue());
		comboBox.setToolTipText(getToolTipText());
		System.out.println("I set the selectedItem to: "+requestedCondition.getValue()+" and the combobox is displaying: "+comboBox.getSelectedItem().toString());
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
		lblIcon.setBounds(625, 0, 23, 23);
		add(lblIcon);

	}
	
	private ImageIcon createWarningImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/warning.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			System.err.println("Can't find warning image");
			return null;
		}
	}

	private ImageIcon createCheckImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/check.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			System.err.println("Can't find check image");
			return null;
		}
	}

	private ImageIcon createLeftArrowImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/left_arrow_galazio_23.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			System.err.println("Can't find arrow image");
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

	
	@Override
	public String getToolTipText(){
		switch (requestedCondition.getConditionConstant()){
		case DATA_RETENTION:
			return "Edit this field to demand your preferred length of time you want the service provider to keep your data on their servers.";
		case MAY_BE_INFERRED:
			return "Edit this field to indicate if you want this data to be combined with other data to infer further information about you.";
		case RIGHT_TO_ACCESS_HELD_DATA:
			return "Edit this field to request having access to all the data that the provider retrieves from you.";
		case RIGHT_TO_CORRECT_INCORRECT_DATA:
			return "Edit this field to request the ability to change held by the service provider about you.";
		case RIGHT_TO_OPTOUT:
			return "Edit this field to request the option to stop disclosing this data at any time.";
		case SHARE_WITH_3RD_PARTIES:
			return "Edit this field to request the level of sharing you allow for this data.";  
		case STORE_IN_SECURE_STORAGE:
			return "Edit this field to request that your data be stored securely by the service provider.";
		}
		return "";
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
