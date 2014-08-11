package org.societies.privacytrust.privacyprotection.privacypolicy.gui.components;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

public class ActionsPanel extends JPanel {

	private JCheckBox readChckBox;
	private JCheckBox writeChckBox;
	private JCheckBox createChckBox;
	private JCheckBox deleteChckBox;
	private JPopupMenu popup;
	private JMenuItem readMenuItem;
	private JMenuItem writeMenuItem;
	private JMenuItem createMenuItem;
	private JMenuItem deleteMenuItem;
	
	private RequestItem reqItem;
	private List<PPNPreferenceDetailsBean> preferences;
	private JCheckBox chckbxReadOptional;
	private JCheckBox chckbxWriteOptional;
	private JCheckBox chckbxCreateOptional;
	private JCheckBox chckbxDeleteOptional;

	/**
	 * Create the panel.
	 */
	public ActionsPanel(RequestItem reqItem) {
		this.reqItem = reqItem;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};

		setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		panel.setToolTipText("Right click on each checkbox to view the associated preference");
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{59, 0, 0};
		gbl_panel.rowHeights = new int[]{23, 23, 23, 23, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		readChckBox = new JCheckBox("Read");
		readChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		GridBagConstraints gbc_readChckBox = new GridBagConstraints();
		gbc_readChckBox.anchor = GridBagConstraints.WEST;
		gbc_readChckBox.insets = new Insets(0, 0, 5, 5);
		gbc_readChckBox.gridx = 0;
		gbc_readChckBox.gridy = 0;
		panel.add(readChckBox, gbc_readChckBox);
						
						chckbxReadOptional = new JCheckBox("optional");
						GridBagConstraints gbc_chckbxReadOptional = new GridBagConstraints();
						gbc_chckbxReadOptional.insets = new Insets(0, 0, 5, 0);
						gbc_chckbxReadOptional.gridx = 1;
						gbc_chckbxReadOptional.gridy = 0;
						panel.add(chckbxReadOptional, gbc_chckbxReadOptional);
				
				
						writeChckBox = new JCheckBox("Write");
						writeChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
						GridBagConstraints gbc_writeChckBox = new GridBagConstraints();
						gbc_writeChckBox.anchor = GridBagConstraints.WEST;
						gbc_writeChckBox.insets = new Insets(0, 0, 5, 5);
						gbc_writeChckBox.gridx = 0;
						gbc_writeChckBox.gridy = 1;
						panel.add(writeChckBox, gbc_writeChckBox);
				
				chckbxWriteOptional = new JCheckBox("optional");
				GridBagConstraints gbc_chckbxWriteOptional = new GridBagConstraints();
				gbc_chckbxWriteOptional.insets = new Insets(0, 0, 5, 0);
				gbc_chckbxWriteOptional.gridx = 1;
				gbc_chckbxWriteOptional.gridy = 1;
				panel.add(chckbxWriteOptional, gbc_chckbxWriteOptional);
		
		
				createChckBox = new JCheckBox("Create");
				createChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
				GridBagConstraints gbc_createChckBox = new GridBagConstraints();
				gbc_createChckBox.anchor = GridBagConstraints.WEST;
				gbc_createChckBox.insets = new Insets(0, 0, 5, 5);
				gbc_createChckBox.gridx = 0;
				gbc_createChckBox.gridy = 2;
				panel.add(createChckBox, gbc_createChckBox);
				
				chckbxCreateOptional = new JCheckBox("optional");
				GridBagConstraints gbc_chckbxCreateOptional = new GridBagConstraints();
				gbc_chckbxCreateOptional.insets = new Insets(0, 0, 5, 0);
				gbc_chckbxCreateOptional.gridx = 1;
				gbc_chckbxCreateOptional.gridy = 2;
				panel.add(chckbxCreateOptional, gbc_chckbxCreateOptional);
		
		
				deleteChckBox = new JCheckBox("Delete");
				deleteChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
				GridBagConstraints gbc_deleteChckBox = new GridBagConstraints();
				gbc_deleteChckBox.insets = new Insets(0, 0, 0, 5);
				gbc_deleteChckBox.anchor = GridBagConstraints.WEST;
				gbc_deleteChckBox.gridx = 0;
				gbc_deleteChckBox.gridy = 3;
				panel.add(deleteChckBox, gbc_deleteChckBox);
				
				chckbxDeleteOptional = new JCheckBox("optional");
				GridBagConstraints gbc_chckbxDeleteOptional = new GridBagConstraints();
				gbc_chckbxDeleteOptional.gridx = 1;
				gbc_chckbxDeleteOptional.gridy = 3;
				panel.add(chckbxDeleteOptional, gbc_chckbxDeleteOptional);
		System.out.println("actions size: "+reqItem.getActions().size());
		for (Action a: reqItem.getActions()){
			System.out.println("processing action: "+a.getActionConstant());
			if (a.getActionConstant().equals(ActionConstants.READ)){
				readChckBox.setSelected(true);
			}else if (a.getActionConstant().equals(ActionConstants.WRITE)){
				writeChckBox.setSelected(true);
			}else if (a.getActionConstant().equals(ActionConstants.CREATE)){
				createChckBox.setSelected(true);
			}else if (a.getActionConstant().equals(ActionConstants.DELETE)){
				deleteChckBox.setSelected(true);
			}
		}
	}
	
	public List<Action> getActions(){
		List<Action> actions = new ArrayList<Action>();
		if (this.readChckBox.isSelected()){
			Action read = new Action();
			read.setActionConstant(ActionConstants.READ);
			read.setOptional(chckbxReadOptional.isSelected());
			actions.add(read);
		}
		
		if (this.writeChckBox.isSelected()){
			Action write = new Action();
			write.setActionConstant(ActionConstants.WRITE);
			write.setOptional(chckbxWriteOptional.isSelected());
			actions.add(write);
		}
		
		if (this.createChckBox.isSelected()){
			Action create = new Action();
			create.setActionConstant(ActionConstants.CREATE);
			create.setOptional(chckbxCreateOptional.isSelected());
			actions.add(create);
		}
		
		if (this.deleteChckBox.isSelected()){
			Action delete = new Action();
			delete.setActionConstant(ActionConstants.DELETE);
			delete.setOptional(chckbxDeleteOptional.isSelected());
			actions.add(delete);
		}
		
		return actions;
		
		
	}

}
