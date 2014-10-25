package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

public class ConditionsPanel extends JPanel {

	private List<ConditionPanel> conditionPanels;
	private ResponseItem respItem;
	private RequestItem reqItem;
	private boolean firstRound;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ConditionsPanel(RequestItem reqItem, ResponseItem respItem, boolean firstRound) {
		this.reqItem = reqItem;
		this.respItem = respItem;
		this.firstRound = firstRound;

		setLayout(null);

		setBounds(0, 0, 650, 200);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 650, 200);
		add(panel);




		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		int yAxis = 0;
		List<ConditionConstants> conList = new ArrayList<ConditionConstants>();
		for (ConditionConstants con : ConditionConstants.values()){
			conList.add(con);
		}
		conditionPanels = new ArrayList<ConditionPanel>();
		System.out.println(conList.size());


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
			ConditionPanel conPanel = new ConditionPanel(condition, suggestedCondition, firstRound);

			conPanel.setBounds(0, yAxis, 650, 28);
			yAxis+=28;
			panel.add(conPanel);

			conditionPanels.add(conPanel);
			for (ConditionConstants con : conList){
				if (con.equals(condition.getConditionConstant())){
					conList.remove(con);
					break;
				}
			}
		}

		//System.out.println(conList.size());
		for (ConditionConstants con : conList){
			Condition condition = new Condition();
			condition.setConditionConstant(con);
			Condition suggestedCondition = null;
			if (respItem!=null){
				for (Condition sCondition : respItem.getRequestItem().getConditions()){
					if (sCondition.getConditionConstant().equals(condition.getConditionConstant())){
						suggestedCondition = sCondition;
						break;
					}
				}
			}
			ConditionPanel conPanel = new ConditionPanel(condition, suggestedCondition, firstRound);
			conPanel.setBounds(0, yAxis, 650, 28);
			yAxis+=28;
			panel.add(conPanel);
			conditionPanels.add(conPanel);
		}
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
