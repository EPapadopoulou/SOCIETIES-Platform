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
package org.societies.personalisation.UserPreferenceManagement.impl.cis;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceUtils;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.cis.DownloadFeedbackListenerCallBack.FeedbackType;
import org.societies.personalisation.UserPreferenceManagement.impl.merging.PreferenceMerger;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.PersonalisationConstants;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Eliza
 *
 */
public class CommunitiesHandler {

	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private final ICommunityPreferenceManager communityPrefMgr;
	private final ICisManager cisManager;
	private final ICommManager commsMgr;
	private IIdentityManager idManager;
	private final IServiceDiscovery serviceDiscovery;
	private final UserPreferenceManagement userPrefMgr;
	private Hashtable<String, PreferenceDetails> downloadTempTable;
	private Hashtable<String, PreferenceDetails> uploadTempTable;
	private final IUserFeedback userFeedback;
	private final UserPreferenceConditionMonitor pcm;


	//NEW
	private final IServices serviceMgmt;

	public CommunitiesHandler(UserPreferenceConditionMonitor pcm) {
		//TODO: need to check the timers
		this.pcm = pcm;

		this.communityPrefMgr = pcm.getCommunityPreferenceMgr(); 
		this.cisManager = pcm.getCisManager();
		this.commsMgr = pcm.getCommManager();
		this.serviceDiscovery = pcm.getServiceDiscovery();
		this.userPrefMgr = pcm.getPreferenceManager();
		this.userFeedback = pcm.getUserFeedbackMgr();
		this.serviceMgmt = pcm.getServiceMgmt();

		setIdManager(commsMgr.getIdManager());

		this.downloadTempTable = new Hashtable<String, PreferenceDetails>();
		this.uploadTempTable = new Hashtable<String, PreferenceDetails>();


	}

	public DownloaderTask scheduleDownload(Date date){
		Timer downloadTimer = new Timer();
		DownloaderTask downloadTimerTask = new DownloaderTask();
		downloadTimerTask.setDone(false);
		downloadTimer.schedule(downloadTimerTask, date);
		return downloadTimerTask;


	}

	public UploaderTask scheduleUpload(Date date){
		Timer uploadTimer = new Timer();
		UploaderTask uploadTimerTask = new UploaderTask();
		uploadTimerTask.setDone(false);
		uploadTimer.schedule(uploadTimerTask, date);
		return uploadTimerTask;
	}
	public void scheduleTasks(){
		Timer downloadTimer = new Timer();
		Calendar downloaderCalendar = Calendar.getInstance();
		//System.out.println(calendar.getTime().toString());
		downloaderCalendar.set(Calendar.HOUR, 21);
		downloaderCalendar.set(Calendar.MINUTE, 59);
		downloaderCalendar.set(Calendar.SECOND, 59);

		//System.out.println(calendar.getTime().toString());

		DownloaderTask downloadTimerTask = new DownloaderTask();
		downloadTimerTask.setDone(false);
		downloadTimer.schedule(downloadTimerTask, downloaderCalendar.getTime());


		Timer uploadTimer = new Timer();


		Calendar uploadCalendar = Calendar.getInstance();

		uploadCalendar.set(Calendar.HOUR, 23);
		uploadCalendar.set(Calendar.MINUTE, 59);
		uploadCalendar.set(Calendar.SECOND, 59);


		UploaderTask uploadTimerTask = new UploaderTask();
		uploadTimerTask.setDone(false);
		uploadTimer.schedule(uploadTimerTask, uploadCalendar.getTime());
	}


	public void scheduleDownloaderTask(Calendar downloaderCalendar){
		Timer downloadTimer = new Timer();
		DownloaderTask downloadTimerTask = new DownloaderTask();
		downloadTimerTask.setDone(false);
		downloadTimer.schedule(downloadTimerTask, downloaderCalendar.getTime());
	}

	public void scheduleUploaderTask (Calendar uploadCalendar){
		Timer uploadTimer = new Timer();
		UploaderTask uploadTimerTask = new UploaderTask();
		uploadTimerTask.setDone(false);
		uploadTimer.schedule(uploadTimerTask, uploadCalendar.getTime());
	}

