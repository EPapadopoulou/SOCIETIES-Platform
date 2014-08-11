package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.ActionsPanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.ConditionsPanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.DecisionPanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.PurposePanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.ToggleButtonActionListener;

import com.alee.laf.separator.WebSeparator;


public class RequestItemPanel extends JPanel {

	private RequestItem item;
	private ActionsPanel actionsPanel;
	private ConditionsPanel conditionsPanel;
	private JButton btnPersonalise;
	private DecisionPanel decisionPanel;
	private JButton btnRestoreChanges;
	private boolean firstRound;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public RequestItemPanel(RequestItem requestItem, ResponseItem responseItem, boolean firstRound) {
		
		this.item = requestItem;
		this.firstRound = firstRound;
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
		
		PurposePanel purposePanel = new PurposePanel(requestItem.getPurpose());
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
		actionsPanel = new ActionsPanel(requestItem, responseItem, null, firstRound);
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
		
		conditionsPanel = new ConditionsPanel(requestItem, responseItem, firstRound);
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
		
		JLabel lblDecision = new JLabel("Decision");
		GridBagConstraints gbc_lblDecision = new GridBagConstraints();
		gbc_lblDecision.insets = new Insets(0, 0, 5, 5);
		gbc_lblDecision.gridx = 0;
		gbc_lblDecision.gridy = 6;
		add(lblDecision, gbc_lblDecision);
		
		ToggleButtonActionListener toggleButtonActionListener = new ToggleButtonActionListener(this);
		Decision decision = Decision.PERMIT;
		if (responseItem!=null){
			decision = responseItem.getDecision();
		}
		
		decisionPanel = new DecisionPanel(toggleButtonActionListener, decision);
		GridBagConstraints gbc_decisionPanel = new GridBagConstraints();
		gbc_decisionPanel.anchor = GridBagConstraints.WEST;
		gbc_decisionPanel.insets = new Insets(0, 20, 5, 0);
		gbc_decisionPanel.fill = GridBagConstraints.VERTICAL;
		gbc_decisionPanel.gridx = 1;
		gbc_decisionPanel.gridy = 6;
		add(decisionPanel, gbc_decisionPanel);
		
		WebSeparator separator_3 = new WebSeparator();
		separator_3.setDrawSideLines(false);
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridwidth = 2;
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 7;
		add(separator_3, gbc_separator_3);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.insets = new Insets(0, 20, 0, 0);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 8;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		btnPersonalise = new JButton("Get Personalised Suggestions");
		btnPersonalise.addActionListener(new PersonalisationActionListener());
		btnPersonalise.setToolTipText("Click here to apply your privacy preferences and get personalised suggestions for how to negotiate based on your previous negotiation interactions");
		GridBagConstraints gbc_btnPersonalise = new GridBagConstraints();
		gbc_btnPersonalise.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPersonalise.insets = new Insets(0, 5, 5, 0);
		gbc_btnPersonalise.gridx = 0;
		gbc_btnPersonalise.gridy = 0;
		panel.add(btnPersonalise, gbc_btnPersonalise);
		
		btnRestoreChanges = new JButton("Restore changes");
		btnRestoreChanges.addActionListener(new ResetActionListener());
		btnRestoreChanges.setToolTipText("Click here to reset the form to view the original Request Policy without personalisation suggestions");
		GridBagConstraints gbc_btnRestoreChanges = new GridBagConstraints();
		gbc_btnRestoreChanges.insets = new Insets(0, 5, 5, 0);
		gbc_btnRestoreChanges.anchor = GridBagConstraints.EAST;
		gbc_btnRestoreChanges.gridx = 1;
		gbc_btnRestoreChanges.gridy = 0;
		panel.add(btnRestoreChanges, gbc_btnRestoreChanges);
		this.setMaximumSize(new Dimension(800, 600));
		this.setMinimumSize(new Dimension(800, 600));

	}
	

	public void setEnabledPanels(boolean bool){
		this.actionsPanel.setEnabledChckBoxes(bool);
	}
	
	
	class PersonalisationActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (RequestItemPanel.this.btnPersonalise.equals(e.getSource())){
				RequestItemPanel.this.applyPersonalisation();
			}
			
		}
		
	}


	class ResetActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (RequestItemPanel.this.btnRestoreChanges.equals(arg0.getSource())){
				RequestItemPanel.this.resetChanges();
			}
			
		}
		
	}
	public void applyPersonalisation() {
		
		this.conditionsPanel.applyPersonalisation();
		this.actionsPanel.applyPersonalisation();
		this.decisionPanel.applyPersonalisation();
	}


	public void resetChanges() {
		this.conditionsPanel.resetChanges();
		this.actionsPanel.resetChanges();
		this.decisionPanel.resetChanges();
		
	}


	public ResponseItem getResponseItem() {
		ResponseItem respItem = new ResponseItem();
		
		
		if (this.decisionPanel.getDecision().equals(Decision.PERMIT)){
			if (hasResponseBeenEditedByUser()){
				respItem.setDecision(Decision.INDETERMINATE);
			}
			else {
				respItem.setDecision(Decision.PERMIT);
			}
		}else{
			respItem.setDecision(Decision.DENY);
		}
		RequestItem reqItem = new RequestItem();
		
		reqItem.setActions(this.actionsPanel.getActions());
		reqItem.setConditions(this.conditionsPanel.getConditions());
		reqItem.setResource(this.item.getResource());
		reqItem.setPurpose(this.item.getPurpose());
		respItem.setRequestItem(reqItem);
		return respItem;
	}


	private boolean hasResponseBeenEditedByUser() {
		if (!this.actionsPanel.match()){
			return false;
		}
		
		if (!this.conditionsPanel.match()){
			return false;
		}
		
		return true;
	}

}
