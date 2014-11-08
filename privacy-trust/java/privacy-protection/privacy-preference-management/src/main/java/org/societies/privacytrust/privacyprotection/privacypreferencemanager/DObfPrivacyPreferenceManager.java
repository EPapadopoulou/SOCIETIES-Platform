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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
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
public class DObfPrivacyPreferenceManager {


	private final IIdentity userIdentity;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private PrivacyPreferenceManager privPrefMgr;

	
	public DObfPrivacyPreferenceManager(PrivacyPreferenceManager privPrefMgr) {
		this.privPrefMgr = privPrefMgr;
		this.userIdentity = privPrefMgr.getIdm().getThisNetworkNode();
		
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
				logging.debug("Returning obfuscation level: {}", ((DObfOutcome) outcome).getObfuscationLevel());
				return ((DObfOutcome) outcome).getObfuscationLevel();
			}else{
				logging.debug("ERROR, IPrivacyOutcome not instanceof DobfOutcome");
				return -1;
			}
		}else{
			logging.debug("Did not find dobf preference: {}", details);
			return -1;
		}



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
				ctxAttributeIdentifier = CtxIDChanger.changeOwner(userIdentity.getBareJid(), ctxAttributeIdentifier);

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
			CtxIdentifier ctxId = CtxIDChanger.changeOwner(this.userIdentity.getBareJid(), (CtxAttributeIdentifier) ctxModelObject.getId());
			details.setResource(ResourceUtils.create(ctxId));
			Integer obfLevel = evaluateDObfPreference(details);
			if (obfLevel>=0){
				map.put(ctxModelObject, new Integer(obfLevel));
			}else{
				map.put(ctxModelObject, new Integer(0));
			}
		}

		logging.debug("getObfuscationLevel result {}",map);
		return map;
	}

}
