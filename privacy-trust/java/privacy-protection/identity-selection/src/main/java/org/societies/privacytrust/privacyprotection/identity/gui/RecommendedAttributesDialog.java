package org.societies.privacytrust.privacyprotection.identity.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;

import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class RecommendedAttributesDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private List<CtxAttribute> selectedCtxAttributes = new ArrayList<CtxAttribute>();
	private JTable attributesTable;
	private RecommendedAttributesTableModel model;


	/**
	 * Create the dialog.
	 */
	public RecommendedAttributesDialog(JDialog parentDialog) {
		super(parentDialog, "Recommended Attributes", true);
		
		setBounds(100, 100, 560, 376);
		
		getContentPane().setLayout(null);
		contentPanel.setBounds(10, 11, 524, 267);
		contentPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Recommended Attributes for new Identity", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("<html>The system suggests that you link the attributes in the list below with your new identity. Tick the attributes that you accept to be linked with your new identity. </html>");
		lblNewLabel.setBounds(10, 22, 504, 57);
		contentPanel.add(lblNewLabel);
		
		model = new RecommendedAttributesTableModel();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 90, 504, 165);
		contentPanel.add(scrollPane);

		attributesTable = new JTable(model);
		scrollPane.setViewportView(attributesTable);
		
		
		JPanel btnPanel = new JPanel();
		btnPanel.setBounds(10, 293, 524, 41);
		getContentPane().add(btnPanel);
		btnPanel.setLayout(null);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setBounds(10, 11, 102, 23);
		btnPanel.add(cancelButton);
		cancelButton.setActionCommand("Cancel");
		JButton okButton = new JButton("OK");
		okButton.setBounds(401, 11, 113, 23);
		btnPanel.add(okButton);
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedCtxAttributes = model.getValues();
				dispose();
				
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				
			}
		});
		
	}

	public List<CtxAttribute> getSelectedAttributes(Hashtable<String, CtxAttribute> table){
		
		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements()){
			String dataType = keys.nextElement();
			if (table.get(dataType)==null){
				
			}else{
				this.model.addAttribute(table.get(dataType));
			}
		}
		this.attributesTable.setModel(model);
		this.setVisible(true);
		return selectedCtxAttributes;
		
	}
}
