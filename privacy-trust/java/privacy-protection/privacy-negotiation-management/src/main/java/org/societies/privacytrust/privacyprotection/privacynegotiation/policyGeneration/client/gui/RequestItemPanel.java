package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

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
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		
		setLayout(null);
		setBounds(0,0, 760, 387);
		JLabel lblPurpose = new JLabel("Purpose");
		lblPurpose.setBounds(17, 15, 60, 16);
		add(lblPurpose);
		
		PurposePanel purposePanel = new PurposePanel(requestItem.getPurpose());
		purposePanel.setBounds(97, 11, 650, 27);
		add(purposePanel);
		purposePanel.setLayout(null);
		WebSeparator separator_1 = new WebSeparator(WebSeparator.HORIZONTAL);
		separator_1.setBounds(2, 45, 756, 3);
		
		separator_1.setDrawSideLines(false);//new JSeparator(JSeparator.HORIZONTAL);
		add(separator_1);
		
		JLabel lblConditions = new JLabel("Conditions");
		lblConditions.setBounds(17, 137, 60, 14);
		add(lblConditions);
		
		conditionsPanel = new ConditionsPanel(requestItem, responseItem, firstRound);
		lblConditions.setLabelFor(conditionsPanel);
		conditionsPanel.setBounds(97, 55, 650, 200);
		add(conditionsPanel);
		
		WebSeparator separator_2 = new WebSeparator(WebSeparator.HORIZONTAL);
		separator_2.setBounds(2, 266, 796, 3);
		separator_2.setDrawSideLines(false);
		add(separator_2);
		
		JLabel lblDecision = new JLabel("Decision");
		lblDecision.setBounds(17, 287, 63, 14);
		add(lblDecision);
		
		Decision decision = Decision.PERMIT;
		if (responseItem!=null){
			decision = responseItem.getDecision();
		}
		
		ToggleButtonActionListener btnActionListener = new ToggleButtonActionListener(this);
		decisionPanel = new DecisionPanel(decision, btnActionListener);
		lblDecision.setLabelFor(decisionPanel);
		decisionPanel.setBounds(94, 280, 152, 30);
		add(decisionPanel);
		
		WebSeparator separator_3 = new WebSeparator();
		separator_3.setBounds(-1, 321, 748, 3);
		separator_3.setDrawSideLines(false);
		add(separator_3);
		
		JPanel panel = new JPanel();
		panel.setBounds(97, 335, 650, 40);
		add(panel);
		
		btnPersonalise = new JButton("Get Personalised Suggestions");
		btnPersonalise.setBounds(0, 10, 223, 23);
		btnPersonalise.addActionListener(new PersonalisationActionListener());
		panel.setLayout(null);
		btnPersonalise.setToolTipText("Click here to apply your privacy preferences and get personalised suggestions for how to negotiate based on your previous negotiation interactions");
		panel.add(btnPersonalise);
		if (!firstRound){
			btnPersonalise.setEnabled(false);
		}
		
		btnRestoreChanges = new JButton("Restore changes");
		btnRestoreChanges.setBounds(504, 10, 146, 23);
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
