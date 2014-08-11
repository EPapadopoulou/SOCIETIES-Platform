package org.societies.privacytrust.privacyprotection.privacypolicy.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.privacytrust.privacyprotection.api.policy.BooleanRange;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.api.policy.DataRetentionRange;
import org.societies.privacytrust.privacyprotection.api.policy.ShareDataRange;

public class ConditionsPanel extends JPanel {

	private List<ConditionPanel> conditionPanels;

	private RequestItem reqItem;

	/**
	 * Create the panel.
	 */
	public ConditionsPanel(RequestItem reqItem) {
		this.reqItem = reqItem;


		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};

		setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);




		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;

		List<ConditionConstants> conList = new ArrayList<ConditionConstants>();
		for (ConditionConstants con : ConditionConstants.values()){
			conList.add(con);
		}
		conditionPanels = new ArrayList<ConditionPanel>();
		System.out.println(conList.size());


		for (Condition condition : reqItem.getConditions()){

			ConditionPanel conPanel = new ConditionPanel(condition);


			panel.add(conPanel, gbc);

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


			ConditionPanel conPanel = new ConditionPanel(condition);
			panel.add(conPanel, gbc);
			conditionPanels.add(conPanel);
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

	public ConditionRanges getConditionRanges(){
		

		DataRetentionRange dataRetentionRange = null;
		ShareDataRange shareDataRange = null;
		BooleanRange mayInferBooleanRange = null;
		BooleanRange storeSecureBooleanRange = null;
		BooleanRange accessHeldBooleanRange = null;
		BooleanRange correctDataBooleanRange = null;
		BooleanRange rightToOptOutBooleanRange = null;
		
		for (ConditionPanel conPanel  : this.conditionPanels){
			switch (conPanel.getCondition().getConditionConstant()){
			case DATA_RETENTION:
				dataRetentionRange = conPanel.getDataRetentionRange();
				break;
			case SHARE_WITH_3RD_PARTIES:
				shareDataRange = conPanel.getShareDataRange();
				break;
			case MAY_BE_INFERRED:
				mayInferBooleanRange = conPanel.getBooleanRange();
				break;
			case RIGHT_TO_ACCESS_HELD_DATA:
				accessHeldBooleanRange = conPanel.getBooleanRange();
				break;
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				correctDataBooleanRange = conPanel.getBooleanRange();
				break;
			case RIGHT_TO_OPTOUT:
				rightToOptOutBooleanRange = conPanel.getBooleanRange();
				break;
			case STORE_IN_SECURE_STORAGE:
				storeSecureBooleanRange = conPanel.getBooleanRange();
				break;
			}
		}
		
		ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
		

		return ranges;
	}
}
