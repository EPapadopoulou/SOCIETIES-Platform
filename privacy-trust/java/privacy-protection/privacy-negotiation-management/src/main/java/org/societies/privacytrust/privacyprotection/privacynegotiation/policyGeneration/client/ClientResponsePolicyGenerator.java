package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNDialog;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNWindow;

public class ClientResponsePolicyGenerator {

    private Logger logging = LoggerFactory.getLogger(this.getClass());
	private boolean automatic;
	private PrivacyPolicyNegotiationManager policyMgr;
	HashMap<RequestItem, ResponseItem> preferencesAuto;
	RequestorBean requestor;
	private NegotiationDetailsBean details;
	private RequestPolicy policy;
	
	public ClientResponsePolicyGenerator(PrivacyPolicyNegotiationManager policyMgr, NegotiationDetailsBean details,	RequestPolicy policy){
		this.policyMgr = policyMgr;
		this.policy = policy;
		this.requestor = policy.getRequestor();
		this.details = details;
	}
	public ResponsePolicy generatePolicy() {

		int n = JOptionPane.NO_OPTION;
		if (isAutoPossible()){
			n = JOptionPane.showConfirmDialog(
				    null,
				    "You have negotiated with "+details.getRequestor().getRequestorId()+" before. Do you want me to try to negotiate on your behalf based on the previous negotiation?",
				    "Automatic Privacy Policy Negotiation available",
				    JOptionPane.YES_NO_OPTION);
			
		}
		if (n==JOptionPane.YES_OPTION){
			this.automatic = true;
			return getAutomaticResponsePolicy();
		}
		automatic = false;
		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences = policyMgr.getPrivacyPreferenceManager().evaluatePPNPreferences(policy);	
		this.logging.debug("Found evaluated preferences: "+evaluatePPNPreferences.size());

		PPNWindow window = new PPNWindow(details, evaluatePPNPreferences, true, "");
		return window.getResponsePolicy();

	}
	
	
	private ResponsePolicy getAutomaticResponsePolicy() {
		ResponsePolicy responsePolicy = new ResponsePolicy();
		responsePolicy.setRequestor(requestor);
		responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		Iterator<ResponseItem> iterator = preferencesAuto.values().iterator();
		while (iterator.hasNext()){
			ResponseItem item = iterator.next();
			item.setDecision(Decision.PERMIT);
			responseItems.add(item);
		}
		responsePolicy.setResponseItems(responseItems);
		return responsePolicy;
	}


	private boolean isAutoPossible(){
		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences;
		try {
			evaluatePPNPreferences = policyMgr.getPrivacyPreferenceManager().evaluatePPNPreferencesRequestorIdAndSpecific(this.policy);
			
			for (RequestItem requestItem : evaluatePPNPreferences.keySet()){
				ResponseItem responseItem = evaluatePPNPreferences.get(requestItem);
				if (responseItem == null){
					this.logging.debug("No ppn preference found for datatype: "+requestItem.getResource().getDataType());
					return false;
				}else{
					if (responseItem.getRequestItem().getConditions().size()<7){
						this.logging.debug("PPN preference not full, conditions.size() : "+responseItem.getRequestItem().getConditions().size());
						for (Condition condition :responseItem.getRequestItem().getConditions()){
							this.logging.debug("{}",ConditionUtils.toString(condition));
						}
						return false;
					}
				}
			}
			
		
			preferencesAuto = evaluatePPNPreferences;
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}


	public boolean isAutomatic() {
		return automatic;
	}


}


