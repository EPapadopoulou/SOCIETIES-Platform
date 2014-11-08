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
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseEvent.Response;
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
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
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
	private Hashtable<String, UserResponseEvent> userResponses;

	public AccessControlPreferenceManager(PrivacyPreferenceManager privPrefMgr){
		this.userResponses = new Hashtable<String, UserResponseEvent>();
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



	public IPrivacyOutcome evaluatePreference(PrivacyPreference privPref, List<Condition> conditions, RequestorBean requestor){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.privPrefMgr, requestor, this.userIdentity);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluateAccessCtrlPreference(privPref, conditions);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		//JOptionPane.showMessageDialog(null, results.size());
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}

	/*
	 * OK
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException{
		logging.debug("public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action)");
		
		
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		
		
		for (DataIdentifier dataID : dataIds){
			ResponseItem checkPermission = this.checkPermission(requestor, dataID, action);
			permissions.add(checkPermission);
		}
		return permissions;
/*		if (null==dataIds || dataIds.size()==0){
			this.logging.debug("requested permission without specifying data identifiers!");
			throw new PrivacyException("requested permission without specifying data identifiers!");
		}

		List<String> dataTypes = new ArrayList<String>();
		for (DataIdentifier dataId : dataIds){
			dataTypes.add(dataId.getType());
		}


		Hashtable<ResponseItem, List<Condition>> conditions = new Hashtable<ResponseItem, List<Condition>>();

		String strToPrint = "data identifiers: ";
		for (DataIdentifier dataId : dataIds){
			strToPrint = strToPrint.concat(dataId.getUri()+"\n");
		}
		if (null==action){
			this.logging.debug("requested permission for: "+strToPrint+" without specifying action");
			throw new PrivacyException("requested permission for: "+strToPrint+" without specifying action");	
		}
		if (null==requestor){
			this.logging.debug("requested permission to "+action.getActionConstant().value()+"  these items: "+strToPrint+" with null requestor");
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+"  these items: "+strToPrint+" with null requestor");
		}		
		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\n"+strToPrint+"\n and action: "+action.getActionConstant());

		*//**
		 * retrieve agreed conditions from agreement.
		 *//*

		List<ResponseItem> permissions = new ArrayList<ResponseItem>();

		List<DataIdentifier> preferencesExist = new ArrayList<DataIdentifier>();
		List<AccessControlResponseItem> preferencesDoNotExist = new ArrayList<AccessControlResponseItem>();

		for (DataIdentifier dataId : dataIds){
			AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
			details.setAction(action);
			details.setRequestor(requestor);
			Resource resource = ResourceUtils.create(dataId.getUri());
			details.setResource(resource);

			List<Condition> conditionsListForSingleItem = getConditionsHelperMethod(dataId, conditions.keys());
			ResponseItem evaluateAccCtrlPreference = this.evaluateAccCtrlPreference(details, conditionsListForSingleItem);
			if (evaluateAccCtrlPreference==null){

				AccessControlResponseItem respItem = new AccessControlResponseItem();
				RequestItem reqItem = new RequestItem();
				reqItem.setConditions(conditionsListForSingleItem);
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				reqItem.setActions(actions);
				resource.setDataIdUri(dataId.getUri());
				resource.setScheme(dataId.getScheme());
				resource.setDataType(dataId.getType());
				reqItem.setResource(resource);
				respItem.setRequestItem(reqItem);
				preferencesDoNotExist.add(respItem);
			}else{
				preferencesExist.add(dataId);
				permissions.add(evaluateAccCtrlPreference);
			}
		}
		if (preferencesDoNotExist.size()>0){
			//TODO: AC gui!
			//List<AccessControlResponseItem> list = this.userFeedback.getAccessControlFB(RequestorUtils.toRequestor(requestor, idMgr), preferencesDoNotExist).get();
			
			List<AccessControlResponseItem> list = new ArrayList<AccessControlResponseItem>();
			for (ResponseItem item :preferencesDoNotExist){
				AccessControlDialog dialog = new AccessControlDialog(requestor, item);
				AccessControlResponseItem accessControlResponseItem = dialog.getAccessControlResponseItem();
				list.add(accessControlResponseItem);
			}
			
			for (AccessControlResponseItem item: list){
				if (item.isRemember()){
					this.privPrefMgr.getprivacyDataManagerInternal().updatePermission(requestor, item);
					this.storeDecision(requestor, item.getRequestItem().getResource(), item.getRequestItem().getConditions(), action, item.getDecision());
					this.logging.debug("Stored access control feedback as preference");
				}else{
					this.logging.debug("One-off access granted. Permission not stored permanently");
				}
				if (item.isObfuscationInput()){
					this.logging.debug("item: {}", item);
					this.logging.debug("item resource {}", item.getRequestItem().getResource());
					this.privPrefMgr.getDobfPreferenceCreator().createPreference(requestor, item.getRequestItem().getResource(), item.getObfuscationLevel());
					this.logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
				}
			}
			permissions.addAll(list);
			logging.debug("checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action): {}", permissions);
			return permissions;
		}
		logging.debug("checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action): {}", permissions);
		return permissions;*/
	}

	private List<Condition> getConditionsHelperMethod(DataIdentifier dataId, Enumeration<ResponseItem> fromAgreementItems){
		while (fromAgreementItems.hasMoreElements()){
			ResponseItem responseItem = fromAgreementItems.nextElement();
			if (responseItem.getRequestItem().getResource().getDataType().equalsIgnoreCase(dataId.getType())){
				return responseItem.getRequestItem().getConditions();
			}
		}
		return new ArrayList<Condition>();
	}

	/*
	 * OK
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action) throws PrivacyException{
		this.logging.debug("public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action)");
		if (null==dataId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : null");
			throw new PrivacyException("requested permission for null CtxIdentifier!");
		}
		if (null==action){
			throw new PrivacyException("requested permission for: "+dataId.getUri()+" without specifying action");	
		}
		if (null==requestor){
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+" : "+dataId.getUri()+" with null requestor");
		}
		List<Condition> conditions = new ArrayList<Condition>();
		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\nctxId: "+dataId.getUri()+"\n and action: "+action.getActionConstant());

		
		AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
		details.setAction(action);
		details.setRequestor(requestor);
		Resource resource = ResourceUtils.create(dataId.getUri());
		details.setResource(resource);

		ResponseItem evaluateAccCtrlPreference = this.evaluateAccCtrlPreference(details, conditions);

		if (evaluateAccCtrlPreference==null){			
			AccessControlResponseItem responseItem = new AccessControlResponseItem();

			RequestItem requestItem = new RequestItem();
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			requestItem.setActions(actions);
			requestItem.setConditions(conditions);
			requestItem.setResource(resource);
			responseItem.setRequestItem(requestItem);
			List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
			responseItems.add(responseItem);

			//TODO! AC GUI (SEE LINES 412,413)
			AccessControlDialog dialog = new AccessControlDialog(requestor, responseItem);
			AccessControlResponseItem accessControlResponseItem = dialog.getAccessControlResponseItem();
			

			//AccessControlResponseItem accessControlResponseItem = resultlist.get(0);
			
			if (accessControlResponseItem.isRemember()){
				privPrefMgr.getprivacyDataManagerInternal().updatePermission(requestor, accessControlResponseItem);
				this.storeDecision(requestor, resource, accessControlResponseItem.getRequestItem().getConditions(), action, accessControlResponseItem.getDecision());
				this.logging.debug("Stored access control feedback as preference");
			}else{
				this.logging.debug("One-off access granted. Permission not stored permanently");
			}
			if (accessControlResponseItem.isObfuscationInput()){
				privPrefMgr.getDobfPreferenceCreator().createPreference(requestor, accessControlResponseItem.getRequestItem().getResource(), accessControlResponseItem.getObfuscationLevel());
				this.logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
			}else{
				this.logging.debug("Obfuscation not requested in the access control");
			}
			logging.debug("checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action): "+accessControlResponseItem);
			
			return accessControlResponseItem;
			//responseItem.setDecision(Decision.DENY);
			//return responseItem;
		}else{
			logging.debug("checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action): "+evaluateAccCtrlPreference);
			//TODO: change text to say that preferences sugggest ...
			StringBuilder sb = new StringBuilder();
			if (evaluateAccCtrlPreference.getDecision()==Decision.PERMIT){
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
			
			NotificationEvent notifEvent = new NotificationEvent(uuid, sb.toString());
			InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION_REQUEST, "", this.getClass().getName(), notifEvent);
			try {
				this.privPrefMgr.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				logging.error("Error publishing internal event: {}", event);
				return evaluateAccCtrlPreference;
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
				UserResponseEvent userResponseEvent = userResponses.get(uuid);
				if (userResponseEvent.getResponse().equals(Response.ABORT)){
					/*
					 * copy paste:
					 */
					AccessControlResponseItem responseItem = new AccessControlResponseItem();

					RequestItem requestItem = new RequestItem();
					List<Action> actions = new ArrayList<Action>();
					actions.add(action);
					requestItem.setActions(actions);
					requestItem.setConditions(conditions);
					requestItem.setResource(resource);
					responseItem.setRequestItem(requestItem);
					List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
					responseItems.add(responseItem);
					AccessControlDialog dialog = new AccessControlDialog(requestor, responseItem);
					AccessControlResponseItem accessControlResponseItem = dialog.getAccessControlResponseItem();
					

					//AccessControlResponseItem accessControlResponseItem = resultlist.get(0);
					
					if (accessControlResponseItem.isRemember()){
						privPrefMgr.getprivacyDataManagerInternal().updatePermission(requestor, accessControlResponseItem);
						this.storeDecision(requestor, resource, accessControlResponseItem.getRequestItem().getConditions(), action, accessControlResponseItem.getDecision());
						this.logging.debug("Stored access control feedback as preference");
					}else{
						this.logging.debug("One-off access granted. Permission not stored permanently");
					}
					if (accessControlResponseItem.isObfuscationInput()){
						privPrefMgr.getDobfPreferenceCreator().createPreference(requestor, accessControlResponseItem.getRequestItem().getResource(), accessControlResponseItem.getObfuscationLevel());
						this.logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
					}else{
						this.logging.debug("Obfuscation not requested in the access control");
					}
					logging.debug("checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action): "+accessControlResponseItem);
					
					return accessControlResponseItem;
					/*
					 * end copy paste
					 */
					
					
				}else{
					logging.debug("Received input, all OK, returning preference evaluation result");
					return evaluateAccCtrlPreference;
				}
			}else{
				logging.error("Error retrieving response from user. Returning preference evaluation result");
			return evaluateAccCtrlPreference;
			}
		}

		
	}


