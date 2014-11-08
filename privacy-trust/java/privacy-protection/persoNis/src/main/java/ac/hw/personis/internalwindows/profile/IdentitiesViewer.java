package ac.hw.personis.internalwindows.profile;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractListModel;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;

import ac.hw.personis.PersonisHelper;

public class IdentitiesViewer extends JInternalFrame {
	private JTable dataTable;
	private PersonisHelper helper;
	private IIdentity userMainIdentity;
	private ICtxBroker ctxBroker;
	private Hashtable<String, List<CtxAttribute>> identityDataTable;
	private JList identityJList;
	private Logger logging = LoggerFactory.getLogger(getClass());
	private Hashtable<String, List<String>> servicesByUserIdentity;
	private JLabel usedForLbl;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IdentitiesViewer frame = new IdentitiesViewer(null);
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
	public IdentitiesViewer(PersonisHelper helper) {
		this.helper = helper;
		setTitle("My Identities");


		getInformation();
		servicesByUserIdentity = helper.getServicesByUserIdentity();
		setBounds(100, 100, 647, 548);
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 86, 133, 423);
		getContentPane().add(panel);
		panel.setLayout(null);

		String[] identities = new String[identityDataTable.size()];
		Enumeration<String> keys = this.identityDataTable.keys();
		int i = 0;
		while (keys.hasMoreElements()){
			identities[i]=keys.nextElement();
			i++;
		}
		identityJList = new JList(identities);
		identityJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String selectedIdentity = (String) identityJList.getSelectedValue();
				if (identityDataTable.containsKey(selectedIdentity)){
					IdentityInformationTableModel model = new IdentityInformationTableModel(identityDataTable.get(selectedIdentity));
					dataTable.setModel(model);
				}
				String text = "<html>This identity has been used with these services: ";
				if (servicesByUserIdentity.containsKey(selectedIdentity)){
					
					StringBuilder sb = new StringBuilder();
					sb.append(text);
					List<String> services = servicesByUserIdentity.get(selectedIdentity);
					int lastIndexOf = services.size()-1;
					for (int i=0; i<services.size(); i++){
						String service = services.get(i);
						sb.append(service);
						if (i<lastIndexOf){
							sb.append(", ");
						}else{
							sb.append(".");
						}
					}
					usedForLbl.setText(sb.toString());
					
				}else{
					usedForLbl.setText(text+" none.");
				}
			}
		});
		identityJList.setBounds(10, 52, 113, 360);
		panel.add(identityJList);

		JLabel lblIdentities = new JLabel("Identities");
		lblIdentities.setHorizontalAlignment(SwingConstants.CENTER);
		lblIdentities.setBounds(10, 11, 113, 32);
		panel.add(lblIdentities);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 11, 611, 64);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("Select an identity from the left to see the data linked to that identity");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(42, 11, 542, 44);
		panel_1.add(lblNewLabel);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(153, 86, 468, 423);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 51, 448, 184);
		panel_2.add(scrollPane);

		dataTable = new JTable();
		dataTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(dataTable);
		dataTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = dataTable.getSelectedRow();
				if (selectedRow>=0){
					StringBuilder sb = new StringBuilder();
					sb.append("Data type: "+dataTable.getModel().getValueAt(selectedRow, 0)+System.getProperty( "line.separator" ));
					sb.append("Data value : "+dataTable.getModel().getValueAt(selectedRow, 1)+System.getProperty( "line.separator" ));
					sb.append("Data URI: "+dataTable.getModel().getValueAt(selectedRow, 2)+System.getProperty( "line.separator" ));
					JOptionPane.showMessageDialog(IdentitiesViewer.this, sb.toString(), "Details", JOptionPane.INFORMATION_MESSAGE, null);
				}
			}
		});

		JLabel lblNewLabel_1 = new JLabel("Data associated with selected identity");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 11, 448, 29);
		panel_2.add(lblNewLabel_1);
		
		
		usedForLbl = new JLabel("This identity has been used with these services: ");
		
		
		usedForLbl.setBounds(50, 271, 391, 69);
		panel_2.add(usedForLbl);

	}

	private void getInformation() {

		identityDataTable = new Hashtable<String, List<CtxAttribute>>();
		if (this.helper == null){
			this.logging.debug("My PersonisHelper is null");
			//init fake data
		}else{
			userMainIdentity = this.helper.getCommsMgr().getIdManager().getThisNetworkNode();
			ctxBroker = this.helper.getCtxBroker();

			IIdentitySelection identitySelection = this.helper.getIdentitySelection();
			List<IIdentity> allIdentities = identitySelection.getAllIdentities();
			this.logging.debug("Retrieved "+allIdentities.size()+" from IIdentity selection");
			for (IIdentity identity: allIdentities){
				this.logging.debug("Retrieving attributes linked to identity: "+identity.getBareJid());
				if (!identity.getJid().equalsIgnoreCase(userMainIdentity.getJid())){

					List<CtxIdentifier> linkedAttributes = identitySelection.getLinkedAttributes(identity);
					this.logging.debug("Identity<List<CtxIdentifier>>: {}", linkedAttributes);
					List<CtxAttribute> attributes = new ArrayList<CtxAttribute>();
					for (CtxIdentifier ctxID :linkedAttributes){
						try {
							CtxAttribute ctxAttribute = (CtxAttribute) ctxBroker.retrieve(CtxIDChanger.changeOwner(userMainIdentity.getBareJid(), (CtxAttributeIdentifier) ctxID)).get();
							logging.debug("Retrieved ctxAttribute {}", ctxID);
							if (ctxAttribute!=null){
								logging.debug("Adding non-null ctxAttribute {}", ctxID);
								attributes.add(ctxAttribute);
							}
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
					identityDataTable.put(identity.getBareJid(), attributes);
				}
			}
		}

		logging.debug("Finished loading identity information. Loaded: "+identityDataTable.size()+" identities");
	}
}
