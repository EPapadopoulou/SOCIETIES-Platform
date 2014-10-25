package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import javax.swing.Icon;

public class ActionsPanel extends JPanel {
	private Logger logging = LoggerFactory.getLogger(this.getClass());

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
	private ResponseItem respItem;
	private boolean firstRound;
	private JLabel lblWriteIcon;
	private JLabel lblCreateIcon;
	private JLabel lblDeleteIcon;

	private JLabel lblReadIcon;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ActionsPanel(RequestItem reqItem, ResponseItem respItem,boolean firstRound) {
		this.reqItem = reqItem;
		this.respItem = respItem;
		this.firstRound = firstRound;
		setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 90, 98);
		panel.setToolTipText("Right click on each checkbox to view the associated preference");
		add(panel);
		panel.setLayout(null);
		readChckBox = new JCheckBox("Read");
		readChckBox.setBounds(0, 0, 84, 23);
		readChckBox.setEnabled(false);
		readChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(readChckBox);



		writeChckBox = new JCheckBox("Write");
		writeChckBox.setBounds(0, 23, 84, 23);
		writeChckBox.setEnabled(false);
		writeChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(writeChckBox);


		createChckBox = new JCheckBox("Create");
		createChckBox.setBounds(0, 46, 84, 23);
		createChckBox.setEnabled(false);
		createChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(createChckBox);


		deleteChckBox = new JCheckBox("Delete");
		deleteChckBox.setBounds(0, 69, 84, 23);
		deleteChckBox.setEnabled(false);
		deleteChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(deleteChckBox);

		JPanel iconsPanel = new JPanel();
		iconsPanel.setBounds(90, 0, 66, 98);
		add(iconsPanel);
		iconsPanel.setLayout(null);

		
		lblReadIcon = new JLabel();
		lblReadIcon.setBounds(0, 0, 23, 23);
		iconsPanel.add(lblReadIcon);

		lblWriteIcon = new JLabel();
		lblWriteIcon.setBounds(0, 23, 23, 23);
		iconsPanel.add(lblWriteIcon);

		lblCreateIcon = new JLabel();
		lblCreateIcon.setBounds(0, 46, 23, 23);
		iconsPanel.add(lblCreateIcon);

