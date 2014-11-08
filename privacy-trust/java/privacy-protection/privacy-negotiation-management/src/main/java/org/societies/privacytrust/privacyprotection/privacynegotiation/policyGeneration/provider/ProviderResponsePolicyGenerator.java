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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider;

import gate.gui.NewResourceDialog;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyRegistryManager;
import org.societies.privacytrust.privacyprotection.api.policy.BooleanRange;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.api.policy.DataRetentionRange;
import org.societies.privacytrust.privacyprotection.api.policy.ShareDataRange;


/**
 * @author Elizabeth
 *
 */
public class ProviderResponsePolicyGenerator {
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private IPrivacyPolicyRegistryManager registryManager;
	private NegotiationStatus negStatus;
	//private IFeedbackMgmt feedbackMgr;

	public ProviderResponsePolicyGenerator(IPrivacyPolicyRegistryManager registryManager){
		negStatus = NegotiationStatus.SUCCESSFUL;
		this.registryManager = registryManager;
	}

	private ResponsePolicy failNegotiation(RequestPolicy myPolicy){
		ResponsePolicy toReturn = new ResponsePolicy();
		toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
		toReturn.setRequestor(myPolicy.getRequestor());
		toReturn.setResponseItems(new ArrayList<ResponseItem>());
		return toReturn;
	}
	/**
	 * TODO urgent: fix this method that didn't accept to deny optional RequestItem, Action or Condition!!!
	 * @param clientResponse
	 * @param myPolicy
	 * @return
	 */
	public ResponsePolicy generateResponse(ResponsePolicy clientResponse, RequestPolicy myPolicy){
		Hashtable<String,ConditionRanges> conditionRanges = new Hashtable<String, ConditionRanges>();
		try {
			conditionRanges = this.registryManager.getConditionRanges(myPolicy.getRequestor());
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ResponsePolicy editedResponsePolicy = new ResponsePolicy();
		editedResponsePolicy.setRequestor(RequestorUtils.copyOf(myPolicy.getRequestor()));
		
		if (clientResponse.getNegotiationStatus().equals(NegotiationStatus.FAILED)){
			this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor()));
			return failNegotiation(myPolicy);
		}

		Hashtable<ResponseItem, RequestItem> table = new Hashtable<ResponseItem, RequestItem>();

		//align user's response item to provider's request item
		for (ResponseItem responseItem : clientResponse.getResponseItems()){
			for (RequestItem rItem : myPolicy.getRequestItems()){
				if (responseItem.getRequestItem().getResource().getDataType().equalsIgnoreCase(rItem.getResource().getDataType())){
					table.put(ResponseItemUtils.copyOf(responseItem), RequestItemUtils.copyOf(rItem));
				}
			}
		}

