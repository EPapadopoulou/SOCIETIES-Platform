package org.societies.privacytrust.privacyprotection.privacynegotiation.identityCreation.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import javax.swing.JTable;
import javax.swing.JScrollPane;

public class IdentityCreationGUIDialog extends JDialog implements ActionListener, ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Hashtable<String, List<CtxIdentifier>> identitiesTable;
	private Agreement agreement;
	private JFrame frame;
	private JTextField txtIdentityName;
	private Vector<String> dataTypes;
	private ICtxBroker ctxBroker;
	//key: dataType, value = list of available ctxidentifiers for that data type
	private Hashtable<String, List<CtxAttribute>> dataTable;
	private MyListModel myListModel;
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


	/**
	 * Create the dialog.
	 */
	public IdentityCreationGUIDialog(JFrame frame, Agreement agreement, ICtxBroker ctxBroker, IIdentity userId) {
		super(frame, "Identity Creation", true);
		this.frame = frame;
		this.agreement = agreement;
		this.ctxBroker = ctxBroker;
		userIdentity = userId;
		setup();

		setBounds(100, 100, 631, 676);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblIdentityName = new JLabel("Identity name:");
			lblIdentityName.setBounds(10, 23, 87, 27);
			contentPanel.add(lblIdentityName);
		}

		txtIdentityName = new JTextField();
		txtIdentityName.setBounds(120, 23, 486, 27);
		contentPanel.add(txtIdentityName);
		txtIdentityName.setColumns(10);

		JPanel panel = new JPanel();
		panel.setBounds(10, 84, 270, 328);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblRequestedData = new JLabel("Requested data:");
		lblRequestedData.setBounds(10, 11, 250, 14);
		panel.add(lblRequestedData);

		dataTypeJList = new JList(dataTypes);
		dataTypeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//dataTypeList.getSelectionModel().addListSelectionListener(this);
		dataTypeJList.addListSelectionListener(this);
		dataTypeJList.setBounds(0, 38, 270, 290);
		panel.add(dataTypeJList);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(336, 84, 270, 212);
		contentPanel.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblAttributesInYour = new JLabel("Attributes in your profile:");
		lblAttributesInYour.setBounds(10, 11, 250, 14);
		panel_1.add(lblAttributesInYour);

		ctxAttributeJList = new JList();
		ctxAttributeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ctxAttributeJList.setBounds(0, 38, 270, 163);
		panel_1.add(ctxAttributeJList);

		btnCreate = new JButton("Create new attribute");
		btnCreate.addActionListener(this);
		btnCreate.setBounds(336, 316, 270, 36);
		contentPanel.add(btnCreate);

		btnAddSelected = new JButton("Add selected attribute");
		btnAddSelected.addActionListener(this);
		btnAddSelected.setBounds(336, 376, 270, 36);
		contentPanel.add(btnAddSelected);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 594, 665, 45);
			contentPanel.add(buttonPane);
			{
				okButton = new JButton("OK");
				okButton.setBounds(497, 5, 111, 34);
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.setLayout(null);
				buttonPane.add(okButton);

				getRootPane().setDefaultButton(okButton);
			}

		}

		identityInfoModel = new IdentityInformationTableModel();

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 449, 596, 124);
		contentPanel.add(scrollPane);
		identityInformationJTable = new JTable(identityInfoModel);
		scrollPane.setViewportView(identityInformationJTable);
	}

	private void setup(){
		this.dataTable = new Hashtable<String, List<CtxAttribute>>();
		try {
			person = this.ctxBroker.retrieveIndividualEntity(this.userIdentity).get();

			dataTypes = new Vector<String>();

			for (ResponseItem item : agreement.getRequestedItems()){
				String dataType = item.getRequestItem().getResource().getDataType();
				dataTypes.add(dataType);
				List<CtxAttribute> list = new ArrayList<CtxAttribute>();

				Set<CtxAttribute> attributes = person.getAttributes(dataType);
				Iterator<CtxAttribute> iterator = attributes.iterator();
				while(iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					list.add(attribute);
				}
				this.dataTable.put(dataType, list);

			}
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

	public Hashtable<String, List<CtxIdentifier>> getIdentityInformation(){
		this.setVisible(true);
		return this.identitiesTable;
	}
	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(this.okButton)){
			if (this.txtIdentityName.getText()==null || this.txtIdentityName.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "Please enter a name for your new identity", "Identity name missing", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (this.dataTypes.size()!=this.identityInfoModel.getRowCount()){
				JOptionPane.showMessageDialog(this, "You haven't added all the required data", "Setup not complete", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			List<CtxIdentifier> ctxIDs = this.identityInfoModel.getValues();
			this.identitiesTable = new Hashtable<String, List<CtxIdentifier>>();
			identitiesTable.put(txtIdentityName.getText(), ctxIDs);
			//TODO: setup identitiesTable
			this.dispose();	
		}else if (event.getSource().equals(this.btnAddSelected)){
			try{

				String selectedItem = (String) this.ctxAttributeJList.getSelectedValue();


				CtxAttribute ctxAttribute = this.myListModel.getAttributeFromName(selectedItem);
				if (ctxAttribute==null){
					JOptionPane.showMessageDialog(this, "Select an item from your profile.");
					return;
				}
				this.identityInfoModel.addAttribute(ctxAttribute);
				this.identityInformationJTable.setModel(identityInfoModel);
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
		}


	}

	private void createCtxAttribute(String dataType, String value){
		if (person!=null){
			try {
				CtxAttribute ctxAttribute = this.ctxBroker.createAttribute(this.person.getId(), dataType).get();
				ctxAttribute.setStringValue(value);
				ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();
				this.myListModel = (MyListModel) this.ctxAttributeJList.getModel();
				this.myListModel.addItem(ctxAttribute);
				this.ctxAttributeJList.setModel(myListModel);
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

	class MyListModel extends AbstractListModel implements ListModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private List<CtxAttribute> ctxAttributes;

		private List<String> friendlyNames;
		private CtxAttribute selectedAttribute;

		public MyListModel(List<CtxAttribute> ctxAttributes) {
			this.ctxAttributes = ctxAttributes;
			this.friendlyNames = new ArrayList<String>();

			for (CtxAttribute attribute : ctxAttributes){
				StringBuilder sb = new StringBuilder();
				sb.append(attribute.getType()+": "+attribute.getStringValue());
				friendlyNames.add(sb.toString());
			}

		}

		@Override
		public Object getElementAt(int index) {
			return this.friendlyNames.get(index);
		}

		public CtxAttribute getItemAt(int index){
			if (index>=0){
				return ctxAttributes.get(index);
			}
			return null;
		}

		public CtxAttribute getAttributeFromName(String friendlyName){
			int indexOf = this.friendlyNames.indexOf(friendlyName);
			if (indexOf>-1){
				return this.ctxAttributes.get(indexOf);
			}

			return null;
		}

		@Override
		public int getSize() {
			return this.ctxAttributes.size();
		}


		public Object getSelectedItem() {
			return this.selectedAttribute;
		}


		public void addItem(CtxAttribute ctxAttribute){
			this.ctxAttributes.add(ctxAttribute);
			StringBuilder sb = new StringBuilder();
			sb.append(ctxAttribute.getType()+": "+ctxAttribute.getStringValue());
			friendlyNames.add(sb.toString());

		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		//this.logging.debug("ValueChanged called with first index: "+event.getFirstIndex());


		String selectedDataType = (String) dataTypeJList.getSelectedValue();
		if (dataTable.containsKey(selectedDataType)){
			this.logging.debug("Updating model: "+selectedDataType);
			List<CtxAttribute> list = dataTable.get(selectedDataType);
			myListModel = new MyListModel(list);
			this.ctxAttributeJList.setModel(myListModel);
		}

	}
}