/*	private void storeDecision(RequestorBean requestor, DataIdentifier dataId, List<Condition> conditions,Action action,  PrivacyOutcomeConstantsBean decision){
		Resource resource = new Resource();
		resource.setDataIdUri(dataId.getUri());
		resource.setScheme(dataId.getScheme());
		resource.setDataType(dataId.getType());
		List<RequestorBean> requestors = new ArrayList<RequestorBean>();
		requestors.add(requestor);

		try {
			AccessControlOutcome outcome = new AccessControlOutcome(decision);




			AccessControlPreferenceDetailsBean detailsBean = new AccessControlPreferenceDetailsBean();
			AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(detailsBean, this.createAccessCtrlPrivacyPreference(conditions,action, resource, outcome));

			detailsBean.setRequestor(requestor);
			detailsBean.setAction(action);
			detailsBean.setResource(resource);
			storeAccCtrlPreference(detailsBean, model);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//}
	}*/


	private void storeDecision(RequestorBean requestor, Resource resource, List<Condition> conditions,Action action,  Decision decision){

		List<RequestorBean> requestors = new ArrayList<RequestorBean>();
		requestors.add(requestor);

		try {
			AccessControlOutcome outcome;
			if (decision.equals(Decision.PERMIT)){
				outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
			}else{
				outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);

			}

			AccessControlPreferenceDetailsBean detailsBean = new AccessControlPreferenceDetailsBean();
			AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(detailsBean, this.createAccessCtrlPrivacyPreference(conditions,action, resource, outcome));

			detailsBean.setRequestor(requestor);
			detailsBean.setAction(action);
			detailsBean.setResource(resource);
			
			AccessControlPreferenceTreeModel accCtrlPreference = getAccCtrlPreference(detailsBean);
			if (accCtrlPreference==null){
				storeAccCtrlPreference(detailsBean, model);
				return;
			}
			PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
			PrivacyPreference mergeAccCtrlPreference = merger.mergeAccCtrlPreference(detailsBean, accCtrlPreference.getRootPreference(), model.getRootPreference());
			if (mergeAccCtrlPreference==null){
				storeAccCtrlPreference(detailsBean, model);
				return;
			}
			storeAccCtrlPreference(detailsBean, new AccessControlPreferenceTreeModel(detailsBean, mergeAccCtrlPreference));
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//}
	}


	private PrivacyPreference createAccessCtrlPrivacyPreference(List<Condition> conditions, Action action, Resource resource, AccessControlOutcome outcome) throws PrivacyException{
		PrivacyPreference withAllConditionsPreference = this.createPreferenceWithPrivacyConditions(conditions, action, outcome);
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
					else if (ctxModelObject!=null && ctxModelObject instanceof CtxEntity){
						CtxEntity ctxEntity = (CtxEntity) ctxModelObject;
						// TODO for Eliza: check if it is relevant to add a ContextPreferenceCondition or not
						//						condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxEntity.getOwnerId());
						//						conditionPreference = new PrivacyPreference(condition);
						//						conditionPreference.add(withAllConditionsPreference);
						//						return conditionPreference;
						// comment from Eliza: this will never happen.
					}
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
	private PrivacyPreference createPreferenceWithPrivacyConditions(
			List<Condition> conditions, Action action,
			AccessControlOutcome outcome) {

		PrivacyPreference rootPreference = new PrivacyPreference(outcome); 
		for (Condition condition : conditions){
			rootPreference = this.getPrivacyCondition(rootPreference, condition);
		}

		return rootPreference;
	}

	private PrivacyPreference getPrivacyCondition(PrivacyPreference preference, Condition condition){

		PrivacyPreference pref = new PrivacyPreference(new PrivacyCondition(condition));
		pref.add(preference);
		return pref;

	}
	private ResponseItem createResponseItem(RequestorBean requestor, DataIdentifier dataId, Action action, List<Condition> conditions, Decision decision){

		RequestItem reqItem = new RequestItem();
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		reqItem.setActions(actions);
		reqItem.setConditions(conditions);
		Resource resource = ResourceUtils.create(dataId);
		reqItem.setResource(resource);
		ResponseItem respItem = new ResponseItem();
		respItem.setDecision(decision);
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
				IPrivacyOutcome evaluatePreference = this.evaluatePreference(model.getPref(), conditions, details.getRequestor());

				if (evaluatePreference!=null){
					if (evaluatePreference instanceof AccessControlOutcome){
						DataIdentifier dataId = ResourceUtils.getDataIdentifier(details.getResource());
						if (((AccessControlOutcome) evaluatePreference).getEffect().equals(PrivacyOutcomeConstantsBean.ALLOW)){
							return this.createResponseItem(details.getRequestor(), dataId, details.getAction(), conditions, Decision.PERMIT);
						}else{
							return this.createResponseItem(details.getRequestor(), dataId, details.getAction(), conditions, Decision.DENY);
						}
					}else{
						throw new PrivacyException("An unexpected error occured. The evaluated outcome was not of type AccessControlOutcome");
					}
				}
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		this.logging.debug("Could not find preference for given details");
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

	private boolean contains(List<CtxIdentifier> dataIds, String uri){
		for (CtxIdentifier ctxId : dataIds){
			if (ctxId.getUri().equals(uri)){
				return true;
			}
		}

		return false;
	}



	public boolean deleteAccCtrlPreferences() {
		return privPrefMgr.getPrefCache().removeAccCtrlPreferences();
	}




	@Override
	public void handleInternalEvent(InternalEvent event) {
		this.logging.debug("Received event: {}", event.geteventType());
		if (event.geteventInfo() instanceof UserResponseEvent){
			UserResponseEvent uREvent = (UserResponseEvent) event.geteventInfo();
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