		//init list for holding edited response items;
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		//if the response policy doesn't contain all the items in the request policy, fail the negotiation
		if (table.size()!=myPolicy.getRequestItems().size()){
			this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor())+" size of response items not equal to size of request items");
			return failNegotiation(myPolicy);
		}
		
		
		Enumeration<ResponseItem> respItems = table.keys();
		//for each response item
		while (respItems.hasMoreElements()){
			ResponseItem responseItem = respItems.nextElement();
			RequestItem requestItem = table.get(responseItem);
			
			//check that we have ranges for this item
			if (!conditionRanges.containsKey(requestItem.getResource().getDataType())){
				this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor())+" could not find condition ranges for data type: "+requestItem.getResource().getDataType());
				return failNegotiation(myPolicy);
			}
			ConditionRanges ranges = conditionRanges.get(requestItem.getResource().getDataType());
			//if user's Decision is not DENY
			if (!responseItem.getDecision().equals(Decision.DENY)) {
				//check that user's action list is acceptable, returns null if the actions are not acceptable
				
				ResponseItem newResponseItem = getSatisfiableActions(requestItem, responseItem);
				
				if (newResponseItem!=null) {
					newResponseItem.setDecision(responseItem.getDecision());
					newResponseItem = getSatisfiableConditions(newResponseItem, requestItem, responseItem, myPolicy.getRequestor(), ranges);
					
					if (newResponseItem==null){
						this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor())+" conditions could not be satisfied");						
						return failNegotiation(myPolicy);
					}
				} else {
					this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor())+" actions request could not be satisfied");
					return failNegotiation(myPolicy);
				}
				
				responseItems.add(newResponseItem);
				this.logging.debug("Adding responseItem to ResponsePolicy: "+ResponseItemUtils.toString(newResponseItem));
			}else{//if resource is not optional, fail the negotiation, otherwise do not include the item in the editedResponsePolicy
				if (!requestItem.isOptional()){
					this.logging.debug("Negotiation FAILED for: "+RequestorUtils.toXmlString(myPolicy.getRequestor())+" denied access to non-optional resource");
					return failNegotiation(myPolicy);
				}
			}
		}
		editedResponsePolicy.setNegotiationStatus(negStatus);
		editedResponsePolicy.setResponseItems(responseItems);
		return editedResponsePolicy;

	}

	private ResponseItem getSatisfiableConditions(ResponseItem toEditResponseItem, RequestItem requestItem,
			ResponseItem responseItem, RequestorBean requestor, ConditionRanges conditionRanges) {
		List<Condition> conditions = new ArrayList<Condition>();

		this.logging.debug("getSatisfiableConditions for "+requestItem.getResource().getDataType());


		if (conditionRanges==null){
			return null;
		}
		List<Condition> requestedConditions = responseItem.getRequestItem().getConditions();


		Hashtable<Condition,Condition> userProviderConditionsTable = new Hashtable<Condition, Condition>();
		for (Condition userCondition : requestedConditions){
			for (Condition providerCondition: requestItem.getConditions()){
				if (userCondition.getConditionConstant().equals(providerCondition.getConditionConstant())){
					userProviderConditionsTable.put(userCondition, providerCondition);
				}
			}
		}

		Enumeration<Condition> keys = userProviderConditionsTable.keys();
		while (keys.hasMoreElements()){
			Condition userCondition = keys.nextElement();
			switch (userCondition.getConditionConstant()){
			case DATA_RETENTION:
				DataRetentionRange dataRetentionRange = conditionRanges.getDataRetentionRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+dataRetentionRange.getValue1()+" - "+dataRetentionRange.getValue2());
				if (dataRetentionRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
					
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(dataRetentionRange.getBestDataRetentionAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case MAY_BE_INFERRED:
				BooleanRange mayInferBooleanRange = conditionRanges.getMayInferBooleanRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+mayInferBooleanRange.getValue1()+" - "+mayInferBooleanRange.getValue2());
				if (mayInferBooleanRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(mayInferBooleanRange.getBestConditionValueAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case RIGHT_TO_ACCESS_HELD_DATA:
				BooleanRange accessHeldBooleanRange = conditionRanges.getAccessHeldBooleanRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+accessHeldBooleanRange.getValue1()+" - "+accessHeldBooleanRange.getValue2());
				if (accessHeldBooleanRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(accessHeldBooleanRange.getBestConditionValueAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				BooleanRange correctDataBooleanRange = conditionRanges.getCorrectDataBooleanRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+correctDataBooleanRange.getValue1()+" - "+correctDataBooleanRange.getValue2());				
				if (correctDataBooleanRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(correctDataBooleanRange.getBestConditionValueAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case RIGHT_TO_OPTOUT:
				BooleanRange rightToOptOutBooleanRange = conditionRanges.getRightToOptOutBooleanRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+rightToOptOutBooleanRange.getValue1()+" - "+rightToOptOutBooleanRange.getValue2());				
				if (rightToOptOutBooleanRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(rightToOptOutBooleanRange.getBestConditionValueAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case SHARE_WITH_3RD_PARTIES:
				ShareDataRange shareDataRange = conditionRanges.getShareDataRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+shareDataRange.getValue1()+" - "+shareDataRange.getValue2());
				if (shareDataRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(shareDataRange.getBestShareDataAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;
			case STORE_IN_SECURE_STORAGE:
				BooleanRange storeSecureBooleanRange = conditionRanges.getStoreSecureBooleanRange();
				logging.debug(userCondition.getConditionConstant()+" user request: "+userCondition.getValue()+" my accepted range: "+storeSecureBooleanRange.getValue1()+" - "+storeSecureBooleanRange.getValue2());
				if (storeSecureBooleanRange.isInRange(userCondition.getValue())){
					conditions.add(userCondition);
				}else {
					Condition condition = new Condition();
					condition.setConditionConstant(userCondition.getConditionConstant());
					condition.setValue(storeSecureBooleanRange.getBestConditionValueAvailable());
					conditions.add(condition);
					toEditResponseItem.setDecision(Decision.INDETERMINATE);
					this.negStatus = NegotiationStatus.ONGOING;
				}
				break;

			}
		}


		toEditResponseItem.getRequestItem().setConditions(conditions);
		
		return toEditResponseItem;
	}



	private ResponseItem getSatisfiableActions(RequestItem reqItem, ResponseItem respItem) {
		RequestItem requestItem = new RequestItem();
		requestItem.setActions(respItem.getRequestItem().getActions());
		requestItem.setResource(reqItem.getResource());
		requestItem.setPurpose(reqItem.getPurpose());
		ResponseItem responseItem = new ResponseItem();
		responseItem.setRequestItem(requestItem);
		return responseItem;
/*		boolean different = false;
		List<Action> myActions = reqItem.getActions();
		List<Action> requestedActions = respItem.getRequestItem().getActions();
		List<Action> missingActions = new ArrayList<Action>();
		for (Action myAction: myActions){
			boolean found = false;
			for (Action requestedAction : requestedActions){
				if (myAction.getActionConstant().equals(requestedAction.getActionConstant())){
					found = true;
				}
			}
			if (!found){
				missingActions.add(myAction);
				different = true;
			}
		}

		boolean satisfied = true;
		for(Action action : missingActions){

			if (!action.isOptional()){
				satisfied = false;
			}
		}

		if (satisfied){
			RequestItem requestItem = new RequestItem();
			requestItem.setActions(requestedActions);
			requestItem.setResource(reqItem.getResource());
			requestItem.setPurpose(reqItem.getPurpose());
			ResponseItem responseItem = new ResponseItem();
			responseItem.setRequestItem(requestItem);
			if (different){
				responseItem.setDecision(Decision.INDETERMINATE);
				
			}
			return responseItem;
		}

		return null;*/
	}

	/*	public ResponsePolicy generateResponse(ResponsePolicy clientResponse, RequestPolicy myPolicy){
		if (clientResponse.getNegotiationStatus().equals(NegotiationStatus.FAILED)){
			//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 1");
			ResponsePolicy toReturn = new ResponsePolicy();
			toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
			toReturn.setRequestor(myPolicy.getRequestor());
			toReturn.setResponseItems(new ArrayList<ResponseItem>());
			return toReturn;
		}


	 * Algorithm: 
	 * for every response Item in the policy:
	 * IF Decision.PERMIT 
	 * 		then leave as is
	 * else IF Decision.DENY OR Decision.NOT_APPLICABLE
	 * 		IF responseItem.getRequestItem.isOptional()
	 * 			remove from ResponsePolicy
	 * 		ELSE
	 * 			set NegotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems  list
	 * else IF Decision.INDETERMINATE 
	 * 		a) compare list of actions from client and list of actions from my policy
	 * 			IF action exists in my policy but not in client list
	 * 				IF Action.isOptional()
	 * 					leave as is
	 * 				ELSE 
	 * 					set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
	 * 		b) compare list of conditions from client and list of conditions from my policy
	 * 
	 * 			IF condition exists in client but not in my policy
	 * 				use FeedbackGUI /later use preferences
	 * 				IF feedback
	 * 					leave as is
	 * 				ELSE
	 * 					set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
	 * 			ELSE
	 * 				IF condition exists in my policy but not in client:
	 * 					IF condition.isOptional()
	 * 						leave as is
	 * 					ELSE
	 * 						use FeedbackGUI /later use preferences
	 * 						IF feedback
	 * 							leave as is
	 * 						ELSE
	 * 						set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
	 * 			ELSE 
	 * 				IF condition exists in both policies but the value is different
	 * 					use FeedbackGUI /later use preferences
	 * 					IF feedback
	 * 						leave as is
	 * 					ELSE
	 * 						set negotiationStatus.FAILED and return ResponsePolicy with empty ResponseItems list
	 * 			ELSE
	 * 				IF condition exists in both policies and values are the same
	 * 					leave as is			 


		List<ResponseItem> clientResponseItems = clientResponse.getResponseItems();
		List<ResponseItem> itemsToRemove = new ArrayList<ResponseItem>();
		for (ResponseItem responseItem : clientResponseItems){
			if (null == responseItem.getDecision() || responseItem.getDecision().equals(Decision.DENY)  || responseItem.getDecision().equals(Decision.NOT_APPLICABLE)){
				if (responseItem.getRequestItem().isOptional()){
					//clientResponseItems.remove(responseItem);
					itemsToRemove.add(responseItem);
				}else{
					//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 2");
					ResponsePolicy toReturn = new ResponsePolicy();
					toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
					toReturn.setRequestor(myPolicy.getRequestor());
					toReturn.setResponseItems(new ArrayList<ResponseItem>());
					return toReturn;
				}
			}else if (responseItem.getDecision().equals(Decision.INDETERMINATE)){
				Resource resource = responseItem.getRequestItem().getResource(); 
				List<RequestItem> myRequests = myPolicy.getRequestItems();
				RequestItem myRequest = null;
				//get the Actions I have stated in my service privacy policy for this particular resource 
				for (RequestItem item : myRequests){
					if (item.getResource().getDataType().equals(resource.getDataType())){
						myRequest = item;
					}
				}
				if (myRequest!=null){
					List<Action> myActions = myRequest.getActions();

					List<Action> clientActions = responseItem.getRequestItem().getActions();
					//COMPARE ACTIONS * START *
					for (Action action : myActions){ 
						if (!(containsAction(clientActions,action))){
							if (!(action.isOptional())){
								//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 3");
								ResponsePolicy toReturn = new ResponsePolicy();
								toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
								toReturn.setRequestor(myPolicy.getRequestor());
								toReturn.setResponseItems(new ArrayList<ResponseItem>());
								return toReturn;
							}
						}
					}

					//COMPARE ACTIONS * END *


					//COMPARE CONDITIONS * START *
					List<Condition> clientConditions = responseItem.getRequestItem().getConditions();
					List<Condition> myConditions = myRequest.getConditions();

					//for every client condition
					for (Condition clientCondition : clientConditions){

						//check if the client condition exists in my conditions list
						Condition con = this.containsIgnoreValue(myConditions, clientCondition);
						//if condition exists in both policies
						if (con!=null){
							if (con.getValue().equalsIgnoreCase(clientCondition.getValue())){
								//value is the smae 
							}else{
								//check IF OPTIONAL
								if (clientCondition.isOptional()){
									//condition is optional so we can ignore it without bothering the user
								}
								else{
									//condition is mandatory so we're going to ask the user
									Hashtable<String,Object> params = new Hashtable<String,Object>();
									params.put("localPolicyDetails", con.getConditionConstant()+": "+con.getValue());
									params.put("remotePolicyDetails", clientCondition.getConditionConstant()+": "+clientCondition.getValue());
									//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
									//TODO: use rules - no user
									Boolean response = true;
									if (!response.booleanValue()){
										//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 4");
										ResponsePolicy toReturn = new ResponsePolicy();
										toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
										toReturn.setRequestor(myPolicy.getRequestor());
										toReturn.setResponseItems(new ArrayList<ResponseItem>());
										return toReturn;
									}
								}
							}
						}else{//condition only exists in client
							if (clientCondition.isOptional()){
								//condition is optional so we can ignore it without bothering the user
							}else{
								//condition is mandatory so we're going to ask the user
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("localPolicyDetails", "You have not included this condition in your policy");
								params.put("remotePolicyDetails",clientCondition.getConditionConstant()+": "+clientCondition.getValue());
								//TODO: use rules - no user
								Boolean response = true;
								//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
								if (!response){
									//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 5");
									ResponsePolicy toReturn = new ResponsePolicy();
									toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
									toReturn.setRequestor(myPolicy.getRequestor());
									toReturn.setResponseItems(new ArrayList<ResponseItem>());
									return toReturn;
								}
							}
						}
					}

					//now we're going to check if conditions exist in my policy and removed in the response
					for (Condition myCondition : myConditions){
						Condition clientCondition = this.containsIgnoreValue(clientConditions, myCondition);
						//if myCondition is not included in the 
						if (clientCondition==null){
							//if it's not optional ask the user
							if (!myCondition.isOptional()){
								Hashtable<String, Object> params = new Hashtable<String, Object>();
								params.put("localPolicyDetails",myCondition.getConditionConstant()+": "+myCondition.getValue());
								params.put("remotePolicyDetails", "The client has not included this condition in his policy");
								//TODO: use rules - no user
								Boolean response = true;
								//Boolean response = (Boolean) this.getFeedbackManager().getExplicitFB(FeedbackGUITypes.NEGOTIATION, params);
								if (!response.booleanValue()){
									//JOptionPane.showMessageDialog(null, "Provider: Negotiation Failed 6");
									ResponsePolicy toReturn = new ResponsePolicy();
									toReturn.setNegotiationStatus(NegotiationStatus.FAILED);
									toReturn.setRequestor(myPolicy.getRequestor());
									toReturn.setResponseItems(new ArrayList<ResponseItem>());
									return toReturn;
								}
							}
						}
					}

					//COMPARE CONDITIONS * END *
				}
			}
		}

		for (ResponseItem r : itemsToRemove){
			if (clientResponse.getResponseItems().contains(r)){
				clientResponse.getResponseItems().remove(r);
			}
		}
		clientResponse.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);


		return clientResponse;
	}*/

	private Condition containsIgnoreValue(List<Condition> list, Condition c){
		for (Condition con : list){
			if (c.getConditionConstant().equals(con.getConditionConstant())){
				return con;
			}
		}
		return null;
	}

	private boolean containsAction(List<Action> actions, Action a){
		for (Action action : actions){
			if (action.getActionConstant().equals(a.getActionConstant())){
				return true;
			}
		}

		return false;
	}

}
