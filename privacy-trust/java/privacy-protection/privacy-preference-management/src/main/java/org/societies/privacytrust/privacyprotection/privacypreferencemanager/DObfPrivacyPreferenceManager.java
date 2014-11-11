/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent.NotificationType;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationDobfEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseDObfEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;

/**
 * @author Eliza
 *
 */
public class DObfPrivacyPreferenceManager extends EventListener{


	private static final String ERROR = "error";
	private final IIdentity userIdentity;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private PrivacyPreferenceManager privPrefMgr;
	private Hashtable<String, UserResponseDObfEvent> userResponses; 

	public DObfPrivacyPreferenceManager(PrivacyPreferenceManager privPrefMgr) {
		this.userResponses = new Hashtable<String, UserResponseDObfEvent>();
		this.privPrefMgr = privPrefMgr;
		this.userIdentity = privPrefMgr.getIdm().getThisNetworkNode();
		String[] eventTypes = new String[]{EventTypes.PERSONIS_NOTIFICATION_DOBF_RESPONSE};
		this.privPrefMgr.getEventMgr().subscribeInternalEvent(this, eventTypes, null);
	}
	/**
	 * new methods;
	 */
	public boolean deleteDObfPreference(DObfPreferenceDetailsBean details) {
		return privPrefMgr.getPrefCache().removeDObfPreference(details);

	}


	public Integer evaluateDObfPreference(DObfPreferenceDetailsBean details) {
		DObfPreferenceTreeModel model = privPrefMgr.getPrefCache().getDObfPreference(details);

		if (model!=null){
			logging.debug("Found dobf preference: {}",details);
			IPrivacyOutcome outcome = evaluatePreference(model.getRootPreference(), details.getRequestor());
			if (outcome instanceof DObfOutcome){
				return this.getUserInput((DObfOutcome) outcome, details);				
			}else{
				logging.debug("ERROR, IPrivacyOutcome not instanceof DobfOutcome");
				return getUserInput(null, details);
			}
		}else{
			logging.debug("Did not find dobf preference: {}", details);
			return getUserInput(null, details);
		}
	}



