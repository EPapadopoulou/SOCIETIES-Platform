package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

public class NegotiationListener extends EventListener{

	private AccessControlPreferenceCreator accCtrlPrefCreator;
	private PPNPreferenceMerger ppnMerger;

	public NegotiationListener(PrivacyPreferenceManager ppMgr) {
		this.accCtrlPrefCreator = ppMgr.getAccCtrlPreferenceCreator();
		this.ppnMerger =  ppMgr.getPpnPreferenceMerger();
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
		}else if (event.geteventType().equals(EventTypes.IDENTITY_CREATED)){
			
		}

	}

}
