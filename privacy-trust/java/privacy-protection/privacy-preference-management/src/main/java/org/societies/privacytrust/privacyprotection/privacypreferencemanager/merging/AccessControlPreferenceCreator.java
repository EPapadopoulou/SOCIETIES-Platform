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
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.AccessControlPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

/**
 * This class creates access control preferences for static (not sensed) context attributes
 * based on the result of the negotiation
 * @author Eliza
 * 
 */
public class AccessControlPreferenceCreator{

	private final IEventMgr eventMgr;
	private IPrivacyAgreementManager agreementMgr;
	private ICtxBroker ctxBroker;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IIdentityManager idMgr;
	private final AccessControlPreferenceManager accCtrlPrefMgr;
	private PrivacyPreferenceManager ppMgr;

	private final IIdentity userIdentity;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private String[] sensedDataTypes;


	public AccessControlPreferenceCreator(PrivacyPreferenceManager ppMgr){
		this.ppMgr = ppMgr;
		this.eventMgr = ppMgr.getEventMgr();
		this.agreementMgr = ppMgr.getAgreementMgr();
		this.ctxBroker = ppMgr.getCtxBroker();
		this.privacyDataManagerInternal = ppMgr.getprivacyDataManagerInternal();
		this.idMgr = ppMgr.getCommsMgr().getIdManager();
		this.accCtrlPrefMgr = ppMgr.getAccessControlPreferenceManager();
		this.userIdentity = idMgr.getThisNetworkNode();

		sensedDataTypes = new String[]{CtxAttributeTypes.TEMPERATURE, 
				CtxAttributeTypes.STATUS,
				CtxAttributeTypes.LOCATION_SYMBOLIC,
				CtxAttributeTypes.LOCATION_COORDINATES,
				CtxAttributeTypes.ACTION};
	}



	public void notifyNegotiationResult(final InternalEvent event) {
		new Thread() {

			public void run() {
				try{
					logging.debug("Received event: name: "+event.geteventName()+" - type: "+event.geteventType());
					if (event.geteventInfo() instanceof PPNegotiationEvent){
						PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();

						Agreement agreement = ppnEvent.getAgreement();
						logging.debug("Retrieved agreement from PPNegotiationEvent with "+agreement.getRequestedItems().size()+" requestItems");

						List<ResponseItem> responseItems = agreement.getRequestedItems();
						String dataTypes = "";
						for (ResponseItem item: responseItems){
							String dataType = item.getRequestItem().getResource().getDataType();
							dataTypes = dataTypes.concat(dataType+", ");
						}
						logging.debug("Agreement contains the following datatypes: "+dataTypes);
						for (ResponseItem tempItem: responseItems){
							ResponseItem item = ResponseItemUtils.copyOf(tempItem);
							String dataType = item.getRequestItem().getResource().getDataType();
							logging.debug("Processing datatype: "+dataType+" with scheme: "+item.getRequestItem().getResource().getScheme());
							if (item.getRequestItem().getResource().getScheme().equals(DataIdentifierScheme.CONTEXT)){
								try {
									CtxAttributeIdentifier ctxID = CtxIDChanger.changeIDOwner(userIdentity.getBareJid(),new CtxAttributeIdentifier(item.getRequestItem().getResource().getDataIdUri()));

									//here, AttributeSelection preferences should be used to select which attribute should be used for this requestor and selected identity
									//instead, we're going to create the preference for all returned CtxIDs if not sensed

									//added check in case the attribute is created manually and Quality and OriginType are null
									if(!isAttributeSensed(ctxID.getType())){
										//the following line should replace the preceding line when OriginType is properly handled.
										//if (ctxAttribute.getQuality()==null || ctxAttribute.getQuality().getOriginType()==null || ctxAttribute.getQuality().getOriginType().equals(CtxOriginType.MANUALLY_SET)){
										logging.debug("ctxID: "+ctxID.getUri()+" will get an accessControl preference that is not context dependent");
										ResponseItem copyOf = ResponseItemUtils.copyOf(item);
										copyOf.getRequestItem().getResource().setDataIdUri(ctxID.getUri());
										processThisResource(agreement.getRequestor(), copyOf);
										//}
									}

								} catch (CtxException e) {
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
									processThisResource(agreement.getRequestor(), item);
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
					
					
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}}.start();

	}


	private boolean isAttributeSensed(String type) {

		for (String sensedType : sensedDataTypes){
			if (sensedType.equalsIgnoreCase(type)){
				return true;
			}
		}

		return false;
	}
	private void processThisResource(RequestorBean requestor, ResponseItem item) throws PrivacyException, InvalidFormatException {

		for (Action action : item.getRequestItem().getActions()){

			AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
			details.setAction(action);
			details.setRequestor(requestor);
			details.setResource(item.getRequestItem().getResource());
			this.logging.debug("Creating an accessControl preference for: \n"+PrivacyPreferenceUtils.toString(details));
			AccessControlPreferenceTreeModel accCtrlPreference = this.accCtrlPrefMgr.getAccCtrlPreference(details);

			if (null!=accCtrlPreference){
				this.logging.debug("Merging! An accessControl preference already exists for these details:\n"+PrivacyPreferenceUtils.toString(details));

				PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(this.ppMgr);
				PrivacyPreference mergePreferences = merger.mergeAccCtrlPreference(details, accCtrlPreference.getPref(), this.createAccCtrlPreference(item, details).getPref());
				if (mergePreferences!=null){
					AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(details, mergePreferences);
					this.accCtrlPrefMgr.storeAccCtrlPreference(details, model);
				}
			}else{
				this.logging.debug("Creating NEW preference for these details:\n"+PrivacyPreferenceUtils.toString(details));
				AccessControlPreferenceTreeModel createAccCtrlPreference = this.createAccCtrlPreference(item, details);
				this.logging.debug("Created new preference: "+createAccCtrlPreference.toString());
				this.accCtrlPrefMgr.storeAccCtrlPreference(details, createAccCtrlPreference);
			}
		}

		//this.privacyDataManagerInternal.updatePermission(requestor, item);

	}
	private AccessControlPreferenceTreeModel createAccCtrlPreference(
			ResponseItem item, AccessControlPreferenceDetailsBean details) {
		//PrivacyPreference preference;
		AccessControlOutcome outcome;
		if (item.getDecision().equals(Decision.PERMIT)){
			outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
		}else{
			outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);
		}

		/*		List<Condition> conditions = item.getRequestItem().getConditions();
		preference = createConditionPreferences(conditions, new PrivacyPreference(outcome)); 
		return new AccessControlPreferenceTreeModel(details, preference);*/
		return new AccessControlPreferenceTreeModel(details, new PrivacyPreference(outcome));

	}
	private PrivacyPreference createConditionPreferences(
			List<Condition> conditions, PrivacyPreference privacyPreference) {

		for (Condition condition : conditions){
			this.logging.debug("Adding condition to accCtrlPreference: "+ConditionUtils.toString(condition));
			PrivacyPreference preference = new PrivacyPreference(new PrivacyCondition(condition));
			preference.add(privacyPreference);
			privacyPreference = (PrivacyPreference) preference.getRoot();
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
