package ac.hw.personis.preference;

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
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.MyMouseAdapter;
import ac.hw.personis.preference.table.DetailTableModel;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;

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

		
		
		
		JPanel panel_DOBF = new JPanel();
		getContentPane().add(panel_DOBF);
		panel_DOBF.setLayout(new BoxLayout(panel_DOBF, BoxLayout.Y_AXIS));

		JLabel lblDataObfuscationPreferences = new JLabel("Data Obfuscation Preferences");
		panel_DOBF.add(lblDataObfuscationPreferences);

		dobfModel = new DetailTableModel(this.dobfDetailsTable.keys());
		
		dobfTable = new JTable(dobfModel);
		JScrollPane scrollPane_dobf = new JScrollPane(dobfTable);
		scrollPane_dobf.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_DOBF.add(scrollPane_dobf);

		scrollPane_dobf.setViewportView(dobfTable);
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
			String title = this.getTitleString(bean.getRequestor(), bean.getResource(), false);
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
		sb.append("\tResource: ");
		sb.append(resource.getScheme()+"://");
		sb.append(resource.getDataType());

		if (printCtxID){
			sb.append("\tID:"+resource.getDataIdUri());
		}

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

		sb.append("\tIdentity: "+identity);

		return sb.toString();
	}


	public void showPreference(String id, PrivacyPreferenceTypeConstants type){
		switch(type){
		case PRIVACY_POLICY_NEGOTIATION:
			PPNPreferenceDetailsBean ppnDetailsBean = this.ppnDetailsTable.get(id);
			PPNPrivacyPreferenceTreeModel ppnPrefModel = this.personisHelper.getPrivacyPreferenceManager().getPPNPreference(ppnDetailsBean);
			//JOptionPane.showMessageDialog(this, ppnPrefModel.getRootPreference().toString());
			PreferenceGUI ppnGui = new PreferenceGUI(ppnPrefModel.getRootPreference());
			ppnGui.setVisible(true);
			break;
		case ACCESS_CONTROL:
			AccessControlPreferenceDetailsBean acDetailsBean = this.accDetailsTable.get(id);
			AccessControlPreferenceTreeModel accPrefModel = this.personisHelper.getPrivacyPreferenceManager().getAccCtrlPreference(acDetailsBean);
			//JOptionPane.showMessageDialog(this, accPrefModel.getRootPreference().toString());
			PreferenceGUI acGui = new PreferenceGUI(accPrefModel.getRootPreference());
			acGui.setVisible(true);
			break;
		case IDENTITY_SELECTION:
			IDSPreferenceDetailsBean idsDetailsBean = this.idsDetailsTable.get(id);
			IDSPrivacyPreferenceTreeModel idsPrefModel = this.personisHelper.getPrivacyPreferenceManager().getIDSPreference(idsDetailsBean);
			//JOptionPane.showMessageDialog(this, idsPrefModel.getRootPreference().toString());
			PreferenceGUI idsGui = new PreferenceGUI(idsPrefModel.getRootPreference());
			idsGui.setVisible(true);
			break;
		case ATTRIBUTE_SELECTION:
			AttributeSelectionPreferenceDetailsBean attrDetailsBean = this.attrSelDetailsTable.get(id);
			AttributeSelectionPreferenceTreeModel attrSelPrefModel = this.personisHelper.getPrivacyPreferenceManager().getAttrSelPreference(attrDetailsBean);
			PreferenceGUI attrGui = new PreferenceGUI(attrSelPrefModel.getRootPreference());
			attrGui.setVisible(true);
			break;
		case DATA_OBFUSCATION:
			DObfPreferenceDetailsBean dobfDetailsBean = this.dobfDetailsTable.get(id);
			DObfPreferenceTreeModel dobfPrefModel = this.personisHelper.getPrivacyPreferenceManager().getDObfPreference(dobfDetailsBean);
			//JOptionPane.showMessageDialog(this, dobfPrefModel.getRootPreference().toString());
			PreferenceGUI dobfGui = new PreferenceGUI(dobfPrefModel.getRootPreference());
			dobfGui.setVisible(true);
			break;

		}
	}
}
