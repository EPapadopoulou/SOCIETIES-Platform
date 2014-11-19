package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;


import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.ConditionsPanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.DecisionPanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.PurposePanel;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.ToggleButtonActionListener;

import com.alee.laf.separator.WebSeparator;


public class RequestItemPanel extends JPanel {

	private RequestItem item;
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
		String string = "    Conditions for accessing: "+requestItem.getResource().getDataType()+"    ";
		
		
		TitledBorder titledBorder = new TitledBorder(null, string, TitledBorder.LEADING, TitledBorder.TOP, null, null);
		
		titledBorder.setTitleFont(new Font("Tahoma", Font.BOLD, 14));
		titledBorder.setTitleColor(new Color(10,29,139));
		setBorder(titledBorder);
		
		setLayout(null);
		setBounds(0,0, 760, 387);
		JLabel lblPurpose = new JLabel("Purpose");
		lblPurpose.setBounds(18, 32, 100, 16);
		add(lblPurpose);
		
		PurposePanel purposePanel = new PurposePanel(requestItem.getPurpose());
		lblPurpose.setLabelFor(purposePanel);
		purposePanel.setBounds(120, 30, 650, 27);
		add(purposePanel);
		purposePanel.setLayout(null);
		WebSeparator separator_1 = new WebSeparator(WebSeparator.HORIZONTAL);
		separator_1.setBounds(3, 57, 756, 3);
		
		separator_1.setDrawSideLines(false);//new JSeparator(JSeparator.HORIZONTAL);
		add(separator_1);
		
		JLabel lblConditions = new JLabel("Conditions");
		lblConditions.setBounds(18, 149, 100, 14);
		add(lblConditions);
		
		conditionsPanel = new ConditionsPanel(requestItem, responseItem, firstRound);
		lblConditions.setLabelFor(conditionsPanel);
		conditionsPanel.setBounds(120, 67, 650, 200);
		add(conditionsPanel);
		
		WebSeparator separator_2 = new WebSeparator(WebSeparator.HORIZONTAL);
		separator_2.setBounds(3, 278, 747, 3);
		separator_2.setDrawSideLines(false);
		add(separator_2);
		
		JLabel lblDecision = new JLabel("Decision");
		lblDecision.setBounds(18, 299, 100, 14);
		add(lblDecision);
		
		Decision decision = Decision.PERMIT;
		if (responseItem!=null){
			decision = responseItem.getDecision();
		}
		
		ToggleButtonActionListener btnActionListener = new ToggleButtonActionListener(this);
		decisionPanel = new DecisionPanel(decision, btnActionListener);
		lblDecision.setLabelFor(decisionPanel);
		decisionPanel.setBounds(120, 292, 152, 26);
		add(decisionPanel);
		
		WebSeparator separator_3 = new WebSeparator();
		separator_3.setBounds(0, 333, 748, 3);
		separator_3.setDrawSideLines(false);
		add(separator_3);
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 345, 745, 26);
		add(panel);
		
		btnPersonalise = new JButton("Get Personalised Suggestions");
		btnPersonalise.setBounds(10, 0, 223, 26);
		btnPersonalise.addActionListener(new PersonalisationActionListener());
		panel.setLayout(null);
		btnPersonalise.setToolTipText("Click here to apply your privacy preferences and get personalised suggestions for how to negotiate based on your previous negotiation interactions");
		panel.add(btnPersonalise);
		if (!firstRound){
			btnPersonalise.setEnabled(false);
		}
		
		btnRestoreChanges = new JButton("Restore changes");
		btnRestoreChanges.setBounds(600, 0, 140, 26);
		btnRestoreChanges.addActionListener(new ResetActionListener());
		btnRestoreChanges.setToolTipText("Click here to reset the form to view the original Request Policy without personalisation suggestions");
		panel.add(btnRestoreChanges);
		if (!firstRound){
			btnRestoreChanges.setEnabled(false);
		}
		this.setMaximumSize(new Dimension(800, 600));
		this.setMinimumSize(new Dimension(800, 600));

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
		this.decisionPanel.applyPersonalisation();
	}


	public void resetChanges() {
		this.conditionsPanel.resetChanges();
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
		
		reqItem.setConditions(this.conditionsPanel.getConditions());
		reqItem.setResource(this.item.getResource());
		reqItem.setPurpose(this.item.getPurpose());
		reqItem.setActions(this.item.getActions());
		respItem.setRequestItem(reqItem);
		return respItem;
	}


	private boolean hasResponseBeenEditedByUser() {
		
		if (!this.conditionsPanel.match()){
			return false;
		}
		
		return true;
	}
}
