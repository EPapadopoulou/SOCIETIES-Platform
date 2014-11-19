/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.privacypolicy.servicepolicygui.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class RequestItemEditor extends JFrame implements ItemListener
{
	private JPanel contentPanel;
	private JPanel resourcePanel;
	private JLabel resourceLabel;
	private JComboBox resourceTypeList;
	private JPanel actionsPanel;
	private ActionsTableModel actionsModel;
	private JTable actionsTable;
	private JButton addActionBtn;
	private JButton removeActionBtn;
	private JPanel conditionsPanel;
	private JTable conditionsTable;
	private JButton addConditionBtn;
	private JButton removeConditionBtn;
	private JButton btnSave;
	private JButton btnDiscard;
	private ConditionsTableModel conditionsModel;
	private JLabel schemeLabel;
	private JComboBox schemeList;
	private List<String> contextTypes = new ArrayList<String>();
	private List<String> cisTypes = new ArrayList<String>();
	private List<String> activityTypes = new ArrayList<String>();
	private List<String> cssTypes = new ArrayList<String>();
	private List<String> deviceTypes = new ArrayList<String>();
	private JPanel purposePanel;
	private JTextArea purposeTxtArea;

	public RequestItemEditor(ActionListener listener)
	{
		super("Requested Items Editor");
		this.setupDataTypes();
		this.contentPanel = new JPanel();
		this.contentPanel.setBorder(BorderFactory.createTitledBorder("Resource Editor"));
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		//gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, 0.0, 1.0, 1.0, 0.0};
		//gbl_contentPanel.columnWeights = new double[]{1.0, 1.0};
		this.contentPanel.setLayout(gbl_contentPanel);

		this.resourcePanel = new JPanel();
		resourcePanel.setBorder(new TitledBorder(null, "Resource Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbPanel2 = new GridBagLayout();
		gbPanel2.columnWeights = new double[]{0.0, 1.0};
		this.resourcePanel.setLayout(gbPanel2);

		this.schemeLabel = new JLabel("Scheme");
		GridBagConstraints gbc_schemeLabel = new GridBagConstraints();
		gbc_schemeLabel.insets = new Insets(0, 5, 0, 5);
		gbc_schemeLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_schemeLabel.anchor = GridBagConstraints.WEST;
		gbc_schemeLabel.gridy = 0;
		gbc_schemeLabel.gridx = 0;
		this.resourcePanel.add(this.schemeLabel, gbc_schemeLabel);

		this.schemeList = new JComboBox(getSchemeList());
		schemeLabel.setLabelFor(schemeList);
		this.schemeList.setSelectedIndex(0);
		this.schemeList.addItemListener(this);

		GridBagConstraints gbc_schemeList = new GridBagConstraints();
		gbc_schemeList.insets = new Insets(0, 5, 0, 0);
		gbc_schemeList.fill = GridBagConstraints.HORIZONTAL;
		gbc_schemeList.gridy = 0;
		gbc_schemeList.gridx = 1;
		this.resourcePanel.add(this.schemeList, gbc_schemeList);

		this.resourceLabel = new JLabel("ResourceType");
		GridBagConstraints gbc_resourceLabel = new GridBagConstraints();
		gbc_resourceLabel.insets = new Insets(0, 5, 0, 5);
		gbc_resourceLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_resourceLabel.gridy = 1;
		gbc_resourceLabel.gridx = 0;
		this.resourcePanel.add(this.resourceLabel, gbc_resourceLabel);

		this.resourceTypeList = new JComboBox(getCtxAttributeTypesList());
		resourceLabel.setLabelFor(resourceTypeList);
		this.resourceTypeList.setEditable(true);

		GridBagConstraints gbc_resourceTypeList = new GridBagConstraints();
		gbc_resourceTypeList.insets = new Insets(0, 5, 0, 0);
		gbc_resourceTypeList.fill = GridBagConstraints.HORIZONTAL;
		gbc_resourceTypeList.anchor = GridBagConstraints.WEST;
		gbc_resourceTypeList.gridy = 1;
		gbc_resourceTypeList.gridx = 1;
		this.resourcePanel.add(this.resourceTypeList, gbc_resourceTypeList);


		GridBagConstraints gbc_resourcePanel = new GridBagConstraints();
		gbc_resourcePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_resourcePanel.insets = new Insets(0, 0, 5, 0);
		gbc_resourcePanel.gridwidth = 2;
		gbc_resourcePanel.gridy = 0;
		gbc_resourcePanel.gridx = 0;
		this.contentPanel.add(this.resourcePanel, gbc_resourcePanel);



		this.actionsPanel = new JPanel();
		this.actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		GridBagLayout gbActionsPanel = new GridBagLayout();
		//gbActionsPanel.rowWeights = new double[]{1.0, 0.0};
		//gbActionsPanel.columnWeights = new double[]{1.0, 0.0};
		this.actionsPanel.setLayout(gbActionsPanel);


		this.actionsModel = new ActionsTableModel();
		this.actionsTable = new JTable(this.actionsModel);
		JScrollPane scpList0 = new JScrollPane(this.actionsTable);
		GridBagConstraints gbc_scpList0 = new GridBagConstraints();
		gbc_scpList0.fill = GridBagConstraints.BOTH;
		gbc_scpList0.gridwidth = 2;
		gbc_scpList0.gridy = 0;
		gbc_scpList0.gridx = 0;
		this.actionsPanel.add(scpList0, gbc_scpList0);

		this.addActionBtn = new JButton("Add Action");
		this.addActionBtn.setActionCommand("addAction");
		this.addActionBtn.addActionListener(listener);

		GridBagConstraints gbc_addActionBtn = new GridBagConstraints();
		gbc_addActionBtn.anchor = GridBagConstraints.WEST;
		gbc_addActionBtn.gridy = 1;
		gbc_addActionBtn.gridx = 0;
		this.actionsPanel.add(this.addActionBtn, gbc_addActionBtn);

		this.removeActionBtn = new JButton("Remove Action");
		this.removeActionBtn.addActionListener(listener);
		this.removeActionBtn.setActionCommand("removeAction");

		GridBagConstraints gbc_removeActionBtn = new GridBagConstraints();
		gbc_removeActionBtn.anchor = GridBagConstraints.EAST;
		gbc_removeActionBtn.gridy = 1;
		gbc_removeActionBtn.gridx = 1;
		this.actionsPanel.add(this.removeActionBtn, gbc_removeActionBtn);

		purposePanel = new JPanel();
		purposePanel.setBorder(new TitledBorder(null, "Purpose", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_purposePanel = new GridBagConstraints();
		gbc_purposePanel.gridheight = 2;
		gbc_purposePanel.gridwidth = 2;
		gbc_purposePanel.insets = new Insets(0, 0, 5, 0);
		gbc_purposePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_purposePanel.gridx = 0;
		gbc_purposePanel.gridy = 1;
		contentPanel.add(purposePanel, gbc_purposePanel);
		

		GridBagLayout gbl_purposePanel = new GridBagLayout();
		gbl_purposePanel.columnWeights = new double[]{0.0, 1.0};
		gbl_purposePanel.rowWeights = new double[]{0.0};
		purposePanel.setLayout(gbl_purposePanel);
		
		purposeTxtArea = new JTextArea();
		purposeTxtArea.setRows(3);
		GridBagConstraints gbc_purposeTxtArea = new GridBagConstraints();
		gbc_purposeTxtArea.gridheight = 2;
		gbc_purposeTxtArea.gridwidth = 2;
		gbc_purposeTxtArea.insets = new Insets(0, 5, 0, 0);
		gbc_purposeTxtArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_purposeTxtArea.gridx = 0;
		gbc_purposeTxtArea.gridy = 0;
		purposePanel.add(purposeTxtArea, gbc_purposeTxtArea);
		

		
		GridBagConstraints gbc_actionsPanel = new GridBagConstraints();
		gbc_actionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_actionsPanel.gridwidth = 2;
		gbc_actionsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_actionsPanel.gridy = 3;
		gbc_actionsPanel.gridx = 0;
		this.contentPanel.add(this.actionsPanel, gbc_actionsPanel);

		this.conditionsPanel = new JPanel();
		this.conditionsPanel.setBorder(BorderFactory.createTitledBorder("Conditions"));
		GridBagLayout gbConditionsPanel = new GridBagLayout();
		//gbConditionsPanel.rowWeights = new double[]{1.0, 0.0};
		//gbConditionsPanel.columnWeights = new double[]{1.0, 0.0};
		this.conditionsPanel.setLayout(gbConditionsPanel);

		this.conditionsModel = new ConditionsTableModel();

		this.conditionsTable = new JTable(this.conditionsModel);
		JScrollPane jsp = new JScrollPane(this.conditionsTable);

		GridBagConstraints gbc_jsp = new GridBagConstraints();
		gbc_jsp.fill = GridBagConstraints.BOTH;
		gbc_jsp.gridwidth = 2;
		gbc_jsp.gridy = 0;
		gbc_jsp.gridx = 0;
		this.conditionsPanel.add(jsp, gbc_jsp);

		this.addConditionBtn = new JButton("Add Condition");
		this.addConditionBtn.addActionListener(listener);
		this.addConditionBtn.setActionCommand("addCondition");

		GridBagConstraints gbc_addConditionBtn = new GridBagConstraints();
		gbc_addConditionBtn.anchor = GridBagConstraints.WEST;
		gbc_addConditionBtn.gridy = 1;
		gbc_addConditionBtn.gridx = 0;
		this.conditionsPanel.add(this.addConditionBtn, gbc_addConditionBtn);

		this.removeConditionBtn = new JButton("Remove Condition");
		this.removeConditionBtn.addActionListener(listener);
		this.removeConditionBtn.setActionCommand("removeCondition");

		GridBagConstraints gbc_removeConditionBtn = new GridBagConstraints();
		gbc_removeConditionBtn.gridy = 1;
		gbc_removeConditionBtn.gridx = 1;
		gbc_removeConditionBtn.anchor = GridBagConstraints.EAST;
		this.conditionsPanel.add(this.removeConditionBtn, gbc_removeConditionBtn);

		GridBagConstraints gbc_conditionsPanel = new GridBagConstraints();
		gbc_conditionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_conditionsPanel.gridwidth = 2;
		gbc_conditionsPanel.gridy = 4;
		gbc_conditionsPanel.gridx = 0;
		this.contentPanel.add(this.conditionsPanel, gbc_conditionsPanel);

		this.btnSave = new JButton("Save");
		this.btnSave.addActionListener(listener);
		this.btnSave.setActionCommand("saveResource");

		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridy = 5;
		gbc_btnSave.gridx = 0;
		this.contentPanel.add(this.btnSave, gbc_btnSave);

		this.btnDiscard = new JButton("Discard");
		this.btnDiscard.setActionCommand("discard");
		this.btnDiscard.addActionListener(listener);

		GridBagConstraints gbc_btnDiscard = new GridBagConstraints();
		gbc_btnDiscard.gridy = 5;
		gbc_btnDiscard.gridx = 1;
		this.contentPanel.add(this.btnDiscard, gbc_btnDiscard);

		setDefaultCloseOperation(0);

		setContentPane(this.contentPanel);
		pack();
		setVisible(true);
	}


	private void setupDataTypes() {
		this.cisTypes = new ArrayList<String>();
		this.cisTypes.add("cis-member-list");
		this.cisTypes.add("cis-list");

		this.deviceTypes = new ArrayList<String>();
		this.deviceTypes.add("meta-data");

		this.activityTypes = new ArrayList<String>();
		this.activityTypes.add("activityfeed");


	}

	private String[] getSchemeList() {
		DataIdentifierScheme[] fields = DataIdentifierScheme.values();

		ArrayList<String> tempNames = new ArrayList<String>();
		for (int i=0; i<fields.length; i++){
			if (!fields[i].name().equalsIgnoreCase("CSS"))
				tempNames.add(fields[i].name());
		}
		String[] names = new String[tempNames.size()];
		for (int i=0; i<tempNames.size(); i++){
			names[i] = tempNames.get(i);
		}

		return names;

	}

	private String[] getCtxAttributeTypesList() {
		Field[] fields = CtxAttributeTypes.class.getDeclaredFields();



		List<String> list = new ArrayList<String>();
		

		
		for (int i=0; i<fields.length; i++){
			Class<?> t = fields[i].getType();
			if (t==java.lang.String.class){ 

				try {
					System.out.println(fields[i].get(null));
					this.contextTypes.add((String) fields[i].get(null));

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		String[] values = new String[contextTypes.size()];
		return (String[]) contextTypes.toArray(values);
	}

	public void addAction(ActionConstants action, Boolean optional) {
		Vector row = new Vector();
		row.add(action);
		row.add(optional);
		this.actionsModel.addRow(row);
		this.actionsTable.setModel(this.actionsModel);
	}

	public void removeSelectedAction()
	{
		int index = this.actionsTable.getSelectedRow();
		if (index >= 0)
			this.actionsModel.removeRow(index);
		else
			JOptionPane.showMessageDialog(this, "Select an Action to remove");
	}

	public void addCondition(ConditionConstants condition, String value, Boolean b)
	{
		Vector row = new Vector();
		row.add(condition);
		row.add(value);
		row.add(b);
		this.conditionsModel.addRow(row);
		this.conditionsTable.setModel(this.conditionsModel);
	}

	public void removeSelectedCondition() {
		int index = this.conditionsTable.getSelectedRow();
		if (index >= 0)
			this.conditionsModel.removeRow(index);
		else
			JOptionPane.showMessageDialog(this, "Select a Condition to remove");
	}

	public RequestItem getRequestItem()
	{
		String resourceType = this.resourceTypeList.getSelectedItem().toString().trim();
		if (resourceType == null) {
			JOptionPane.showMessageDialog(this, "Please enter a resource type");
			return null;
		}
		if (resourceType.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a resource type");
			return null;
		}
		
		if (purposeTxtArea.getText() == null || purposeTxtArea.getText().isEmpty()){
			JOptionPane.showMessageDialog(this, "Please type the purpose for which you are requesting access to the user's "+resourceType);
			return null;
		}
		
		ArrayList<Action> actions = new ArrayList<Action>();
		for (int i = 0; i < this.actionsModel.getRowCount(); i++) {
			ActionConstants ac = (ActionConstants)this.actionsModel.getValueAt(i, 0);
			Boolean optional = (Boolean)this.actionsModel.getValueAt(i, 1);
			Action a = new Action();
			a.setActionConstant(ac);
			a.setOptional(optional.booleanValue());
			actions.add(a);
		}

		Resource resource = ResourceUtils.create(DataIdentifierScheme.valueOf(this.schemeList.getSelectedItem().toString()), resourceType);
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i < this.conditionsModel.getRowCount(); i++) {
			ConditionConstants cc = (ConditionConstants)this.conditionsModel.getValueAt(i, 0);
			String value = (String)this.conditionsModel.getValueAt(i, 1);
			Boolean optional = (Boolean)this.conditionsModel.getValueAt(i, 2);
			Condition condition = new Condition();
			condition.setConditionConstant(cc);
			condition.setValue(value);
			condition.setOptional(optional.booleanValue());
			conditions.add(condition);
		}
		RequestItem item = new RequestItem();
		item.setPurpose(purposeTxtArea.getText());
		item.setActions(actions);
		item.setConditions(conditions);
		item.setResource(resource);
		return item;
	}

	public void itemStateChanged(ItemEvent e) {
		System.out.println("pre - ResourceTypeList.isEditable()="+resourceTypeList.isEditable());
		if (e.getStateChange()==ItemEvent.SELECTED){
			String scheme = (String) e.getItem();
			System.out.println("selected scheme: "+scheme);
			this.resourceTypeList.removeAllItems();
			if (DataIdentifierScheme.CIS.name().equals(scheme)){
				for (String cisType : this.cisTypes){
					this.resourceTypeList.addItem(cisType);
				}
				this.resourceTypeList.setEditable(false);
			}else if (DataIdentifierScheme.ACTIVITY.name().equals(scheme)){
				for (String activityType : this.activityTypes){
					this.resourceTypeList.addItem(activityType);
				}
				this.resourceTypeList.setEditable(false);
			}else if (DataIdentifierScheme.CONTEXT.name().equals(scheme)){
				for (String contextType : this.contextTypes){
					this.resourceTypeList.addItem(contextType);
				}
				this.resourceTypeList.setEditable(true);
			}else if (DataIdentifierScheme.CSS.name().equals(scheme)){
				for (String cssType : this.cssTypes){
					this.resourceTypeList.addItem(cssType);
				}
				this.resourceTypeList.setEditable(false);
			}else if(DataIdentifierScheme.DEVICE.name().equals(scheme)){
				for (String deviceType : this.deviceTypes){
					this.resourceTypeList.addItem(deviceType);
				}
				this.resourceTypeList.setEditable(false);

			}
		}
		System.out.println("post - ResourceTypeList.isEditable()="+resourceTypeList.isEditable());

	}


}
