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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.TextNotificationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent.NotificationType;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseAccCtrlEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.AccessControlDialog;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.PrivacyPreferenceMerger;

/**
 * @author Eliza
 *
 */
public class AccessControlPreferenceManager extends EventListener{

	private final static Logger logging = LoggerFactory.getLogger(AccessControlPreferenceManager.class);

	private String[] sensedDataTypes;
	private IIdentity userIdentity;
	private PrivacyPreferenceManager privPrefMgr;
	private Hashtable<String, UserResponseAccCtrlEvent> userResponses;
	

	public AccessControlPreferenceManager(PrivacyPreferenceManager privPrefMgr){
		this.userResponses = new Hashtable<String, UserResponseAccCtrlEvent>();
		this.privPrefMgr = privPrefMgr;

		userIdentity = privPrefMgr.getIdm().getThisNetworkNode();
		sensedDataTypes = new String[]{CtxAttributeTypes.TEMPERATURE, 
				CtxAttributeTypes.STATUS,
				CtxAttributeTypes.LOCATION_SYMBOLIC,
				CtxAttributeTypes.LOCATION_COORDINATES,
				CtxAttributeTypes.ACTION};
		String[] eventTypes = new String[]{EventTypes.PERSONIS_NOTIFICATION_RESPONSE};
		this.privPrefMgr.getEventMgr().subscribeInternalEvent(this, eventTypes, null);
	}



	

	private boolean isAttributeSensed(String type) {
		for (String sensedType : sensedDataTypes){
			if (sensedType.equalsIgnoreCase(type)){
				return true;
			}
		}
		return false;
	}