		lblDeleteIcon = new JLabel();
		lblDeleteIcon.setBounds(0, 69, 23, 23);
		iconsPanel.add(lblDeleteIcon);
		System.out.println("actions size: "+reqItem.getActions().size());
		for (Action a: reqItem.getActions()){
			if (a.getActionConstant().equals(ActionConstants.READ)){
				readChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					readChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.WRITE)){
				writeChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					writeChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.CREATE)){
				createChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					createChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.DELETE)){
				deleteChckBox.setSelected(true);
				if(firstRound && a.isOptional()){
					deleteChckBox.setEnabled(true);
				}
			}
		}

		setupPopupMenu();
	}

	private ImageIcon createWarningImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/warning.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	private ImageIcon createCheckImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/check.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	private ImageIcon createLeftArrowImageIcon() {
		java.net.URL imgURL = getClass().getResource("/images/left_arrow_galazio_23.png");
		if (imgURL!=null){
			return new ImageIcon(imgURL);
		}else{
			this.logging.error("Can't find warning image");
			return null;
		}
	}

	public void setEnabledChckBoxes(boolean bool){
		for (Action a: reqItem.getActions()){
			if (a.getActionConstant().equals(ActionConstants.READ)){
				readChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					readChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.WRITE)){
				writeChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					writeChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.CREATE)){
				createChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					createChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.DELETE)){
				deleteChckBox.setSelected(true);
				if(firstRound && a.isOptional()){
					deleteChckBox.setEnabled(true);
				}
			}
		}
	}

	private void setupPopupMenu(){
		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Click here to view the associated preference");
		menuItem.addActionListener(new PopupMenuItemActionListener());
		popup.add(menuItem);
		menuItem = new JMenuItem("Another popup menu item");
		menuItem.addActionListener(new PopupMenuItemActionListener());
		popup.add(menuItem);

		//Add listener to components that can bring up popup menus.
		MouseListener popupListener = new PopupListener();
		readChckBox.addMouseListener(popupListener);
		writeChckBox.addMouseListener(popupListener);
		createChckBox.addMouseListener(popupListener);
		deleteChckBox.addMouseListener(popupListener);
	}



	public void applyPersonalisation(){
		if (respItem!=null){
			for (Action requestedAction : this.reqItem.getActions()){
				//can only edit the actions that are optional
				if (requestedAction.isOptional()){
					boolean userAllowed = false;
					//check if this action is included in the respItem:
					for (Action suggestedAction : this.respItem.getRequestItem().getActions()){
						if (suggestedAction.getActionConstant().equals(requestedAction.getActionConstant())){
							userAllowed = true;
						}
					}
					//personalise
					if (userAllowed){
						switch (requestedAction.getActionConstant()){
						case READ:	this.readChckBox.setSelected(true);
						lblReadIcon.setIcon(createLeftArrowImageIcon());
						break;
						case WRITE:	this.writeChckBox.setSelected(true);
						lblWriteIcon.setIcon(createLeftArrowImageIcon());
						break;
						case CREATE: this.createChckBox.setSelected(true);
						lblCreateIcon.setIcon(createLeftArrowImageIcon());
						break;
						case DELETE: this.deleteChckBox.setSelected(true);
						lblDeleteIcon.setIcon(createLeftArrowImageIcon());
						break;
						}
					}
				}
			}
		}

	}

	public void resetChanges(){
		//reset all
		this.readChckBox.setSelected(false);
		this.readChckBox.setEnabled(false);
		this.writeChckBox.setSelected(false);
		this.writeChckBox.setEnabled(false);
		this.createChckBox.setSelected(false);
		this.createChckBox.setEnabled(false);
		this.deleteChckBox.setSelected(false);
		this.deleteChckBox.setEnabled(false);

		this.lblCreateIcon.setIcon(null);
		this.lblDeleteIcon.setIcon(null);
		this.lblReadIcon.setIcon(null);
		this.lblWriteIcon.setIcon(null);
		for (Action a: reqItem.getActions()){
			if (a.getActionConstant().equals(ActionConstants.READ)){
				readChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					readChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.WRITE)){
				writeChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					writeChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.CREATE)){
				createChckBox.setSelected(true);
				if (firstRound && a.isOptional()){
					createChckBox.setEnabled(true);
				}
			}else if (a.getActionConstant().equals(ActionConstants.DELETE)){
				deleteChckBox.setSelected(true);
				if(firstRound && a.isOptional()){
					deleteChckBox.setEnabled(true);
				}
			}
		}
	}

	class PopupListener extends MouseAdapter {


		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup = new JPopupMenu();

				readMenuItem = new JMenuItem("View 'Read' preference");
				readMenuItem.addActionListener(new PopupMenuItemActionListener());
				writeMenuItem = new JMenuItem("View 'Write' preference");
				writeMenuItem.addActionListener(new PopupMenuItemActionListener());
				createMenuItem = new JMenuItem("View 'Create' preference");
				createMenuItem.addActionListener(new PopupMenuItemActionListener());
				deleteMenuItem = new JMenuItem("View 'Delete' preference");
				deleteMenuItem.addActionListener(new PopupMenuItemActionListener());


				popup.add(readMenuItem);
				popup.add(writeMenuItem);
				popup.add(createMenuItem);
				popup.add(deleteMenuItem);

				popup.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}

	class PopupMenuItemActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(readMenuItem)){
				JOptionPane.showMessageDialog(ActionsPanel.this, "READ - Not implemented yet");
			}else if (e.getSource().equals(writeMenuItem)){
				JOptionPane.showMessageDialog(ActionsPanel.this, "WRITE - Not implemented yet");
			}else if (e.getSource().equals(createMenuItem)){
				JOptionPane.showMessageDialog(ActionsPanel.this, "CREATE - Not implemented yet");
			}else if (e.getSource().equals(deleteMenuItem)){
				JOptionPane.showMessageDialog(ActionsPanel.this, "DELETE - Not implemented yet");
			}  
		}

	}

	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		if (this.readChckBox.isSelected()){
			Action action = new Action();
			action.setActionConstant(ActionConstants.READ);
			actions.add(action);
		}
		if (this.writeChckBox.isSelected()){
			Action action = new Action();
			action.setActionConstant(ActionConstants.WRITE);
			actions.add(action);
		}
		if (this.createChckBox.isSelected()){
			Action action = new Action();
			action.setActionConstant(ActionConstants.CREATE);
			actions.add(action);
		}
		if (this.deleteChckBox.isSelected()){
			Action action = new Action();
			action.setActionConstant(ActionConstants.DELETE);
			actions.add(action);
		}
		return actions;
	}

	public boolean match() {
		List<Action> userActions = this.getActions();
		List<Action> requestedActions = this.reqItem.getActions();


		for (Action a: requestedActions){
			if (!actionExistsInList(a, userActions)){
				return false;
			}
		}

		return true;
	}

	private boolean actionExistsInList(Action action, List<Action> actions){
		for (Action a: actions){
			if (a.getActionConstant().equals(action.getActionConstant())){
				return true;
			}
		}
		return false;
	}
}
