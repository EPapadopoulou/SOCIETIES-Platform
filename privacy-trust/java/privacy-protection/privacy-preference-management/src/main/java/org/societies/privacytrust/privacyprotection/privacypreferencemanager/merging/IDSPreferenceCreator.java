/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
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



	public void notifyIdentitySelected(final InternalEvent event){ 
		new Thread() {

			public void run() {
				try{
					if (event.geteventInfo() instanceof PPNegotiationEvent){
						logging.debug("event.geteventInfo is of type Agreement");
						PPNegotiationEvent negEvent = (PPNegotiationEvent) event.geteventInfo();
						try {
							logging.debug("creating identity selection preference");
							createIdentitySelectionPreferences(negEvent.getAgreement());
							logging.debug("created identity selection preference");
						} catch (InvalidFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						logging.debug("event.geteventInfo NOT of type Agreement but of: "+event.geteventInfo().getClass().getName());
					}
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}}.start();
	}
	
	
	private void createIdentitySelectionPreferences(Agreement agreement) throws InvalidFormatException, InterruptedException, ExecutionException, TrustException {

		IIdentity selectedIdentity = this.privPrefMgr.getIdm().fromJid(agreement.getUserIdentity());

		//TODO: create generic and specific IDS pref
		
		PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
		merger.addIDSDecision(selectedIdentity, agreement.getRequestor());
		
		RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(agreement.getRequestor().getRequestorId());
		
		//requestor id only:
		merger.addIDSDecision(selectedIdentity, requestor);
		
		
		//generic:
		TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());

		TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CSS, privPrefMgr.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
		TrustQuery trustQuery = new TrustQuery(trustorID);
		trustQuery.setTrusteeId(trusteeID);
		Double trustValue = privPrefMgr.getTrustBroker().retrieveTrustValue(trustQuery).get();
		merger.addIDSDecision(selectedIdentity, trustValue);
		
		//PrivacyPreference  idsPreference = this.createIdentitySelectionPreference(agreement.getRequestor(), preference);
		
		
		//generic:
		//storeGeneric(selectedIdentity, idsPreference);
	}

	private void storeGeneric(IIdentity selectedIdentity, PrivacyPreference  idsPreference){
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedIdentity.getBareJid());
		IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, idsPreference);
		this.privPrefMgr.storeIDSPreference(details, model);
		this.logging.debug("Stored IDS preference (generic)");

	}

	private void storeSpecific(IIdentity selectedIdentity, RequestorBean requestor, PrivacyPreference  idsPreference){
		
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedIdentity.getBareJid());
		details.setRequestor(requestor);
		IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, idsPreference);
		this.privPrefMgr.storeIDSPreference(details, model);
		this.logging.debug("Stored IDS preference (specific)");

	}
	private PrivacyPreference  createIdentitySelectionPreference(RequestorBean requestor, PrivacyPreference outcomePreference){

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


