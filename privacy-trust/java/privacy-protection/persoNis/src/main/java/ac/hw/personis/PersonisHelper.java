/**
 * 
 */
package ac.hw.personis;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;

import ac.hw.personis.dataInit.policies.PolicyFileLoader;
import ac.hw.personis.dataInit.policies.RangesBBC;
import ac.hw.personis.dataInit.policies.RangesGoogle;
import ac.hw.personis.dataInit.policies.RangesHWU;
import ac.hw.personis.dataInit.policies.RangesITunes;
import ac.hw.personis.dataInit.policies.XMLPolicyReader;
import ac.hw.personis.event.NegotiationListener;
import ac.hw.personis.internalwindows.apps.Appsv2;
import ac.hw.personis.internalwindows.apps.ImagePanel;
import ac.hw.personis.services.BBCNewsService;
import ac.hw.personis.services.BBCWeatherService;
import ac.hw.personis.services.GoogleMapsService;
import ac.hw.personis.services.HWUService;

/**
 * @author PUMA
 *
 */
public class PersonisHelper {

	private ICtxBroker ctxBroker;
	private IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager;
	private IPrivacyPolicyManagerInternal privacyPolicyManager;
	private IPrivacyAgreementManager privacyAgreementManager;
	private ICommManager commsMgr;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private IEventMgr eventMgr;
	private IIdentitySelection identitySelection;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private ITrustBroker trustBroker;
	
	private Application application;
	private RequestorServiceBean googleRequestor;
	private RequestorServiceBean hwuRequestor;
	private RequestorServiceBean bbcNewsRequestor;
	private RequestorServiceBean bbcWeatherRequestor;
	//private RequestorServiceBean itunesRequestor;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Appsv2 appsPage;
	
	private NegotiationListener negotiationResultListener;
	public static final String HWU_CAMPUS_GUIDE_APP = "HWU Campus Guide App";
	public static final String GOOGLE_VENUE_FINDER = "Google Venue finder";
	public static final String BBC_NEWS_APP = "BBC News App";
	public static final String BBC_WEATHER_APP = "BBC Weather App";
	//public static final String ITUNES_MUSIC_APP = "iTunes Music Service";
	
	
	private Hashtable<String, List<String>> requestDataTypesPerService; 
	private Hashtable<String, Agreement> agreementsTable;
	private Random rand = new Random();
	private Hashtable<String, IIdentity> identityPerService;
	private List<String> storeApps;
	private List<String> installedApps;
	private GoogleMapsService googleService;
	private HWUService hwuService;
	private BBCNewsService bbcNewsService;
	private BBCWeatherService bbcWeatherService;

	public PersonisHelper(){

	
		UIManager.put("ClassLoader", getClass().getClassLoader());
		this.agreementsTable = new Hashtable<String, Agreement>();
		this.requestDataTypesPerService = new Hashtable<String, List<String>>();
		this.identityPerService = new Hashtable<String, IIdentity>();
		ServiceResourceIdentifier serviceIDGoogle = ServiceModelUtils.generateServiceResourceIdentifierFromString("GoogleVenueFinder www.google.com/venueFinder");
		String googleId = "www.google.com";
		googleRequestor = new RequestorServiceBean();
		googleRequestor.setRequestorId(googleId);
		googleRequestor.setRequestorServiceId(serviceIDGoogle);

		ServiceResourceIdentifier serviceIDHWU = ServiceModelUtils.generateServiceResourceIdentifierFromString("HWUCampusGuide www.hw.ac.uk/campusGuideApp");
		String hwuId = "www.hw.ac.uk";
		hwuRequestor = new RequestorServiceBean();
		hwuRequestor.setRequestorId(hwuId);
		hwuRequestor.setRequestorServiceId(serviceIDHWU);    	

		ServiceResourceIdentifier serviceIDBBCNews = ServiceModelUtils.generateServiceResourceIdentifierFromString("BBCNews www.bbc.co.uk/news");
		String bbcId = "bbc.co.uk";
		bbcNewsRequestor = new RequestorServiceBean();
		bbcNewsRequestor.setRequestorId(bbcId);
		bbcNewsRequestor.setRequestorServiceId(serviceIDBBCNews);    	

		ServiceResourceIdentifier serviceIDBBCWeather = ServiceModelUtils.generateServiceResourceIdentifierFromString("BBCWeather www.bbc.co.uk/weather");
		bbcWeatherRequestor = new RequestorServiceBean();
		bbcWeatherRequestor.setRequestorId(bbcId);
		bbcWeatherRequestor.setRequestorServiceId(serviceIDBBCWeather);    	

				
		
/*		ServiceResourceIdentifier serviceIDiTunes = ServiceModelUtils.generateServiceResourceIdentifierFromString("iTunes www.apple.com/iTunes");
		String iTunesId = "itunes.apple.com";
		itunesRequestor = new RequestorServiceBean();
		itunesRequestor.setRequestorId(iTunesId);
		itunesRequestor.setRequestorServiceId(serviceIDiTunes);    */	

				
	}

