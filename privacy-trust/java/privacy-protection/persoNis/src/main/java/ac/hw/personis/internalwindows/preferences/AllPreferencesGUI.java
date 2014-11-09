package ac.hw.personis.internalwindows.preferences;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;

import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.PrivacyPermission;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.MyMouseAdapter;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AllPreferencesGUI extends JInternalFrame {
	private PersonisHelper personisHelper;
	private Hashtable<String, IDSPreferenceDetailsBean> idsDetailsTable;
	private Hashtable<String, AttributeSelectionPreferenceDetailsBean> attrSelDetailsTable;
	private Hashtable<String, DObfPreferenceDetailsBean> dobfDetailsTable;
	private Hashtable<String, AccessControlPreferenceDetailsBean> accDetailsTable;
	private Hashtable<String, PPNPreferenceDetailsBean> ppnDetailsTable;
	private JTable ppnTable;
	private JTable accTable;
	private JTable idsTable;
	private JTable attrTable;
	private JTable dobfTable;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private DetailTableModel ppnModel;
	private DetailTableModel accModel;
	private DetailTableModel idsModel;
	private DetailTableModel attrModel;
	private DetailTableModel dobfModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AllPreferencesGUI frame = new AllPreferencesGUI(new PersonisHelper());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AllPreferencesGUI(PersonisHelper personisHelper) {
		this.personisHelper = personisHelper;
		this.setupData();
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel buttonsPanel = new JPanel();
		getContentPane().add(buttonsPanel);

		JButton btnDeleteAllPreferences = new JButton("Delete all preferences");
		buttonsPanel.add(btnDeleteAllPreferences);

		JButton btnDeleteAllPermissions = new JButton("Delete all permissions");
		btnDeleteAllPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				List<String> installedApps = AllPreferencesGUI.this.personisHelper.getInstalledApps();
				for (String app : installedApps){
					Agreement agreement = AllPreferencesGUI.this.personisHelper.getAgreement(app);
					List<ResponseItem> requestedItems = agreement.getRequestedItems();
					for (ResponseItem item : requestedItems){
						IPrivacyDataManagerInternal privacyDataManagerInternal = AllPreferencesGUI.this.personisHelper.getPrivacyDataManagerInternal();
						try {
							
							
							RequestItem requestItem = item.getRequestItem();
							logging.debug("Deleting "+requestItem.getResource().getDataType()+" "+agreement.getRequestor()+" "+ResourceUtils.getDataIdentifier(requestItem.getResource()));
							privacyDataManagerInternal.deletePermissions(agreement.getRequestor(), ResourceUtils.getDataIdentifier(requestItem.getResource()), requestItem.getActions());
						} catch (MalformedCtxIdentifierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (PrivacyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		buttonsPanel.add(btnDeleteAllPermissions);

		JPanel panelPPN = new JPanel();
		getContentPane().add(panelPPN);
		panelPPN.setLayout(new BoxLayout(panelPPN, BoxLayout.Y_AXIS));

		JLabel label = new JLabel("PPN Preferences");
		panelPPN.add(label);

		ppnModel = new DetailTableModel(this.ppnDetailsTable.keys());
		ppnTable = new JTable(ppnModel);
		ppnTable.addMouseListener(new MyMouseAdapter(ppnTable, this, PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION));
		JScrollPane scrollPane_ppn = new JScrollPane(ppnTable);
		scrollPane_ppn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelPPN.add(scrollPane_ppn);


		scrollPane_ppn.setViewportView(ppnTable);

		JButton btnDeleteAllPpn = new JButton("Delete all PPN preferences");
		btnDeleteAllPpn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean deletePPNPreferences = AllPreferencesGUI.this.personisHelper.getPrivacyPreferenceManager().deletePPNPreferences();
				if (deletePPNPreferences){
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "All PPN preferences deleted");
				}else{
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "An error occurred and not all PPN preferences could be deleted");
				}
				AllPreferencesGUI.this.refreshData();
			}
		});
		panelPPN.add(btnDeleteAllPpn);

		JPanel panel_ACC = new JPanel();
		getContentPane().add(panel_ACC);
		panel_ACC.setLayout(new BoxLayout(panel_ACC, BoxLayout.Y_AXIS));

		JLabel lblAccessControlPreferences = new JLabel("Access Control Preferences");
		panel_ACC.add(lblAccessControlPreferences);

		accModel = new DetailTableModel(this.accDetailsTable.keys());
		this.logging.debug("AccModel row count: "+accModel.getRowCount());
		accTable = new JTable(accModel);
		accTable.addMouseListener(new MyMouseAdapter(accTable, this, PrivacyPreferenceTypeConstants.ACCESS_CONTROL));
		JScrollPane scrollPane_acc = new JScrollPane(accTable);
		scrollPane_acc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_ACC.add(scrollPane_acc);


		scrollPane_acc.setViewportView(accTable);

		JButton btnDeleteAllAccctrl = new JButton("Delete all AccCtrl preferences");
		btnDeleteAllAccctrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean deleteAccCtrlPreferences = AllPreferencesGUI.this.personisHelper.getPrivacyPreferenceManager().deleteAccCtrlPreferences();
				if (deleteAccCtrlPreferences){
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "All AccCtrl preferences deleted");
				}else{
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "An error occurred and not all AccCtrl preferences could be deleted");
				}
				refreshData();
			}
		});
		panel_ACC.add(btnDeleteAllAccctrl);



		JPanel panel_IDS = new JPanel();
		getContentPane().add(panel_IDS);
		panel_IDS.setLayout(new BoxLayout(panel_IDS, BoxLayout.Y_AXIS));

		JLabel lblIdentitySelectionPreferences = new JLabel("Identity Selection Preferences");
		panel_IDS.add(lblIdentitySelectionPreferences);

		idsModel = new DetailTableModel(this.idsDetailsTable.keys());
		idsTable = new JTable(idsModel);
		idsTable.addMouseListener(new MyMouseAdapter(idsTable, this, PrivacyPreferenceTypeConstants.IDENTITY_SELECTION));

		JScrollPane scrollPane_ids = new JScrollPane(idsTable);
		scrollPane_ids.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_IDS.add(scrollPane_ids);


		scrollPane_ids.setViewportView(idsTable);

		JButton btnDeleteAllIds = new JButton("Delete all IDS preferences");
		btnDeleteAllIds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean deleteIDSPreferences = AllPreferencesGUI.this.personisHelper.getPrivacyPreferenceManager().deleteIDSPreferences();
				if (deleteIDSPreferences){
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "All IDS preferences deleted");

				}else{
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "An error occurred and not all IDS preferences could be deleted");
				}
				refreshData();
			}
		});
		panel_IDS.add(btnDeleteAllIds);




		JPanel panel_ATTR = new JPanel();
		getContentPane().add(panel_ATTR);
		panel_ATTR.setLayout(new BoxLayout(panel_ATTR, BoxLayout.Y_AXIS));

		JLabel lblAttributeSelectionPreferences = new JLabel("Attribute Selection Preferences");
		panel_ATTR.add(lblAttributeSelectionPreferences);

		attrModel = new DetailTableModel(this.attrSelDetailsTable.keys());
		attrTable = new JTable(attrModel);
		attrTable.addMouseListener(new MyMouseAdapter(attrTable, this, PrivacyPreferenceTypeConstants.ATTRIBUTE_SELECTION));

		JScrollPane scrollPane_attr = new JScrollPane(attrTable);
		scrollPane_attr.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_ATTR.add(scrollPane_attr);


		scrollPane_attr.setViewportView(attrTable);

		JButton btnDeleteAllAttrsel = new JButton("Delete all AttrSel preferences");
		btnDeleteAllAttrsel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean deleteAttSelPreferences = AllPreferencesGUI.this.personisHelper.getPrivacyPreferenceManager().deleteAttSelPreferences();
				if (deleteAttSelPreferences){
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "All AttrSel preferences deleted");
				}else{
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "An error occured and not all AttrSel preferences could be deleted");
				}
				refreshData();
			}
		});
		panel_ATTR.add(btnDeleteAllAttrsel);




		JPanel panel_DOBF = new JPanel();
		getContentPane().add(panel_DOBF);
		panel_DOBF.setLayout(new BoxLayout(panel_DOBF, BoxLayout.Y_AXIS));

		JLabel lblDataObfuscationPreferences = new JLabel("Data Obfuscation Preferences");
		panel_DOBF.add(lblDataObfuscationPreferences);

		dobfModel = new DetailTableModel(this.dobfDetailsTable.keys());

		dobfTable = new JTable(dobfModel);
		dobfTable.addMouseListener(new MyMouseAdapter(dobfTable, this, PrivacyPreferenceTypeConstants.DATA_OBFUSCATION));
		JScrollPane scrollPane_dobf = new JScrollPane(dobfTable);
		scrollPane_dobf.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_DOBF.add(scrollPane_dobf);

		scrollPane_dobf.setViewportView(dobfTable);

		JButton btnDeleteAllDobf = new JButton("Delete all DObf preferences");
		btnDeleteAllDobf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean deleteDObfPreferences = AllPreferencesGUI.this.personisHelper.getPrivacyPreferenceManager().deleteDObfPreferences();
				if (deleteDObfPreferences){
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "All DObf preferences deleted");

				}
				else{
					JOptionPane.showMessageDialog(AllPreferencesGUI.this, "An error occured and not all DObf preferences could be deleted");
				}
				refreshData();
			}
		});
		panel_DOBF.add(btnDeleteAllDobf);
		this.pack();

	}

	public void refreshData(){
		this.logging.debug("Refreshing data tables");
		this.setupData();
		this.ppnModel.refreshData(this.ppnDetailsTable.keys());
		this.ppnTable.setModel(ppnModel);
		this.accModel.refreshData(this.accDetailsTable.keys());
		this.accTable.setModel(accModel);
		this.idsModel.refreshData(this.idsDetailsTable.keys());
		this.idsTable.setModel(idsModel);
		this.attrModel.refreshData(this.attrSelDetailsTable.keys());
		this.attrTable.setModel(attrModel);
		this.dobfModel.refreshData(this.dobfDetailsTable.keys());
		this.dobfTable.setModel(dobfModel);
		//JOptionPane.showMessageDialog(this, "Refreshed Data");
	}

	private void setupData() {

		/*
		 * PPN
		 */
		IPrivacyPreferenceManager privacyPreferenceManager = this.personisHelper.getPrivacyPreferenceManager();
		List<PPNPreferenceDetailsBean> ppnPreferenceDetails = privacyPreferenceManager.getPPNPreferenceDetails();
		logging.debug("Found "+ppnPreferenceDetails.size()+" ppn details");
		ppnDetailsTable = new Hashtable<String, PPNPreferenceDetailsBean>();

		for (PPNPreferenceDetailsBean bean : ppnPreferenceDetails){
			String title = this.getPPNTitleString(bean.getRequestor(), bean.getResource(), bean.getCondition(), false);
			ppnDetailsTable.put(title, bean);
		}

		/*
		 * Access Control 
		 */
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails = privacyPreferenceManager.getAccCtrlPreferenceDetails();
		logging.debug("Found "+accCtrlPreferenceDetails.size()+" accCtrl details");
		accDetailsTable = new Hashtable<String, AccessControlPreferenceDetailsBean>();

		for (AccessControlPreferenceDetailsBean bean : accCtrlPreferenceDetails){
			String title = this.getTitleString(bean.getRequestor(), bean.getResource(), true);
			this.logging.debug("Adding: "+title);
			accDetailsTable.put(title, bean);
			this.logging.debug("size of keys: "+accDetailsTable.keySet().size());
		}

		/*
		 * DOBF
		 */
		List<DObfPreferenceDetailsBean> dObfPreferenceDetails = privacyPreferenceManager.getDObfPreferenceDetails();
		logging.debug("Found "+dObfPreferenceDetails.size()+" dobf details");
		dobfDetailsTable = new Hashtable<String, DObfPreferenceDetailsBean>();

		for (DObfPreferenceDetailsBean bean : dObfPreferenceDetails){
			String title = this.getTitleString(bean.getRequestor(), bean.getResource(), true);
			dobfDetailsTable.put(title, bean);
		}

		/*
		 * IDS
		 */
		List<IDSPreferenceDetailsBean> idsPreferenceDetails = privacyPreferenceManager.getIDSPreferenceDetails();
		logging.debug("Found "+idsPreferenceDetails.size()+" ids details");
		idsDetailsTable = new Hashtable<String, IDSPreferenceDetailsBean>();

		for (IDSPreferenceDetailsBean bean : idsPreferenceDetails){

			String title = this.getIDSString(bean.getRequestor(), bean.getAffectedIdentity());
			idsDetailsTable.put(title, bean);
		}


		List<AttributeSelectionPreferenceDetailsBean> attrSelPreferenceDetails = privacyPreferenceManager.getAttrSelPreferenceDetails();
		logging.debug("Found "+attrSelPreferenceDetails.size()+" attrSel details");
		attrSelDetailsTable = new Hashtable<String, AttributeSelectionPreferenceDetailsBean>();

		for (AttributeSelectionPreferenceDetailsBean bean: attrSelPreferenceDetails){
			Resource resource = new Resource();
			resource.setDataType(bean.getDataType());
			resource.setScheme(DataIdentifierScheme.CONTEXT);
			String title = this.getTitleString(bean.getRequestor(), resource, false);
			title = title + "\tActions: "+bean.getActions();
			attrSelDetailsTable.put(title, bean);
		}
		//privacyPreferenceManager.getAttrSelPreferenceDetails();

	}

	private String getTitleString(RequestorBean requestor, Resource resource, boolean printCtxID){
		StringBuilder sb = new StringBuilder();
		sb.append("Requestor: ");
		if (requestor instanceof RequestorServiceBean){
			if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getGoogleRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.GOOGLE_VENUE_FINDER);
			}else if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getHwuRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
			}else{
				sb.append(ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) requestor).getRequestorServiceId()));
			}
		}
		sb.append("\t Resource: ");
		sb.append(resource.getScheme()+"://");
		sb.append(resource.getDataType());

		if (printCtxID){
			sb.append("\t ID:"+resource.getDataIdUri());
		}

		return sb.toString();
	}

	private String getPPNTitleString(RequestorBean requestor, Resource resource, ConditionConstants cc, boolean printCtxID){
		StringBuilder sb = new StringBuilder();
		sb.append("Requestor: ");
		if (requestor instanceof RequestorServiceBean){
			if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getGoogleRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.GOOGLE_VENUE_FINDER);
			}else if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getHwuRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
			}else{
				sb.append(ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) requestor).getRequestorServiceId()));
			}
		}
		sb.append("\t Resource: ");
		sb.append(resource.getScheme()+"://");
		sb.append(resource.getDataType());

		if (printCtxID){
			sb.append("\t ID:"+resource.getDataIdUri());
		}

		sb.append("\t Condition: ");
		sb.append(cc);
		return sb.toString();
	}
	private String getIDSString(RequestorBean requestor, String identity){
		StringBuilder sb = new StringBuilder();
		sb.append("Requestor: ");
		if (requestor instanceof RequestorServiceBean){
			if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getGoogleRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.GOOGLE_VENUE_FINDER);
			}else if (ServiceModelUtils.compare(((RequestorServiceBean) requestor).getRequestorServiceId(), this.personisHelper.getHwuRequestor().getRequestorServiceId())){
				sb.append(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
			}else{
				sb.append(ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) requestor).getRequestorServiceId()));
			}
		}

		sb.append("\t Identity: "+identity);

		return sb.toString();
	}


	public void showPreference(String id, PrivacyPreferenceTypeConstants type){
		switch(type){
		case PRIVACY_POLICY_NEGOTIATION:
			PPNPreferenceDetailsBean ppnDetailsBean = this.ppnDetailsTable.get(id);
			PPNPrivacyPreferenceTreeModel ppnPrefModel = this.personisHelper.getPrivacyPreferenceManager().getPPNPreference(ppnDetailsBean);
			//JOptionPane.showMessageDialog(this, ppnPrefModel.getRootPreference().toString());
			PreferenceGUI ppnGui = new PreferenceGUI(ppnPrefModel.getRootPreference(), ppnPrefModel.getDetails().getResource().getDataType());
			ppnGui.setVisible(true);
			break;
		case ACCESS_CONTROL:
			AccessControlPreferenceDetailsBean acDetailsBean = this.accDetailsTable.get(id);
			AccessControlPreferenceTreeModel accPrefModel = this.personisHelper.getPrivacyPreferenceManager().getAccCtrlPreference(acDetailsBean);
			//JOptionPane.showMessageDialog(this, accPrefModel.getRootPreference().toString());
			PreferenceGUI acGui = new PreferenceGUI(accPrefModel.getRootPreference(), accPrefModel.getDetails().getResource().getDataType());
			acGui.setVisible(true);
			break;
		case IDENTITY_SELECTION:
			IDSPreferenceDetailsBean idsDetailsBean = this.idsDetailsTable.get(id);
			IDSPrivacyPreferenceTreeModel idsPrefModel = this.personisHelper.getPrivacyPreferenceManager().getIDSPreference(idsDetailsBean);
			//JOptionPane.showMessageDialog(this, idsPrefModel.getRootPreference().toString());
			PreferenceGUI idsGui = new PreferenceGUI(idsPrefModel.getRootPreference(), idsPrefModel.getDetails().getAffectedIdentity());
			idsGui.setVisible(true);
			break;
		case ATTRIBUTE_SELECTION:
			AttributeSelectionPreferenceDetailsBean attrDetailsBean = this.attrSelDetailsTable.get(id);
			AttributeSelectionPreferenceTreeModel attrSelPrefModel = this.personisHelper.getPrivacyPreferenceManager().getAttrSelPreference(attrDetailsBean);
			PreferenceGUI attrGui = new PreferenceGUI(attrSelPrefModel.getRootPreference(), attrSelPrefModel.getDetails().getDataType());
			attrGui.setVisible(true);
			break;
		case DATA_OBFUSCATION:
			DObfPreferenceDetailsBean dobfDetailsBean = this.dobfDetailsTable.get(id);
			DObfPreferenceTreeModel dobfPrefModel = this.personisHelper.getPrivacyPreferenceManager().getDObfPreference(dobfDetailsBean);
			//JOptionPane.showMessageDialog(this, dobfPrefModel.getRootPreference().toString());
			PreferenceGUI dobfGui = new PreferenceGUI(dobfPrefModel.getRootPreference(), dobfPrefModel.getDetails().getResource().getDataType());
			dobfGui.setVisible(true);
			break;

		}
	}
}