	//**NEW**//
	public ServiceResourceIdentifier getClientServiceID(ServiceResourceIdentifier serviceServerID) {
		List<Service> services = new ArrayList<Service>();
		try {
			services = serviceDiscovery.getLocalServices().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		for(Service service : services) {
			if(ServiceUtils.compare(serviceServerID, serviceMgmt.getServerServiceIdentifier(service.getServiceIdentifier()))){
				return service.getServiceIdentifier();
			}
		}
		return null;
	}

/*	private void changeServiceResourceIdentifier(ServiceResourceIdentifier serviceID, IPreferenceTreeModel model) {

		model.getPreferenceDetails().setServiceID(serviceID);
		IPreference rootPreference = model.getRootPreference();

		Enumeration<IPreference> breadthFirstEnumeration = rootPreference.breadthFirstEnumeration();
		while (breadthFirstEnumeration.hasMoreElements()){
			IPreference node = breadthFirstEnumeration.nextElement();
			if (node.getUserObject() != null){
				if (node.getUserObject() instanceof PreferenceOutcome){
					PreferenceOutcome outcome = (PreferenceOutcome) node.getUserObject();
					outcome.setServiceID(serviceID);

				}
			}
		}

	}*/

	public class UploaderTask extends TimerTask{
		private Logger logging = LoggerFactory.getLogger(this.getClass());

		private boolean done = false;

		private List<String> list;



		@Override
		public void run() {
			if (logging.isDebugEnabled()){
				this.logging.debug("Scheduled run");
			}
			IIdentity userId = getIdManager().getThisNetworkNode();
			List<ICis> cisList = cisManager.getCisList();
			if(this.logging.isDebugEnabled()){
				this.logging.debug("Processing: "+cisList.size()+" CISs");
			}
			for (ICis cis: cisList){
				try {

					IIdentity cisId = getIdManager().fromJid(cis.getCisId());

					//List<Service> services = serviceDiscovery.getServices(cisId).get();
					List<Service> services = serviceDiscovery.getLocalServices().get();


					//**NEW**//
					HashMap <ServiceResourceIdentifier, ServiceResourceIdentifier> serviceServerID = new HashMap<ServiceResourceIdentifier, ServiceResourceIdentifier>();

					if(null!=services) {
						for(Service s : services) {
							serviceServerID.put(s.getServiceIdentifier(), serviceMgmt.getServerServiceIdentifier(s.getServiceIdentifier()));
						}
					} 

					//List<PreferenceDetails> matchingDetails = findRelevantPreferences(services, userPrefMgr.getPreferenceDetailsForAllPreferences());

					//logging.debug("Adding to matchingDetails:" + serviceServerID);
					List<PreferenceDetails> matchingDetails = findRelevantPreferences(serviceServerID, userPrefMgr.getPreferenceDetailsForAllPreferences());
					if(this.logging.isDebugEnabled()){
						this.logging.debug("Found relevant matching details: "+matchingDetails.size());
					}
					List<PreferenceDetails> allowedToUpload = new ArrayList<PreferenceDetails>();
					List<PreferenceDetails> toBeChecked = new ArrayList<PreferenceDetails>();
					for (PreferenceDetails prefDetail : matchingDetails){

						PreferenceDetails communityPreferenceManagerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(getIdManager(), prefDetail.getServiceID(), PersonalisationConstants.UPLOAD);


						IPreferenceOutcome preference = pcm.getPreferenceManager().getPreference(userId, communityPreferenceManagerDetails.getServiceType(), communityPreferenceManagerDetails.getServiceID(), communityPreferenceManagerDetails.getPreferenceName());
						if (preference!=null){
							if (preference.getvalue().equalsIgnoreCase(PersonalisationConstants.YES)){

								logging.debug("Adding to allowedToUpload: " + prefDetail.toString());
								allowedToUpload.add(prefDetail);
							}
						}else{
							toBeChecked.add(prefDetail);
							logging.debug("Adding to has to be checked: " + prefDetail.toString());
						}
					}


					List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
					for (PreferenceDetails detail : allowedToUpload){
						//ServiceResourceIdentifier serverID = detail.getServiceID();
						//ServiceResourceIdentifier localID = getClientServiceID(serverID);
						//detail.setServiceID(localID);
						IPreferenceTreeModel model = pcm.getPreferenceManager().getModel(userId, detail);
						if (model!=null){
							//changeServiceResourceIdentifier(serverID, model);
							models.add(model);
						}
					}


					if (models.size()>0){
						if(this.logging.isDebugEnabled()){
							this.logging.debug("Uploading: "+models.size()+" preferences to CIS: "+cisId);
						}

						communityPrefMgr.uploadUserPreferences(cisId, models);
					}
					if (toBeChecked.size()>0){
						logging.debug("Will send user feedback!");
						String[] userFriendlyListofDetails = getUserFriendlyListofDetails(toBeChecked, FeedbackType.UPLOAD);


						list = userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, new ExpProposalContent("Please select which of these preferences you want to upload to the CIS anonymously", userFriendlyListofDetails), new DownloadFeedbackListenerCallBack(cisId,uploadTempTable, pcm, FeedbackType.UPLOAD)).get();
					}


				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceDiscoveryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			this.setDone(true);
		}

		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

	}

	public class DownloaderTask extends TimerTask{
		private Logger logging = LoggerFactory.getLogger(this.getClass());


		private boolean done = false;


		@Override
		public void run() {
			List<ICis> cisList = cisManager.getCisList();
			for (ICis cis: cisList){
				try {
					IIdentity cisId = getIdManager().fromJid(cis.getCisId());
					IIdentity userId = getIdManager().getThisNetworkNode();
					List<PreferenceDetails> communityPreferenceDetails = communityPrefMgr.getCommunityPreferenceDetails(cisId);
					List<PreferenceDetails> listofPreferencesToDownload = new ArrayList<PreferenceDetails>();

					List<PreferenceDetails> listtoBeChecked = new ArrayList<PreferenceDetails>();
					//first check if there is a preference for downloading these 

					for (PreferenceDetails details : communityPreferenceDetails){



						PreferenceDetails managerDetails = PreferenceUtils.getCommunityPreferenceManagerDetails(getIdManager(), details.getServiceID(), PersonalisationConstants.DOWNLOAD);
						IPreferenceOutcome preference = CommunitiesHandler.this.userPrefMgr.getPreference(getIdManager().getThisNetworkNode(), managerDetails.getServiceType(), managerDetails.getServiceID(), managerDetails.getPreferenceName());
						if (preference!=null){
							if (preference.getvalue().matches(PersonalisationConstants.YES)){
								listofPreferencesToDownload.add(details);
							}
						}else{
							listtoBeChecked.add(details);
						}
					}


					//download the ones that we know the user wants to download

					List<IPreferenceTreeModel> downloadedCommunityPreferences = communityPrefMgr.getCommunityPreferences(cisId, listofPreferencesToDownload);


					for (IPreferenceTreeModel communityModel : downloadedCommunityPreferences){
						ServiceResourceIdentifier serviceClientID = getClientServiceID(communityModel.getPreferenceDetails().getServiceID());
						logging.debug("I am searching for: " + serviceClientID);

						if(null!=serviceClientID) {
							PreferenceDetails preferenceDetails = communityModel.getPreferenceDetails();
							preferenceDetails.setServiceID(serviceClientID);
							IPreferenceTreeModel model = userPrefMgr.getModel(null, preferenceDetails);

					//		changeServiceResourceIdentifier(serviceClientID, communityModel);
							
							if (model==null){
								
								userPrefMgr.storePreference(userId, preferenceDetails, communityModel.getRootPreference());
								pcm.processPreferenceChanged(userId, preferenceDetails.getServiceID(), preferenceDetails.getServiceType(), preferenceDetails.getPreferenceName());
							}else{
								PreferenceMerger merger = new PreferenceMerger(pcm.getUserFeedbackMgr());
								PreMerger preMerger = new PreMerger(pcm.getCtxBroker(), userId);
								IPreference replaceCtxIdentifiers = preMerger.replaceCtxIdentifiers(communityModel.getRootPreference());
								if (replaceCtxIdentifiers!=null){
									IPreference mergeTrees = merger.mergeTrees(model.getRootPreference(), replaceCtxIdentifiers, "");
									userPrefMgr.storePreference(userId, preferenceDetails, mergeTrees);
									pcm.processPreferenceChanged(userId, preferenceDetails.getServiceID(), preferenceDetails.getServiceType(), preferenceDetails.getPreferenceName());
								}
							}
						}
						else {
							logging.debug("Could not find client service ID of service:" + ServiceModelUtils.serviceResourceIdentifierToString(communityModel.getPreferenceDetails().getServiceID()));
						}
					}


					if (listtoBeChecked.size()>0){
						String[] options = getUserFriendlyListofDetails(listtoBeChecked, FeedbackType.DOWNLOAD);

						DownloadFeedbackListenerCallBack feedbackListener = new DownloadFeedbackListenerCallBack(cisId,downloadTempTable, pcm, FeedbackType.DOWNLOAD);
						userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, new ExpProposalContent("Please select which community preferences you want to download", options), feedbackListener).get();
					}
					this.setDone(true);
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

		}



		public boolean isDone() {
			return done;
		}

		public void setDone(boolean done) {
			this.done = done;
		}

	}

	private String[] getUserFriendlyListofDetails(List<PreferenceDetails> communityPreferenceDetails, FeedbackType type) {

		List<String> options = new ArrayList<String>();
		if (type.equals(FeedbackType.DOWNLOAD)){
			this.downloadTempTable.clear();
		}else{
			this.uploadTempTable.clear();	
		}
		for (PreferenceDetails d: communityPreferenceDetails){
			String key = "PreferenceName: "+d.getPreferenceName()+" for ServiceID: "+ServiceModelUtils.serviceResourceIdentifierToString(d.getServiceID());
			options.add(key);
			if (type.equals(FeedbackType.DOWNLOAD)){
				this.downloadTempTable.put(key, d);
			}else{
				this.uploadTempTable.put(key, d);
			}
		}


		return options.toArray(new String[options.size()]);
	}

	private List<PreferenceDetails> findRelevantPreferences(HashMap<ServiceResourceIdentifier, ServiceResourceIdentifier> servicesID, List<PreferenceDetails> details){

		logging.debug("Find rel pref");
		logging.debug(servicesID.size() + " size. Details size: " + details.size());
		List<PreferenceDetails> preferences = new ArrayList<PreferenceDetails>();
		Set<ServiceResourceIdentifier> clientServiceIDs = servicesID.keySet();
		for (ServiceResourceIdentifier serviceIdentifier: clientServiceIDs){
			//ServiceResourceIdentifier serviceIdentifier = id.getServiceIdentifier();
			for (PreferenceDetails detail : details){
				logging.debug(detail.getServiceID().getServiceInstanceIdentifier() + " = " + serviceIdentifier.getServiceInstanceIdentifier());
				if (ServiceModelUtils.compare(serviceIdentifier, detail.getServiceID())){
		//!			detail.setServiceID(servicesID.get(serviceIdentifier));
					preferences.add(detail);
				}
			}
		}

		return preferences;
	}

	private List<PreferenceDetails> findRelevantPreferenceDetails(List<Service> services, List<PreferenceDetails> details){
		ArrayList<PreferenceDetails> preferences = new ArrayList<PreferenceDetails>(); 
		IIdentity userId = this.getIdManager().getThisNetworkNode();
		for (Service service: services){
			ServiceResourceIdentifier serviceIdentifier = service.getServiceIdentifier();
			for (PreferenceDetails detail : details){
				if (ServiceModelUtils.compare(serviceIdentifier, detail.getServiceID())){
					preferences.add(detail);
				}
			}
		}

		return preferences;
	}

	public IIdentityManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IIdentityManager idManager) {
		this.idManager = idManager;
	}
}
