package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.EventQueue;
import java.util.HashMap;

import javax.swing.JFrame;

import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.MockRequestPolicy;

public class PPNWindow {

	private JFrame frame;
	private PPNDialog dialog;
	private boolean firstRound = false;

	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PPNWindow window = new PPNWindow(MockRequestPolicy.getNegotiationDetailsBean(), MockRequestPolicy.getItems(), false);
					//window.frame.setVisible(true);
					window.getResponsePolicy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 * @param negDetails 
	 * @param items 
	 * @param message 
	 */
	public PPNWindow(NegotiationDetailsBean negDetails, HashMap<RequestItem, ResponseItem> items, boolean firstRound, String message) {
		this.firstRound  = firstRound;
		initialize();
		dialog = new PPNDialog(this.frame, negDetails, items, firstRound, message);
		dialog.requestFocus();
		
	}

	public ResponsePolicy getResponsePolicy(){
		this.frame.dispose();
		return this.dialog.getResponsePolicy();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
