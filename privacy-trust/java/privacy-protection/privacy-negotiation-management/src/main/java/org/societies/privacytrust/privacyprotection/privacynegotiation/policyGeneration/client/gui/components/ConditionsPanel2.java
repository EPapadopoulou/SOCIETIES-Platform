package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

public class ConditionsPanel2 extends JPanel {

	private List<ConditionPanel> conditionPanels;
	private ResponseItem respItem;
	private RequestItem reqItem;
	private boolean firstRound;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ConditionsPanel2(RequestorBean requestor, RequestItem reqItem, ResponseItem respItem, boolean firstRound) {
		this.reqItem = reqItem;
		this.respItem = respItem;
		this.firstRound = firstRound;

		setLayout(null);

		setBounds(0, 0, 650, 200);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 650, 200);
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		
		conditionPanels = new ArrayList<ConditionPanel>();


		ConditionPanel dataRetention = null;
		ConditionPanel share = null;
		ConditionPanel infer = null;
		ConditionPanel optOut = null;
		ConditionPanel store = null;
		ConditionPanel access = null;
		ConditionPanel correct = null;
		for (Condition condition : reqItem.getConditions()){
			Condition suggestedCondition = null;
			if (respItem!=null){
				if (respItem.getRequestItem().getConditions()!=null){
					for (Condition sCondition : respItem.getRequestItem().getConditions()){
						if (sCondition.getConditionConstant().equals(condition.getConditionConstant())){
							suggestedCondition = sCondition;
							break;
						}
					}
				}
			}
			switch (condition.getConditionConstant()){
			case DATA_RETENTION:
				dataRetention = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(dataRetention);
				break;
			case SHARE_WITH_3RD_PARTIES:
				share = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(share);
				break;
			case MAY_BE_INFERRED:
				infer = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(infer);
				break;
			case STORE_IN_SECURE_STORAGE:
				store = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(store);
				break;
			case RIGHT_TO_OPTOUT:
				optOut = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(optOut);
				break;
			case RIGHT_TO_ACCESS_HELD_DATA:
				access = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(access);
				break;
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				correct = new ConditionPanel(requestor, condition, suggestedCondition, firstRound, reqItem.getResource().getDataType());
				conditionPanels.add(correct);
				
			}
			
		}
		
		
		int yAxis = 0;
		
		
		dataRetention.setBounds(0, yAxis, 650, 28);
		panel.add(dataRetention);
		yAxis+=28;
		
		share.setBounds(0, yAxis, 650, 28);
		panel.add(share);
		yAxis+=28;
		
		infer.setBounds(0, yAxis, 650, 28);
		panel.add(infer);
		yAxis+=28;
		
		store.setBounds(0, yAxis, 650, 28);
		panel.add(store);
		yAxis+=28;
		
		optOut.setBounds(0, yAxis, 650, 28);
		panel.add(optOut);
		yAxis+=28;
		
		access.setBounds(0, yAxis, 650, 28);
		panel.add(access);
		yAxis+=28;
		
		correct.setBounds(0, yAxis, 650, 28);
		panel.add(correct);
		yAxis+=28;

	}

	public void applyPersonalisation() {
		for (ConditionPanel conPanel : this.conditionPanels){
			conPanel.applyPersonalisation();
		}

	}

	public void resetChanges(){
		for (ConditionPanel conPanel : this.conditionPanels){
			conPanel.resetChanges();
		}
	}

	public List<Condition> getConditions() {
		List<Condition> conditions = new ArrayList<Condition>();
		for (ConditionPanel conPanel  : this.conditionPanels){
			Condition condition = conPanel.getCondition();
			if (condition!=null){
				conditions.add(condition);
			}
		}
		return conditions;
	}

	public boolean match() {
		//if any of the conditions are different return false
		for (ConditionPanel conPanel : conditionPanels){
			if (!conPanel.match()){
				return false;
			}
		}
		//if none of the conditios have been altered
		return true;
	}
}
