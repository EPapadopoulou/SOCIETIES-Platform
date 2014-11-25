package org.societies.privacytrust.privacyprotection.identity.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.identity.IdentitySelection;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;

public class IdentityCreationGUIDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Hashtable<String, List<CtxIdentifier>> identitiesTable;
	private Agreement agreement;
	private JFrame frame;
	private JTextField txtIdentityName;
	//private ICtxBroker ctxBroker;
	//key: dataType, value = list of available ctxidentifiers for that data type
	private Hashtable<String, List<CtxAttributeWrapper>> dataTable;

	private IIdentity userIdentity;
	private JList ctxAttributeJList;

	private Logger logging = LoggerFactory.getLogger(getClass());
	private JList dataTypeJList;
	private JTable identityInformationJTable;
	private JButton btnAddSelected;
	private JButton btnCreate;
	private JButton okButton;
	private IdentityInformationTableModel identityInfoModel;
	private IndividualCtxEntity person;
	private JScrollPane scrollPane;
	private JButton btnPersonalise;
	private List<IIdentity> allIdentities;
	//private IPrivacyPreferenceManager privacyPreferenceManager;
	private IdentitySelection identitySelection;

	private final static String[] reservedTypes = new String[]{
		CtxAttributeTypes.CACI_MODEL,
		CtxAttributeTypes.CAUI_MODEL,
		CtxAttributeTypes.CRIST_MODEL,
		CtxAttributeTypes.CSS_DOMAIN_SERVER,
		CtxAttributeTypes.CSS_NODE_STATUS,
		CtxAttributeTypes.CSS_NODE_TYPE,
		CtxAttributeTypes.D_NET,
		CtxAttributeTypes.PARAMETER_NAME,
		CtxAttributeTypes.PRIVACY_POLICY,
		CtxAttributeTypes.PRIVACY_POLICY_REGISTRY,
		CtxAttributeTypes.SERVICE_PRIVACY_POLICY_REGISTRY,
		CtxAttributeTypes.SNAPSHOT_REG,
		CtxAttributeTypes.SOCIAL_NETWORK_CONNECTOR,
		CtxAttributeTypes.UID,
		CtxAttributeTypes.ACTION,
		CtxAttributeTypes.EXTENDED_PROFILE,
		CtxAttributeTypes.ID,
		CtxAttributeTypes.IS_INTERACTABLE,
		CtxAttributeTypes.LAST_ACTION,
		CtxAttributeTypes.LOCATION_ID,
		CtxAttributeTypes.LOCATION_PARENT_ID,
		CtxAttributeTypes.LOCATION_PERSONAL_TAGS,
		CtxAttributeTypes.LOCATION_PUBLIC_TAGS,
		CtxAttributeTypes.LOCATION_TYPE,
		CtxAttributeTypes.MAC_ADDRESS,
		CtxAttributeTypes.PHONES,
		CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT,
		CtxAttributeTypes.TYPE,
		CtxAttributeTypes.PROFILE_IMAGE_URL,
		CtxAttributeTypes.TURNSON,
		CtxAttributeTypes.PROFILE};
	private JButton btnCancel;
	private JLabel lblIdentityName;
	private JButton btnReset;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JButton btnRemoveSelected;

	/**
	 * Create the dialog.
	 */
	public IdentityCreationGUIDialog(JFrame frame, Agreement agreement, IdentitySelection identitySelection, IIdentity userId, List<IIdentity> allIdentities) {
		super(frame, "Identity Creation", true);

		this.frame = frame;
		this.agreement = agreement;
		this.identitySelection = identitySelection;


		userIdentity = userId;
		this.allIdentities = allIdentities;

		setBounds(100, 100, 631, 676);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			lblIdentityName = new JLabel("Identity name:");
			lblIdentityName.setBounds(10, 11, 108, 27);
			contentPanel.add(lblIdentityName);
		}

		txtIdentityName = new JTextField();
		lblIdentityName.setLabelFor(txtIdentityName);
		txtIdentityName.setBounds(120, 11, 486, 27);
		contentPanel.add(txtIdentityName);
		txtIdentityName.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Requested data (Click to see stored values):", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 49, 270, 321);
		contentPanel.add(panel);
		panel.setLayout(null);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 36, 250, 236);
		panel.add(scrollPane_1);
	


		
		dataTypeJList = new JList(this.getModel());
		
		dataTypeJList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()){

					if (dataTypeJList.getSelectedIndex()>=0){
						String selectedDataType = (String) dataTypeJList.getSelectedValue();
						if (dataTable.containsKey(selectedDataType)){
							List<CtxAttributeWrapper> list = dataTable.get(selectedDataType);

							DefaultListModel model = (DefaultListModel) ctxAttributeJList.getModel();
							model.removeAllElements();
							for (CtxAttributeWrapper wrappedItem : list){
								model.addElement(wrappedItem);
							}

							//JOptionPane.showMessageDialog(IdentityCreationGUIDialog.this, "size of model list: "+model.getSize());
							ctxAttributeJList.setModel(model);
							ctxAttributeJList.setSelectedIndex(0);
							scrollPane_2.revalidate();

						}else{
							DefaultListModel model = (DefaultListModel) ctxAttributeJList.getModel();
							model.removeAllElements();
							ctxAttributeJList.setModel(model);
							scrollPane_2.revalidate();

						}
					}
				}
			}
		});
		scrollPane_1.setViewportView(dataTypeJList);
		dataTypeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


		JButton btnCreateNewType = new JButton("Add a data type");
		btnCreateNewType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String dataType = JOptionPane.showInputDialog(IdentityCreationGUIDialog.this, "Type the data type below");
				if (dataType.isEmpty()){
					JOptionPane.showMessageDialog(IdentityCreationGUIDialog.this, "Data type cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				}else{

					DefaultListModel dataTypesListModel = (DefaultListModel) dataTypeJList.getModel();

					if (dataTypesListModel.contains(dataType)){
						JOptionPane.showMessageDialog(IdentityCreationGUIDialog.this, "Data type already exists. Select it from the list on the left", "Data type exists", JOptionPane.INFORMATION_MESSAGE);
					}else{

						dataTypesListModel.addElement(dataType);

						dataTypeJList.setModel(dataTypesListModel);
						//scrollPane_1.revalidate();
						JOptionPane.showMessageDialog(IdentityCreationGUIDialog.this, "Added type "+dataType);
					}
				}

			}
		});
		btnCreateNewType.setBounds(96, 283, 164, 30);
		panel.add(btnCreateNewType);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Actual data stored in your profile", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(335, 49, 270, 256);
		contentPanel.add(panel_1);
		panel_1.setLayout(null);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 30, 250, 112);
		panel_1.add(scrollPane_2);

		DefaultListModel model = new DefaultListModel();
		ctxAttributeJList = new JList(model);
		scrollPane_2.setViewportView(ctxAttributeJList);
		ctxAttributeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		btnAddSelected = new JButton("Add selected attribute");
		btnAddSelected.setBounds(10, 167, 250, 36);
		panel_1.add(btnAddSelected);

		btnCreate = new JButton("Add new attribute of this type");
		btnCreate.setBounds(10, 214, 250, 36);
		panel_1.add(btnCreate);
		btnCreate.addActionListener(this);
		btnAddSelected.addActionListener(this);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 594, 615, 45);
			contentPanel.add(buttonPane);
			{
				okButton = new JButton("OK >>");
				okButton.setBounds(450, 5, 155, 34);
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.setLayout(null);
				buttonPane.add(okButton);

				getRootPane().setDefaultButton(okButton);
			}

			btnCancel = new JButton("Cancel");
			if (this.allIdentities.size()==0 && agreement!=null){
				btnCancel.setEnabled(false);
				btnCancel.setVisible(false);
			}
			btnCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					int showConfirmDialog = JOptionPane.showConfirmDialog(IdentityCreationGUIDialog.this, "Are you sure you want to close this window? All information will be lost", "Confirm closing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (showConfirmDialog==JOptionPane.YES_OPTION){
						IdentityCreationGUIDialog.this.identitiesTable = new Hashtable<String, List<CtxIdentifier>>();
						dispose();
					}

				}
			});
			btnCancel.setBounds(10, 5, 155, 34);
			buttonPane.add(btnCancel);

		}

		identityInfoModel = new IdentityInformationTableModel();

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 19, 575, 111);

		identityInformationJTable = new JTable(identityInfoModel);
		scrollPane.setViewportView(identityInformationJTable);
		btnPersonalise = new JButton("Get recommended attributes");
		if (null==agreement){
			btnPersonalise.setEnabled(false);
		}
		btnPersonalise.setBounds(290, 330, 315, 36);
		btnPersonalise.addActionListener(this);
		contentPanel.add(btnPersonalise);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Linked data to this identity", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 395, 595, 188);
		panel_2.setLayout(null);
		panel_2.add(scrollPane);
		contentPanel.add(panel_2);

		btnReset = new JButton("Reset data links");
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				identityInfoModel = new IdentityInformationTableModel();
				identityInformationJTable.setModel(identityInfoModel);
				//identityInformationJTable.setModel(new id);

			}
		});
		btnReset.setBounds(413, 138, 172, 36);
		panel_2.add(btnReset);

		btnRemoveSelected = new JButton("Remove selected");
		btnRemoveSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = identityInformationJTable.getSelectedRow();
				if (selectedRow>-1){
					identityInfoModel.remove(selectedRow);
				}
				identityInformationJTable.setModel(identityInfoModel);
			}
		});
		btnRemoveSelected.setBounds(10, 141, 189, 36);
		panel_2.add(btnRemoveSelected);
		if (null!=agreement){
			btnCreateNewType.setVisible(false);
		}
		
	}

	private boolean isReservedType(CtxAttribute ctxAttr){
		for (String str : reservedTypes){
			if (str.equalsIgnoreCase(ctxAttr.getType())){
				return true;
			}
		}

		return false;
	}
	
	private DefaultListModel getDefaultModel() throws MalformedCtxIdentifierException{
		DefaultListModel dataTypesListModel = new DefaultListModel();
		
		CtxAttributeIdentifier id = new CtxAttributeIdentifier("context://eliza.societies.local2/ENTITY/person/1/ATTRIBUTE/name/16");
		CtxAttribute attribute = new CtxAttribute(id);
		CtxAttributeWrapper wrapper = new CtxAttributeWrapper(attribute);
		List<CtxAttributeWrapper> list = new ArrayList<IdentityCreationGUIDialog.CtxAttributeWrapper>();
		list.add(wrapper);
		dataTable.put(attribute.getType(), list);
		dataTypesListModel.addElement(attribute.getType());
		return dataTypesListModel;
	}
	private DefaultListModel getModel(){
		this.dataTable = new Hashtable<String, List<CtxAttributeWrapper>>();

		if (this.agreement==null){

			//if agreement is null, window started from GUI, not negotiation
			try {
				person = this.identitySelection.getCtxBroker().retrieveIndividualEntity(this.userIdentity).get();
				DefaultListModel dataTypesListModel = new DefaultListModel();
				Set<CtxAttribute> attributes = person.getAttributes();
				Iterator<CtxAttribute> iterator = attributes.iterator();

				while (iterator.hasNext()){
					List<CtxAttributeWrapper> wrapperList = new ArrayList<IdentityCreationGUIDialog.CtxAttributeWrapper>();
					CtxAttribute attr = iterator.next();
					if (!isReservedType(attr)){
						if (dataTable.containsKey(attr.getType())){
							CtxAttributeWrapper wrappedAttr = new CtxAttributeWrapper(attr);

							dataTable.get(attr.getType()).add(wrappedAttr);
						}else{
							wrapperList.add(new CtxAttributeWrapper(attr));
							dataTable.put(attr.getType(), wrapperList);

							dataTypesListModel.addElement(attr.getType());
						}
					}

				}
				return dataTypesListModel;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{

			try {
				person = this.identitySelection.getCtxBroker().retrieveIndividualEntity(this.userIdentity).get();

				DefaultListModel dataTypesListModel = new DefaultListModel();

				for (ResponseItem item : agreement.getRequestedItems()){
					String dataType = item.getRequestItem().getResource().getDataType();
					dataTypesListModel.addElement(dataType);
					List<CtxAttributeWrapper> wrappedList = new ArrayList<CtxAttributeWrapper>();

					Set<CtxAttribute> attributes = person.getAttributes(dataType);
					Iterator<CtxAttribute> iterator = attributes.iterator();
					while(iterator.hasNext()){
						CtxAttribute attribute = iterator.next();
						wrappedList.add(new CtxAttributeWrapper(attribute));
					}
					this.dataTable.put(dataType, wrappedList);

				}
				return dataTypesListModel;
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new DefaultListModel();
	}

	public Hashtable<String, List<CtxIdentifier>> getIdentityInformation(){
		this.setVisible(true);
		return this.identitiesTable;
	}
	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(this.okButton)){

			if (this.txtIdentityName.getText().contains("@")){
				JOptionPane.showMessageDialog(this, "Your identity cannot contain the character '@'. ", "Invalid character", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (this.txtIdentityName.getText().contains(".")){
				JOptionPane.showMessageDialog(this, "Your identity cannot contain the character '.' . ", "Invalid character", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (this.txtIdentityName.getText().contains(" ")){
				JOptionPane.showMessageDialog(this, "Your identity cannot have spaces", "Invalid character", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (this.txtIdentityName.getText()==null || this.txtIdentityName.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "Please enter a name for your new identity", "Identity name missing", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			

			for (IIdentity id : this.allIdentities){
				if (id.getBareJid().equalsIgnoreCase(this.txtIdentityName.getText())){
					JOptionPane.showMessageDialog(this, "You already have an identity with the same name please select a different name for your new identity", "Identity name already exists", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
			}
			if (this.agreement!=null){
				if (this.dataTable.size()!=this.identityInfoModel.getRowCount()){
					JOptionPane.showMessageDialog(this, "You haven't added all the required data", "Setup not complete", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			int showConfirmDialog = JOptionPane.showConfirmDialog(this, "Are you happy with your selection?", "Please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (showConfirmDialog==JOptionPane.YES_OPTION){
				List<CtxIdentifier> ctxIDs = this.identityInfoModel.getValues();
				this.identitiesTable = new Hashtable<String, List<CtxIdentifier>>();
				identitiesTable.put(txtIdentityName.getText(), ctxIDs);
				if (null==agreement){
					IIdentity createdIdentity = this.identitySelection.createIdentity(txtIdentityName.getText(), ctxIDs);
					if (createdIdentity!=null){
						JOptionPane.showMessageDialog(this, "Your new identity "+createdIdentity.getBareJid()+" has been created!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				this.dispose();	

			}
		}else if (event.getSource().equals(this.btnAddSelected)){
			try{

				if (this.ctxAttributeJList.getSelectedIndex()>=0){
					CtxAttributeWrapper wrappedCtxAttribute =  (CtxAttributeWrapper) this.ctxAttributeJList.getSelectedValue();
					this.identityInfoModel.addAttribute(wrappedCtxAttribute.getAttribute());
					this.identityInformationJTable.setModel(identityInfoModel);	
				}else{
					JOptionPane.showMessageDialog(this, "Select an item from your profile.");
					return;	
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}else if (event.getSource().equals(this.btnCreate)){
			String dataType = (String) this.dataTypeJList.getSelectedValue();

			String value = JOptionPane.showInputDialog(this, "Please enter a value for "+dataType);
			if (value==null || value.isEmpty()){
				JOptionPane.showMessageDialog(this, "You must enter a value", "Error creating attribute", JOptionPane.INFORMATION_MESSAGE);
			}else{

				createCtxAttribute(dataType, value);
			}
		}else if (event.getSource().equals(this.btnPersonalise)){

			logging.debug("btnPersonalise");
			Hashtable<Resource, CtxIdentifier> recommendedAttributes = this.identitySelection.getPrivacyPreferenceManager().evaluateAttributeSelectionPreferences(agreement);
			logging.debug("evaluated attribute selection preferences: "+recommendedAttributes.size());
			if (recommendedAttributes.size()==0){
				JOptionPane.showMessageDialog(IdentityCreationGUIDialog.this, "No suitable attributes were found", "Recommended Attributes Search Result", JOptionPane.INFORMATION_MESSAGE); 
			}else{
				Hashtable<String, CtxAttribute> inputToRecGUI = new Hashtable<String, CtxAttribute>();
				Iterator<CtxIdentifier> iterator = recommendedAttributes.values().iterator();
				while (iterator.hasNext()){
					CtxIdentifier ctxIdentifier = iterator.next();
					List<CtxAttributeWrapper> list = this.dataTable.get(ctxIdentifier.getType());
					for (CtxAttributeWrapper wrapper : list){
						if (wrapper.equals(ctxIdentifier)){
							CtxAttribute attribute = wrapper.getAttribute();
							inputToRecGUI.put(attribute.getType(), attribute);
						}
					}
				}
				RecommendedAttributesDialog dialog = new RecommendedAttributesDialog(this);
				List<CtxAttribute> selectedAttributes = dialog.getSelectedAttributes(inputToRecGUI);
				if (selectedAttributes.size()>0){
					for (CtxAttribute ctxAttribute : selectedAttributes){
						this.identityInfoModel.addAttribute(ctxAttribute);	
					}
					this.identityInformationJTable.setModel(identityInfoModel);
				}

			}
			/*			if (this.identityInformationJTable.getModel().getRowCount()==0){
				Enumeration<CtxIdentifier> ctxIDs = recommendedAttributes.elements();
				while (ctxIDs.hasMoreElements()){
					CtxIdentifier ctxID = ctxIDs.nextElement();
					try {
						CtxAttribute ctxAttr = (CtxAttribute) this.ctxBroker.retrieve(ctxID).get();
						this.identityInfoModel.addAttribute(ctxAttr);
						this.identityInformationJTable.setModel(identityInfoModel);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}*/
		}


	}

	private void createCtxAttribute(String dataType, String value){
		if (person!=null){
			try {
				CtxAttribute ctxAttribute = this.identitySelection.getCtxBroker().createAttribute(this.person.getId(), dataType).get();
				ctxAttribute.setStringValue(value);
				ctxAttribute = (CtxAttribute) this.identitySelection.getCtxBroker().update(ctxAttribute).get();
				//this.myListModel = (CtxAttributesListModel) this.ctxAttributeJList.getModel();
				DefaultListModel model = (DefaultListModel) ctxAttributeJList.getModel();
				CtxAttributeWrapper wrappedAttribute = new CtxAttributeWrapper(ctxAttribute);
				model.addElement(wrappedAttribute);
				this.ctxAttributeJList.setModel(model);
				this.dataTable.get(dataType).add(wrappedAttribute);
				JOptionPane.showMessageDialog(this, dataType+" with value "+value+" was added to your profile.","Data added", JOptionPane.INFORMATION_MESSAGE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			JOptionPane.showMessageDialog(this, "There was an error retrieving data from the database", "Error creating data type", JOptionPane.ERROR_MESSAGE);
		}
	}


	class CtxAttributeWrapper implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 
		 */
		private final CtxAttribute attribute;

		public CtxAttributeWrapper (CtxAttribute attribute){
			this.attribute = attribute;
		}

		@Override
		public String toString(){
			return attribute.getType()+" : "+attribute.getStringValue();
		}

		public CtxIdentifier getCtxIdentifier(){
			return this.attribute.getId();
		}

		@Override
		public boolean equals(Object object){
			if (object instanceof CtxAttribute){
				if (!((CtxAttribute) object).getId().toUriString().equalsIgnoreCase(attribute.getId().toUriString())){
					return false;
				}
			}

			if (object instanceof CtxAttributeWrapper){
				return equals(((CtxAttributeWrapper) object).getAttribute());
			}
			return true;
		}

		public CtxAttribute getAttribute() {
			return attribute;
		}
	}


	/*	class CtxAttributesListModel extends DefaultListModel{
	 *//**
	 * 
	 *//*
		private static final long serialVersionUID = 1L;


		public CtxAttributesListModel(List<CtxAttribute> ctxAttributes) {
			//this.ctxAttributes = ctxAttributes;
			super();
			super.copyInto(ctxAttributes.toArray());



		}

		@Override
		public Object getElementAt(int index) {
			return super.getElementAt(index);
			//			return this.friendlyNames.get(index);
		}


		private String getFriendly(CtxAttribute ctxAttr){
			return ctxAttr.getType()+" = "+ctxAttr.getStringValue();
		}
		public CtxAttribute getAttributeFromName(String friendlyName){
			Enumeration<CtxAttribute> keys  = (Enumeration<CtxAttribute>) this.elements();
			while (keys.hasMoreElements()){
				CtxAttribute ctxAttribute = keys.nextElement();
				String friendly = getFriendly(ctxAttribute);
				if (friendly.equalsIgnoreCase(friendlyName)){
					return ctxAttribute;
				}
			}
			return null;
		}


		@Override
		public int getSize() {
			return super.getSize();
		}

		@Override
		public void addElement(Object object){
			if (object instanceof CtxAttribute){
				super.addElement(object);

			}//else ignore 
		}


	}*/


}
