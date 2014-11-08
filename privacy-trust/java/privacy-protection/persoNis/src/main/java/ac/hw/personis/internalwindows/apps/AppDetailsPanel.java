package ac.hw.personis.internalwindows.apps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.RequestorServiceBean;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.ButtonActionListener;
import ac.hw.personis.internalwindows.apps.ServicePanel.ServiceAction;
import ac.hw.personis.services.GoogleMapsService;

public class AppDetailsPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblDetails;
	private JButton btnInstall;
	private ButtonActionListener actionListener;
	private PersonisHelper helper;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private int negotiationCounter = 0;
	private RequestorServiceBean requestor;

	/**
	 * Create the panel.
	 */
	public AppDetailsPanel(PersonisHelper helper) {
		this.helper = helper;
		setLayout(null);

		lblDetails = new JLabel("Details");
		lblDetails.setVerticalAlignment(SwingConstants.TOP);
		lblDetails.setBounds(0, 11, 678, 124);
		add(lblDetails);

		btnInstall = new JButton("Install");
		btnInstall.setActionCommand(ServiceAction.INSTALL.name());
		btnInstall.addActionListener(this);
		btnInstall.setBounds(612, 146, 78, 23);
		add(btnInstall);

	}

	public void setDetailsText(String text){
		this.lblDetails.setText(text);
	}

	public void setActionCommand(String action){
		this.btnInstall.setActionCommand(action);
	}

	public void addActionListener(ButtonActionListener actionListener){

		this.actionListener = actionListener;

	}

	public void changeActionDetails(RequestorServiceBean requestor){

		this.requestor = requestor;

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		negotiationCounter ++;
		NegotiationDetailsBean negDetails = new NegotiationDetailsBean();
		negDetails.setNegotiationID(negotiationCounter);
		negDetails.setRequestor(this.requestor);
		try {
			logging.debug("Starting negotiation with requestor: "+ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) negDetails.getRequestor()).getRequestorServiceId()));
			this.helper.getPrivacyPolicyNegotiationManager().negotiateServicePolicy(negDetails);
			logging.debug("Negotiation started");
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