	public void init(){
		UIManager.put("ClassLoader", getClass().getClassLoader());
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}

		application = new Application(this);
		application.getFrame().setVisible(true);
		appsPage = application.getAppsPage();
		//create privacy policies for googleRequestor and hwuRequestor and store them in the PrivacyPolicyManager
		//first check that the PrivacyPolicyManager doesn't already ahve them:

		try {
			RequestPolicy googlePolicy = this.privacyPolicyManager.getPrivacyPolicy(googleRequestor);
			if (null==googlePolicy){
				//googlePolicy = createPolicy(googleRequestor, GOOGLE_VENUE_FINDER);
				googlePolicy = getGooglePolicy();
				Hashtable<String, ConditionRanges> ranges = RangesGoogle.getRanges(googlePolicy);
				this.logging.debug(printPolicyDetails(googlePolicy));
				this.privacyPolicyManager.updatePrivacyPolicy(googlePolicy, ranges);
				Enumeration<String> keys = ranges.keys();
				List<String> requestedDataTypes = new ArrayList<String>();
				while (keys.hasMoreElements()){
					String dataType = keys.nextElement();
					requestedDataTypes.add(dataType);
				}
				this.requestDataTypesPerService.put(GOOGLE_VENUE_FINDER, requestedDataTypes);
				logging.debug("updated privacy policy for google");
			}else{
				logging.debug("PrivacyPolicyManager already has policy of google:");
				this.logging.debug(printPolicyDetails(googlePolicy));
			}

			RequestPolicy hwuPolicy = this.privacyPolicyManager.getPrivacyPolicy(hwuRequestor);
			if (null==hwuPolicy){

				//hwuPolicy = createPolicy(hwuRequestor, HWU_CAMPUS_GUIDE_APP);
				hwuPolicy = getHWUPolicy();
				this.logging.debug(printPolicyDetails(hwuPolicy));
				Hashtable<String,ConditionRanges> ranges = RangesHWU.getRanges(hwuPolicy);
				this.privacyPolicyManager.updatePrivacyPolicy(hwuPolicy, ranges);
				Enumeration<String> keys = ranges.keys();
				List<String> requestedDataTypes = new ArrayList<String>();
				while (keys.hasMoreElements()){
					String dataType = keys.nextElement();
					requestedDataTypes.add(dataType);
				}
				this.requestDataTypesPerService.put(HWU_CAMPUS_GUIDE_APP, requestedDataTypes);
				this.logging.debug("updated privacy policy for HWU");
			}else{
				logging.debug("PrivacyPolicyManager already has policy of HWU:");
				this.logging.debug(printPolicyDetails(hwuPolicy));
			}
			
			RequestPolicy bbcPolicy = this.privacyPolicyManager.getPrivacyPolicy(bbcNewsRequestor);
			if (null==bbcPolicy){
				//bbcPolicy = createPolicy(bbcRequestor, BBC_NEWS_APP);
				bbcPolicy = getBBCNewsPolicy();
				this.logging.debug(printPolicyDetails(bbcPolicy));
				Hashtable<String,ConditionRanges> ranges = RangesBBC.getRanges(bbcPolicy);
				this.privacyPolicyManager.updatePrivacyPolicy(bbcPolicy, ranges);
				Enumeration<String> keys = ranges.keys();
				List<String> requestedDataTypes = new ArrayList<String>();
				while (keys.hasMoreElements()){
					String dataType = keys.nextElement();
					requestedDataTypes.add(dataType);
				}
				this.requestDataTypesPerService.put(BBC_NEWS_APP, requestedDataTypes);
				this.logging.debug("Updated privacy policy for BBC");
			}else{
				logging.debug("PrivacyPolicyManager already has policy for BBC:");
				this.logging.debug(printPolicyDetails(bbcPolicy));
			}
			
			RequestPolicy bbcWeatherPolicy = this.privacyPolicyManager.getPrivacyPolicy(bbcWeatherRequestor);
			if (null==bbcWeatherPolicy){
				//itunesPolicy = createPolicy(itunesRequestor, ITUNES_MUSIC_APP);
				bbcWeatherPolicy = getBBCWeatherPolicy();
				this.logging.debug(printPolicyDetails(bbcWeatherPolicy));
				Hashtable<String,ConditionRanges> ranges = RangesBBC.getRanges(bbcWeatherPolicy);
				this.privacyPolicyManager.updatePrivacyPolicy(bbcWeatherPolicy, ranges);
				Enumeration<String> keys = ranges.keys();
				List<String> requestedDataTypes = new ArrayList<String>();
				while (keys.hasMoreElements()){
					String dataType = keys.nextElement();
					requestedDataTypes.add(dataType);
				}
				this.requestDataTypesPerService.put(BBC_WEATHER_APP, requestedDataTypes);
				this.logging.debug("Updated privacy policy for BBC Weather");
			}else{
				logging.debug("PrivacyPolicyManager already has policy for BBC Weather:");
				logging.debug(printPolicyDetails(bbcWeatherPolicy));
			}
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		negotiationResultListener = new NegotiationListener(application);
		negotiationResultListener.subscribe(this.eventMgr);
	}
	


