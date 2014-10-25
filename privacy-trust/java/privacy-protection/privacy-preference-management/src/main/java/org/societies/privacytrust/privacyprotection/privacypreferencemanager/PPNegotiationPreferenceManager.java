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
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache.PreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.PrivacyPreferenceConditionMonitor;

/**
 * @author Eliza
 *
 */
public class PPNegotiationPreferenceManager {

	private PreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final PrivacyPreferenceConditionMonitor privacyPCM;
	private final ITrustBroker trustBroker;
	private IIdentity userIdentity;

	public PPNegotiationPreferenceManager(PreferenceCache prefCache, PrivateContextCache contextCache, PrivacyPreferenceConditionMonitor privacyPCM, ITrustBroker trustBroker, IIdentity userIdentity){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.privacyPCM = privacyPCM;
		this.trustBroker = trustBroker;
		this.userIdentity = userIdentity;



	}


	public HashMap<RequestItem,ResponseItem> evaluatePPNPreferences(RequestPolicy requestPolicy) throws PrivacyException{

		HashMap<RequestItem, ResponseItem> result = new HashMap<RequestItem, ResponseItem>();
		List<RequestItem> requestItems = requestPolicy.getRequestItems();
		for (RequestItem item : requestItems){
			ResponseItem evaluatedResponseItem = null;
			List<Condition> conditions = new ArrayList<Condition>();
			//for every Condition in the RequestItem;
			for (Condition condition : item.getConditions()){
				this.logging.debug("Finding preference for resource: "+item.getResource().getDataType());
				PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
				details.setRequestor(requestPolicy.getRequestor());
				details.setResource(item.getResource());
				details.setCondition(condition.getConditionConstant());
				
				PPNPrivacyPreferenceTreeModel model = this.prefCache.getPPNPreference(details);

				if (model!=null){
					this.logging.debug("Found PPN model for resource: "+item.getResource().getDataType()+" and condition: "+condition.getConditionConstant());
					PreferenceEvaluator ppE = new PreferenceEvaluator(contextCache, trustBroker, requestPolicy.getRequestor(), this.userIdentity );
					Condition evaluatedCondition = ppE.evaluatePPNPreferences(model);
					if (evaluatedCondition==null){
						this.logging.debug("PPN evaluation returned null for resource : "+item.getResource().getDataType()+" and condition: "+condition.getConditionConstant()+" and specific requestor "+RequestorUtils.toString(requestPolicy.getRequestor()));
					}
					else{
						conditions.add(evaluatedCondition);
					}
				}else{
					this.logging.debug("Not found for resource : "+item.getResource().getDataType()+" and condition: "+condition.getConditionConstant()+" and specific requestor, attempting to find generic preference");
					details.setRequestor(null);
					model  = this.prefCache.getPPNPreference(details);
					if (model==null){
						this.logging.debug("Not found any generic ppn preference for resource: "+item.getResource().getDataType()+" and condition: "+condition.getConditionConstant());

					}else{
						this.logging.debug("Found generic ppn preference for resource: "+item.getResource().getDataType());
						PreferenceEvaluator ppE = new PreferenceEvaluator(contextCache, trustBroker, requestPolicy.getRequestor(), this.userIdentity);
						Condition evaluatedCondition = ppE.evaluatePPNPreferences(model);
						if (evaluatedCondition==null){
							this.logging.debug("PPN evaluation returned null for resource : "+item.getResource().getDataType()+" and condition: "+condition.getConditionConstant()+" and generic requestor");
						}else{
							conditions.add(evaluatedCondition);
						}
					}
				}
			}
			//if found at least one condition, create a responseItem object and add it
			if (!conditions.isEmpty()){
				evaluatedResponseItem = new ResponseItem();
				RequestItem newItem = RequestItemUtils.copyOf(item);
				newItem.setConditions(conditions);
				evaluatedResponseItem.setRequestItem(newItem);
			}
			//we add the evaluatedResponseItem in the table even though it might be null. if it's null that means there is no preference information available
			result.put(item, evaluatedResponseItem);

		}

		this.logging.debug("Returning evaluatedPreferences size: "+result.size());
		return result;


	}


	/**
	 * new methods;
	 */

	public boolean deletePPNPreference(PPNPreferenceDetailsBean details) {
		return 	this.prefCache.removePPNPreference(details);


	}

	public PPNPrivacyPreferenceTreeModel getPPNPreference(
			PPNPreferenceDetailsBean details) {
		return this.prefCache.getPPNPreference(details);
	}

	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails() {
		return this.prefCache.getPPNPreferenceDetails();
	}

	public boolean storePPNPreference(PPNPreferenceDetailsBean details,
			PPNPrivacyPreferenceTreeModel model) throws PrivacyException {

		if (model.getDetails().equals(details)){
			this.logging.debug("Request to add preference :\n"+details.toString());

			if (this.prefCache.addPPNPreference(details, model)){
				return true;
			}else{
				throw new PrivacyException("Error storing PPN preference");

			}
		}	

		throw new PrivacyException("PPNPreferenceDetailsBean parameter did not match PPNPrivacyPreferenceTreeModel.getDetails()");		


	}


	public boolean deletePPNPreferences() {
		return this.prefCache.removePPNPreferences();

	}
}
