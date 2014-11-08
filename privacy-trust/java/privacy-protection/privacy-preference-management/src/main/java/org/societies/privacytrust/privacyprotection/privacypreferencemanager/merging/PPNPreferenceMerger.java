package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
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

				TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CSS, this.privacyPreferenceManager.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
				TrustQuery trustQuery = new TrustQuery(trustorID);
				trustQuery.setTrusteeId(trusteeID);
				Double trustValue = trustBroker.retrieveTrustValue(trustQuery).get();


				List<ResponseItem> responseItems = agreement.getRequestedItems();

				for (ResponseItem item : responseItems){
					ResponseItem responseItem = ResponseItemUtils.copyOf(item);
					responseItem = stripCtxIdentifiers(responseItem);
					for (Condition condition : responseItem.getRequestItem().getConditions()){

						PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
						details.setRequestor(requestor);
						details.setResource(responseItem.getRequestItem().getResource());
						details.setCondition(condition.getConditionConstant());

						this.logging.debug("Creating PPN preference with details: "+PrivacyPreferenceUtils.toString(details));
						PPNPrivacyPreferenceTreeModel ppnPreference = this.privacyPreferenceManager.getPPNPreference(details);

						PPNPrivacyPreferenceTreeModel model = null;
						if (ppnPreference==null){
							this.logging.debug("Creating new PPN preference with requestor");
							model = createPPNPreference(requestor,responseItem.getRequestItem().getResource(), condition);
						}else{

							this.logging.debug("Merging PPN preference");
							model = mergePPNPreference(requestor, responseItem.getRequestItem().getResource(), condition, ppnPreference);
						}

						if (model==null){
							this.logging.debug("the model is null");
						}
						privacyPreferenceManager.storePPNPreference(details, model);


						//now merge and store requestor agnostic preferences

						PPNPreferenceDetailsBean copyOfDetails = PrivacyPreferenceUtils.copyOf(details);
						copyOfDetails.setRequestor(null);
						PPNPrivacyPreferenceTreeModel ppnPreference2 = this.privacyPreferenceManager.getPPNPreference(copyOfDetails);
						PPNPrivacyPreferenceTreeModel model2 = null;
						if (ppnPreference2==null){
							this.logging.debug("Creating new PPN preference without requestor");
							model2 = this.createPPNPreference(trustValue, responseItem.getRequestItem().getResource(), condition);
						}else{
							model2 = this.mergePPNPreference(trustValue, responseItem.getRequestItem().getResource(), condition, ppnPreference2);
						}

						privacyPreferenceManager.storePPNPreference(copyOfDetails, model2);
					}
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
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}

	private ResponseItem stripCtxIdentifiers(ResponseItem responseItem) {
		Resource r = new Resource();
		r.setDataType(responseItem.getRequestItem().getResource().getDataType());
		r.setScheme(responseItem.getRequestItem().getResource().getScheme());
		responseItem.getRequestItem().setResource(r);
		return responseItem;
	}

	private PPNPrivacyPreferenceTreeModel mergePPNPreference(RequestorBean requestor, Resource resource, Condition condition, PPNPrivacyPreferenceTreeModel ppnPreference) throws PrivacyException {
		PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(this.privacyPreferenceManager);

		return merger.mergePPNPreference(createPPNPreference(requestor, resource, condition), ppnPreference);
	}

	private PPNPrivacyPreferenceTreeModel mergePPNPreference(Double trustValue, Resource resource, Condition condition, PPNPrivacyPreferenceTreeModel ppnPreference) throws PrivacyException {
		PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger( this.privacyPreferenceManager);
		return merger.mergePPNPreference(createPPNPreference(trustValue, resource, condition), ppnPreference);
		
	}
	private PPNPrivacyPreferenceTreeModel createPPNPreference(RequestorBean requestor, Resource resource, Condition condition) throws PrivacyException {

		PPNPOutcome outcome = new PPNPOutcome(condition);
		PrivacyPreference outcomePreference = new PrivacyPreference(outcome);
		this.logging.debug("##$$@ Creating preference with outcome: "+outcomePreference.toString());
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
		details.setRequestor(RequestorUtils.copyOf(requestor));
		details.setResource(ResourceUtils.copyOf(resource));
		details.setCondition(condition.getConditionConstant());
		PPNPrivacyPreferenceTreeModel model  = new PPNPrivacyPreferenceTreeModel(details, outcomePreference);
		return  model;

	}

	private PPNPrivacyPreferenceTreeModel createPPNPreference(Double trustValue, Resource resource, Condition condition) throws PrivacyException {

		PPNPOutcome outcome = new PPNPOutcome(condition);
		PrivacyPreference outcomePreference = new PrivacyPreference(outcome);
		TrustPreferenceCondition trustCondition = new TrustPreferenceCondition(trustValue);
		PrivacyPreference  preference = new PrivacyPreference(trustCondition);
		preference.add(outcomePreference);
		this.logging.debug("##$$@ Creating preference with outcome: "+outcomePreference.toString());
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
		details.setResource(ResourceUtils.copyOf(resource));
		details.setCondition(condition.getConditionConstant());
		PPNPrivacyPreferenceTreeModel model  = new PPNPrivacyPreferenceTreeModel(details, preference);
		return  model;

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