	public AccessControlOutcome evaluatePreference(PrivacyPreference privPref, List<Condition> conditions, RequestorBean requestor){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.privPrefMgr, requestor, this.userIdentity);
		return ppE.evaluateAccessCtrlPreference(privPref, conditions);
	}

	/*
	 * OK
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException{

		if (dataIds==null || dataIds.size()==0){
			logging.debug("requested permission for null CtxIdentifier. returning : null");
			throw new PrivacyException("requested permission for null CtxIdentifier!");
		}
		if (null==action){
			throw new PrivacyException("requested permission for: "+dataIds+" without specifying action");
		}
		if (null==requestor){
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+" : "+dataIds+" with null requestor");
		}
		logging.debug("public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action)");


		List<ResponseItem> permissions = new ArrayList<ResponseItem>();


		for (DataIdentifier dataID : dataIds){
			ResponseItem checkPermission = this.checkPermission(requestor, dataID, action);
			permissions.add(checkPermission);
			StringBuilder sb = new StringBuilder();
			if (checkPermission.getDecision()==Decision.PERMIT){
				sb.append("Permission granted to "+requestor.getRequestorId());
				
			}else{
				sb.append("Permission denied to "+requestor.getRequestorId());
			}
			sb.append(" to "+action.getActionConstant().name());
			sb.append(" your "+dataID.getType());
			try {
				TextNotificationEvent txtNotifEvent = new TextNotificationEvent(sb.toString());
				InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_TEXT, "", this.getClass().getName(), txtNotifEvent);

				this.privPrefMgr.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}
		return permissions;
	}
	
	
	private boolean containsKeys(List<String> uuids){
		for (String uuid : uuids){
			if (!this.userResponses.containsKey(uuid)){
				return false;
			}
		}
		
		return true;
	}

	/*
	 * OK
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action) throws PrivacyException{
		logging.debug("public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action)");
		if (null==dataId){
			logging.debug("requested permission for null CtxIdentifier. returning : null");
			throw new PrivacyException("requested permission for null CtxIdentifier!");
		}
		if (null==action){
			throw new PrivacyException("requested permission for: "+dataId.getUri()+" without specifying action");	
		}
		if (null==requestor){
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+" : "+dataId.getUri()+" with null requestor");
		}
		List<Condition> conditions = new ArrayList<Condition>();
		logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\nctxId: "+dataId.getUri()+"\n and action: "+action.getActionConstant());


		AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
		details.setAction(action);
		details.setRequestor(requestor);
		Resource resource = ResourceUtils.create(dataId.getUri());
		details.setResource(resource);

		//ResponseItem evaluateAccCtrlPreference = this.evaluateAccCtrlPreference(details, conditions);

		AccessControlOutcome accCtrlOutcome = this.evaluateAccessControlPreference(details, conditions);
		if (accCtrlOutcome==null){			

			//return getUserInput(details);
			StringBuilder sb  = new StringBuilder();
			sb.append("Service: "+requestor.getRequestorId());
			sb.append(" requests access to "+action.getActionConstant().toString().toLowerCase());
			sb.append(" your data: "+resource.getDataType());
			String uuid = UUID.randomUUID().toString();
			
			NotificationAccCtrlEvent notifEvent = null;
			notifEvent = new NotificationAccCtrlEvent(uuid, sb.toString(), NotificationType.SIMPLE, PrivacyOutcomeConstantsBean.ALLOW);
			
			InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_REQUEST, "", this.getClass().getName(), notifEvent);
			try {
				this.privPrefMgr.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				logging.error("Error publishing internal event: {}", event);
				return getUserInput(details);
			}
			while (!this.userResponses.containsKey(uuid)){
				synchronized (this.userResponses) {
					try {
						this.userResponses.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (this.userResponses.containsKey(uuid)){
				UserResponseAccCtrlEvent userResponseEvent = userResponses.get(uuid);
				logging.debug("Received user response: "+userResponseEvent.getEffect());
				storeDecision(details, userResponseEvent.getEffect());
				return createResponseItem(requestor, dataId, action, conditions,userResponseEvent.getEffect());
			}else{
				logging.debug("Error receiving input through notificationPanel. using JDialog");
				return getUserInput(details);
			}
		}else{


			logging.debug("checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action): "+accCtrlOutcome);
			StringBuilder sb = new StringBuilder();
			if ((accCtrlOutcome).getEffect()==PrivacyOutcomeConstantsBean.ALLOW){
				sb.append("Your preferences suggest that I give access to");
			}else{
				sb.append("Your preferences suggest that I block access to");
			}
			sb.append(requestor.getRequestorId());
			sb.append(" to ");
			sb.append(action.getActionConstant().toString().toLowerCase()); 
			sb.append(" your data: ");
			sb.append(resource.getDataType());
			String uuid = UUID.randomUUID().toString();

			NotificationAccCtrlEvent notifEvent = null;
			if (accCtrlOutcome.getConfidenceLevel()>=60){
				notifEvent = new NotificationAccCtrlEvent(uuid, sb.toString(), NotificationType.TIMED, accCtrlOutcome.getEffect());
			}else {
				notifEvent = new NotificationAccCtrlEvent(uuid, sb.toString(), NotificationType.SIMPLE, accCtrlOutcome.getEffect());
			}
			InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_REQUEST, "", this.getClass().getName(), notifEvent);
			try {
				this.privPrefMgr.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				logging.error("Error publishing internal event: {}", event);
				return getUserInput(details);
			}
			while (!this.userResponses.containsKey(uuid)){
				synchronized (this.userResponses) {
					try {
						this.userResponses.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//inform the user and then return it

			if (this.userResponses.containsKey(uuid)){
				UserResponseAccCtrlEvent userResponseEvent = userResponses.get(uuid);
				logging.debug("Received user response: "+userResponseEvent.getEffect());
				if (userResponseEvent.isUserClicked()){
					if (accCtrlOutcome.getEffect()==userResponseEvent.getEffect()){
						logging.debug("User response is the same as evaluated preference, updating conf level");
						this.updateConfidenceLevel(details, accCtrlOutcome.getUuid(), true);
					}else{
						//no need to update the confidence of the existing preference negatively, the conflict 
						//should be reflected in the merging process
						//logging.debug("User response is not the same as evaluated preference, updating conf level");
						//this.updateConfidenceLevel(details, accCtrlOutcome.getUuid(), false);
						logging.debug("User response is not the same as evaluated preference. Storing new decision");
						storeDecision(details, userResponseEvent.getEffect());
					}
					return createResponseItem(requestor, dataId, action, conditions,userResponseEvent.getEffect());
				}else{
					return createResponseItem(requestor, dataId, action, conditions,userResponseEvent.getEffect());
				}

			}else{
				logging.error("Error retrieving response from user. Returning preference evaluation result");
				return createResponseItem(requestor, dataId, action, conditions, (accCtrlOutcome.getEffect()));
			}
		}
	}

	private void updateConfidenceLevel(AccessControlPreferenceDetailsBean details, String uuid, boolean positive){
		logging.debug("UpdateConfidenceLevel ({}), uuid {}", details.getResource().getDataType(), uuid);
		AccessControlPreferenceTreeModel accCtrlPreference = this.getAccCtrlPreference(details);
		if (accCtrlPreference!=null){
			PrivacyPreference rootPreference = accCtrlPreference.getPref().getRoot();
			Enumeration<PrivacyPreference> postorderEnumeration = rootPreference.postorderEnumeration();
			while (postorderEnumeration.hasMoreElements()){
				PrivacyPreference privacyPreference = postorderEnumeration.nextElement();
				
				if (privacyPreference.isLeaf()){
					
					AccessControlOutcome outcome = (AccessControlOutcome) privacyPreference.getOutcome();
					logging.debug("checking leaf uuid {}, with given uuid {}", outcome.getUuid(), uuid);
					if (outcome.getUuid().equalsIgnoreCase(uuid)){
						logging.debug("Found outcome with uuid, going to update now, current confidence Level: {}", outcome.getConfidenceLevel());
						outcome.updateConfidenceLevel(positive);
						logging.debug("Updated confidence level: {}", outcome.getConfidenceLevel());
						this.storeAccCtrlPreference(details, accCtrlPreference);
						logging.debug("stored preference with updated confidence level");
					}
				}
			}
		}else{
			logging.debug("UpdateConfidenceLevel: Could not retrieve preference with details: {}", PrivacyPreferenceUtils.toString(details));
		}
	}

	private ResponseItem getUserInput(AccessControlPreferenceDetailsBean details)
			throws PrivacyException {
		AccessControlResponseItem responseItem = new AccessControlResponseItem();

		RequestItem requestItem = new RequestItem();
		List<Action> actions = new ArrayList<Action>();
		actions.add(details.getAction());
		requestItem.setActions(actions);
		requestItem.setConditions(new ArrayList<Condition>());
		requestItem.setResource(details.getResource());
		responseItem.setRequestItem(requestItem);
		List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
		responseItems.add(responseItem);
		AccessControlDialog dialog = new AccessControlDialog(details.getRequestor(), responseItem);
		AccessControlResponseItem accessControlResponseItem = dialog.getAccessControlResponseItem();


		//AccessControlResponseItem accessControlResponseItem = resultlist.get(0);

		Decision decision = accessControlResponseItem.getDecision();
		if (accessControlResponseItem.isRemember()){
			privPrefMgr.getprivacyDataManagerInternal().updatePermission(details.getRequestor(), accessControlResponseItem);

			if (decision==Decision.PERMIT){
				this.storeDecision(details, PrivacyOutcomeConstantsBean.ALLOW);
				logging.debug("Stored access control feedback as preference");

			}else if (decision==Decision.DENY){
				this.storeDecision(details, PrivacyOutcomeConstantsBean.BLOCK);
				logging.debug("Stored access control feedback as preference");
			}
		}else{
			logging.debug("One-off access granted. Permission not stored permanently");
		}
		if (decision==Decision.PERMIT){
			if (accessControlResponseItem.isObfuscationInput()){
				privPrefMgr.getDobfPreferenceCreator().createPreference(details.getRequestor(), accessControlResponseItem.getRequestItem().getResource(), accessControlResponseItem.getObfuscationLevel());
				logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
			}else{
				logging.debug("Obfuscation not requested in the access control");
			}
		}
		logging.debug("checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action): "+accessControlResponseItem);

		return accessControlResponseItem;
	}



	private void storeDecision(AccessControlPreferenceDetailsBean detailsBean,  PrivacyOutcomeConstantsBean decision){
		try {
			AccessControlOutcome outcome = new AccessControlOutcome(decision);

			AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(detailsBean, this.createAccessCtrlPrivacyPreference(detailsBean.getAction(), detailsBean.getResource(), outcome));

			AccessControlPreferenceTreeModel accCtrlPreference = getAccCtrlPreference(detailsBean);
			if (accCtrlPreference==null){
				logging.debug("Could not retrieve accCtrlPreference, storing new decision");
				storeAccCtrlPreference(detailsBean, model);
				return;
			}
			logging.debug("Found accCtrlPreference, going to merge trees: existing: {} ", accCtrlPreference.getPref().getRoot().toTreeString(), model.getPref().getRoot());
			PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
			
			PrivacyPreference mergeAccCtrlPreference = merger.mergeAccCtrlPreference(detailsBean, accCtrlPreference.getPref().getRoot(), model.getPref().getRoot());
			
			if (mergeAccCtrlPreference==null){
				logging.debug("Could not merge preferences, merger returned null");
				//storeAccCtrlPreference(detailsBean, model);
				return;
			}
			logging.debug("storing merged preference: {}", mergeAccCtrlPreference.toTreeString());
			storeAccCtrlPreference(detailsBean, new AccessControlPreferenceTreeModel(detailsBean, mergeAccCtrlPreference));
		} catch (PrivacyException e) {
			e.printStackTrace();
		}


	}


	private PrivacyPreference createAccessCtrlPrivacyPreference(Action action, Resource resource, AccessControlOutcome outcome) throws PrivacyException{
		PrivacyPreference withAllConditionsPreference = new PrivacyPreference(outcome);
		//PrivacyPreference withAllConditionsPreference = this.createPreferenceWithPrivacyConditions(conditions, action, outcome);
		if (resource.getScheme().equals(DataIdentifierScheme.CONTEXT)){
			try {
				// 2013-04-16: updated by Olivier to replace CtxAttributeIdentifier to CtxIdentifier. Add logic to handle also CtxEntity and not just CtxAttribute
				CtxIdentifier ctxIdentifier = (CtxIdentifier) ResourceUtils.getDataIdentifier(resource);
				CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().retrieve(ctxIdentifier).get();
				ContextPreferenceCondition condition;
				PrivacyPreference conditionPreference;
				// -- CtxAttribute
				if (ctxModelObject!=null && ctxModelObject instanceof CtxAttribute){
					CtxAttribute ctxAttribute = (CtxAttribute) ctxModelObject;
					// CtxAttribute is inferred or sensed: add a privacy preference condition
					if (isAttributeSensed(ctxAttribute.getType())){
						switch (ctxAttribute.getValueType()){
						case DOUBLE:
							condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getDoubleValue().toString());
							conditionPreference = new PrivacyPreference(condition);
							conditionPreference.add(withAllConditionsPreference);
							return conditionPreference;

						case INTEGER:
							condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getIntegerValue().toString());
							conditionPreference = new PrivacyPreference(condition);
							conditionPreference.add(withAllConditionsPreference);
							return conditionPreference;
						case STRING: 
							condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getStringValue());
							conditionPreference = new PrivacyPreference(condition);
							conditionPreference.add(withAllConditionsPreference);
							return conditionPreference;
						}
					}
					// -- CtxEntity
					//else if (ctxModelObject!=null && ctxModelObject instanceof CtxEntity){CtxEntity ctxEntity = (CtxEntity) ctxModelObject;
					// TODO for Eliza: check if it is relevant to add a ContextPreferenceCondition or not
					//						condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxEntity.getOwnerId());
					//						conditionPreference = new PrivacyPreference(condition);
					//						conditionPreference.add(withAllConditionsPreference);
					//						return conditionPreference;
					// comment from Eliza: this will never happen.
					//}
				}else{
					throw new PrivacyException("Could not create access control preference as there was no ctxAttribute found in DB with the provided dataIdentifier");
				}


			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return withAllConditionsPreference;
	}



	private ResponseItem createResponseItem(RequestorBean requestor, DataIdentifier dataId, Action action, List<Condition> conditions, PrivacyOutcomeConstantsBean effect) throws PrivacyException{

		RequestItem reqItem = new RequestItem();
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		reqItem.setActions(actions);
		reqItem.setConditions(conditions);
		Resource resource = ResourceUtils.create(dataId);
		reqItem.setResource(resource);
		ResponseItem respItem = new ResponseItem();
		if (effect == PrivacyOutcomeConstantsBean.ALLOW){
			respItem.setDecision(Decision.PERMIT);	
		}else if (effect == PrivacyOutcomeConstantsBean.BLOCK){
			respItem.setDecision(Decision.DENY);
		}else{
			throw new PrivacyException("Outcome effect not initialised. Can't create ResponseItem");
		}

		respItem.setRequestItem(reqItem);

		return respItem;
	}



	/**
	 * new methods;
	 */

	public boolean deleteAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		return privPrefMgr.getPrefCache().removeAccCtrlPreference(details);
	}



	public ResponseItem evaluateAccCtrlPreference(
			AccessControlPreferenceDetailsBean details, List<Condition> conditions) throws PrivacyException {

		AccessControlPreferenceTreeModel model = privPrefMgr.getPrefCache().getAccCtrlPreference(details);

		if (model!=null){
			try {
				//return this.checkPreferenceForAccessControl(details, model, conditions);
				//IPrivacyOutcome evaluatePreference = this.evaluatePreference(model.getPref(), conditions, details.getRequestor());
				AccessControlOutcome outcome = this.evaluatePreference(model.getPref(), conditions, details.getRequestor());
				if (outcome!=null){

					DataIdentifier dataId = ResourceUtils.getDataIdentifier(details.getResource());
					return this.createResponseItem(details.getRequestor(), dataId, details.getAction(), conditions, (outcome).getEffect());
				}
				logging.debug("Preference found but evaluation returned null");
				return null;
				
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logging.debug("Could not find preference for given details");
		return null;

	}


	private AccessControlOutcome evaluateAccessControlPreference(AccessControlPreferenceDetailsBean details, List<Condition> conditions) throws PrivacyException {

		AccessControlPreferenceTreeModel model = privPrefMgr.getPrefCache().getAccCtrlPreference(details);

		if (model!=null){

			//return this.checkPreferenceForAccessControl(details, model, conditions);
			AccessControlOutcome evaluatePreference = this.evaluatePreference(model.getPref(), conditions, details.getRequestor());

			if (evaluatePreference!=null){
				
				return  evaluatePreference;
				
			}
			logging.debug("Preference evaluation did not yield any results");
			return  null;
		}
		logging.debug("Could not find preference for given details");
		return null;

	}



	public AccessControlPreferenceTreeModel getAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		return privPrefMgr.getPrefCache().getAccCtrlPreference(details);
	}
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails() {
		return privPrefMgr.getPrefCache().getAccCtrlPreferenceDetails();
	}

	public boolean storeAccCtrlPreference(
			AccessControlPreferenceDetailsBean details,
			AccessControlPreferenceTreeModel model) {
		Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> newDetails = new Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>>();
		try {
			CtxAttributeIdentifier ctxId = new CtxAttributeIdentifier(details.getResource().getDataIdUri());
			ArrayList<AccessControlPreferenceDetailsBean> list = new ArrayList<AccessControlPreferenceDetailsBean>();
			list.add(details);
			newDetails.put(ctxId, list); 
			privPrefMgr.getAccCtrlMonitor().monitorThisContext(newDetails);	
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return privPrefMgr.getPrefCache().addAccCtrlPreference(details, model);
	}


	/**
	 * 
	 * @param requestor 
	 * @param dataIds	the list of requestedItems for which preferences exist
	 * @return			a hashtable whose keys represent the preference conditions (context)
	 */
	public Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> getContextConditions(Requestor requestor, List<DataIdentifier> dataIds){
		Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> detailsToBeMonitored = new Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>>();


		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails = this.getAccCtrlPreferenceDetails();
		String display = "";
		for (AccessControlPreferenceDetailsBean detail : accCtrlPreferenceDetails){
			display = display.concat("\nRequestor: "+RequestorUtils.toString(detail.getRequestor())+", resource: "+ResourceUtils.toString(detail.getResource())+", action: "+detail.getAction().toString());
		}
		//JOptionPane.showMessageDialog(null, "Found prefs: "+accCtrlPreferenceDetails.size()+display);

		//for every requested item in the privacy policy
		for (DataIdentifier requestedDataId : dataIds){
			//JOptionPane.showMessageDialog(null, "requested ids loop: "+requestedDataId.getType());
			//for every preference 
			for (AccessControlPreferenceDetailsBean detail: accCtrlPreferenceDetails){
				//JOptionPane.showMessageDialog(null, "requested detail loop: "+detail.getResource().getDataType()+" \n"+detail.getRequestor().getRequestorId());
				//if the preference refers to this resource
				if (requestedDataId.getType().equalsIgnoreCase(detail.getResource().getDataType())){
					//if the preference refers to this requestor


					if (RequestorUtils.equals(detail.getRequestor(), RequestorUtils.toRequestorBean(requestor))){
						//JOptionPane.showMessageDialog(null, "Requestor: "+RequestorUtils.toString(detail.getRequestor())+" vs "+RequestorUtils.toString(RequestorUtils.toRequestorBean(requestor)));
						//retrieve the preference, iterate through it, and retrieve all the conditions
						AccessControlPreferenceTreeModel accCtrlPreference = this.getAccCtrlPreference(detail);
						PrivacyPreference rootPreference = accCtrlPreference.getRootPreference();
						Enumeration<PrivacyPreference> postorderEnumeration = rootPreference.postorderEnumeration();
						ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();
						while (postorderEnumeration.hasMoreElements()){

							PrivacyPreference nextElement = postorderEnumeration.nextElement();

							if (nextElement.getUserObject()!=null){
								//JOptionPane.showMessageDialog(null, "Processing element "+nextElement.getUserObject().toString());
								if (nextElement.getUserObject() instanceof ContextPreferenceCondition){
									CtxIdentifier contextConditionID =((ContextPreferenceCondition)nextElement.getCondition()).getCtxIdentifier(); 
									//if the list doesn't already contain this condition
									if (!ctxIds.contains(contextConditionID)){
										ctxIds.add(contextConditionID);
									}
								}
							}
						}


						for (CtxIdentifier ctxId : ctxIds){
							//if the ctxId already exists as a key, add the preference details to the list 
							if (detailsToBeMonitored.containsKey(ctxId)){
								detailsToBeMonitored.get(ctxId).add(detail);
							}else{
								//else add the new ctxID as key and add the preference details in the list
								ArrayList<AccessControlPreferenceDetailsBean> list = new ArrayList<AccessControlPreferenceDetailsBean>();
								list.add(detail);
								detailsToBeMonitored.put(ctxId, list);
							}
						}
					}
				}
			}
		}


		return detailsToBeMonitored;
	}



	public boolean deleteAccCtrlPreferences() {
		return privPrefMgr.getPrefCache().removeAccCtrlPreferences();
	}




	@Override
	public void handleInternalEvent(InternalEvent event) {
		logging.debug("Received event: {}", event.geteventType());
		if (event.geteventInfo() instanceof UserResponseAccCtrlEvent){
			UserResponseAccCtrlEvent uREvent = (UserResponseAccCtrlEvent) event.geteventInfo();
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
