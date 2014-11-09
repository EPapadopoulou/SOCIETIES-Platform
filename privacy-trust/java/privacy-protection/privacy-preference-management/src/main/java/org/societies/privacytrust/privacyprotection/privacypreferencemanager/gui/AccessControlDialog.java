package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestorScopeValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.dataobfuscation.ObfuscationLevels;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.SystemColor;

import javax.swing.UIManager;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JSlider;
import javax.swing.JCheckBox;

public class AccessControlDialog extends JDialog implements ActionListener, ChangeListener, ItemListener{

	private final JPanel contentPanel = new JPanel();
	private AccessControlResponseItem accessControlResponseItem;
	private ResponseItem requestedItem;
	private JButton permitButton;
	private JButton denyButton;
	private JSlider slider;
	private JLabel obfValueLabel;
	private JCheckBox rememberChkBox;
	private RequestorBean requestor;
	private JLabel questionLabel;
	private JCheckBox chckbxObfuscate;
	private static final String[] locations = new String[]{"EM1.69, MACS, Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"MACS, Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"EH14 4AS, Edinburgh, Scotland, UK",
		"Edinburgh, Scotland, UK",
		"Scotland, UK",
		"UK",
		"Europe",
		"Earth"};
	private static final String[] names = new String[]{"John Smith", "J. Smith", "John S.", "J.S", "user"};
	private static final String[] birthdays = new String[]{"5/8/1995","July 1995", "1995", "Before 1994"};
	private static final String[] emails = new String[]{"j.smith@hw.ac.uk","anonymous21312@societies.eu"};
	

	/**
	 * Create the dialog.
	 */
	public AccessControlDialog(RequestorBean requestor, ResponseItem requestedItem) {
		this.requestor = requestor;
		this.requestedItem = requestedItem;
		setTitle("Access Request");
		setAlwaysOnTop(true);
		setModal(true);
		setBounds(100, 100, 573, 382);
		getContentPane().setLayout(null);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		for (int i=0; i<locations.length; i++){
			labelTable.put(new Integer(i), new JLabel(""+i));
		}
		JPanel buttonPane = new JPanel();
		buttonPane.setBounds(0, 292, 547, 50);
		getContentPane().add(buttonPane);
		buttonPane.setLayout(null);

		permitButton = new JButton("Permit");
		permitButton.setForeground(new Color(0, 51, 153));
		permitButton.setBounds(364, 11, 173, 34);
		permitButton.setActionCommand("Permit");
		permitButton.addActionListener(this);
		buttonPane.add(permitButton);

		denyButton = new JButton("Deny");
		denyButton.setForeground(new Color(0, 51, 153));
		denyButton.setBounds(10, 11, 173, 34);
		denyButton.setActionCommand("Deny");
		denyButton.addActionListener(this);
		buttonPane.add(denyButton);
		contentPanel.setBounds(0, 0, 547, 285);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);


		questionLabel = new JLabel("");
		questionLabel.setBounds(10, 11, 474, 58);
		contentPanel.add(questionLabel);

		rememberChkBox = new JCheckBox("Remember my decision");
		rememberChkBox.setSelected(true);
		rememberChkBox.setBounds(6, 88, 162, 32);
		contentPanel.add(rememberChkBox);

		JPanel dobfPanel = new JPanel();
		dobfPanel.setBounds(10, 127, 527, 158);
		contentPanel.add(dobfPanel);
		dobfPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Obfuscation Level", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 51, 153)));
		dobfPanel.setLayout(null);

		obfValueLabel = new JLabel("Use the slider to see an example");
		obfValueLabel.setBounds(10, 112, 507, 30);
		dobfPanel.add(obfValueLabel);

		slider = new JSlider();
		slider.setMajorTickSpacing(1);
		slider.setMinorTickSpacing(1);
		slider.setMinimum(0);
		slider.setMaximum(ObfuscationLevels.getApplicableObfuscationLevels(requestedItem.getRequestItem().getResource().getDataType())-1);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setBounds(10, 56, 507, 45);
		dobfPanel.add(slider);

		chckbxObfuscate = new JCheckBox("Obfuscate");
		chckbxObfuscate.setSelected(true);
		chckbxObfuscate.addItemListener(this);
		chckbxObfuscate.setBounds(10, 23, 97, 23);
		dobfPanel.add(chckbxObfuscate);
		slider.setLabelTable( labelTable );
		slider.addChangeListener(this);
		
		

	}

	private String getQuestionText(){
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		String action = "read";
		try{
			action = this.requestedItem.getRequestItem().getActions().get(0).getActionConstant().name().toLowerCase();
		}catch (Exception e){
			e.printStackTrace();
		}
		if (requestor instanceof RequestorServiceBean){
			String serviceName = ((RequestorServiceBean) requestor).getRequestorServiceId().getServiceInstanceIdentifier();
			String dataType = this.requestedItem.getRequestItem().getResource().getDataType().toLowerCase();
			sb.append(serviceName+" requests access to <span style=\"color:blue; font-weight:bold;\"> "+action+" your "+dataType+"</span>. If you want to allow access, click Permit, otherwise click Deny.");
			sb.append(System.getProperty( "line.separator" ));
			sb.append("Use the obfuscation slider below to alter the quality of information.");

		}

		sb.append("</html>");
		return sb.toString();
	}

	public AccessControlResponseItem getAccessControlResponseItem(){

		this.questionLabel.setText(this.getQuestionText());
		
		this.setVisible(true);
		return this.accessControlResponseItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Prepare the response item and dispose the dialog
		this.accessControlResponseItem = new AccessControlResponseItem();
		if (e.getSource().equals(permitButton)){
			this.accessControlResponseItem.setDecision(Decision.PERMIT);
		}else{
			this.accessControlResponseItem.setDecision(Decision.DENY);
		}
		accessControlResponseItem.setRemember(rememberChkBox.isSelected());
		accessControlResponseItem.setRequestItem(this.requestedItem.getRequestItem());
		accessControlResponseItem.setObfuscationLevel(slider.getValue());
		accessControlResponseItem.setObfuscationInput(chckbxObfuscate.isSelected());
		
		this.dispose();
	}

	/*
	 * Used with the slider
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		this.obfValueLabel.setText("Slider level: "+slider.getValue());
		String dataType = requestedItem.getRequestItem().getResource().getDataType();
		if (dataType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			this.obfValueLabel.setText(locations[slider.getValue()]);
		}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
			this.obfValueLabel.setText(names[slider.getValue()]);
		}if (dataType.equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
			this.obfValueLabel.setText(birthdays[slider.getValue()]);
		}if (dataType.equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
			this.obfValueLabel.setText(emails[slider.getValue()]);
		}
		

	}

	/*
	 * Used with the checkbox
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange()==ItemEvent.DESELECTED){
			this.slider.setEnabled(false);
		}else if (event.getStateChange()==ItemEvent.SELECTED){
			this.slider.setEnabled(true);
		}
		
	}
}
