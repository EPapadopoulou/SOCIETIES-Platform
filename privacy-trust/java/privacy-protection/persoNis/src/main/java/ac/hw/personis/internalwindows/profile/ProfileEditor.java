package ac.hw.personis.internalwindows.profile;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JButton;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

import ac.hw.personis.PersonisHelper;

import javax.swing.border.TitledBorder;

public class ProfileEditor extends JInternalFrame {
	private JTextField textField;
	private PersonisHelper helper;
	private IIdentity userID;
	private CtxAttribute selectedCtxAttribute;
	private JTextField txtDataType;
	private JTextField txtURI;
	private JTextField txtValue;
	private ICtxBroker ctxBroker;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProfileEditor frame = new ProfileEditor(null);
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
	public ProfileEditor(PersonisHelper helper) {
		this.helper = helper;
		ctxBroker = ProfileEditor.this.helper.getCtxBroker();

		userID = helper.getCommsMgr().getIdManager().getThisNetworkNode();
		setBounds(100, 100, 546, 476);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Search Box", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 513, 141);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Enter a data type you want to search for or create");
		lblNewLabel.setBounds(10, 18, 397, 35);
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		lblNewLabel.setLabelFor(textField);
		textField.setBounds(10, 52, 493, 34);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					List<CtxIdentifier> list = ctxBroker.lookup(userID, CtxModelType.ATTRIBUTE, textField.getText()).get();
					if (list.size()==0){
						JOptionPane.showMessageDialog(ProfileEditor.this, textField.getText()+" not found", "Results", JOptionPane.INFORMATION_MESSAGE);
					}else if (list.size()==1){
						selectedCtxAttribute = (CtxAttribute) ctxBroker.retrieve(list.get(0)).get();
						if (selectedCtxAttribute!=null){
							txtDataType.setText(selectedCtxAttribute.getType());
							txtURI.setText(selectedCtxAttribute.getId().getUri());
							txtValue.setText(selectedCtxAttribute.getStringValue());
						}
					}else {
						String[] possibilities = new String[list.size()];
						for (int i = 0; i< list.size(); i++){
							possibilities[i] = list.get(i).getUri();
						}
						
						
						String s = (String)JOptionPane.showInputDialog(
						                    ProfileEditor.this,
						                    "Results show that more than one entry exists for this data type. Please select which one you want to edit from the list below",
						                    "Multiple entries found",
						                    JOptionPane.QUESTION_MESSAGE,
						                    null,
						                    possibilities, 
						                    possibilities[0]);
						CtxIdentifier ctxID = new CtxAttributeIdentifier(s);
						if (ctxID!=null){
							selectedCtxAttribute = (CtxAttribute) ctxBroker.retrieve(ctxID).get();
							if (selectedCtxAttribute!=null){
								txtDataType.setText(selectedCtxAttribute.getType());
								txtURI.setText(selectedCtxAttribute.getId().getUri());
								txtValue.setText(selectedCtxAttribute.getStringValue());
							}
						}
						
						
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
		});
		btnSearch.setBounds(360, 98, 143, 32);
		panel.add(btnSearch);
		
		JButton btnCreate = new JButton("Create New");
		btnCreate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (textField.getText().isEmpty()){
					JOptionPane.showMessageDialog(ProfileEditor.this, "Type a data type in the box", "Input needed", JOptionPane.ERROR_MESSAGE);
				}else{
					try {
						IndividualCtxEntity scope = ctxBroker.retrieveIndividualEntity(userID).get();
						selectedCtxAttribute = ctxBroker.createAttribute(scope.getId(), textField.getText()).get();
						txtDataType.setText(selectedCtxAttribute.getType());
						txtURI.setText(selectedCtxAttribute.getId().getUri());
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
			}
		});
		btnCreate.setBounds(10, 97, 169, 32);
		panel.add(btnCreate);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(10, 163, 513, 247);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblDataType = new JLabel("Data Type");
		lblDataType.setBounds(21, 28, 227, 35);
		panel_1.add(lblDataType);
		
		txtDataType = new JTextField();
		txtDataType.setColumns(10);
		txtDataType.setBounds(258, 28, 245, 34);
		panel_1.add(txtDataType);
		
		JLabel lblUri = new JLabel("URI");
		lblUri.setBounds(21, 84, 227, 35);
		panel_1.add(lblUri);
		
		txtURI = new JTextField();
		txtURI.setColumns(10);
		txtURI.setBounds(258, 84, 245, 34);
		panel_1.add(txtURI);
		
		JLabel lblCurrentValue = new JLabel("Current value");
		lblCurrentValue.setBounds(21, 137, 227, 35);
		panel_1.add(lblCurrentValue);
		
		txtValue = new JTextField();
		txtValue.setColumns(10);
		txtValue.setBounds(258, 137, 245, 34);
		panel_1.add(txtValue);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedCtxAttribute==null){
					JOptionPane.showMessageDialog(ProfileEditor.this, "No data selected for update. Use the search box to look for a data type first.", "Nothing selected", JOptionPane.ERROR_MESSAGE);
				}else {
					if (txtValue.getText().isEmpty()){
						int n = JOptionPane.showConfirmDialog(
							    ProfileEditor.this,
							    "The value field is blank. Are you trying to delete the value of this data? Click yes to update the data type with an empty field. Click no to cancel updating the value",
							    "Empty value detected",
							    JOptionPane.YES_NO_OPTION);
						if (n==JOptionPane.NO_OPTION){
							return;
						}
					}
					try {
						selectedCtxAttribute = (CtxAttribute) ctxBroker.retrieve(selectedCtxAttribute.getId()).get();
						selectedCtxAttribute.setStringValue(txtValue.getText());
						selectedCtxAttribute = (CtxAttribute) ctxBroker.update(selectedCtxAttribute).get();
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
				
			}
		});
		btnUpdate.setBounds(358, 200, 145, 36);
		panel_1.add(btnUpdate);

	}
}
