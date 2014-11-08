package org.societies.privacytrust.privacyprotection.privacypreferencemanager.test.personis;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.PrivacyPreferenceMerger;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.test.MyIdentity;
import org.springframework.scheduling.annotation.AsyncResult;

public class TestEvaluation {

	
	private IIdentity userIdentity;
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private ITrustBroker trustBroker;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IUserFeedback userFeedback;
	private IPrivacyAgreementManager agreementMgr;
	private IEventMgr eventMgr;
	private IIdentityManager idMgr;
	private PrivacyPreferenceManager privPrefMgr;
	private RequestorServiceBean googleRequestor;
	private AttributeSelectionPreferenceTreeModel attrSelPreference;
	private CtxAttributeIdentifier ctxID1;
	private List<Condition> agreementConditions;
	private CtxAttributeIdentifier ctxID2;
	private CtxAttributeIdentifier ctxID3;
	private AttributeSelectionPreferenceTreeModel attrSelPreference2;
	private PrivacyPreference mergeAttrSelPreference;

	
	@Before
	public void setup(){
		agreementConditions = new ArrayList<Condition>();
		userIdentity = new MyIdentity(IdentityType.CSS, "eliza", "societies.eu");
		setupRequestor();
		ctxBroker = Mockito.mock(ICtxBroker.class);
		commsMgr = Mockito.mock(ICommManager.class);
		trustBroker = Mockito.mock(ITrustBroker.class);
		privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
		userFeedback = Mockito.mock(IUserFeedback.class);
		agreementMgr = Mockito.mock(IPrivacyAgreementManager.class);
		eventMgr = Mockito.mock(IEventMgr.class);
		idMgr = Mockito.mock(IIdentityManager.class);
		
		/*
		 * Mock context
		 * 
		 */

		mockContext();
		setupAttrSelPreference();
		setupNewTree();

		
		
		/*
		 * Mock Comms
		 */
		Mockito.when(idMgr.getThisNetworkNode()).thenReturn((INetworkNode) userIdentity);
		Mockito.when(commsMgr.getIdManager()).thenReturn(idMgr);
		

		/*
		 * Mock trust
		 */
		try {
			Mockito.when(trustBroker.retrieveTrustValue((TrustQuery) Mockito.anyObject())).thenReturn(new AsyncResult<Double>(72.0));
		} catch (TrustException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		privPrefMgr = new PrivacyPreferenceManager();
		privPrefMgr.setCtxBroker(ctxBroker);
		privPrefMgr.setCommsMgr(commsMgr);
		privPrefMgr.setTrustBroker(trustBroker);
		privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		privPrefMgr.setUserFeedback(userFeedback);
		privPrefMgr.setAgreementMgr(agreementMgr);
		privPrefMgr.setEventMgr(eventMgr);
		privPrefMgr.initialisePrivacyPreferenceManager();
	}
	


	private void setupAttrSelPreference() {
		
		AttributeSelectionPreferenceDetailsBean details = new AttributeSelectionPreferenceDetailsBean();
		List<Action> actions = new ArrayList<Action>();
		Action actionRead = ActionUtils.create(ActionConstants.READ);
		actions.add(actionRead);
		details.setActions(actions);
		details.setDataType(ctxID1.getType());
		details.setRequestor(googleRequestor);
		
		
		Hashtable<TrustPreferenceCondition, List<PrivacyCondition>> preferenceContents = createConditions();
		Enumeration<TrustPreferenceCondition> keys = preferenceContents.keys();
		PrivacyPreference rootPreference = new PrivacyPreference();
		
		while (keys.hasMoreElements()){
			CtxAttributeIdentifier ctxID = null;
			
			
			TrustPreferenceCondition trustCondition = keys.nextElement();
			if (trustCondition.getTrustThreshold() == 45.0){
				ctxID = ctxID1;
			}else if (trustCondition.getTrustThreshold() == 69.0){
				ctxID = ctxID2;
			}else{
				ctxID = ctxID3;
			}
			
			AttributeSelectionOutcome outcome = new AttributeSelectionOutcome(ctxID);
			PrivacyPreference outcomePreference = new PrivacyPreference(outcome);
			
			List<PrivacyCondition> list = preferenceContents.get(trustCondition);
			for (PrivacyCondition pCon : list){
			
				PrivacyPreference temp = new PrivacyPreference(pCon);
				temp.add(outcomePreference);
				outcomePreference = (PrivacyPreference) outcomePreference.getRoot();
				
			}
			PrivacyPreference preference = new PrivacyPreference(trustCondition);
			preference.add(outcomePreference.getRoot());
			rootPreference.add(preference);
		}
		attrSelPreference = new AttributeSelectionPreferenceTreeModel(details, rootPreference.getRoot());
		
		System.out.println(rootPreference.toTreeString());
	}

	private Hashtable<TrustPreferenceCondition, List<PrivacyCondition>> createConditions() {
		TrustPreferenceCondition trustCondition45 = new TrustPreferenceCondition(45.0);
		TrustPreferenceCondition trustCondition69 = new TrustPreferenceCondition(69.0);
		TrustPreferenceCondition trustCondition83 = new TrustPreferenceCondition(83.0);
		
		Hashtable<TrustPreferenceCondition, List<PrivacyCondition>> table = new Hashtable<TrustPreferenceCondition, List<PrivacyCondition>>();
		
		List<PrivacyCondition> conditionList45 = createPrivacyConditions();
		table.put(trustCondition45, conditionList45);
		List<PrivacyCondition> conditionList69 = createPrivacyConditions();
		table.put(trustCondition69, conditionList69);
		List<PrivacyCondition> conditionList83 = createPrivacyConditions();
		table.put(trustCondition83, conditionList83);
		
		return table;
	}

	
	private List<PrivacyCondition> createPrivacyConditions() {
		List<PrivacyCondition> privacyConditions = new ArrayList<PrivacyCondition>();
		
		ConditionConstants[] conditionConstants = ConditionConstants.values();
		for (ConditionConstants cc : conditionConstants){
			
			Condition condition = new Condition();
			condition.setConditionConstant(cc);
			String[] ccValues = PrivacyConditionsConstantValues.getValues(cc);
			switch (cc){
			case DATA_RETENTION:
				condition.setValue(ccValues[ccValues.length-2]);
				break;
			case MAY_BE_INFERRED:
				condition.setValue(ccValues[0]);
				break;
			case RIGHT_TO_ACCESS_HELD_DATA:
				condition.setValue(ccValues[0]);
				break;
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				condition.setValue(ccValues[0]);
				break;
			case RIGHT_TO_OPTOUT:
				condition.setValue(ccValues[0]);
				break;
			case SHARE_WITH_3RD_PARTIES:
				condition.setValue(ccValues[1]);
				break;
			case STORE_IN_SECURE_STORAGE:
				condition.setValue(ccValues[0]);
				break;
			}
			privacyConditions.add(new PrivacyCondition(condition));
			
			if (!agreementConditions.contains(condition)){
				agreementConditions.add(condition);
			}
		}
		return privacyConditions;
	}

	

	private void setupNewTree(){
		Hashtable<TrustPreferenceCondition, List<PrivacyCondition>> createConditions = this.createConditions();
		Enumeration<TrustPreferenceCondition> keys = createConditions.keys();
		while (keys.hasMoreElements()){
			List<PrivacyCondition> list = createConditions.get(keys.nextElement());
			for (PrivacyCondition con : list){
				if (con.getCondition().getConditionConstant().equals(ConditionConstants.MAY_BE_INFERRED)){
					con.getCondition().setValue(PrivacyConditionsConstantValues.getValues(con.getCondition().getConditionConstant())[1]);
				}
			}
		}
		
		
		AttributeSelectionPreferenceDetailsBean details = new AttributeSelectionPreferenceDetailsBean();
		List<Action> actions = new ArrayList<Action>();
		Action actionRead = ActionUtils.create(ActionConstants.READ);
		actions.add(actionRead);
		details.setActions(actions);
		details.setDataType(ctxID1.getType());
		details.setRequestor(googleRequestor);
		
		
		Enumeration<TrustPreferenceCondition> keys1 = createConditions.keys();
		PrivacyPreference rootPreference = new PrivacyPreference();
		
		while (keys1.hasMoreElements()){
			CtxAttributeIdentifier ctxID = null;
			
			
			TrustPreferenceCondition trustCondition = keys1.nextElement();
			if (trustCondition.getTrustThreshold() == 45.0){
				ctxID = ctxID1;
			}else if (trustCondition.getTrustThreshold() == 69.0){
				ctxID = ctxID2;
			}else{
				ctxID = ctxID3;
			}
			
			AttributeSelectionOutcome outcome = new AttributeSelectionOutcome(ctxID);
			PrivacyPreference outcomePreference = new PrivacyPreference(outcome);
			
			List<PrivacyCondition> list = createConditions.get(trustCondition);
			for (PrivacyCondition pCon : list){
			
				PrivacyPreference temp = new PrivacyPreference(pCon);
				temp.add(outcomePreference);
				outcomePreference = (PrivacyPreference) outcomePreference.getRoot();
				
			}
			PrivacyPreference preference = new PrivacyPreference(trustCondition);
			preference.add(outcomePreference.getRoot());
			rootPreference.add(preference);
		}
		attrSelPreference2 = new AttributeSelectionPreferenceTreeModel(details, rootPreference.getRoot());
		
		System.out.println(rootPreference.toTreeString());
	}
	public void mockContext(){
		
		try {
			
			
			/*
			 * init PrivPrefMgr
			 */
			List<CtxIdentifier> ctxEntityIds = new ArrayList<CtxIdentifier>();
			
			Mockito.when(ctxBroker.lookup(this.userIdentity, CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(ctxEntityIds));
			
			ctxID1 = new CtxAttributeIdentifier("context://eliza@societies.eu/ENTITY/person/1/ATTRIBUTE/locationSymbolic/1");
			ctxID2 = new CtxAttributeIdentifier("context://eliza@societies.eu/ENTITY/person/1/ATTRIBUTE/locationSymbolic/2");
			ctxID3 = new CtxAttributeIdentifier("context://eliza@societies.eu/ENTITY/person/1/ATTRIBUTE/locationSymbolic/3");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setupRequestor(){
		ServiceResourceIdentifier serviceIDGoogle = ServiceModelUtils.generateServiceResourceIdentifierFromString("GoogleVenueFinder www.google.com/venueFinder");
		String googleId = "venueFinder.google.com";
		googleRequestor = new RequestorServiceBean();
		googleRequestor.setRequestorId(googleId);
		googleRequestor.setRequestorServiceId(serviceIDGoogle);
	}
	
	@Test
	public void testEvaluation(){
		PreferenceEvaluator ppE = new PreferenceEvaluator(privPrefMgr, googleRequestor, userIdentity);
		CtxIdentifier evaluateAttrPreference = ppE.evaluateAttrPreference(attrSelPreference, agreementConditions);
		System.out.println("Evaluate result: "+evaluateAttrPreference);
		
		
	}
	
	@Test
	public void testMerging(){
		PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
		mergeAttrSelPreference = merger.mergeAttrSelPreference(attrSelPreference.getDetails(), attrSelPreference.getRootPreference(), attrSelPreference2.getRootPreference());
		System.out.println("Merge result: "+mergeAttrSelPreference.toTreeString());
		PrivacyPreference root = mergeAttrSelPreference.getRoot();
		System.out.println(root.getChildCount());
	
	}
	


}
