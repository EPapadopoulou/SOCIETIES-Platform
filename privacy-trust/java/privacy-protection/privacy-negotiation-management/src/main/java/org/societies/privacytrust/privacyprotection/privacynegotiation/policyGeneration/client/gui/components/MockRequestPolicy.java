package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.GroupLayout.Alignment;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.util.Tuple;

public class MockRequestPolicy {

	public static HashMap<RequestItem, ResponseItem> getItems(){

	
		HashMap<RequestItem, ResponseItem> items = new HashMap<RequestItem, ResponseItem>();
		String[] typeList = new String[]{CtxAttributeTypes.AGE, 
				CtxAttributeTypes.INTERESTS, 
				CtxAttributeTypes.LOCATION_COORDINATES, 
				CtxAttributeTypes.ACTIVITIES, 
				CtxAttributeTypes.LOCATION_SYMBOLIC, 
				CtxAttributeTypes.EMAIL, 
				CtxAttributeTypes.BIRTHDAY, 
				CtxAttributeTypes.ADDRESS_HOME, 
				CtxAttributeTypes.FOOD,
				CtxAttributeTypes.LANGUAGES,
				CtxAttributeTypes.OCCUPATION}; 
		
		
		for (String str : typeList){
			Tuple tuple = getTuple(str, Decision.PERMIT);	
			items.put(tuple.getReqItem(), tuple.getRespItem());
		}
		
		return items;
	}
	

	static Tuple getTuple(String contextType, Decision decision){
		ArrayList<Action> actions = new ArrayList<Action>();
		Action actionRead = new Action();
		actionRead.setActionConstant(ActionConstants.READ);
		Action actionWrite = new Action();
		actionWrite.setActionConstant(ActionConstants.WRITE);
		Action actionCreate = new Action();
		actionCreate.setActionConstant(ActionConstants.CREATE);
		Action actionDelete = new Action();
		actionDelete.setActionConstant(ActionConstants.DELETE);
		actions.add(actionRead);
		actions.add(actionWrite);
		actions.add(actionCreate);
		actions.add(actionDelete);
		
		
		Condition conditionDataRetention = new Condition();
		conditionDataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
		conditionDataRetention.setValue(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[3]);

		Condition conditionShare3p  = new Condition();
		conditionShare3p.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		conditionShare3p.setValue("Yes");

		Condition conditionRightToOptOut = new Condition();
		conditionRightToOptOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
		conditionRightToOptOut.setValue("Yes");


		Condition conditionStoreSecure = new Condition();
		conditionStoreSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
		conditionStoreSecure.setValue("Yes");

		ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(conditionStoreSecure);
		conditions.add(conditionShare3p);
		conditions.add(conditionDataRetention);
		conditions.add(conditionRightToOptOut);
		
		Resource resource = new Resource();
		resource.setScheme(DataIdentifierScheme.CONTEXT);
		resource.setDataType(contextType);
		
		
		RequestItem reqItem  = new RequestItem();
		reqItem.setResource(resource);
		reqItem.setActions(actions);
		reqItem.setConditions(conditions);
		
		RequestItem reqItem2  = new RequestItem();
		reqItem2.setResource(resource);
		reqItem2.setActions((List<Action>) actions.clone());
		reqItem2.setConditions((List<Condition>) conditions.clone());
		
		
		ResponseItem respItem = new ResponseItem();
		respItem.setRequestItem(reqItem2);
		respItem.setDecision(decision);
		
		
		Tuple tuple = new Tuple(reqItem, respItem);
		return tuple;
	}
	public static NegotiationDetailsBean getNegotiationDetailsBean(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestorServiceBean requestorServiceBean;
		requestorServiceBean = new RequestorServiceBean();
		requestorServiceBean.setRequestorId(requestorId.getJid());
		requestorServiceBean.setRequestorServiceId(serviceId);

		NegotiationDetailsBean negotiationDetailsBean = new NegotiationDetailsBean();
		negotiationDetailsBean.setNegotiationID(1);
		negotiationDetailsBean.setRequestor(requestorServiceBean);
		return negotiationDetailsBean;
	}
	public static RequestPolicy getRequestPolicy(){
		
		
		
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestorServiceBean requestorServiceBean;
		requestorServiceBean = new RequestorServiceBean();
		requestorServiceBean.setRequestorId(requestorId.getJid());
		requestorServiceBean.setRequestorServiceId(serviceId);

		ArrayList<Action> actions = new ArrayList<Action>();
		Action actionRead = new Action();
		actionRead.setActionConstant(ActionConstants.READ);
		Action actionWrite = new Action();
		actionWrite.setActionConstant(ActionConstants.WRITE);
		Action actionCreate = new Action();
		actionCreate.setActionConstant(ActionConstants.CREATE);
		Action actionDelete = new Action();
		actionDelete.setActionConstant(ActionConstants.DELETE);
		actions.add(actionRead);
		actions.add(actionWrite);
		actions.add(actionCreate);
		actions.add(actionDelete);

		Condition conditionDataRetention = new Condition();
		conditionDataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
		conditionDataRetention.setValue(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[3]);

		Condition conditionShare3p  = new Condition();
		conditionShare3p.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		conditionShare3p.setValue("Yes");

		Condition conditionRightToOptOut = new Condition();
		conditionRightToOptOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
		conditionRightToOptOut.setValue("Yes");


		Condition conditionStoreSecure = new Condition();
		conditionStoreSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
		conditionStoreSecure.setValue("Yes");

		Condition conditionCorrect = new Condition();
		conditionCorrect.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
		conditionCorrect.setValue("Yes");
		
		Condition conditionAccess = new Condition();
		conditionAccess.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
		conditionAccess.setValue("Yes");
		
		Condition mayInfer = new Condition();
		mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
		mayInfer.setValue("Yes");
		
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		conditions.add(conditionStoreSecure);
		conditions.add(conditionShare3p);
		conditions.add(conditionDataRetention);
		conditions.add(conditionRightToOptOut);
		conditions.add(conditionAccess);
		conditions.add(mayInfer);
		conditions.add(conditionCorrect);
		
		
		Resource rLocation = new Resource();
		rLocation.setScheme(DataIdentifierScheme.CONTEXT);
		rLocation.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		RequestItem itemLocation = new RequestItem();
		itemLocation.setResource(rLocation);
		itemLocation.setActions(actions);
		itemLocation.setConditions(conditions);


		/*
		 * status requestItem
		 */

		Resource rStatus = new Resource();
		rStatus.setScheme(DataIdentifierScheme.CONTEXT);
		rStatus.setDataType(CtxAttributeTypes.STATUS);

		RequestItem itemStatus = new RequestItem();
		itemStatus.setResource(rStatus);
		itemStatus.setActions(actions);
		itemStatus.setConditions(conditions);

		/* ----------------------------------------------------*/

		

		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(itemLocation);
		requests.add(itemStatus);
		requests.add(itemStatus);
		requests.add(itemStatus);

		RequestPolicy policy = new RequestPolicy();
		policy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
		policy.setRequestItems(requests);
		
		policy.setRequestor(requestorServiceBean);

		return policy;
	}
	
