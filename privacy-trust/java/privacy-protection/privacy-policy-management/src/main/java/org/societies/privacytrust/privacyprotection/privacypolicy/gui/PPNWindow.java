package org.societies.privacytrust.privacyprotection.privacypolicy.gui;

import java.awt.EventQueue;
import java.util.Hashtable;

import javax.swing.JFrame;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.components.MockRequestPolicy;


public class PPNWindow {

	private JFrame frame;
	private PPNDialog dialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PPNWindow window = new PPNWindow(MockRequestPolicy.getRequestPolicy());
					//window.frame.setVisible(true);
					window.getRequestPolicy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @param negDetails 
	 * @param items 
	 */
	public PPNWindow(RequestPolicy policy) {
		initialize();
		dialog = new PPNDialog(this.frame, policy);
		dialog.setAlwaysOnTop(true);
		
	}

	public RequestPolicy getRequestPolicy(){
		this.frame.dispose();
		return this.dialog.getRequestPolicy();
	}
	
	public Hashtable<String, ConditionRanges> getConditionRanges(){
		return this.dialog.getConditionRanges();
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
