/**
 * 
 */
package org.societies.personalisation.UserPreferenceManagement.test.eval;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.evaluation.PreferenceEvaluator;
import org.societies.personalisation.UserPreferenceManagement.impl.evaluation.PrivateContextCache;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.UserPreferenceManagement.test.MockIdentity;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;

/**
 * @author Puma
 *
 */
public class TestEvaluationAlgorithm {

	
	private static final String MORNING = "morning";
	private static final String WORKING = "working";
	private static final String IN_A_MEETING = "in_a_meeting";
	private static final String WORK = "work";
	private static final String AFTERNOON = "afternoon";
	private static final String VOLUME = "volume";
	private static final String SLEEPING = "sleeping";
	private static final String HOME = "home";
	private UserPreferenceConditionMonitor monitor;
	private PrivateContextCache cache;
	private PreferenceEvaluator evaluator;
	private IPreference split_preference;
	private IPreference unsplit_preference;
	private IndividualCtxEntity ctxEntity;

	private IIdentity userId;
	private ServiceResourceIdentifier serviceID;
	
	private CtxAttribute ctxLocationAttribute;
	private CtxAttribute ctxActivityAttribute;
	private CtxAttribute ctxTimeOfDayAttribute;
	
	
	
	@Before
	public void setup(){
		userId = new MockIdentity(IdentityType.CSS, "myId", "domain");
		this.serviceID = new ServiceResourceIdentifier();
		try {
			this.serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serviceID.setServiceInstanceIdentifier("MediaPlayer");
		
		monitor = Mockito.mock(UserPreferenceConditionMonitor.class);
		cache = Mockito.mock(PrivateContextCache.class);
		evaluator = new PreferenceEvaluator(cache, monitor);
		setupContext();
		setupSplitPreference();
		setupUnSplitPreference();
		Mockito.when(cache.getContextValue(this.ctxActivityAttribute.getId())).thenReturn(this.ctxActivityAttribute.getStringValue());
		Mockito.when(cache.getContextValue(this.ctxLocationAttribute.getId())).thenReturn(this.ctxLocationAttribute.getStringValue());
		Mockito.when(cache.getContextValue(this.ctxTimeOfDayAttribute.getId())).thenReturn(this.ctxTimeOfDayAttribute.getStringValue());
	}
	
	private void setupSplitPreference() {
		split_preference = new PreferenceTreeNode(); //root preference
		
		IPreference activitySleeping = this.getConditionPreference(this.ctxActivityAttribute.getId(), SLEEPING);
		IPreference volume5 = this.getOutcomePreference("5");
		activitySleeping.add(volume5);
		split_preference.add(activitySleeping);
		
		split_preference.add(this.getOutcomePreference("50"));
		
		IPreference locationHome = this.getConditionPreference(this.ctxLocationAttribute.getId(), HOME);
		locationHome.add(this.getOutcomePreference("10"));
		
		IPreference timeAfternoon = this.getConditionPreference(this.ctxTimeOfDayAttribute.getId(), AFTERNOON);
		timeAfternoon.add(this.getOutcomePreference("100"));
		locationHome.add(timeAfternoon);
		split_preference.add(locationHome);
		
		IPreference activityWorking = this.getConditionPreference(this.ctxActivityAttribute.getId(), WORKING);
		activityWorking.add(this.getOutcomePreference("20"));
		locationHome.add(activityWorking);
		
		IPreference locationWork = this.getConditionPreference(this.ctxLocationAttribute.getId(), WORK);
		locationWork.add(this.getOutcomePreference("100"));
		IPreference activityInAMeeting = this.getConditionPreference(this.ctxActivityAttribute.getId(), IN_A_MEETING);
		activityInAMeeting.add(this.getOutcomePreference("10"));
		locationWork.add(activityInAMeeting);
		split_preference.add(locationWork);
		
	}
	
	private void setupUnSplitPreference(){
		this.unsplit_preference = this.getConditionPreference(this.ctxLocationAttribute.getId(), HOME);
		this.unsplit_preference.add(this.getOutcomePreference("100"));
		IPreference activitySleeping = this.getConditionPreference(this.ctxActivityAttribute.getId(), SLEEPING);
		activitySleeping.add(this.getOutcomePreference("0"));
		
		this.unsplit_preference.add(activitySleeping);
	}
	private IPreference getConditionPreference(CtxAttributeIdentifier id, String value){
		ContextPreferenceCondition condition = new ContextPreferenceCondition(id, OperatorConstants.EQUALS, value, id.getType());
		return new PreferenceTreeNode(condition);
	}
	
	private IPreference getOutcomePreference(String value){
		IPreferenceOutcome outcome = new PreferenceOutcome(serviceID, serviceID.getServiceInstanceIdentifier(), VOLUME, value);
		return new PreferenceTreeNode(outcome);
	}

	private void setupContext() {
		
		CtxEntityIdentifier ctxEntityId = new CtxEntityIdentifier(userId.getJid(), CtxEntityTypes.PERSON, new Long(1));
		ctxEntity = new IndividualCtxEntity(ctxEntityId);
		

		
		CtxAttributeIdentifier ctxLocationAttributeId = new CtxAttributeIdentifier(ctxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		ctxLocationAttribute = new CtxAttribute(ctxLocationAttributeId);
		ctxLocationAttribute.setStringValue(HOME);
		
		CtxAttributeIdentifier ctxActivityAttributeId = new CtxAttributeIdentifier(ctxEntityId, CtxAttributeTypes.STATUS, new Long(1));
		ctxActivityAttribute = new CtxAttribute(ctxActivityAttributeId);
		ctxActivityAttribute.setStringValue(WORKING);
		
		
		CtxAttributeIdentifier ctxATimeOfDayAttributeId = new CtxAttributeIdentifier(ctxEntityId, CtxAttributeTypes.TIME_OF_DAY, new Long(1));
		ctxTimeOfDayAttribute = new CtxAttribute(ctxATimeOfDayAttributeId);
		ctxTimeOfDayAttribute.setStringValue(AFTERNOON);
		
		
	}


	@Test
	public void testPreferenceEvaluation(){
		System.out.println("Testing split");
		Hashtable<IPreferenceOutcome, List<CtxIdentifier>> evaluatePreference = evaluator.evaluatePreference(split_preference);
		
		Enumeration<IPreferenceOutcome> keys = evaluatePreference.keys();
		while (keys.hasMoreElements()){
			IPreferenceOutcome nextElement = keys.nextElement();
			System.out.println("Outcome: "+nextElement.toString()+"\nConditions: "+evaluatePreference.get(nextElement));
		}
	}
	
	@Test
	public void testPreferenceEvaluationUnsplit(){
		System.out.println("Testing UNSPLIT");
		Hashtable<IPreferenceOutcome, List<CtxIdentifier>> evaluatePreference = evaluator.evaluatePreference(unsplit_preference);
		
		Enumeration<IPreferenceOutcome> keys = evaluatePreference.keys();
		while (keys.hasMoreElements()){
			IPreferenceOutcome nextElement = keys.nextElement();
			System.out.println("Outcome: "+nextElement.toString()+"\nConditions: "+evaluatePreference.get(nextElement));
		}
	}
}
