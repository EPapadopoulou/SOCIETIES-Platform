package ac.hw.personis.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.identity.RequestorBean;

import ac.hw.personis.Application;

public class NegotiationListener extends EventListener{

	
    private Logger logging = LoggerFactory.getLogger(this.getClass());

	private IEventMgr eventMgr;

	private Application application;
	
	
	public NegotiationListener(Application application){
		this.application = application;
		
		
	}
	
	
	public void subscribe(IEventMgr eventMgr){
		this.eventMgr = eventMgr;

		String[] eventTypes = new String[] {
				EventTypes.FAILED_NEGOTIATION_EVENT,
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT};
		
		this.eventMgr.subscribeInternalEvent(this, eventTypes, null);
	}
	
	
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		String type = event.geteventType();
		
		logging.info("Internal event received: {}", type);
		logging.debug("*** event name : " + event.geteventName());
		logging.debug("*** event source : " + event.geteventSource());
		
		if (type.equals(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT)) {
			
			PPNegotiationEvent payload = (PPNegotiationEvent) event.geteventInfo();
			RequestorBean requestor = payload.getDetails().getRequestor();
			application.notifySuccessfulNegotiation(requestor, payload.getAgreement());
			
		}else if (type.equals(EventTypes.FAILED_NEGOTIATION_EVENT)){
			FailedNegotiationEvent payload = (FailedNegotiationEvent) event.geteventInfo();
			RequestorBean requestor = payload.getDetails().getRequestor();
			application.notifyFailedNegotiation(requestor);
		}
		
	}

}
