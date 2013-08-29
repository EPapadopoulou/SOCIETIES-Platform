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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.AccessControlPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

/**
 * This class creates access control preferences for static (not sensed) context attributes
 * based on the result of the negotiation
 * @author Eliza
 * 
 */
public class AccessControlPreferenceCreator extends EventListener{

	private final IEventMgr eventMgr;
	private IPrivacyAgreementManager agreementMgr;
	private ICtxBroker ctxBroker;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IIdentityManager idMgr;
	private final AccessControlPreferenceManager accCtrlPrefMgr;
	private PrivacyPreferenceManager ppMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	
	public AccessControlPreferenceCreator(PrivacyPreferenceManager ppMgr){
		this.ppMgr = ppMgr;
		this.eventMgr = ppMgr.getEventMgr();
		this.agreementMgr = ppMgr.getAgreementMgr();
		this.ctxBroker = ppMgr.getCtxBroker();
		this.privacyDataManagerInternal = ppMgr.getprivacyDataManagerInternal();
		this.idMgr = ppMgr.getCommsMgr().getIdManager();
		this.accCtrlPrefMgr = ppMgr.getAccessControlPreferenceManager();
		try{
			this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, "");
		}catch(Exception e){
			System.out.println("could not subscribe to event: "+EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT);
		}

	}
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInternalEvent(InternalEvent event) {

		if (event.geteventInfo() instanceof PPNegotiationEvent){
			PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();
			IAgreement agreementObj = ppnEvent.getAgreement();
			Agreement agreement = AgreementUtils.toAgreementBean(agreementObj);

			List<ResponseItem> responseItems = agreement.getRequestedItems();
			for (ResponseItem item: responseItems){
				String dataType = item.getRequestItem().getResource().getDataType();
				if (item.getRequestItem().getResource().getScheme().equals(DataIdentifierScheme.CONTEXT)){
					try {
						if (ctxBroker==null){
							this.logging.debug("broker null");
						}
						List<CtxIdentifier> ctxIDList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, dataType).get();
						if (ctxIDList.size()==0){
							if (containsCreateAction(item.getRequestItem().getActions())){
								this.privacyDataManagerInternal.updatePermission(RequestorUtils.toRequestor(agreement.getRequestor(), this.idMgr), ResponseItemUtils.toResponseItem(item));

							}

						}else{
							//here, AttributeSelection preferences should be used to select which attribute should be used for this requestor and selected identity
							//instead, we're going to create the preference for all returned CtxIDs if not sensed
							for (CtxIdentifier ctxID : ctxIDList){
								CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(ctxID).get();
								if (ctxAttribute!=null){
									if (ctxAttribute.getQuality().getOriginType().equals(CtxOriginType.MANUALLY_SET)){
										this.processThisResource(agreement.getRequestor(), item);
									}
								}
							}

						}
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (PrivacyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					try {
						this.processThisResource(agreement.getRequestor(), item);
					} catch (PrivacyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

	}


	private void processThisResource(RequestorBean requestor, ResponseItem item) throws PrivacyException, InvalidFormatException {
		for (Action action : item.getRequestItem().getActions()){
			AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
			AccessControlPreferenceTreeModel accCtrlPreference = this.accCtrlPrefMgr.getAccCtrlPreference(details);
			if (null!=accCtrlPreference){
				PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(this.ctxBroker, this.ppMgr);
				IPrivacyPreference mergePreferences = merger.mergeAccCtrlPreference(details, accCtrlPreference.getPref(), this.createAccCtrlPreference(item, details).getPref());
				if (mergePreferences!=null){
					AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(details, mergePreferences);
					this.accCtrlPrefMgr.storeAccCtrlPreference(details, model);
				}
			}else{
				this.accCtrlPrefMgr.storeAccCtrlPreference(details, this.createAccCtrlPreference(item, details));
			}
		}
		this.privacyDataManagerInternal.updatePermission(RequestorUtils.toRequestor(requestor, this.idMgr), ResponseItemUtils.toResponseItem(item));

	}
	private AccessControlPreferenceTreeModel createAccCtrlPreference(
			ResponseItem item, AccessControlPreferenceDetailsBean details) {
		PrivacyPreference preference;
		AccessControlOutcome outcome;
		if (item.getDecision().equals(Decision.PERMIT)){
			outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
		}else{
			outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);
		}

		List<Condition> conditions = item.getRequestItem().getConditions();
		preference = createConditionPreferences(conditions, new PrivacyPreference(outcome)); 
		return new AccessControlPreferenceTreeModel(details, preference);

	}
	private PrivacyPreference createConditionPreferences(
			List<Condition> conditions, PrivacyPreference privacyPreference) {
		for (Condition condition : conditions){
			PrivacyPreference preference = new PrivacyPreference(new PrivacyCondition(condition));
			preference.add(privacyPreference);
			privacyPreference = preference;
		}
		return privacyPreference;
	}
	private boolean containsCreateAction(List<Action> actions){
		for (Action action : actions){
			if (action.getActionConstant().equals(ActionConstants.CREATE)){
				return true;
			}
		}
		return false;
	}

	/*	public static void main(String[] args){
		Condition condition = new Condition();
		condition.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		condition.setValue("");

		Condition condition2 = new Condition();
		condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
		condition2.setValue("");

		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(condition);
		conditions.add(condition2);

		PrivacyPreference privacyPreference = new PrivacyPreference(new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW));
		PrivacyPreference pref = new AccessControlPreferenceCreator(null, null, null, null, null, null).createConditionPreferences(conditions, privacyPreference);
		System.out.println(pref.toString());
	}*/
}
