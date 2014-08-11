/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacynegotiation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyRegistryManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentityOption;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.api.policy.BooleanRange;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.api.policy.DataRetentionRange;
import org.societies.privacytrust.privacyprotection.api.policy.ShareDataRange;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.provider.NegotiationAgent;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.MockRequestPolicy;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author PUMA
 *
 */
public class TestIdentitySelection {



	private PrivacyPolicyNegotiationManager privacyPolicyNegotiationManager;
	private NegotiationDetailsBean details;
	private ICommManager commsMgr;
	private ICtxBroker ctxBroker;
	private IIdentitySelection identitySelection;
	private NegotiationAgent negotiationAgent;
	private IIdentityManager idm;
	private MyIdentity userIdentity;
	private IndividualCtxEntity person;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private IPrivacyPolicyManagerInternal policyMgr;
	private IPrivacyPolicyRegistryManager policyRegistryManager;
	private Hashtable<String, ConditionRanges> conditionRangesTable;
	private RequestPolicy requestPolicy;

	@Before
	public void setup(){

		userIdentity = new MyIdentity(IdentityType.CSS, "eliza","societies.local2");

		privacyPolicyNegotiationManager = new PrivacyPolicyNegotiationManager();
		idm = Mockito.mock(IIdentityManager.class);
		commsMgr = Mockito.mock(ICommManager.class);		
		Mockito.when(idm.getThisNetworkNode()).thenReturn(userIdentity);
		Mockito.when(commsMgr.getIdManager()).thenReturn(idm);


		privacyPolicyNegotiationManager.setCommsMgr(commsMgr);

		ctxBroker = Mockito.mock(ICtxBroker.class);
		privacyPolicyNegotiationManager.setCtxBroker(ctxBroker);
		privacyPolicyNegotiationManager.setEventMgr(Mockito.mock(IEventMgr.class));
		identitySelection = Mockito.mock(IIdentitySelection.class);
		privacyPolicyNegotiationManager.setIdentitySelection(identitySelection);
		negotiationAgent = new NegotiationAgent();
		negotiationAgent.setCommsMgr(commsMgr);

		policyMgr = Mockito.mock(IPrivacyPolicyManagerInternal.class);
		negotiationAgent.setPolicyMgr(policyMgr);


		negotiationAgent.initialiseNegotiationAgent();
		policyRegistryManager = Mockito.mock(IPrivacyPolicyRegistryManager.class);

		privacyPolicyNegotiationManager.setNegotiationAgent(negotiationAgent);

		privacyPolicyNegotiationManager.setPrefMgr(Mockito.mock(IUserPreferenceManagement.class));
		privacyPolicyNegotiationManager.setPrivacyAgreementManagerInternal(Mockito.mock(IPrivacyAgreementManagerInternal.class));
		privacyPolicyNegotiationManager.setPrivacyDataManagerInternal(Mockito.mock(IPrivacyDataManagerInternal.class));
		privacyPolicyNegotiationManager.setPrivacyPolicyManager(policyMgr);
		privacyPreferenceManager = Mockito.mock(IPrivacyPreferenceManager.class);
		privacyPolicyNegotiationManager.setPrivacyPreferenceManager(privacyPreferenceManager);
		privacyPolicyNegotiationManager.setUserFeedback(Mockito.mock(IUserFeedback.class));


		setupMocks();
		privacyPolicyNegotiationManager.initialisePrivacyPolicyNegotiationManager();
	}

