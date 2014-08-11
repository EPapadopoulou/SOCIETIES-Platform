package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

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
	private ResponseItem respItem;
	private List<PPNPreferenceDetailsBean> preferences;
	private boolean thirdRound;

	/**
	 * Create the panel.
	 * @param firstRound 
	 */
	public ActionsPanel(RequestItem reqItem, ResponseItem respItem, List<PPNPreferenceDetailsBean> preferences, boolean firstRound) {
		this.reqItem = reqItem;
		this.respItem = respItem;
		this.preferences = preferences;
		this.thirdRound = firstRound;
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


		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		readChckBox = new JCheckBox("Read");
		readChckBox.setEnabled(firstRound);
		readChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(readChckBox);

		

		writeChckBox = new JCheckBox("Write");
		writeChckBox.setEnabled(firstRound);
		writeChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(writeChckBox);
		

		createChckBox = new JCheckBox("Create");
		createChckBox.setEnabled(firstRound);
		createChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(createChckBox);
		

		deleteChckBox = new JCheckBox("Delete");
		deleteChckBox.setEnabled(firstRound);
		deleteChckBox.setToolTipText("Right click on each checkbox to view the associated preference");
		panel.add(deleteChckBox);
		System.out.println("actions size: "+reqItem.getActions().size());
		for (Action a: reqItem.getActions()){
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

		setupPopupMenu();
	}

	public void setEnabledChckBoxes(boolean bool){
		for (Action a: reqItem.getActions()){
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
			boolean allowed = false;
			//check if this action is included in the respItem:
			for (Action suggestedAction : this.respItem.getRequestItem().getActions()){
				if (suggestedAction.getActionConstant().equals(requestedAction.getActionConstant())){
					allowed = true;
				}
			}

			//reset all
			this.readChckBox.setSelected(false);
			this.writeChckBox.setSelected(false);
			this.createChckBox.setSelected(false);
			this.deleteChckBox.setSelected(false);
			
			//personalise
			if (allowed){
				switch (requestedAction.getActionConstant()){
				case READ:	this.readChckBox.setSelected(true);
					break;
				case WRITE:	this.writeChckBox.setSelected(true);
					break;
				case CREATE: this.createChckBox.setSelected(true);
					break;
				case DELETE: this.deleteChckBox.setSelected(true);
					break;
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
		
		for (Action a: reqItem.getActions()){
			switch (a.getActionConstant()){
			case READ:	this.readChckBox.setSelected(true);
						this.readChckBox.setEnabled(true);
				break;
			case WRITE:	this.writeChckBox.setSelected(true);
						this.writeChckBox.setEnabled(true);
				break;
			case CREATE: this.createChckBox.setSelected(true);
						this.createChckBox.setEnabled(true);
				break;
			case DELETE: this.deleteChckBox.setSelected(true);
						this.deleteChckBox.setEnabled(true);
				break;
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
