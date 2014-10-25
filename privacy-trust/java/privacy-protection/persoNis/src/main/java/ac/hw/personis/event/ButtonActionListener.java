/**
 * 
 */
package ac.hw.personis.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.RequestorServiceBean;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.services.GoogleMapsService;

/**
 * @author PUMA
 *
 */
public class ButtonActionListener implements ActionListener{

	private PersonisHelper personisHelper;
	private int negotiationCounter = 0;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private RequestorServiceBean requestor;

	public ButtonActionListener(PersonisHelper helper, RequestorServiceBean requestor){
		this.logging.debug("Created Button listener for: "+ServiceModelUtils.serviceResourceIdentifierToString(requestor.getRequestorServiceId()));
		this.personisHelper = helper;
		this.requestor = requestor;

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equalsIgnoreCase("INSTALL")){
			logging.debug("Action performed method called");
			negotiationCounter ++;
			NegotiationDetailsBean negDetails = new NegotiationDetailsBean();
			negDetails.setNegotiationID(negotiationCounter);
			negDetails.setRequestor(this.requestor);
			try {
				logging.debug("Starting negotiation with requestor: "+ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) negDetails.getRequestor()).getRequestorServiceId()));
				this.personisHelper.getPrivacyPolicyNegotiationManager().negotiateServicePolicy(negDetails);
				logging.debug("Negotiation started");
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (event.getActionCommand().equalsIgnoreCase("LAUNCH")){
			if (RequestorUtils.equals(requestor, this.personisHelper.getGoogleRequestor())){
				GoogleMapsService service = new GoogleMapsService(this.personisHelper);
				service.setVisible(true);
			}else if (RequestorUtils.equals(requestor, personisHelper.getHwuRequestor())){
				JOptionPane.showMessageDialog(personisHelper.getApplication().getAppsPage(), "Not implemented yet");
			}
		}
	}
}