	private void setupConditionRanges() {
		try {
			this.conditionRangesTable = new Hashtable<String, ConditionRanges>();

			List<RequestItem> requestItems = this.requestPolicy.getRequestItems();

			for (RequestItem item : requestItems){

				List<Condition> conditions = item.getConditions();
				BooleanRange rightToOptOutBooleanRange = null;
				BooleanRange correctDataBooleanRange = null;
				BooleanRange accessHeldBooleanRange = null;
				BooleanRange storeSecureBooleanRange = null;
				BooleanRange mayInferBooleanRange = null;
				ShareDataRange shareDataRange = null;
				DataRetentionRange dataRetentionRange = null;

				for (Condition condition : conditions){
					String[] values = PrivacyConditionsConstantValues.getValues(condition.getConditionConstant());
					switch (condition.getConditionConstant()){
					case DATA_RETENTION:
						dataRetentionRange = new DataRetentionRange(values[0], values[values.length-1]);
						break;
					case MAY_BE_INFERRED:
						mayInferBooleanRange = new BooleanRange(values[0], values[values.length-1], condition.getConditionConstant());
						break;
					case RIGHT_TO_ACCESS_HELD_DATA:
						accessHeldBooleanRange = new BooleanRange(values[0], values[values.length-1], condition.getConditionConstant());
						break;
					case RIGHT_TO_CORRECT_INCORRECT_DATA:
						correctDataBooleanRange = new BooleanRange(values[0], values[values.length-1], condition.getConditionConstant());
						break;
					case RIGHT_TO_OPTOUT:
						rightToOptOutBooleanRange = new BooleanRange(values[0], values[values.length-1], condition.getConditionConstant());
						break;
					case SHARE_WITH_3RD_PARTIES:
						shareDataRange = new ShareDataRange(values[0], values[values.length-1]);
						break;
					case STORE_IN_SECURE_STORAGE:
						storeSecureBooleanRange = new BooleanRange(values[0], values[values.length-1], condition.getConditionConstant());
						break;
					}
				}
				ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);

				this.conditionRangesTable.put(item.getResource().getDataType(), ranges);
			}
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void setupMocks() {
		try {		
			this.details = MockRequestPolicy.getNegotiationDetailsBean();
			HashMap<RequestItem, ResponseItem> evaluatedPreferences = MockRequestPolicy.getItems();

			requestPolicy = new RequestPolicy();
			requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
			requestPolicy.setRequestor(details.getRequestor());
			List<RequestItem> requestItemList = new ArrayList<RequestItem>();
			ResponsePolicy responsePolicy = new ResponsePolicy();

			responsePolicy.setRequestor(details.getRequestor());
			List<ResponseItem> responseItemList = new ArrayList<ResponseItem>();
			Iterator<RequestItem> iterator = evaluatedPreferences.keySet().iterator();
			while (iterator.hasNext()){
				RequestItem next = iterator.next();
				requestItemList.add(next);
				responseItemList.add(evaluatedPreferences.get(next));
			}

			requestPolicy.setRequestItems(requestItemList);
			responsePolicy.setResponseItems(responseItemList);

			setupConditionRanges();


			Mockito.when(this.policyMgr.getPrivacyPolicy(details.getRequestor())).thenReturn(requestPolicy);
			Mockito.when(this.policyMgr.getPolicyRegistryManager()).thenReturn(policyRegistryManager);
			Mockito.when(this.policyRegistryManager.getConditionRanges(details.getRequestor())).thenReturn(conditionRangesTable);
			//Mockito.when(negotiationAgentRemote.getPolicy((RequestorBean) Mockito.anyObject())).thenReturn(new AsyncResult<RequestPolicy>(requestPolicy));
			CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userIdentity.getJid(), "Person", new Long(1));
			person = new IndividualCtxEntity(ctxId);

			for (RequestItem item : requestPolicy.getRequestItems()){

				CtxAttributeIdentifier id = new CtxAttributeIdentifier(ctxId, item.getResource().getDataType(), new Long(1));
				id.setScheme(DataIdentifierScheme.CONTEXT);
				CtxAttribute attribute = new CtxAttribute(id);
				person.addAttribute(attribute);
			}

			Mockito.when(ctxBroker.retrieveIndividualEntity(userIdentity)).thenReturn(new AsyncResult<IndividualCtxEntity>(person));

			Mockito.when(privacyPreferenceManager.evaluatePPNPreferences(requestPolicy)).thenReturn(evaluatedPreferences);

			Mockito.when(this.identitySelection.processIdentityContext((IAgreement) Mockito.anyObject())).thenReturn(new ArrayList<IIdentityOption>());

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Test
	public void test(){
		try {
			privacyPolicyNegotiationManager.negotiateServicePolicy(details);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
	}
}
