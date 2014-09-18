package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

public class NegotiationListener extends EventListener{

	private AccessControlPreferenceCreator accCtrlPrefCreator;
	private PPNPreferenceMerger ppnMerger;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private IDSPreferenceCreator idsPrefCreator;
	
	private AttrSelPreferenceCreator attrSelPreferenceCreator;
	
	public NegotiationListener(PrivacyPreferenceManager ppMgr) {
		this.accCtrlPrefCreator = ppMgr.getAccCtrlPreferenceCreator();
		this.ppnMerger =  ppMgr.getPpnPreferenceMerger();
		this.idsPrefCreator = ppMgr.getIdsPreferenceCreator();
		this.attrSelPreferenceCreator = ppMgr.getAttrSelPreferenceCreator();
		try{
			ppMgr.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT,EventTypes.IDENTITY_CREATED, EventTypes.IDENTITY_SELECTED}, null);

		}catch(Exception e){
			System.out.println("could not subscribe to event: "+EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT);
		}
	}
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT)){
			
			this.accCtrlPrefCreator.notifyNegotiationResult(event);	
			this.ppnMerger.notifyNegotiationResult(event);
			this.idsPrefCreator.notifyIdentitySelected(event);
			
			this.logging.debug("finished processing negotiation event");
		}else if (event.geteventType().equals(EventTypes.IDENTITY_CREATED)){
			this.logging.debug("Received IDENTITY CREATED event");
			this.attrSelPreferenceCreator.notifyIdentityCreated(event);
		}

	}

}
