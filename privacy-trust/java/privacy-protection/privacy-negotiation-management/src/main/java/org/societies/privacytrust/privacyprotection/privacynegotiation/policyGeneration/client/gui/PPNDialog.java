package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.MockRequestPolicy;

import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.TabOrientation;
import com.seaglasslookandfeel.*;

public class PPNDialog extends JDialog implements ActionListener, WindowListener{
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	//private JDialog frame;
	private NegotiationDetailsBean negDetails;
	private HashMap<RequestItem, ResponseItem> items;

	private List<RequestItemPanel> reqItemPanels = new ArrayList<RequestItemPanel>();
	private JButton btnCancel;
	private JButton btnContinue;
	private ResponsePolicy policyEdited;

	private boolean firstRound;

	private JLabel lblPrivacyPolicyNegotiation;
	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {

					PPNDialog window = new PPNDialog(new JFrame(),MockRequestPolicy.getNegotiationDetailsBean(), MockRequestPolicy.getItems(), true);
					ResponsePolicy responsePolicy = window.getResponsePolicy();
					System.out.println(responsePolicy);
					//window.setVisible(true);
					//window.pack();
					//window.setVisible(true);
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 * @param firstRound 
	 */

	public PPNDialog(JFrame frame, NegotiationDetailsBean negDetails, HashMap<RequestItem, ResponseItem> items, boolean firstRound, String message){

		super(frame, "Privacy Policy Negotiation Form", true);
		this.firstRound = firstRound;
		UIManager.put("ClassLoader", getClass().getClassLoader());
		frame.setAlwaysOnTop(true);
		initialize();
		this.addWindowListener(this);
		System.out.println("Initialising");
		this.negDetails = negDetails;
		this.items = items;
		this.logging.debug(printDetails());

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

		if (firstRound){
			message = "<html>You are negotiating with "+getFriendlyNameOfProvider(negDetails.getRequestor())+ ". "
					+ "The information provided below presents the terms and conditions that will apply when you disclose data "
					+ "to "+getFriendlyNameOfService(negDetails.getRequestor())+". Configure usage per your needs and then click Continue to proceed with the negotiation.</html>";
		}
		lblPrivacyPolicyNegotiation = new JLabel(message);
		GridBagConstraints gbc_lblPrivacyPolicyNegotiation = new GridBagConstraints();
		gbc_lblPrivacyPolicyNegotiation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPrivacyPolicyNegotiation.insets = new Insets(20, 50, 20, 50);
		gbc_lblPrivacyPolicyNegotiation.gridx = 0;
		gbc_lblPrivacyPolicyNegotiation.gridy = 0;
		this.getContentPane().add(lblPrivacyPolicyNegotiation, gbc_lblPrivacyPolicyNegotiation);
		JSCAccordion accordion = new JSCAccordion();
		Iterator<RequestItem> keys = this.items.keySet().iterator();
		System.out.println("Size of request item list: "+this.items.keySet().size());
		while (keys.hasNext()){
			RequestItem requestItem = keys.next();
			ResponseItem responseItem = this.items.get(requestItem);

			RequestItemPanel requestItemPanel = new RequestItemPanel(requestItem, responseItem, firstRound);
			accordion.addTab("Terms of discosure of "+requestItem.getResource().getDataType(), requestItemPanel);
			this.logging.debug("Adding tab for: "+requestItem.getResource().getDataType());
			System.out.println("Adding tab for: "+requestItem.getResource().getDataType());
			reqItemPanels.add(requestItemPanel);
			accordion.setTabHeight(30);
		}		

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		this.getContentPane().add(scrollPane, gbc_scrollPane);


		scrollPane.setViewportView(accordion);
		accordion.setTabOrientation(TabOrientation.VERTICAL);



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

		btnCancel = new JButton("Cancel ");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		btnCancel.addActionListener(this);
		gbc_btnCancel.anchor = GridBagConstraints.WEST;
		gbc_btnCancel.insets = new Insets(5, 20, 5, 20);
		gbc_btnCancel.gridx = 0;
		gbc_btnCancel.gridy = 0;
		panel.add(btnCancel, gbc_btnCancel);

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

	private String printDetails() {
		StringBuilder sb = new StringBuilder();

		Iterator<RequestItem> iterator = this.items.keySet().iterator();
		sb.append("Printing details:\n");
		while (iterator.hasNext()){
			RequestItem requestItem = iterator.next();
			ResponseItem responseItem = this.items.get(requestItem);

			List<Action> requestedActions = requestItem.getActions();


			List<Action> responseActions = new ArrayList<Action>();
			if (responseItem!=null){
				responseItem.getRequestItem().getActions();
			}

			sb.append(requestItem.getResource().getDataType());

			sb.append("Actions:");
			for (Action reqAction : requestedActions){
				sb.append(reqAction.getActionConstant());
			}
			sb.append(" - ");
			for (Action responseAction : responseActions){
				sb.append(responseAction.getActionConstant());
			}

			sb.append("Conditions: ");
			List<Condition> requestConditions = requestItem.getConditions();

			
			List<Condition> responseConditions = new ArrayList<Condition>();
			if (responseItem!=null){
				responseConditions = responseItem.getRequestItem().getConditions();
			}
			for (Condition reqCondition: requestConditions){
				sb.append(reqCondition.getConditionConstant());
				sb.append(reqCondition.getValue()+" - ");
				for (Condition respCondition : responseConditions){
					if (respCondition.getConditionConstant().equals(reqCondition.getConditionConstant())){
						sb.append(respCondition.getValue());
					}
				}
				sb.append("\n");
			}
			
			sb.append("\n");
		}


		return sb.toString();
	}

	/**
	 * TODO: CHANGE ACCORDINGLY!!!!
	 * @param requestor
	 * @return
	 */
	private String getFriendlyNameOfService(RequestorBean requestor) {
		if (requestor instanceof RequestorServiceBean){
			ServiceResourceIdentifier serviceID = ((RequestorServiceBean) requestor).getRequestorServiceId();
			if (ServiceModelUtils.serviceResourceIdentifierToString(serviceID).contains("HWUCampusGuide")){
				return "HWUCampusGuide";
			}else if (ServiceModelUtils.serviceResourceIdentifierToString(serviceID).contains("GoogleVenueFinder")){
				return "GoogleVenueFinder";
			}
		}
		return requestor.getRequestorId();
	}
	/**
	 * TODO: CHANGE ACCORDINGLY!!!!
	 * @param requestor
	 * @return
	 */
	private String getFriendlyNameOfProvider(RequestorBean requestor){
		if (requestor.getRequestorId().contains("Google")){
			return "Google Inc";
		}else if (requestor.getRequestorId().contains("hwu")){
			return "Heriot-Watt University";
		}else return requestor.getRequestorId();

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


	public ResponsePolicy getResponsePolicy() {
		this.setVisible(true);
		System.out.println("Returning policyEdited");
		return this.policyEdited;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Button clicked "+e.getSource().toString());
		if (e.getSource().equals(this.btnCancel)){
			this.policyEdited = null;
		}else if (e.getSource().equals(this.btnContinue)){

			List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
			for (RequestItemPanel panel : this.reqItemPanels){
				ResponseItem responseItem = panel.getResponseItem();
				responseItems.add(responseItem);
			}
			policyEdited = new ResponsePolicy();
			policyEdited.setNegotiationStatus(NegotiationStatus.ONGOING);
			policyEdited.setRequestor(this.negDetails.getRequestor());
			policyEdited.setResponseItems(responseItems);
		}
		this.dispose();
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

		if (this.negDetails.getRequestor() instanceof RequestorServiceBean){
			int showConfirmDialog = JOptionPane.showConfirmDialog(this, "Closing this window will stop the installation of the service. Do you want to cancel?", "Cancel installation?", JOptionPane.YES_NO_OPTION);
			if (showConfirmDialog == JOptionPane.YES_OPTION){
				PPNDialog.this.btnCancel.doClick();
			}
		}else {
			int showConfirmDialog = JOptionPane.showConfirmDialog(this, "Closing this window will stop the joining the CIS process. Do you want to cancel?", "Cancel join?", JOptionPane.YES_NO_OPTION);
			if (showConfirmDialog == JOptionPane.YES_OPTION){
				PPNDialog.this.btnCancel.doClick();
			}
		}
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
