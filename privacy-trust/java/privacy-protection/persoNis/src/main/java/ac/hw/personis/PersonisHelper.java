/**
 * 
 */
package ac.hw.personis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;

import ac.hw.personis.dataInit.DataInitialiser;
import ac.hw.personis.event.NegotiationListener;

/**
 * @author PUMA
 *
 */
public class PersonisHelper {

	private ICtxBroker ctxBroker;
	private IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager;
	private IPrivacyPolicyManagerInternal privacyPolicyManager;
	private ICommManager commsMgr;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private Application application;

	private RequestorServiceBean googleRequestor;
	private RequestorServiceBean hwuRequestor;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Apps appsPage;
	private IEventMgr eventMgr;
	private NegotiationListener negotiationResultListener;
	public static final String HWU_CAMPUS_GUIDE_APP = "HWU Campus Guide App";
	public static final String GOOGLE_VENUE_FINDER = "Google Venue finder";
	private Random rand = new Random();

	public PersonisHelper(){

		UIManager.put("ClassLoader", getClass().getClassLoader());


		ServiceResourceIdentifier serviceIDGoogle = ServiceModelUtils.generateServiceResourceIdentifierFromString("GoogleVenueFinder www.google.com/venueFinder");
		String googleId = "venueFinder.google.com";
		googleRequestor = new RequestorServiceBean();
		googleRequestor.setRequestorId(googleId);
		googleRequestor.setRequestorServiceId(serviceIDGoogle);

		ServiceResourceIdentifier serviceIDHWU = ServiceModelUtils.generateServiceResourceIdentifierFromString("HWUCampusGuide www.hw.ac.uk/campusGuideApp");
		String hwuId = "campusGuide.hw.ac.uk";
		hwuRequestor = new RequestorServiceBean();
		hwuRequestor.setRequestorId(hwuId);
		hwuRequestor.setRequestorServiceId(serviceIDHWU);    	


	}

	public void init(){
		UIManager.put("ClassLoader", getClass().getClassLoader());


		application = new Application(this);
		application.getFrame().setVisible(true);
		appsPage = application.getAppsPage();
		//create privacy policies for googleRequestor and hwuRequestor and store them in the PrivacyPolicyManager
		//first check that the PrivacyPolicyManager doesn't already ahve them:

		try {
			RequestPolicy googlePolicy = this.privacyPolicyManager.getPrivacyPolicy(googleRequestor);
			if (null==googlePolicy){
				googlePolicy = createPolicy(googleRequestor);
				this.logging.debug(printPolicyDetails(googlePolicy));
				this.privacyPolicyManager.updatePrivacyPolicy(googlePolicy);
				logging.debug("updated privacy policy for google");
			}else{
				logging.debug("PrivacyPolicyManager already has policy of google:");
				this.logging.debug(printPolicyDetails(googlePolicy));
			}

			RequestPolicy hwuPolicy = this.privacyPolicyManager.getPrivacyPolicy(hwuRequestor);
			if (null==hwuPolicy){

				hwuPolicy = createPolicy(hwuRequestor);
				this.logging.debug(printPolicyDetails(hwuPolicy));
				this.privacyPolicyManager.updatePrivacyPolicy(hwuPolicy);
				this.logging.debug("updated privacy policy for HWU");
			}else{
				logging.debug("PrivacyPolicyManager already has policy of HWU:");
				this.logging.debug(printPolicyDetails(hwuPolicy));
			}


		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		negotiationResultListener = new NegotiationListener(application);
		negotiationResultListener.subscribe(this.eventMgr);
	}

	private RequestPolicy createPolicy(RequestorBean requestor){
		RequestPolicy policy = new RequestPolicy();
		policy.setRequestor(requestor);
		policy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
		List<RequestItem> requestItems = new ArrayList<RequestItem>();

		for (String dataType : DataInitialiser.dataTypes){
			requestItems.add(createRequestItem(dataType));
		}
		policy.setRequestItems(requestItems);
		return policy;
	}


	private RequestItem createRequestItem(String dataType) {
		Resource resource = new Resource();
		resource.setDataType(dataType);
		resource.setScheme(DataIdentifierScheme.CONTEXT);
		RequestItem item = new RequestItem();
		
		item.setActions(getActions());
		item.setConditions(getConditions());
		item.setOptional(rand.nextBoolean());
		item.setPurpose("This item will be used for ...");
		item.setResource(resource);
		
		return item;
	}


	private List<Condition> getConditions() {
		List<Condition> conditions = new ArrayList<Condition>();
		
		List<ConditionConstants> cc = Arrays.asList(ConditionConstants.values());
		
		for (ConditionConstants c : cc){
			Condition condition = new Condition();
			condition.setConditionConstant(c);
			condition.setOptional(this.rand.nextBoolean());
			String[] values = PrivacyConditionsConstantValues.getValues(c);
			String value = values[randInt(0, values.length-1)];
			condition.setValue(value);
			conditions.add(condition);
		}
		
		return conditions;
	}

	private List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		List<ActionConstants> ac = Arrays.asList(ActionConstants.values());
		
		
		int stop = this.randInt(1, ac.size());
		
		for (int i = 0; i < stop; i++){
			Action action = new Action();
			action.setActionConstant(ac.get(i));
			actions.add(action);
		}
		
		return actions;
	}

	public int randInt(int min, int max) {

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
	private String printPolicyDetails(RequestPolicy policy) {
		StringBuilder sb = new StringBuilder();

		for (RequestItem item : policy.getRequestItems()){
			sb.append(item.getResource().getDataType()+": ");
			for (Condition con: item.getConditions()){
				sb.append(con.getConditionConstant()+" -> "+con.getValue()+"\t");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	public IPrivacyPolicyNegotiationManager getPrivacyPolicyNegotiationManager() {
		return privacyPolicyNegotiationManager;
	}
	public void setPrivacyPolicyNegotiationManager(
			IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager) {
		this.privacyPolicyNegotiationManager = privacyPolicyNegotiationManager;
	}
	public IPrivacyPolicyManagerInternal getPrivacyPolicyManager() {
		return privacyPolicyManager;
	}
	public void setPrivacyPolicyManager(IPrivacyPolicyManagerInternal privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}
	public ICommManager getCommsMgr() {
		return commsMgr;
	}
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
	}
	public IPrivacyPreferenceManager getPrivacyPreferenceManager() {
		return privacyPreferenceManager;
	}
	public void setPrivacyPreferenceManager(IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
	}

	public RequestorServiceBean getGoogleRequestor() {
		return googleRequestor;
	}

	public void setGoogleRequestor(RequestorServiceBean googleRequestor) {
		this.googleRequestor = googleRequestor;
	}

	public RequestorServiceBean getHwuRequestor() {
		return hwuRequestor;
	}

	public void setHwuRequestor(RequestorServiceBean hwuRequestor) {
		this.hwuRequestor = hwuRequestor;
	}

	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public Application getApplication() {
		// TODO Auto-generated method stub
		return this.application;
	}




}
