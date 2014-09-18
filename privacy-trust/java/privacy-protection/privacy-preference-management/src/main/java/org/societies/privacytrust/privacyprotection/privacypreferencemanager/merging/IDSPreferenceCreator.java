/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

/**
 * @author PUMA
 *
 */
public class IDSPreferenceCreator {

	private PrivacyPreferenceManager privPrefMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public IDSPreferenceCreator(PrivacyPreferenceManager privPrefMgr){
		this.privPrefMgr = privPrefMgr;
	}



	public void notifyIdentitySelected(InternalEvent event){ 
		
		if (event.geteventInfo() instanceof PPNegotiationEvent){
			this.logging.debug("event.geteventInfo is of type Agreement");
			PPNegotiationEvent negEvent = (PPNegotiationEvent) event.geteventInfo();

			

			try {
				this.logging.debug("creating identity selection preference");
				this.createIdentitySelectionPreferences(negEvent.getAgreement());
				this.logging.debug("created identity selection preference");
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			this.logging.debug("event.geteventInfo NOT of type Agreement but of: "+event.geteventInfo().getClass().getName());
		}

	}
	private void createIdentitySelectionPreferences(Agreement agreement) throws InvalidFormatException {

		IIdentity selectedIdentity = this.privPrefMgr.getIdm().fromJid(agreement.getUserIdentity());
		IdentitySelectionPreferenceOutcome outcome = new IdentitySelectionPreferenceOutcome(selectedIdentity);
		PrivacyPreference preference = new PrivacyPreference(outcome);
		//TODO: create generic and specific IDS pref
		IPrivacyPreference idsPreference = this.createIdentitySelectionPreference(agreement.getRequestor(), preference);
		this.logging.debug("Constructed IDS creation preference");
		//specific:
		storeSpecific(selectedIdentity, agreement.getRequestor(), preference);
		//generic:
		storeGeneric(selectedIdentity, idsPreference);
	}
	
	private void storeGeneric(IIdentity selectedIdentity, IPrivacyPreference idsPreference){
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedIdentity.getBareJid());
		IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, idsPreference);
		this.privPrefMgr.storeIDSPreference(details, model);
		this.logging.debug("Stored IDS preference (generic)");
		
	}

	private void storeSpecific(IIdentity selectedIdentity, RequestorBean requestor, IPrivacyPreference idsPreference){
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedIdentity.getBareJid());
		details.setRequestor(requestor);
		IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, idsPreference);
		this.privPrefMgr.storeIDSPreference(details, model);
		this.logging.debug("Stored IDS preference (specific)");
		
	}
	private IPrivacyPreference createIdentitySelectionPreference(RequestorBean requestor, PrivacyPreference outcomePreference){

		TrustPreferenceCondition trustCondition = getTrustCondition(requestor);
		PrivacyPreference privacyPreference = new PrivacyPreference(trustCondition);
		
		privacyPreference.add(outcomePreference);
		
		return privacyPreference;
		
		
		
	}

	private TrustPreferenceCondition getTrustCondition(RequestorBean requestor){
		try{
			ITrustBroker trustBroker = privPrefMgr.getTrustBroker();

			TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());

			TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CIS, this.privPrefMgr.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
			TrustQuery trustQuery = new TrustQuery(trustorID);
			trustQuery.setTrusteeId(trusteeID);
			Double trustValue = trustBroker.retrieveTrustValue(trustQuery).get();

			TrustPreferenceCondition condition = new TrustPreferenceCondition(trustValue);
			return condition;
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
		return null;
	}
}


