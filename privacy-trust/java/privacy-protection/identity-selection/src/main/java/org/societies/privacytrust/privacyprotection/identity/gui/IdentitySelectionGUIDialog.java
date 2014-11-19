package org.societies.privacytrust.privacyprotection.identity.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.societies.api.identity.IIdentity;

public class IdentitySelectionGUIDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private List<IIdentity> identities;
	private JComboBox cmbIdentities;
	private JFrame frame;
	private JButton okButton;
	private IIdentity identity;
	private IIdentity recommendedIdentity;



	/**
	 * Create the dialog.
	 * @param recommendedIdentity 
	 */
	public IdentitySelectionGUIDialog(String providerFriendlyName, JFrame frame, List<IIdentity> identities, IIdentity recommendedIdentity) {
		super(frame,"Identity Selection", true);
		this.frame = frame;

		this.identities = identities;
		this.recommendedIdentity = recommendedIdentity;
		setBounds(100, 100, 506, 303);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><p style='width: 350px;'>");
		String text = "The following identities can be used to satisfy the "+providerFriendlyName+" data requests. Please select which one you wish to use to represent yourself to "+providerFriendlyName+".";
		sb.append(text);
		sb.append("</p> </body> </html>");
		JLabel lblTheFollowingIdentities = new JLabel(sb.toString());
		lblTheFollowingIdentities.setBounds(5, 11, 480, 75);
		contentPanel.add(lblTheFollowingIdentities);



		MyComboBoxModel model = new MyComboBoxModel(identities);
		cmbIdentities = new JComboBox(model);
		cmbIdentities.setSelectedIndex(0);
		cmbIdentities.setEditable(false);
		cmbIdentities.setBounds(15, 90, 458, 43);
		contentPanel.add(cmbIdentities);

		JButton btnCreateNew = new JButton("Create New Identity");
		btnCreateNew.setBounds(301, 156, 172, 28);
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
		btnSuggest.setBounds(15, 156, 230, 28);
		contentPanel.add(btnSuggest);
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
			fl_buttonPane.setVgap(10);
			fl_buttonPane.setHgap(10);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Continue");
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
