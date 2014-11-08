package ac.hw.personis.internalwindows.apps;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.ButtonActionListener;

public class ServicePanel extends JPanel {

	private ServiceAction action;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public enum ServiceAction {
		LAUNCH, INSTALL
	}
	/**
	 * Create the panel.
	 * @param helper 
	 * @param listener 
	 * @param serviceID 
	 */
	public ServicePanel(String serviceName, RequestorServiceBean requestor, PersonisHelper helper, ServiceAction action, ActionListener listener) {
		
		this.action = action;
		this.logging.debug("Adding requestor panel for: "+ServiceModelUtils.serviceResourceIdentifierToString(requestor.getRequestorServiceId()));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblServicename = new JLabel(serviceName);
		GridBagConstraints gbc_lblServicename = new GridBagConstraints();
		gbc_lblServicename.insets = new Insets(0, 0, 5, 5);
		gbc_lblServicename.gridx = 0;
		gbc_lblServicename.gridy = 0;
		add(lblServicename, gbc_lblServicename);
		
		JLabel lblServiceprovider = new JLabel(requestor.getRequestorId());
		GridBagConstraints gbc_lblServiceprovider = new GridBagConstraints();
		gbc_lblServiceprovider.insets = new Insets(0, 0, 5, 0);
		gbc_lblServiceprovider.gridx = 1;
		gbc_lblServiceprovider.gridy = 0;
		add(lblServiceprovider, gbc_lblServiceprovider);
		
		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 1;
		add(lblDescription, gbc_lblDescription);
		
		String titleButton = "";
		if (action.equals(ServiceAction.INSTALL)){
			titleButton = "Install";
		}else{
			titleButton = "Launch";
		}
		JButton btnInstall = new JButton(titleButton);
		btnInstall.addActionListener(listener);
		btnInstall.setActionCommand(titleButton);
		GridBagConstraints gbc_btnInstall = new GridBagConstraints();
		gbc_btnInstall.gridx = 1;
		gbc_btnInstall.gridy = 1;
		add(btnInstall, gbc_btnInstall);

	}
	public ServiceAction getAction() {
		return action;
	}
	public void setButtonAction(ServiceAction action) {
		this.action = action;
	}


	
}
