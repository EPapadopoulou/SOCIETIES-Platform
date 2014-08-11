package org.societies.privacytrust.privacyprotection.privacypolicy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.components.ActionsPanel;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.components.ConditionsPanel;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.components.PurposePanel;

import com.alee.laf.separator.WebSeparator;


public class RequestItemPanel extends JPanel {

	private RequestItem item;
	private ActionsPanel actionsPanel;
	private ConditionsPanel conditionsPanel;
	private PurposePanel purposePanel;

	/**
	 * Create the panel.
	 */
	public RequestItemPanel(RequestItem requestItem) {
		
		this.item = requestItem;
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{75, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblPurpose = new JLabel("Purpose");
		GridBagConstraints gbc_lblPurpose = new GridBagConstraints();
		gbc_lblPurpose.fill = GridBagConstraints.VERTICAL;
		gbc_lblPurpose.insets = new Insets(0, 0, 5, 5);
		gbc_lblPurpose.gridx = 0;
		gbc_lblPurpose.gridy = 0;
		add(lblPurpose, gbc_lblPurpose);
		
		//remove following 4 lines
		String purpose = "Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		purpose +="Your location will be tracked to offer you services nearby";
		String label = "<html>"+purpose+"</html>";
		
		purposePanel = new PurposePanel(requestItem.getPurpose());
		GridBagConstraints gbc_purposePanel = new GridBagConstraints();
		gbc_purposePanel.insets = new Insets(20, 20, 5, 5);
		gbc_purposePanel.fill = GridBagConstraints.BOTH;
		gbc_purposePanel.gridx = 1;
		gbc_purposePanel.gridy = 0;
		add(purposePanel, gbc_purposePanel);
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_2.gridwidth = 2;
		gbc_2.anchor = GridBagConstraints.WEST;
		gbc_2.insets = new Insets(0, 0, 5, 0);
		gbc_2.gridx = 0;
		gbc_2.gridy = 1;
		WebSeparator separator_1 = new WebSeparator(WebSeparator.HORIZONTAL);
		
		separator_1.setDrawSideLines(false);//new JSeparator(JSeparator.HORIZONTAL);
		add(separator_1, gbc_2);
		
		JLabel lblActions = new JLabel("Actions");
		GridBagConstraints gbc_lblActions = new GridBagConstraints();
		gbc_lblActions.insets = new Insets(0, 0, 5, 5);
		gbc_lblActions.gridx = 0;
		gbc_lblActions.gridy = 2;
		add(lblActions, gbc_lblActions);
		GridBagConstraints gbc_3 = new GridBagConstraints();
		gbc_3.anchor = GridBagConstraints.WEST;
		gbc_3.insets = new Insets(0, 20, 5, 0);
		gbc_3.gridx = 1;
		gbc_3.gridy = 2;
		actionsPanel = new ActionsPanel(requestItem);
		add(actionsPanel, gbc_3);
		GridBagLayout gbl_actionsPanel = new GridBagLayout();
		gbl_actionsPanel.columnWidths = new int[]{0};
		gbl_actionsPanel.rowHeights = new int[]{0};
		gbl_actionsPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_actionsPanel.rowWeights = new double[]{Double.MIN_VALUE};
		actionsPanel.setLayout(gbl_actionsPanel);
		GridBagConstraints gbc_4 = new GridBagConstraints();
		gbc_4.insets = new Insets(0, 20, 5, 20);
		gbc_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_4.gridwidth = 2;
		gbc_4.anchor = GridBagConstraints.WEST;
		gbc_4.gridx = 0;
		gbc_4.gridy = 3;
		WebSeparator separator = new WebSeparator(WebSeparator.HORIZONTAL);
		separator.setDrawSideLines(false);
		add(separator, gbc_4);
		
		JLabel lblConditions = new JLabel("Conditions");
		GridBagConstraints gbc_lblConditions = new GridBagConstraints();
		gbc_lblConditions.insets = new Insets(0, 0, 5, 5);
		gbc_lblConditions.gridx = 0;
		gbc_lblConditions.gridy = 4;
		add(lblConditions, gbc_lblConditions);
		
		conditionsPanel = new ConditionsPanel(requestItem);
		GridBagConstraints gbc_conditionsPanel = new GridBagConstraints();
		gbc_conditionsPanel.anchor = GridBagConstraints.WEST;
		gbc_conditionsPanel.insets = new Insets(0, 20, 5, 0);
		gbc_conditionsPanel.gridx = 1;
		gbc_conditionsPanel.gridy = 4;
		add(conditionsPanel, gbc_conditionsPanel);
		
		WebSeparator separator_2 = new WebSeparator(WebSeparator.HORIZONTAL);
		separator_2.setDrawSideLines(false);
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.anchor = GridBagConstraints.WEST;
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 5;
		add(separator_2, gbc_separator_2);

		this.setMaximumSize(new Dimension(800, 600));
		this.setMinimumSize(new Dimension(800, 600));

	}

	public RequestItem getRequestItem() {
		RequestItem requestItem = new RequestItem();
		requestItem.setResource(this.item.getResource());
		requestItem.setActions(this.actionsPanel.getActions());
		requestItem.setConditions(this.conditionsPanel.getConditions());
		requestItem.setPurpose(this.purposePanel.getPurpose());
		return requestItem;
	}
	
	public ConditionRanges getConditionRanges(){
		return this.conditionsPanel.getConditionRanges();
		
		
	}
	

}
