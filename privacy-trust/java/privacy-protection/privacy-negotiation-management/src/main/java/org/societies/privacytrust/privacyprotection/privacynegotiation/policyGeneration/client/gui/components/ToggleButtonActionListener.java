package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.RequestItemPanel;

public class ToggleButtonActionListener implements ActionListener{

	private RequestItemPanel panel;
	public ToggleButtonActionListener(RequestItemPanel panel){
		this.panel = panel;
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JToggleButton btn = (JToggleButton) e.getSource();
		if (btn.isSelected()){
			btn.setText("Allow Access");
		}else{
			
			btn.setText("Deny Access");
		}
	}
	
}