	public void negotiationCompleted(Agreement agreement) throws InvalidFormatException{
		List<String> dataTypes = new ArrayList<String>();
		List<ResponseItem> requestedItems = agreement.getRequestedItems();
		for (ResponseItem item: requestedItems){
			dataTypes.add(item.getRequestItem().getResource().getDataType());
		}
		if (RequestorUtils.equals(agreement.getRequestor(), this.googleRequestor)){
			this.requestDataTypesPerService.put(GOOGLE_VENUE_FINDER, dataTypes);
			identityPerService.put(GOOGLE_VENUE_FINDER, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			agreementsTable.put(GOOGLE_VENUE_FINDER, agreement);

		}else if (RequestorUtils.equals(agreement.getRequestor(), this.hwuRequestor)){
			this.requestDataTypesPerService.put(HWU_CAMPUS_GUIDE_APP, dataTypes);
			identityPerService.put(HWU_CAMPUS_GUIDE_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			agreementsTable.put(HWU_CAMPUS_GUIDE_APP, agreement);
		}else if (RequestorUtils.equals(agreement.getRequestor(), this.bbcNewsRequestor)){
			this.requestDataTypesPerService.put(BBC_NEWS_APP, dataTypes);
			identityPerService.put(BBC_NEWS_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			agreementsTable.put(BBC_NEWS_APP, agreement);
		}else if (RequestorUtils.equals(agreement.getRequestor(), this.bbcWeatherRequestor)){
			this.requestDataTypesPerService.put(BBC_WEATHER_APP, dataTypes);	
			identityPerService.put(BBC_WEATHER_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			agreementsTable.put(BBC_WEATHER_APP, agreement);
		}
	}
	
	public IIdentity getUserID(String service){
		return this.identityPerService.get(service);
	}
	
	private RequestPolicy getGooglePolicy(){
		XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, this.commsMgr.getIdManager());
		//File file = new File("policies\\google.xml");        
		//System.out.println(file.getAbsolutePath());
		//RequestPolicy policy = reader.readPolicyFromFile(file);
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/policies/google.xml");
        RequestPolicy policy = reader.readPolicyFromFile(resourceAsStream);		
		policy.setRequestor(googleRequestor);
		return policy;
	}
	private RequestPolicy getBBCNewsPolicy(){
		XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, this.commsMgr.getIdManager());
		//File file = new File("policies\\bbc.xml");
		//System.out.println(file.getAbsolutePath());
		//RequestPolicy policy = reader.readPolicyFromFile(file);
		InputStream resourceAsStream = this.getClass().getResourceAsStream("/policies/bbc.xml");
		RequestPolicy policy = reader.readPolicyFromFile(resourceAsStream);
		policy.setRequestor(bbcNewsRequestor);
		return policy;
	}
	private RequestPolicy getHWUPolicy(){
		XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, this.commsMgr.getIdManager());
//		File file = new File("policies\\hwu.xml");
//		System.out.println(file.getAbsolutePath());
//		RequestPolicy policy = reader.readPolicyFromFile(file);
		InputStream resourceAsStream = this.getClass().getResourceAsStream("/policies/hwu.xml");
		RequestPolicy policy = reader.readPolicyFromFile(resourceAsStream);		
		policy.setRequestor(hwuRequestor);
		return policy;
	}
	private RequestPolicy getBBCWeatherPolicy(){
		XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, this.commsMgr.getIdManager());
//		File file = new File("policies\\itunes.xml");
//		System.out.println(file.getAbsolutePath());
//		RequestPolicy policy = reader.readPolicyFromFile(file);
		InputStream resourceAsStream = this.getClass().getResourceAsStream("/policies/bbc.xml");
		RequestPolicy policy = reader.readPolicyFromFile(resourceAsStream);
		policy.setRequestor(bbcWeatherRequestor);
		return policy;
	}
	private RequestPolicy createPolicy(RequestorBean requestor, String friendly){
		XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, this.commsMgr.getIdManager());
		PolicyFileLoader pfLoader  = new PolicyFileLoader(application.getFrame(), friendly);
		File file = pfLoader.getFile();
		RequestPolicy policy = reader.readPolicyFromFile(file);
		policy.setRequestor(requestor);
		return policy;
/*		RequestPolicy policy = new RequestPolicy();
		policy.setRequestor(requestor);
		policy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
		List<RequestItem> requestItems = new ArrayList<RequestItem>();

		for (String dataType : DataInitialiser.dataTypes){
			requestItems.add(createRequestItem(dataType));
		}
		policy.setRequestItems(requestItems);
		return policy;*/
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

