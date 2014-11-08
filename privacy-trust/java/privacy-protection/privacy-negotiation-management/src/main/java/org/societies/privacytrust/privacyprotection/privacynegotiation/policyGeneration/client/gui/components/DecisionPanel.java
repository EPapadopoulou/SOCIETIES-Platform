package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * If the toggleButton is selected (pressed down) the text shows Allow Access
 * 
 */
public class DecisionPanel extends JPanel {

	private JToggleButton tglbtnNewToggleButton;
	private Decision decision;
	
	
	/**
	 * Create the panel.
	 * @param btnActionListener 
	 */
	public DecisionPanel(Decision decision, ToggleButtonActionListener btnActionListener) {

		System.out.println("Initialising Decision Panel with: "+decision);
		this.decision = decision;
		
		tglbtnNewToggleButton = new JToggleButton("Allow Access");
		tglbtnNewToggleButton.setBounds(0, 0, 127, 23);
		
		tglbtnNewToggleButton.setSelected(true);
		tglbtnNewToggleButton.addActionListener(btnActionListener);
		setLayout(null);

		setBounds(0,0, 130, 25);
		add(tglbtnNewToggleButton);
	}

	
	public Decision getDecision(){
		if (this.tglbtnNewToggleButton.isSelected()){
			return Decision.PERMIT;
		}else{
			return Decision.DENY;
		}
	}

	public void applyPersonalisation() {
		if (decision!=null){
			if (decision.equals(Decision.DENY)){
				if(tglbtnNewToggleButton.isSelected()){
					tglbtnNewToggleButton.doClick();
				}else{
					//tglbtnNewToggleButton.doClick();
					//tglbtnNewToggleButton.doClick();
				}
				//tglbtnNewToggleButton.setText("Deny Access");
				
				//tglbtnNewToggleButton.setSelected(false);
				
			}else if (decision.equals(Decision.PERMIT)){
				if (!tglbtnNewToggleButton.isSelected()){
					tglbtnNewToggleButton.doClick();
				}
				//tglbtnNewToggleButton.setText("Allow Access");
				//tglbtnNewToggleButton.setSelected(true);
			}
		}else{
			System.out.println("decision is null");
		}
		
	}

	public void resetChanges() {
		
		if (!tglbtnNewToggleButton.isSelected()){
			tglbtnNewToggleButton.doClick();
		}
		
	}

}
