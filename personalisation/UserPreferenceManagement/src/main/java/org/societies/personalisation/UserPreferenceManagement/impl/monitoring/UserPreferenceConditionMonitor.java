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
package org.societies.personalisation.UserPreferenceManagement.impl.monitoring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.personalisation.model.FeedbackTypes;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.cis.CisEventListener;
import org.societies.personalisation.UserPreferenceManagement.impl.cis.CommunitiesHandler;
import org.societies.personalisation.UserPreferenceManagement.impl.merging.MergingManager;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.ActionInformation;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.IQualityofPreference;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.societies.personalisation.preference.api.model.QualityofPreference;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class UserPreferenceConditionMonitor extends EventListener implements IUserPreferenceConditionMonitor{

	private MonitoringTable mt;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private List<CtxAttributeIdentifier> registered; 
	private ICtxBroker ctxBroker;
	private UserPreferenceManagement prefMgr;
	private IInternalPersonalisationManager persoMgr;
	private MergingManager merging;
	private IC45Learning userPrefLearning;
	private IEventMgr eventMgr;

	private ICommManager commManager;
	private IUserFeedback userFeedbackMgr;
	private ICommunityPreferenceManager communityPreferenceMgr;
	private CisEventListener cisEventListener;
	private IServiceDiscovery serviceDiscovery;
	private ICisManager cisManager;
	private CommunitiesHandler communitiesHandler;
	private Hashtable<String, IPreference> evaluationResults;
	private String doFix;

	public UserPreferenceConditionMonitor(){
		this.evaluationResults = new Hashtable<String, IPreference>();
	}


	public ICtxBroker getCtxBroker() {
		if(this.logging.isDebugEnabled()){
			logging.debug(this.getClass().getName()+": Return ctxBroker");
		}

		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		if(this.logging.isDebugEnabled()){
			logging.debug(this.getClass().getName()+": Got ctxBroker");
		}

		this.ctxBroker = ctxBroker;
	}


	public IInternalPersonalisationManager getPersoMgr() {
		if(this.logging.isDebugEnabled()){
			logging.debug(this.getClass().getName()+": Return persoMgr");
		}
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		if(this.logging.isDebugEnabled()){
			logging.debug(this.getClass().getName()+": Got persoMgr");
		}
		this.persoMgr = persoMgr;
	}



	/**
	 * @return the userPrefLearning
	 */
	public IC45Learning getUserPrefLearning() {
		return userPrefLearning;
	}


	/**
	 * @param userPrefLearning the userPrefLearning to set
	 */
	public void setUserPrefLearning(IC45Learning userPrefLearning) {
		this.userPrefLearning = userPrefLearning;
	}


	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public void initialisePreferenceManagement(){
		if (doFix.contains("true")){
			this.prefMgr = new UserPreferenceManagement(this.getCtxBroker(), this, true);
		}else{
			this.prefMgr = new UserPreferenceManagement(this.getCtxBroker(), this, false);
		}
		mt = new MonitoringTable();
		registered = new ArrayList<CtxAttributeIdentifier>();

		merging = new MergingManager(getUserPrefLearning(), prefMgr, this);
		this.subscribeForStaticUIMEvents();
		if(this.logging.isDebugEnabled()){
			logging.debug(this.getClass().toString()+": INITIALISED");
		}

		this.cisEventListener  = new CisEventListener(this);
		communitiesHandler = new CommunitiesHandler(this);
		communitiesHandler.scheduleTasks();

	}


	private void subscribeForStaticUIMEvents() {
		String eventFilter = "(&" +
				"(" + CSSEventConstants.EVENT_NAME + "=staticaction)" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/monitoring)" +
				")";
		this.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.UIM_STATIC_ACTION}, eventFilter);
		if(this.logging.isDebugEnabled()){
			this.logging.debug("Subscribed to " + EventTypes.UIM_STATIC_ACTION + " events");
		}

	}


	/**
	 * 
	 * @param ownerId
	 * @param attribute
	 * @param callback
	 * @return 
	 */
	@Override
	public Future<List<IPreferenceOutcome>> getOutcome(IIdentity ownerId, CtxAttribute attribute, String uuid){
		this.prefMgr.updateContext(attribute);
		/*
		 * in this method, we need to check what preferences are affected, request re-evaluation of them, compare last ioutcome with new and send it to 
		 * the proactivity decision maker component
		 */
		if(this.logging.isDebugEnabled()){
			logging.debug("Processing context event : "+attribute.getType());
		}
		List<PreferenceDetails> affectedPreferences = this.mt.getAffectedPreferences(attribute.getId());
		if (affectedPreferences.size()==0){
			//JOptionPane.showMessageDialog(null, "no affected preferences found for ctxID: "+ctxAttr.getCtxIdentifier().toUriString()+",\n ignoring event");
			if(this.logging.isDebugEnabled()){
				logging.debug("no affected preferences found for ctxID: "+attribute.getId().toString()+", ignoring event");
			}
			if(this.logging.isDebugEnabled()){
				this.logging.debug("no affected preferences found for ctxID: "+attribute.getId().toString()+", ignoring event");
			}
			return new AsyncResult<List<IPreferenceOutcome>>(new ArrayList<IPreferenceOutcome>());
		}else{
			if(this.logging.isDebugEnabled()){
				logging.debug("found affected preferences");
			}

			if (null == this.prefMgr){

				if(this.logging.isDebugEnabled()){
					this.logging.debug(UserPreferenceManagement.class.getName()+" not found");
				}
				return new AsyncResult<List<IPreferenceOutcome>>(new ArrayList<IPreferenceOutcome>());
			}else{
				if(this.logging.isDebugEnabled()){
					this.logging.debug(UserPreferenceManagement.class.getName()+" Found");
				}
			}

			List<IPreferenceOutcome> outcomes = prefMgr.reEvaluatePreferences(ownerId,attribute, affectedPreferences, uuid);
			if(this.logging.isDebugEnabled()){
				logging.debug("requested re-evaluation of preferences");
			}

			if(this.logging.isDebugEnabled()){
				logging.debug("Returning outcome");
			}
			return new AsyncResult<List<IPreferenceOutcome>>(outcomes);			
		}
	}


	/**
	 * 
	 * @param ownerId
	 * @param action
	 * @param callback
	 * @return 
	 */
	@Override
	public Future<List<IPreferenceOutcome>> getOutcome(IIdentity ownerId, IAction action, String uuid){
		/*
		 * an action describes a personalisable parameter that the user (manually) or the User Agent (proactively) changed.
		 * An action does not describe a change in the state of the service. i.e. starting or stopping a service. Therefore,
		 * PCM returns the value of the personalisable parameter that was last applied or is currently applicable. 
		 * 
		 * The PCM is notified of changes in the personalisable parameters of a service using context. the User Action Monitor 
		 * populates the context database with this information as soon as it receives an action from a service. 
		 */


		if (!this.mt.isServiceRunning(action.getServiceType(), action.getServiceID())){
			this.processServiceStarted(ownerId, action.getServiceType(), action.getServiceID());
		}

		if(this.logging.isDebugEnabled()){
			this.logging.debug("request for outcome with input: "+ownerId.getJid()+"\n"+action.toString());
		}
		this.merging.processActionReceived(ownerId, action);
		List<IPreferenceOutcome> outcomes = new ArrayList<IPreferenceOutcome>();
		IPreferenceOutcome outcome = this.prefMgr.getPreference(ownerId, action.getServiceType(), action.getServiceID(), action.getparameterName());
		if (outcome!=null){
			outcomes.add(outcome);
		}
		return new AsyncResult<List<IPreferenceOutcome>>(outcomes);


	}


	public void processServiceStarted(IIdentity userId, String serviceType, ServiceResourceIdentifier serviceID){


		//JOptionPaneshowMessageDialog(null, "Processing service started event: "+serviceID.toUriString());
		if(this.logging.isDebugEnabled()){
			this.logging.debug("Adding "+ServiceModelUtils.serviceResourceIdentifierToString(serviceID)+" preference details to tables");
		}
		List<IPreferenceConditionIOutcomeName> conditionIOutcomeName = this.prefMgr.getPreferenceConditions(userId, serviceType, serviceID);
		//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+" received "+conditionIOutcomeName.size()+" preferenceConditions from PrefMgr");
		for (IPreferenceConditionIOutcomeName info : conditionIOutcomeName){
			this.mt.addInfo(info.getICtxIdentifier(), serviceID, serviceType, info.getPreferenceName());
			if(this.logging.isDebugEnabled()){
				this.logging.debug("Added: "+info.getICtxIdentifier().toString()+" to"+serviceID.toString()+" affecting preference: "+info.getPreferenceName());
			}
			//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+"Added: "+info.getCtxIdentifier().toUriString()+" to"+serviceID.toUriString()+" affecting preference: "+info.getPreferenceName());
			if (this.registered.contains(info.getICtxIdentifier())){
				if(this.logging.isDebugEnabled()){
					this.logging.debug("Already subscribed for: "+info.getICtxIdentifier().toUriString());
				}
			}else{
				this.persoMgr.registerForContextUpdate(userId, PersonalisationTypes.UserPreference, info.getICtxIdentifier());
				//this.registerForContextEvent((CtxAttributeIdentifier) info.getICtxIdentifier());
				this.registered.add((CtxAttributeIdentifier) info.getICtxIdentifier());
				if(this.logging.isDebugEnabled()){
					this.logging.debug(userId.getJid()+" Registered for :"+info.getICtxIdentifier().toUriString());
				}
			}
		}
	}


	public void processServiceStopped(IIdentity userId, String serviceType, ServiceResourceIdentifier serviceID){
		if (this.mt.isServiceRunning(serviceType, serviceID)){
			mt.removeServiceInfo(serviceType, serviceID);
		}else{
			if(this.logging.isDebugEnabled()){
				logging.debug("The details of this service were not properly loaded. Nothing to do!");
			}
		}
	}

	public void processPreferenceChanged(IIdentity userID, ServiceResourceIdentifier serviceId, String serviceType, String preferenceName){
		List<CtxIdentifier> ctxIDs = this.prefMgr.getPreferenceConditions(userID, serviceType, serviceId, preferenceName);
		for (CtxIdentifier id : ctxIDs){
			this.mt.addInfo(id, serviceId, serviceType, preferenceName);
			if (this.registered.contains(id)){
				if(this.logging.isDebugEnabled()){
					this.logging.debug("Already subscribed for: "+id.toUriString());
				}
			}else{
				//this.registerForContextEvent((CtxAttributeIdentifier) id);
				this.registered.add((CtxAttributeIdentifier) id);
				this.persoMgr.registerForContextUpdate(userID, PersonalisationTypes.UserPreference, (CtxAttributeIdentifier) id);
			}
		}
		/*IOutcome out = this.prefMgr.getPreference(this.getMyUSERDPI(), serviceType, serviceId, prefName);
		if (out==null){
			this.logging.debug("Preference Manager returned no new outcomes for serviceType:"+serviceType+" and serviceID: "+serviceId);
		}else{
			this.sendToDM(serviceType, serviceId, prefName, out);

		}*/


	}



	@Override
	public Future<IOutcome> getOutcome(IIdentity ownerID, ServiceResourceIdentifier serviceID, String preferenceName) {
		return new AsyncResult<IOutcome> (this.prefMgr.getPreference(ownerID, "", serviceID, preferenceName));
	}


	/**
	 * @return the prefMgr
	 */
	@Override
	public UserPreferenceManagement getPreferenceManager() {
		return prefMgr;
	}


	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void handleInternalEvent(InternalEvent internalEvent) {
		UIMEvent uimEvent = (UIMEvent) internalEvent.geteventInfo();
		Action action = (Action) uimEvent.getAction();
		PreferenceDetails details = new PreferenceDetails();

		PreferenceOutcome outcome = new PreferenceOutcome(action.getServiceID(), action.getServiceType(), action.getparameterName(), action.getvalue(), action.isImplementable(), action.isProactive(), action.isContextDependent());

		details.setServiceID(action.getServiceID());


		details.setServiceType(action.getServiceType());
		outcome.setServiceType(action.getServiceType());


		details.setPreferenceName(action.getparameterName());
		outcome.setparameterName(action.getparameterName());
		outcome.setvalue(action.getvalue());

		PreferenceTreeNode preference = new PreferenceTreeNode(outcome);


		this.prefMgr.storePreference(uimEvent.getUserId(), details, preference);

	}


	public ICommManager getCommManager() {
		return commManager;
	}


	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}


	public IUserFeedback getUserFeedbackMgr() {
		return userFeedbackMgr;
	}


	public void setUserFeedbackMgr(IUserFeedback userFeedbackMgr) {
		this.userFeedbackMgr = userFeedbackMgr;
	}


	public ICommunityPreferenceManager getCommunityPreferenceMgr() {
		return communityPreferenceMgr;
	}


	public void setCommunityPreferenceMgr(ICommunityPreferenceManager communityPreferenceMgr) {
		this.communityPreferenceMgr = communityPreferenceMgr;
	}


	public CisEventListener getCisEventListener() {
		return cisEventListener;
	}


	public void setCisEventListener(CisEventListener cisEventListener) {
		this.cisEventListener = cisEventListener;
	}


	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}


	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}


	public ICisManager getCisManager() {
		return cisManager;
	}


	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}


	public CommunitiesHandler getCommunitiesHandler() {
		// TODO Auto-generated method stub
		return this.communitiesHandler;
	}


	@Override
	public void pushPreferencesToCommunities(Calendar calendar) {

		this.communitiesHandler.scheduleUploaderTask(calendar);

	}


	@Override
	public void downloadPreferencesFromCommunities(Calendar calendar) {
		this.communitiesHandler.scheduleDownloaderTask(calendar);

	}


	@Override
	public void sendFeedback(FeedbackEvent fEvent,	ActionInformation actionInformation) {
		String uuid = actionInformation.getUuid();
		if (this.evaluationResults.containsKey(uuid)){
			String parameterName = fEvent.getAction().getparameterName();
			ServiceResourceIdentifier serviceID = fEvent.getAction().getServiceID();
			String serviceType = fEvent.getAction().getServiceType();

			PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, parameterName);
			IPreferenceTreeModel model = this.prefMgr.getModel(this.commManager.getIdManager().getThisNetworkNode(), details);
			if (model==null){
				if (logging.isDebugEnabled()){
					this.logging.debug("Model doesn't exist for feedback information. This action was not affected by preferences");

				}
				return;
			}

			IPreference matchedPref = this.findMatchingPreference(model.getRootPreference(), this.evaluationResults.get(uuid));
			if (matchedPref==null){
				if (this.logging.isDebugEnabled()){
					this.logging.debug("Could not find the feedback preference inside the model. Could not update confidence level.");
				}
				return;
			}
			IAction action = fEvent.getAction();
			IPreferenceOutcome outcome = matchedPref.getOutcome();

			if (fEvent.getErrorType().equals(FeedbackTypes.USER_ABORTED)){
				if(action.getvalue().equalsIgnoreCase(outcome.getvalue())){

					IQualityofPreference qualityofPreference = outcome.getQualityofPreference();
					qualityofPreference.setLastAborted(Calendar.getInstance().getTime());
					qualityofPreference.increaseAbortedCounter(1);

					matchedPref.setUserObject(outcome);
					this.prefMgr.storePreference(this.commManager.getIdManager().getThisNetworkNode(), details, matchedPref.getRoot());
					if (this.logging.isDebugEnabled()){
						this.logging.debug("Updated QoS of preference for aborted feedback");
					}
				}

			}else if (fEvent.getErrorType().equals(FeedbackTypes.IMPLEMENTED)){
				if(action.getvalue().equalsIgnoreCase(outcome.getvalue())){

					IQualityofPreference qualityofPreference = outcome.getQualityofPreference();
					qualityofPreference.setLastSuccess(Calendar.getInstance().getTime());
					qualityofPreference.increaseSuccessCounter(1);

					matchedPref.setUserObject(outcome);
					this.prefMgr.storePreference(this.commManager.getIdManager().getThisNetworkNode(), details, matchedPref.getRoot());
					if (this.logging.isDebugEnabled()){
						this.logging.debug("Updated QoS of preference for successful feedback");
					}					
				}
			}


		}else{
			if (logging.isDebugEnabled()){
				this.logging.debug("Received feedback for an action that I did not affect");
			}
		}
	}


	private IPreference findMatchingPreference(IPreference rootPreference, IPreference feedbackPreference) {

		Enumeration<IPreference> depthFirstEnumeration = rootPreference.depthFirstEnumeration();
		while (depthFirstEnumeration.hasMoreElements()){
			IPreference nextElement = depthFirstEnumeration.nextElement();

			if (nextElement.getDepth()==feedbackPreference.getDepth()){
				if (logging.isDebugEnabled()){
					this.logging.debug("In the same depth ");

					if (nextElement.getUserObject() instanceof IPreferenceOutcome){
						if (logging.isDebugEnabled()){
							this.logging.debug("Found a preference outcome in the same depth");
						}

						IPreferenceOutcome outcome = (IPreferenceOutcome) nextElement.getUserObject();
						IPreferenceOutcome feedbackOutcome = feedbackPreference.getOutcome();
						if (outcome.getvalue().equalsIgnoreCase(feedbackOutcome.getvalue())){
							return nextElement;
						}
					}
				}
			}
		}


		return null;

	}


	public void addEvaluationResult(String uuid, IPreference p) {
		if (this.logging.isDebugEnabled()){
			this.logging.debug("Adding evaluation result:  "+p.getOutcome().getparameterName()+" = "+p.getOutcome().getvalue());
		}
		this.evaluationResults.put(uuid, p);

	}


	public String getDoFix() {
		return doFix;
	}


	public void setDoFix(String doFix) {
		this.doFix = doFix;
	}


}
