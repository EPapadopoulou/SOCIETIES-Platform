package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.awt.EventQueue;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNDialog;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNWindow;

public class ClientResponsePolicyGenerator {

	private NegotiationDetailsBean details;
	private HashMap<RequestItem, ResponseItem> evaluatePPNPreferences;
	private ResponsePolicy responsePolicy;
    private Logger logging = LoggerFactory.getLogger(this.getClass());

	public ResponsePolicy generatePolicy(PrivacyPolicyNegotiationManager policyMgr, NegotiationDetailsBean details,
			RequestPolicy policy) {
		// TODO Auto-generated method stub
		
		this.details = details;
		evaluatePPNPreferences = policyMgr.getPrivacyPreferenceManager().evaluatePPNPreferences(policy);	
		this.logging.debug("Found evaluated preferences: "+evaluatePPNPreferences.size());
		
		PPNWindow window = new PPNWindow(details, evaluatePPNPreferences, true, "");
		return window.getResponsePolicy();

	}

}


