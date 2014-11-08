package org.societies.privacytrust.privacyprotection.privacypolicy.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.components.MockRequestPolicy;

public class PPNDialog extends JDialog implements ActionListener, WindowListener{
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	//private JDialog frame;
	private RequestorBean requestor;
	private List<RequestItem> requestItems;

	private List<RequestItemPanel> reqItemPanels = new ArrayList<RequestItemPanel>();
	private JButton btnContinue;
	private RequestPolicy policyEdited;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {

					PPNDialog window = new PPNDialog(new JFrame(),MockRequestPolicy.getRequestPolicy());
					RequestPolicy requestPolicy = window.getRequestPolicy();
					System.out.println(requestPolicy);
					//window.setVisible(true);
					//window.pack();
					//window.setVisible(true);
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public PPNDialog(JFrame frame, RequestPolicy policy){
		super(frame, "Privacy Policy Configuration form", true);
		UIManager.put("ClassLoader", getClass().getClassLoader());
		frame.setAlwaysOnTop(true);
		initialize();
		this.addWindowListener(this);
		System.out.println("Initialising");
		this.requestor = policy.getRequestor();
		this.requestItems = policy.getRequestItems();

		//We create a new instance of the UI
		//AccordionUI newUI = (AccordionUI) SeaGlassLookAndFeel.createUI(accordion);

		//We set the UI
		//accordion.setUI(newUI);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{1008, 0};	
		//gridBagLayout.rowHeights = new int[]{730, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		this.getContentPane().setLayout(gridBagLayout);

		JLabel lblPrivacyPolicyNegotiation = new JLabel("<html>You are editing the RequestPolicy for  "+getFriendlyNameOfService(requestor)+". </html>");
		GridBagConstraints gbc_lblPrivacyPolicyNegotiation = new GridBagConstraints();
		gbc_lblPrivacyPolicyNegotiation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPrivacyPolicyNegotiation.insets = new Insets(20, 50, 20, 50);
		gbc_lblPrivacyPolicyNegotiation.gridx = 0;
		gbc_lblPrivacyPolicyNegotiation.gridy = 0;
		this.getContentPane().add(lblPrivacyPolicyNegotiation, gbc_lblPrivacyPolicyNegotiation);
		Accordion accordion = new Accordion();
		System.out.println("Size of request item list: "+this.requestItems.size());
		for (RequestItem requestItem : this.requestItems){			
			RequestItemPanel requestItemPanel = new RequestItemPanel(requestItem);
			accordion.addBar("Terms of discosure of "+requestItem.getResource().getDataType(), requestItemPanel);
			this.logging.debug("Adding tab for: "+requestItem.getResource().getDataType());
			System.out.println("Adding tab for: "+requestItem.getResource().getDataType());
			reqItemPanels.add(requestItemPanel);
		}		

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		this.getContentPane().add(scrollPane, gbc_scrollPane);


		scrollPane.setViewportView(accordion);



		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		this.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{466, 75, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		btnContinue = new JButton("Continue");
		GridBagConstraints gbc_btnContinue = new GridBagConstraints();
		btnContinue.addActionListener(this);
		gbc_btnContinue.insets = new Insets(5, 20, 5, 20);
		gbc_btnContinue.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnContinue.gridx = 1;
		gbc_btnContinue.gridy = 0;
		panel.add(btnContinue, gbc_btnContinue);
		this.getRootPane().setDefaultButton(btnContinue);

		//this.getContentPane().validate();
		//this.getContentPane().repaint();
		System.out.println("Initialised");
		
	}

	/**
	 * TODO: CHANGE ACCORDINGLY!!!!
	 * @param requestor
	 * @return
	 */
	private String getFriendlyNameOfService(RequestorBean requestor) {
		// TODO Auto-generated method stub
		return "Google Maps";
	}
	/**
	 * TODO: CHANGE ACCORDINGLY!!!!
	 * @param requestor
	 * @return
	 */
	private String getFriendlyNameOfProvider(RequestorBean requestor){
		return "Google Inc";
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//WebLookAndFeel.install();
		/*		try {
			UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//frame = new JDialog();
		//frame = new JDialog(null,"Privacy Policy Negotiation form");
		this.setBounds(100, 100, 820, 1100);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}


	public RequestPolicy getRequestPolicy() {
		this.setVisible(true);
		System.out.println("Returning policyEdited");
		return this.policyEdited;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Button clicked "+e.getSource().toString());
		if (e.getSource().equals(this.btnContinue)){
			List<RequestItem> requestItems = new ArrayList<RequestItem>();
			for (RequestItemPanel panel : this.reqItemPanels){
				requestItems.add(panel.getRequestItem());

			}
			policyEdited = new RequestPolicy();
			policyEdited.setRequestor(this.requestor);
			policyEdited.setRequestItems(requestItems);
			
			if (checkAllDone()){
				this.dispose();
			}
			
		}
		
	}

	private boolean checkAllDone() {
		for (RequestItem item : this.policyEdited.getRequestItems()){
			if(item.getPurpose()==null){
				JOptionPane.showMessageDialog(this, "Missing purpose for item: "+item.getResource().getDataType());
				return false;
			}
			if (item.getPurpose().length()==0){
				JOptionPane.showMessageDialog(this, "Missing purpose for item: "+item.getResource().getDataType());				
				return false;
			}
			
			if (item.getActions().size()==0){
				JOptionPane.showMessageDialog(this, "At least one action is required");
			}
		}
		
		return true;
	}

	public Hashtable<String, ConditionRanges> getConditionRanges(){
		Hashtable<String, ConditionRanges> ranges = new Hashtable<String, ConditionRanges>();
		for (RequestItemPanel panel : reqItemPanels){
			ranges.put(panel.getRequestItem().getResource().getDataType(), panel.getConditionRanges());
		}
		
		this.logging.debug(ranges.toString());
		return ranges;
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}


}