	public static ResponsePolicy getResponsePolicy(){
		RequestPolicy requestPolicy = getRequestPolicy();
		
		ResponsePolicy policy = new ResponsePolicy();
		
		List<RequestItem> requestItems = requestPolicy.getRequestItems();
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		for (RequestItem item : requestItems){
			Action action = new Action();
			action.setActionConstant(ActionConstants.READ);
			item.getActions().remove(action);
			ResponseItem respItem = new ResponseItem();
			
			for (Condition c : item.getConditions()){
				if (c.getConditionConstant().equals(ConditionConstants.DATA_RETENTION)){
					c.setValue(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[2]);		
				}else{
					c.setValue(PrivacyConditionsConstantValues.getValues(c.getConditionConstant())[1]);
				}
			}
			
			
			respItem.setRequestItem(item);
			if (respItem.getRequestItem().getResource().getDataType().equals(CtxAttributeTypes.STATUS)){
				respItem.setDecision(Decision.DENY);
			}
			
			if (respItem.getRequestItem().getResource().getDataType().equals(CtxAttributeTypes.STATUS)){
				respItem.setDecision(Decision.DENY);
			}
			//System.out.println("adding item: "+item.toString());
			responseItems.add(respItem);
		}
	
		
		policy.setNegotiationStatus(NegotiationStatus.ONGOING);
		policy.setRequestor(requestPolicy.getRequestor());
		policy.setResponseItems(responseItems);
		return policy;
	}
	

	public static Agreement getAgreement(){
		Agreement agreement = new Agreement();
		ResponsePolicy policy = getResponsePolicy();
		agreement.setRequestedItems(policy.getResponseItems());
		agreement.setRequestor(policy.getRequestor());
		return agreement;
	}
}