	private Integer getUserInput(DObfOutcome outcome, DObfPreferenceDetailsBean details){
		String uuid = UUID.randomUUID().toString();
		if (outcome==null){
			StringBuilder sb = new StringBuilder();
			sb.append("How do you want to obfuscate your ");
			sb.append(details.getResource().getDataType());
			NotificationDobfEvent notifEvent = new NotificationDobfEvent(uuid, sb.toString(), NotificationType.SIMPLE, 0, details.getResource().getDataType());
			UserResponseDObfEvent userInput = getUserInput(notifEvent);
			if (userInput.getUuid().equalsIgnoreCase(ERROR)){
				return -1;
			}
			privPrefMgr.getDobfPreferenceCreator().createPreference(details.getRequestor(), details.getResource(), userInput.getObfuscationLevel());
			return userInput.getObfuscationLevel();
		}else
		if (outcome.getConfidenceLevel()>=60){
			
			StringBuilder sb = new StringBuilder();
			sb.append("Your preferences suggest that I apply obfuscation level ");
			sb.append(((DObfOutcome) outcome).getObfuscationLevel());
			sb.append(" to data type: ");
			sb.append(details.getResource().getDataType());
			NotificationDobfEvent notifEvent = new NotificationDobfEvent(uuid, sb.toString(), NotificationType.TIMED, outcome.getObfuscationLevel(), details.getResource().getDataType());
			UserResponseDObfEvent userInput = this.getUserInput(notifEvent);
			if (userInput.getUuid().equalsIgnoreCase(ERROR)){
				return -1;
			}
			if (userInput.isUserClicked()){
				privPrefMgr.getDobfPreferenceCreator().createPreference(details.getRequestor(), details.getResource(), userInput.getObfuscationLevel());
			}
			return userInput.getObfuscationLevel();
		}else{
			StringBuilder sb = new StringBuilder();
			sb.append("Your preferences suggest that I apply obfuscation level ");
			sb.append(((DObfOutcome) outcome).getObfuscationLevel());
			sb.append(" to data type: ");
			sb.append(details.getResource().getDataType());
			NotificationDobfEvent notifEvent = new NotificationDobfEvent(uuid, sb.toString(), NotificationType.SIMPLE, outcome.getObfuscationLevel(), details.getResource().getDataType());
			UserResponseDObfEvent userInput = this.getUserInput(notifEvent);
			if (userInput.getUuid().equalsIgnoreCase(ERROR)){
				return -1;
			}
			privPrefMgr.getDobfPreferenceCreator().createPreference(details.getRequestor(), details.getResource(), userInput.getObfuscationLevel());
			return userInput.getObfuscationLevel();
		}
		
	}
	
	
	private UserResponseDObfEvent getUserInput(NotificationDobfEvent notifEvent) {


		InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_DOBF_REQUEST, "", this.getClass().getName(), notifEvent);
		try {
			this.privPrefMgr.getEventMgr().publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			logging.error("Error publishing internal event: {}", event);
			return new UserResponseDObfEvent(ERROR, 0, false);
		}
		while (!this.userResponses.containsKey(notifEvent.getUuid())){
			synchronized (this.userResponses) {
				try {
					this.userResponses.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return this.userResponses.get(notifEvent.getUuid());

	}
	private IPrivacyOutcome evaluatePreference(PrivacyPreference privPref, RequestorBean requestor){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.privPrefMgr, requestor, this.userIdentity);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluatePreference(privPref);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}
	public DObfPreferenceTreeModel getDObfPreference(
			DObfPreferenceDetailsBean details) {
		return privPrefMgr.getPrefCache().getDObfPreference(details);
	}

	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails() {
		return privPrefMgr.getPrefCache().getDObfPreferenceDetails();
	}

	public boolean storeDObfPreference(DObfPreferenceDetailsBean details,
			DObfPreferenceTreeModel model) throws PrivacyException {
		if(model.getDetails().equals(details)){
			try {
				CtxAttributeIdentifier ctxAttributeIdentifier = new CtxAttributeIdentifier(details.getResource().getDataIdUri());
				ctxAttributeIdentifier = CtxIDChanger.changeIDOwner(userIdentity.getBareJid(), ctxAttributeIdentifier);

				DObfPreferenceDetailsBean detailsCopy = PrivacyPreferenceUtils.copyOf(details);
				Resource resource = ResourceUtils.create(ctxAttributeIdentifier);
				detailsCopy.setResource(resource);
				DObfPreferenceTreeModel modelCopy = new DObfPreferenceTreeModel(detailsCopy, model.getRootPreference());
				return privPrefMgr.getPrefCache().addDObfPreference(detailsCopy, modelCopy);
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		throw new PrivacyException("DObfPreferenceDetailsBean parameter did not match DObfPrivacyPreferenceTreeModel.getDetails()");		
	}
	public boolean deleteDObfPreferences() {
		return privPrefMgr.getPrefCache().removeDObfPreferences();
	}
	public HashMap<CtxModelObject, Integer> getObfuscationLevel(
			RequestorBean requestor, List<CtxModelObject> ctxDataList) {
		logging.debug("getObfuscationLevel {}", ctxDataList);
		HashMap<CtxModelObject, Integer> map = new HashMap<CtxModelObject, Integer>();
		for (CtxModelObject ctxModelObject : ctxDataList){
			DObfPreferenceDetailsBean details = new DObfPreferenceDetailsBean();
			details.setRequestor(requestor);
			CtxIdentifier ctxId = CtxIDChanger.changeIDOwner(this.userIdentity.getBareJid(), (CtxAttributeIdentifier) ctxModelObject.getId());
			details.setResource(ResourceUtils.create(ctxId));
			Integer obfLevel = evaluateDObfPreference(details);
			if (obfLevel>=0){
				map.put(ctxModelObject, new Integer(obfLevel));
			}else{
				//TODO show popup
				map.put(ctxModelObject, new Integer(0));
			}
		}

		logging.debug("getObfuscationLevel result {}",map);
		return map;
	}


	@Override
	public void handleInternalEvent(InternalEvent event) {
		logging.debug("Received event: {}", event.geteventType());
		if (event.geteventInfo() instanceof UserResponseDObfEvent){
			UserResponseDObfEvent uREvent = (UserResponseDObfEvent) event.geteventInfo();
			this.userResponses.put(uREvent.getUuid(), uREvent);
			synchronized (this.userResponses) {
				this.userResponses.notifyAll();	
			}

		}else{
			logging.error("Received unknown eventInfo object {}", event.geteventInfo());
		}


	}
	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}

}
