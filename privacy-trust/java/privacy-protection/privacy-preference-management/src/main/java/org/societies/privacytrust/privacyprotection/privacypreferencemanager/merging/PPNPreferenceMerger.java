package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

public class PPNPreferenceMerger {

	private PrivacyPreferenceManager privacyPreferenceManager;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public PPNPreferenceMerger(PrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
		// TODO Auto-generated constructor stub
	}
	
	public void notifyNegotiationResult(InternalEvent event){
		this.logging.debug("Received event: name: "+event.geteventName()+" - type: "+event.geteventType());
		if (event.geteventInfo() instanceof PPNegotiationEvent){
			PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();
			Agreement agreement = ppnEvent.getAgreement();
			this.logging.debug("Retrieved agreement from PPNegotiationEvent with "+agreement.getRequestedItems().size()+" requestItems");
			
			
			ITrustBroker trustBroker = this.privacyPreferenceManager.getTrustBroker();
			RequestorBean requestor = agreement.getRequestor();
			
			
			try {
				TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());
				
				TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CIS, this.privacyPreferenceManager.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
				TrustQuery trustQuery = new TrustQuery(trustorID);
				trustQuery.setTrusteeId(trusteeID);
				Double trustValue = trustBroker.retrieveTrustValue(trustQuery).get();
				
				
				List<ResponseItem> responseItems = agreement.getRequestedItems();
				
				for (ResponseItem item : responseItems){
					ResponseItem responseItem = ResponseItemUtils.copyOf(item);
					PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
					details.setRequestor(requestor);
					details.setResource(responseItem.getRequestItem().getResource());
					
					PPNPrivacyPreferenceTreeModel ppnPreference = this.privacyPreferenceManager.getPPNPreference(details);
					
					PPNPrivacyPreferenceTreeModel model = null;
					if (ppnPreference==null){
						this.logging.debug("Creating new PPN preference");
						 model = createPPNPreference(requestor,responseItem);
					}else{
						
						this.logging.debug("Merging PPN preference");
						model = mergePPNPreference(requestor, responseItem, ppnPreference);
					}
					
					if (model==null){
						this.logging.debug("the model is null");
					}
					privacyPreferenceManager.storePPNPreference(details, model);
				}
				
			} catch (MalformedTrustedEntityIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TrustException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}

	private PPNPrivacyPreferenceTreeModel mergePPNPreference(RequestorBean requestor,
			ResponseItem responseItem,
			PPNPrivacyPreferenceTreeModel ppnPreference) {
		PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(this.privacyPreferenceManager.getCtxBroker(), this.privacyPreferenceManager);
		
		return merger.mergePPNPreference(createPPNPreference(requestor, responseItem), ppnPreference);
	}

	private PPNPrivacyPreferenceTreeModel createPPNPreference(RequestorBean requestor,
			ResponseItem responseItem) {
		RequestItem requestItem = RequestItemUtils.copyOf(responseItem.getRequestItem());		
		PPNPOutcome outcome = new PPNPOutcome(responseItem.getDecision(), requestItem.getActions());		
		PrivacyPreference outcomePreference = new PrivacyPreference(outcome);
		this.logging.debug("##$$@ Creating preference with outcome: "+this.toStringOutcome(outcome));
		PrivacyPreference preference = this.createConditionPreferences(requestItem.getConditions(), outcomePreference);		
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
		details.setRequestor(RequestorUtils.copyOf(requestor));
		Resource resource = ResourceUtils.copyOf(requestItem.getResource());
		
		details.setResource(resource);
		PPNPrivacyPreferenceTreeModel model  = new PPNPrivacyPreferenceTreeModel(details, preference);
		return model;
		
	}
	
	private String toStringOutcome(PPNPOutcome outcome){
		StringBuilder sb = new StringBuilder();
		sb.append("Outcome: ");
			sb.append(((PPNPOutcome) outcome).getDecision().toString());
			sb.append("(");
			for (Action action : ((PPNPOutcome) outcome).getActions()){
				sb.append(action.getActionConstant()+", ");
				
			}
			int c = sb.lastIndexOf(", ");
			if (c>-1){
				sb.delete(c, c+1);
			}
			sb.append(")");
			
		
		return sb.toString();
	}
	
	
	private PrivacyPreference createConditionPreferences(
			List<Condition> conditions, PrivacyPreference privacyPreference) {

		for (Condition condition : conditions){
			this.logging.debug("Adding condition to ppnPreference: "+ConditionUtils.toString(condition));
			PrivacyPreference preference = new PrivacyPreference(new PrivacyCondition(condition));
			preference.add(privacyPreference);
			privacyPreference = (PrivacyPreference) preference.getRoot();
		}
		return privacyPreference;
	}
}