	/*public RequestorServiceBean getItunesRequestor() {
		return itunesRequestor;
	}*/

	public RequestorServiceBean getBBCNewsRequestor() {
		return bbcNewsRequestor;
	}

	public RequestorServiceBean getBbcWeatherRequestor() {
		return bbcWeatherRequestor;
	}

	public void setBbcWeatherRequestor(RequestorServiceBean bbcWeatherRequestor) {
		this.bbcWeatherRequestor = bbcWeatherRequestor;
	}

	public List<String> getStoreApps() {
		if (storeApps==null) setupServices();
		return storeApps;
	}
	public List<String> getInstalledApps() {
		if (installedApps==null) setupServices();
		return installedApps;
	}
	
	public Hashtable<String, List<String>> getServicesByUserIdentity(){
		
		
		Hashtable<String, List<String>> table = new Hashtable<String, List<String>>();
		Collection<Agreement> values = this.agreementsTable.values();
		for (Agreement agreement : values){
			RequestorBean requestor = agreement.getRequestor();
			String requestorName = "";
			if (RequestorUtils.equals(requestor, googleRequestor)){
				requestorName = GOOGLE_VENUE_FINDER;
			}else if (RequestorUtils.equals(requestor, hwuRequestor)){
				requestorName = HWU_CAMPUS_GUIDE_APP;
			}else if (RequestorUtils.equals(requestor, bbcNewsRequestor)){
				requestorName = BBC_NEWS_APP;
			}else if (RequestorUtils.equals(requestor, bbcWeatherRequestor)){
				requestorName = BBC_WEATHER_APP;
			}
			String userIdentity = agreement.getUserIdentity();
			if (table.containsKey(userIdentity)){
				table.get(userIdentity).add(requestorName);
			}else{
				List<String> services = new ArrayList<String>();
				services.add(requestorName);
				table.put(userIdentity, services);
			}
		}
		
		return table;
	}
	private void setupServices(){
		this.storeApps = new ArrayList<String>();
		this.installedApps = new ArrayList<String>();
		
		try {
			AgreementEnvelope envelope = this.privacyAgreementManager.getAgreement(RequestorUtils.toRequestor(googleRequestor, this.commsMgr.getIdManager()));
			if (null!=envelope){
				installedApps.add(GOOGLE_VENUE_FINDER);
				Agreement agreement = AgreementUtils.toAgreementBean(envelope.getAgreement());
				this.agreementsTable.put(GOOGLE_VENUE_FINDER, agreement);
				this.identityPerService.put(GOOGLE_VENUE_FINDER, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			}else{
				storeApps.add(GOOGLE_VENUE_FINDER);
			}
			
			AgreementEnvelope envelope1 = this.privacyAgreementManager.getAgreement(RequestorUtils.toRequestor(hwuRequestor, this.commsMgr.getIdManager()));
			if (null!=envelope1){
				installedApps.add(HWU_CAMPUS_GUIDE_APP);
				Agreement agreement = AgreementUtils.toAgreementBean(envelope1.getAgreement());
				this.agreementsTable.put(HWU_CAMPUS_GUIDE_APP, agreement);
				
				this.identityPerService.put(HWU_CAMPUS_GUIDE_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			}else{
				storeApps.add(HWU_CAMPUS_GUIDE_APP);
			}
			
			AgreementEnvelope envelope2 = this.privacyAgreementManager.getAgreement(RequestorUtils.toRequestor(bbcNewsRequestor, this.commsMgr.getIdManager()));
			if (null!=envelope2){
				installedApps.add(BBC_NEWS_APP);
				Agreement agreement = AgreementUtils.toAgreementBean(envelope2.getAgreement());
				this.agreementsTable.put(BBC_NEWS_APP, agreement);
				this.identityPerService.put(BBC_NEWS_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			}else{
				storeApps.add(BBC_NEWS_APP);
			}
			AgreementEnvelope envelope3 = this.privacyAgreementManager.getAgreement(RequestorUtils.toRequestor(bbcWeatherRequestor, this.commsMgr.getIdManager()));
			if (null!=envelope3){
				installedApps.add(BBC_WEATHER_APP);
				Agreement agreement = AgreementUtils.toAgreementBean(envelope3.getAgreement());
				this.agreementsTable.put(BBC_WEATHER_APP, agreement);
				this.identityPerService.put(BBC_WEATHER_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			}else{
				storeApps.add(BBC_WEATHER_APP);
			}
			
			/*AgreementEnvelope envelope3 = this.privacyAgreementManager.getAgreement(RequestorUtils.toRequestor(itunesRequestor, this.commsMgr.getIdManager()));
			if (null!=envelope3){
				installedApps.add(ITUNES_MUSIC_APP);
				Agreement agreement = AgreementUtils.toAgreementBean(envelope3.getAgreement());
				this.agreementsTable.put(ITUNES_MUSIC_APP, agreement);
				this.identityPerService.put(ITUNES_MUSIC_APP, this.commsMgr.getIdManager().fromJid(agreement.getUserIdentity()));
			}else{
				storeApps.add(ITUNES_MUSIC_APP);
			}*/
			
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public IPrivacyAgreementManager getPrivacyAgreementManager() {
		return privacyAgreementManager;
	}

	public void setPrivacyAgreementManager(IPrivacyAgreementManager privacyAgreementManager) {
		this.privacyAgreementManager = privacyAgreementManager;
	}
	
	public Agreement getAgreement(String service){
		return this.agreementsTable.get(service);
	}

	public IIdentitySelection getIdentitySelection() {
		return identitySelection;
	}

	public void setIdentitySelection(IIdentitySelection identitySelection) {
		this.identitySelection = identitySelection;
	}

	public IPrivacyDataManagerInternal getPrivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}

	public void setPrivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}

	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}

	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}


	public void startGoogleService(){
		if (this.googleService == null){
			googleService = new GoogleMapsService(this);
		}else{
			googleService.setVisible(true);
			googleService.requestFocus();
		}
	}
	
	public void startHWUService(){
		if (hwuService==null){
			hwuService = new HWUService(this);
		}else{
			hwuService.setVisible(true);
			hwuService.requestFocus();
		}
	}
	
	public void startBBCNewsService(){
		if (bbcNewsService == null){
			bbcNewsService = new BBCNewsService(this);
		}else{
			bbcNewsService.setVisible(true);
			bbcNewsService.requestFocus();
		}
		
	}
	
	public void startBBCWeatherService(){
		if (bbcWeatherService == null){
			bbcWeatherService = new BBCWeatherService(this);
		}else{
			bbcWeatherService.setVisible(true);
			bbcNewsService.requestFocus();
		}
	}
}
