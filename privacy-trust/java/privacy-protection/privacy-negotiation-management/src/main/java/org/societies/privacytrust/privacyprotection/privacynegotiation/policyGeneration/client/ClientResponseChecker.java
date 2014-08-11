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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.DecisionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponsePolicyUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.PPNWindow;



/**
 * @author Elizabeth
 * @author Olivier Maridat (Trialog)
 *
 */
public class ClientResponseChecker {
	private static Logger LOG = LoggerFactory.getLogger(ClientResponseChecker.class);
	private PrivacyPolicyNegotiationManager negotiationManager;

	public ClientResponseChecker(PrivacyPolicyNegotiationManager negotiationManager){
		this.negotiationManager = negotiationManager;

	}

	/**
	 * Check that the provided ResponsePolicy (inferred by privacy preferences, and approved by the user)
	 * match the existing ResponsePolicy based on the original RequestPolicy
	 * All mandatory fields should at least be in the provider ResponsePolicy
	 * And all mandatory actions and conditions should be in provided RequestItem
	 * @param myPolicy
	 * @param providerPolicy
	 * @return True if provided and requested ResponsePolicy match
	 */
	public ResponsePolicy checkResponse(ResponsePolicy myPolicy, ResponsePolicy providerPolicy){
		// -- Empty requested ResponsePolicy
		if (null == myPolicy || null == myPolicy.getResponseItems() || myPolicy.getResponseItems().size() <= 0) {
			LOG.info("Empty requested policy");
			return null;
		}
		// -- Empty provider ResponsePolicy
		if ((null == providerPolicy || null == providerPolicy.getResponseItems() || providerPolicy.getResponseItems().size() <= 0)
				&& !ResponsePolicyUtils.hasOptionalResponseItemsOnly(myPolicy)) {
			LOG.info("Empty provided policy and requested policy not completely optional");
			return null;
		}
		Hashtable<ResponseItem, ResponseItem> table = new Hashtable<ResponseItem, ResponseItem>();

		//align user's response item to provider's request item
		for (ResponseItem myItem : myPolicy.getResponseItems()){
			for (ResponseItem providerItem : providerPolicy.getResponseItems()){
				if (myItem.getRequestItem().getResource().getDataType().equalsIgnoreCase(providerItem.getRequestItem().getResource().getDataType())){
					table.put(myItem, providerItem);
				}
			}
		}

		List<ResponseItem> itemsNotMatching = new ArrayList<ResponseItem>();
		// -- Check every ResponseItems		
		for (ResponseItem myItem : myPolicy.getResponseItems()){
			LOG.info("Requested item \""+myItem.getRequestItem().getResource().getDataType()+"\"...");


			ResponseItem providerItem = table.get(myItem);

			if (myItem.getDecision().equals(Decision.DENY)){
				if (providerItem!=null){
					return null;
				}
			}

			if (providerItem==null){
				JOptionPane.showMessageDialog(null, "provider is null");
			}
			if (myItem.getRequestItem()==null){
				JOptionPane.showMessageDialog(null, "myItem.getRequestItem(); is null");
			}
			if (providerItem.getRequestItem()==null){
				JOptionPane.showMessageDialog(null, "provideritem.getRequestItem(); is null");
			}
			
			
			if (actionsMatch(myItem.getRequestItem().getActions(), providerItem.getRequestItem().getActions())){
				if (!(conditionsMatch(myItem.getRequestItem().getConditions(), providerItem.getRequestItem().getConditions()))){
					itemsNotMatching.add(providerItem);
				}
			}else{
				itemsNotMatching.add(providerItem);
			}
		}
		if (itemsNotMatching.size()==0){

			for (ResponseItem item: providerPolicy.getResponseItems()){
				item.setDecision(Decision.PERMIT);
			}
			providerPolicy.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);
			return providerPolicy;
		}

		RequestPolicy tempPolicy = new RequestPolicy();
		tempPolicy.setRequestor(myPolicy.getRequestor());
		tempPolicy.setRequestItems(new ArrayList<RequestItem>());
		for (ResponseItem item : itemsNotMatching){
			tempPolicy.getRequestItems().add(item.getRequestItem());
		}


		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences = negotiationManager.getPrivacyPreferenceManager().evaluatePPNPreferences(tempPolicy);

		NegotiationDetailsBean negDetails = new NegotiationDetailsBean();

		negDetails.setRequestor(myPolicy.getRequestor());
		StringBuilder sb = new StringBuilder();
		for (ResponseItem item: itemsNotMatching){
			sb.append(item.getRequestItem().getResource().getDataType());
			sb.append(", ");
		}
		try{
			sb.delete(sb.lastIndexOf(", "), sb.length());
		}catch(Exception e){
			e.printStackTrace();
		}
		String message = "<html>The terms and conditions you requested for the data items: "+sb.toString()+" from the provider were not entirely acceptable. The provider has suggested alternatives. If you accept the alternatives provided, you can continue to install the service. Otherwise, the negotiation will fail. </html>";
		PPNWindow window = new PPNWindow(negDetails, evaluatePPNPreferences, false, message);
		ResponsePolicy userResponsePolicy = window.getResponsePolicy();
		for (ResponseItem item : userResponsePolicy.getResponseItems()){
			if (item.getDecision().equals(Decision.DENY)){
				return null;
			}
		}


		for (ResponseItem item: providerPolicy.getResponseItems()){
			item.setDecision(Decision.PERMIT);
		}
		providerPolicy.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);
		return providerPolicy;
	}




	private boolean conditionsMatch(List<Condition> myConditions,	List<Condition> providerConditions) {
		Hashtable<Condition, Condition> table = new Hashtable<Condition, Condition>();

		for (Condition myCondition: myConditions){
			for (Condition providerCondition : providerConditions){
				if (myCondition.getConditionConstant().equals(providerCondition.getConditionConstant())){
					table.put(myCondition, providerCondition);
					break;
				}
			}
		}

		Enumeration<Condition> keys = table.keys();

		while (keys.hasMoreElements()){
			Condition myCondition = keys.nextElement();
			Condition providerCondition = table.get(myCondition);
			if (!myCondition.getValue().equals(providerCondition.getValue())){
				return false;
			}
		}

		return true;
	}


	private boolean actionsMatch(List<Action> myActions, List<Action> providerActions) {
		for (Action providerAction : providerActions){
			boolean found = false;
			for (Action myAction : myActions){
				if (myAction.getActionConstant().equals(providerAction.getActionConstant())){
					found = true;
				}
			}
			if (!found){
				return false;
			}
		}

		return true;
	}



}
