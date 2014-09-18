package org.societies.privacytrust.privacyprotection.privacynegotiation.identityCreation.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

import org.societies.api.identity.IIdentity;
import org.societies.privacytrust.privacyprotection.api.identity.IdentityImpl;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JComboBox;

public class IdentitySelectionGUIDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private List<IIdentity> identities;
	private JComboBox cmbIdentities;
	private JFrame frame;
	private JButton okButton;
	private IIdentity identity;
	private IIdentity recommendedIdentity;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ArrayList<IIdentity> identities = new ArrayList<IIdentity>();
			IIdentity ideliza  = new IdentityImpl("eliza.societies.local2");
			IIdentity idAnonymous = new IdentityImpl("anonymous234234.societies.local2");
			identities.add(ideliza);
			identities.add(idAnonymous);
			JFrame frame  = new JFrame();
			IdentitySelectionGUIDialog dialog = new IdentitySelectionGUIDialog(frame, identities, ideliza);	
			IIdentity selectedIdentity = dialog.getSelectedIdentity();
			System.out.println("Got Identity: "+selectedIdentity.getJid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param recommendedIdentity 
	 */
	public IdentitySelectionGUIDialog(JFrame frame, List<IIdentity> identities, IIdentity recommendedIdentity) {
		super(frame,"Identity Selection", true);
		this.frame = frame;

		this.identities = identities;
		this.recommendedIdentity = recommendedIdentity;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblTheFollowingIdentities = new JLabel("<html>The following identities can be used to interact with service... Please select which one you wish to use to represent yourself to ... </html>");
		lblTheFollowingIdentities.setBounds(5, 11, 424, 75);
		contentPanel.add(lblTheFollowingIdentities);



		MyComboBoxModel model = new MyComboBoxModel(identities);
		cmbIdentities = new JComboBox(model);
		cmbIdentities.setSelectedIndex(0);
		cmbIdentities.setEditable(false);
		cmbIdentities.setBounds(15, 80, 385, 43);
		contentPanel.add(cmbIdentities);

		JButton btnCreateNew = new JButton("Create New Identity");
		btnCreateNew.setBounds(264, 145, 136, 48);
		btnCreateNew.addActionListener(this);
		contentPanel.add(btnCreateNew);
		
		JButton btnSuggest = new JButton("Get recommended identity");
		btnSuggest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (IdentitySelectionGUIDialog.this.recommendedIdentity==null){
					JOptionPane.showMessageDialog(IdentitySelectionGUIDialog.this, "According to your preferences and your previous \n"
							+ "activity none of your existing identities\n can be recommended for use in this case \nhence the system recommends that you create a new identity.");
				}else{
					JOptionPane.showMessageDialog(IdentitySelectionGUIDialog.this, "According to your preferences and your previous \n"
							+ "activity, it is recommended to use your "+IdentitySelectionGUIDialog.this.recommendedIdentity.getBareJid()+" identity");
					cmbIdentities.setSelectedItem(IdentitySelectionGUIDialog.this.recommendedIdentity);
				}
			}
		});
		btnSuggest.setBounds(15, 145, 188, 48);
		contentPanel.add(btnSuggest);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public IIdentity getSelectedIdentity(){
		this.setVisible(true);
		return identity;
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.okButton)){
			this.identity = (IIdentity) this.cmbIdentities.getSelectedItem();
			this.dispose();
		}else{
			this.identity = null;
			this.dispose();
		}

	}

	class MyComboBoxModel extends AbstractListModel implements ComboBoxModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private List<IIdentity> identities2;

		private IIdentity selectedItem;

		public MyComboBoxModel(List<IIdentity> identities){
			identities2 = identities;
		}

		@Override
		public Object getElementAt(int index) {
			// TODO Auto-generated method stub
			return this.identities2.get(index).getJid();
		}

		@Override
		public int getSize() {
			// TODO Auto-generated method stub
			return this.identities2.size();
		}

		@Override
		public Object getSelectedItem() {
			return this.selectedItem;
		}

		@Override
		public void setSelectedItem(Object anItem) {
			for (IIdentity id : identities2){
				if (id.getJid().equalsIgnoreCase(anItem.toString())){
					this.selectedItem = id;
				}
			}

		}


	}
}
