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


package org.societies.cis.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import java.util.Iterator;
import java.util.List;

import java.util.concurrent.Future;



import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.activity.ActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICis;

import org.societies.api.comm.xmpp.datatypes.Stanza;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;

import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;

import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;

import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.cis.manager.Cis;

import org.springframework.scheduling.annotation.AsyncResult;



import org.societies.api.schema.cis.community.Community;


import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.Join;
import org.societies.api.schema.cis.community.Leave;
//import org.societies.api.schema.cis.community.Leave;
import org.societies.api.schema.cis.community.MembershipCrit;


import org.societies.api.schema.cis.community.Join;
//import org.societies.api.schema.cis.community.Leave;
import org.societies.api.schema.cis.community.Participant;


import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;

import org.societies.api.schema.cis.manager.Delete;
import org.societies.api.schema.cis.manager.DeleteMemberNotification;


// this is the class which manages all the CIS from a CSS
// for the class responsible for editing and managing each CIS instance, consult the CIS

/**
 * @author Thomas Vilarinho (Sintef)
*/

public class CisManager implements ICisManager, IFeatureServer{//, ICommCallback{

	

	List<Cis> ownedCISs; 
	ICISCommunicationMgrFactory ccmFactory;
	IIdentity cisManagerId;
	ICommManager iCommMgr;
	List<CisSubscribedImp> subscribedCISs;
	private SessionFactory sessionFactory;
	ICisDirectoryRemote iCisDirRemote;

	IServiceDiscoveryRemote iServDiscRemote;
	IServiceControlRemote iServCtrlRemote;
	private IPrivacyPolicyManager privacyPolicyManager;


	
	public void startup(){
		//ActivityFeed ret = null;
	
		Session session = sessionFactory.openSession();
		try{
			this.ownedCISs = session.createCriteria(Cis.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
			this.subscribedCISs = session.createCriteria(CisSubscribedImp.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

			LOG.info("Nb of subscri CIS is " + this.subscribedCISs.size());
		}catch(Exception e){
			LOG.error("CISManager startup queries failed..");
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		Iterator<Cis> it = ownedCISs.iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 element.startAfterDBretrieval(this.getSessionFactory(),this.getCcmFactory(),this.privacyPolicyManager);
			 element.setiServCtrlRemote(this.iServCtrlRemote);
			 element.setiServDiscRemote(this.iServDiscRemote);
	     }
		
	//	for(Cis cis : ownedCISs){
	//		cis.startAfterDBretrieval(this.getSessionFactory(),this.getCcmFactory());
	//	}
		Iterator<CisSubscribedImp> i = this.subscribedCISs.iterator();
		 
		while(i.hasNext()){
			CisSubscribedImp element = i.next();
			 element.startAfterDBretrieval(this);
	     }
				
	}

	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
					"http://societies.org/api/schema/activityfeed",	  		
					"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
		//	.singletonList("org.societies.api.schema.cis.manager");
			.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
					"org.societies.api.schema.activityfeed",
					"org.societies.api.schema.cis.community"));

	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);

	public CisManager() {
			this.ownedCISs = new ArrayList<Cis>();	
			this.subscribedCISs = new ArrayList<CisSubscribedImp>();
			


	}
	public void init(){
		while (iCommMgr.getIdManager() ==null)
			;//just wait untill the XCommanager is ready
		
		cisManagerId = iCommMgr.getIdManager().getThisNetworkNode();
		LOG.info("Jid = " + cisManagerId.getBareJid() + ", domain = " + cisManagerId.getDomain() );



		try {
			iCommMgr.register((IFeatureServer)this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} // TODO unregister??

		LOG.info("listener registered");	
		//polManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
		startup();
		LOG.info("CISManager started up with "+this.ownedCISs.size()
				+" owned CISes and "+this.subscribedCISs.size()+" subscribed CISes");
	}


	public IServiceDiscoveryRemote getiServDiscRemote() {
		return iServDiscRemote;
	}
	public void setiServDiscRemote(IServiceDiscoveryRemote iServDiscRemote) {
		this.iServDiscRemote = iServDiscRemote;
	}
	public IServiceControlRemote getiServCtrlRemote() {
		return iServCtrlRemote;
	}
	public void setiServCtrlRemote(IServiceControlRemote iServCtrlRemote) {
		this.iServCtrlRemote = iServCtrlRemote;
	}



	
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}



	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}


	
	public ICommManager getICommMgr() {
		return iCommMgr;
	}



	public void setICommMgr(ICommManager cSSendpoint) {
		iCommMgr = cSSendpoint;
	}


	public ICisDirectoryRemote getiCisDirRemote() {
		return iCisDirRemote;
	}
	public void setiCisDirRemote(ICisDirectoryRemote iCisDirRemote) {
		this.iCisDirRemote = iCisDirRemote;
	}


	/**
	 * Create a new CIS for the CSS represented by cssId. Password is needed and is the
	 * same as the CSS password.
	 * After this method is called a CIS is created with mode set to mode.
	 * 
	 * The CSS who creates the CIS will be the owner. Ownership can be changed
	 * later.
	 * 
	 * TODO: define what values mode can have and what each means.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId and cssPassword are to recognise the user
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * @param mode membership type, e.g 1= read-only.
	 * TODO define mode better.
	 * @return link to the {@link ICisEditor} representing the new CIS, or 
	 * null if the CIS was not created.
	 */
	
	@Override
	public Future<ICisOwned> createCis(String cisName, String cisType,
			Hashtable<String, MembershipCriteria> cisCriteria,
			String description) {

		String pPolicy = "<RequestPolicy></RequestPolicy>";	
		ICisOwned i = this.localCreateCis(cisName, cisType, description,cisCriteria ,pPolicy);
			return new AsyncResult<ICisOwned>(i);
	}
	@Override
	public Future<ICisOwned> createCis(String cisName, String cisType,
			Hashtable<String, MembershipCriteria> cisCriteria,
			String description, String privacyPolicy) {
	

		ICisOwned i = this.localCreateCis(cisName, cisType,  description,cisCriteria,privacyPolicy);
			return new AsyncResult<ICisOwned>(i);
	}
	
	
	
	private Cis getOwnedCisByJid(String jid){
		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisRecord().getCisJID().equals(jid))
				 return element;
	     }
		return null;
		
	}
	
	
	// local version of the deleteCIS
	private boolean deleteOwnedCis(String cisJid){

		
		boolean ret = false;
		if(getOwnedCISs().contains(new Cis(new CisRecord(cisJid)))){
			Cis cis = this.getOwnedCisByJid(cisJid);
			ret = cis.deleteCIS();
			ret = ret && getOwnedCISs().remove(cis);
		}
		
		return ret;
	}
	

	
	
	
	// local version of the createCis
	private ICisOwned localCreateCis(String cisName, String cisType, String description, Hashtable<String, MembershipCriteria> cisCriteria, String privacyPolicy) {
		// TODO: how do we check fo the cssID/pwd?
		//if(cssId.equals(this.CSSendpoint.getIdManager().getThisNetworkNode().getJid()) == false){ // if the cssID does not match with the host owner
		//	LOG.info("cssID does not match with the host owner");
		//	return null;
		//}
		
		// -- Verification
		// Dependency injection
		if (!isDepencyInjectionDone(1)) {
			LOG.error("[Dependency Injection] CisManager::createCis not ready");
			return null;
		}
		// Parameters
		if ((null == privacyPolicy || "".equals(privacyPolicy))) {
			return null;
		}
				
		// TODO: review this logic as maybe I should probably check if it exists before creating


		Cis cis = new Cis(this.cisManagerId.getBareJid(), cisName, cisType, 
		this.ccmFactory,this.iServDiscRemote, this.iServCtrlRemote,this.privacyPolicyManager,this.sessionFactory
		,description,cisCriteria);
		if(cis == null)
			return cis;

		// PRIVACY POLICY CODE

		try {
			IIdentity cssOwnerId = this.cisManagerId;
			IIdentity cisId = iCommMgr.getIdManager().fromJid(cis.getCisId());
			RequestorCis requestorCis = new RequestorCis(cssOwnerId, cisId);
			privacyPolicyManager.updatePrivacyPolicy(privacyPolicy, requestorCis);			
		} catch (InvalidFormatException e) {
			LOG.error("CIS or CSS jid came in bad format");
			e.printStackTrace();
			return null;
		} catch (PrivacyException e) {
			LOG.error("The privacy policy can't be stored.", e);
			if (null != cis) {
				cis.deleteCIS();
				//cis.unregisterCIS();
			}
			LOG.error("CIS deleted.");
			e.printStackTrace();
			return null;
		}

		
		
		//
		
		
		// persisting
		//LOG.info("setting sessionfactory for new cis..: "+sessionFactory.hashCode());
		//this.persist(cis);
		//cis.setSessionFactory(sessionFactory);


		// advertising the CIS to global CIS directory
		CisAdvertisementRecord cisAd = new CisAdvertisementRecord();
		//cisAd.setMode(0);//TODO: update this
		MembershipCrit m = new MembershipCrit();
		Hashtable<String, MembershipCriteria> h= cis.cisCriteria;
		// TODO: add membership criteria in CISAdv
		
		//cisAd.setMembershipCrit();
		cisAd.setName(cis.getName());
		cisAd.setUri(cis.getCisId());
		cisAd.setType(cis.getCisType());
		cisAd.setId(cis.getCisId()); // TODO: check if the id or uri needs the jid
		this.iCisDirRemote.addCisAdvertisementRecord(cisAd);

		
		
		if (getOwnedCISs().add(cis)){
			ICisOwned i = cis;
			return i;
		}else{
			return null;
		}
		
	}

	// internal method used to register that the user has subscribed into a CIS
	// it is triggered by the subscription notification on XMPP
	// TODO: review
	public boolean subscribeToCis(CisRecord i) {

		if(! this.subscribedCISs.contains(new Cis(i))){
			CisSubscribedImp csi = new CisSubscribedImp (new CisRecord(i.getCisName(), i.getCisJID()), this);			
			this.subscribedCISs.add(csi);
			this.persist(csi);
			return true;
		}
		return false;
		
	}
	
	// internal method used to leave from a CIS
	// this is triggered by the receipt of a confirmation of a leave
	// TODO: review
	public boolean unsubscribeToCis(String cisjid) {

		if(subscribedCISs.contains(new CisSubscribedImp(new CisRecord(cisjid)))){
			
			CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(cisjid));
			temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object
			
			if(this.subscribedCISs.remove(temp)) {// removing it from the list
				this.deletePersisted(temp); // removing it from the database
				return true;
			}
			else{
				return false;
			}
		}else{
			return false;
		}
		

	}



/*
	public List<CisRecord> getOwnedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>();

		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 l.add(element.getCisRecord());
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		return l;
	}

	public List<CisRecord> getSubscribedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>(this.subscribedCISs);
		return l;
	}*/



	
	
	@Override
	public List<String> getJavaPackages() {
		return  PACKAGES;
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		// all received IQs contain a community element
		
		LOG.info("get Query received");
		if (payload.getClass().equals(org.societies.api.schema.cis.manager.CommunityManager.class)) {
			CommunityManager c = (CommunityManager) payload;

			if (c.getCreate() != null && c.getCreate().getCommunity() != null) {
				
				// CREATE CIS
				LOG.info("create received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to create a CIS
				
				Create create = c.getCreate(); 
				
				//String ownerJid = create.getCommunity().getOwnerJid(); // TODO: owner must be retrieved other way
				//String cisJid = create.getCommunityJid();
				String cisType = create.getCommunity().getCommunityType();
				String cisName = create.getCommunity().getCommunityName();
				String cisDescription;
				if(create.getCommunity().getDescription() != null)
					cisDescription = create.getCommunity().getDescription();
				else
					cisDescription = "";
				//int cisMode = create.getMembershipMode().intValue();

				if(cisType != null && cisName != null){
					String pPolicy = "<RequestPolicy></RequestPolicy>";						
					Hashtable<String, MembershipCriteria> h = null;
					
					MembershipCrit m = create.getCommunity().getMembershipCrit();
					if (m!=null && m.getCriteria() != null && m.getCriteria().size()>0){
						h =new Hashtable<String, MembershipCriteria>();
						
						// populate the hashtable
						for (Criteria crit : m.getCriteria()) {
							MembershipCriteria meb = new MembershipCriteria();
							meb.setRank(crit.getRank());
							Rule r = new Rule();
							if( r.setOperation(crit.getOperator()) == false) {create.setResult(false); return c;}
							ArrayList<String> a = new ArrayList<String>();
							a.add(crit.getValue1());
							if (crit.getValue2() != null && !crit.getValue2().isEmpty()) a.add(crit.getValue2()); 
							if( r.setValues(a) == false) {create.setResult(false); return c;}
							meb.setRule(r);
							h.put(crit.getAttrib(), meb);
							
						}
					}
					
					ICisOwned icis = localCreateCis( cisName, cisType, cisDescription,h,pPolicy);
		
						
					create.getCommunity().setCommunityJid(icis.getCisId());
					LOG.info("CIS with self assigned ID Created!!");

					return c;  
				}
				else{
					create.setResult(false);
					LOG.info("missing parameter on the create");
					
					// if one of those parameters did not come, we should return an error
					return c;
				}
				// END OF CREATE CIS					

			}
			if (c.getList() != null) {
				LOG.info("list received");
				
				String listingType = "owned"; // default is owned
				if(c.getList().getListCriteria() !=null)
					listingType = c.getList().getListCriteria();
								
				// TODO: redo the list
/*				Communities com = new Communities();
				
				if(listingType.equals("owned") || listingType.equals("all")){
				// GET LIST CODE of ownedCIS
					
					Iterator<Cis> it = ownedCISs.iterator();
					
					while(it.hasNext()){
						CisRecord element = it.next().getCisRecord();
						CisCommunity community = new CisCommunity();
						community.setCommunityJid(element.getCisJID());
						com.getCisCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}

				// GET LIST CODE of subscribedCIS
				if(listingType.equals("subscribed") || listingType.equals("all")){
					//List<CisRecord> li = this.getSubscribedCisList();
					Iterator<CisSubscribedImp> it = subscribedCISs.iterator();
					
					while(it.hasNext()){
						CisSubscribedImp element = it.next();
						CisCommunity community = new CisCommunity();
						community.setCommunityJid(element.getCisId());
						com.getCisCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}*/
				
				return c;

			}
				// END OF LIST
				
			// DELETE CIS
			if (c.getDelete() != null) {

				LOG.info("delete CIS received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to delete a CIS
				
				Delete delete = c.getDelete();
				Delete d2 = new Delete();
				
				if(!this.deleteOwnedCis(delete.getCommunityJid()))
					d2.setValue("error"); // TODO: replace for a proper XMPP error message

				c.setDelete(d2);
				return c;
			}
			// END OF DELETE
				

			if (c.getConfigure() != null) {
				LOG.info("configure received");
				return c;
			}

			
		}
		return null;

	}



	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}



	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("message received with class, id, from: " + payload.getClass() + " , " + stanza.getId() + " , " + stanza.getFrom().getBareJid());
		if (payload.getClass().equals(org.societies.api.schema.cis.manager.CommunityManager.class)) {

			CommunityManager c = (CommunityManager) payload;

			// treating getSubscribedTo notifications
			if (c.getNotification().getSubscribedTo()!= null) {
				LOG.info("subscribedTo received");
				this.subscribeToCis(new CisRecord(c.getNotification().getSubscribedTo().getCommunityName(), c.getNotification().getSubscribedTo().getCommunityJid()));
				
				
				/*	if(this.subscribedCISs.contains(new CisRecord(c.getNotification().getSubscribedTo().getCisJid()))){
						LOG.info("CIS is already part of the list of subscribed CISs");
					}
					else{
						SubscribedTo s = (SubscribedTo) c.getNotification().getSubscribedTo();
						CisRecord r = new CisRecord(s.getCisJid());
						this.subscribeToCis(r);
					}*/
				return;
			}
			
			// treating delete CIS notifications
			if (c.getNotification().getDeleteNotification() != null) {
				LOG.info("delete notification received");
				this.unsubscribeToCis(c.getNotification().getDeleteNotification().getCommunityJid());
/*				DeleteNotification d = (DeleteNotification) c.getNotification().getDeleteNotification();
				if(!this.subscribedCISs.contains(new CisRecord(d.getCommunityJid()))){
					LOG.info("CIS is not part of the list of subscribed CISs");
				}
				else{
					CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(d.getCommunityJid()));
					temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object

					
					this.subscribedCISs.remove(temp);// removing it from the list
					this.deletePersisted(temp); // removing it from the database
				}
				return;*/
			}
			
			// treating deleteMember notifications
			if (c.getNotification().getDeleteMemberNotification() != null) {
				LOG.info("delete member notification received");
				DeleteMemberNotification d = (DeleteMemberNotification) c.getNotification().getDeleteMemberNotification();
				if(d.getMemberJid() != this.cisManagerId.getBareJid()){
					LOG.warn("delete member notification had a different member than me...");
				}
				if(!this.subscribedCISs.contains(new CisRecord(d.getCommunityJid()))){
					LOG.info("CIS is not part of the list of subscribed CISs");
				}
				else{
					CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(d.getCommunityJid()));
					temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object

					
					this.subscribedCISs.remove(temp);// removing it from the list
					this.deletePersisted(temp); // removing it from the database
				}
				return;
			}
		}
		if (payload.getClass().equals(Community.class)) {

			Community c = (Community) payload;

			// treating new member notifications
			if (c.getWho() != null) {
				LOG.info("new member joined a CIS notification received");
				// TODO: do something? or maybe remove those notifications
				return;
			}
			

		}
		
		
	}



	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Deprecated
	public boolean deleteCis(String cssId, String cssPassword, String cisId){
		return false;
	}


	@Override
	public boolean deleteCis(String cisId) {
		// TODO Auto-generated method stub
		return 	this.deleteOwnedCis(cisId);
	}

	@Override
	public List<ICis> getCisList(){
		
		// add subscribed CIS to the list to be returned
		List<ICis> l = new ArrayList<ICis>();
		l.addAll(subscribedCISs);

	
		l.addAll(ownedCISs);
		
		return l;
	}
	
	@Override
	public List<ICis> searchMyCisByName(String name){
		// add subscribed CIS to the list to be returned
		List<ICis> l = new ArrayList<ICis>();
		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 if(element.getName().contains(name))
			 l.add(element);
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		Iterator<CisSubscribedImp> it2 = this.getSubscribedCISs().iterator();
		while(it2.hasNext()){
			CisSubscribedImp element = it2.next();
			 if(element.getName().contains(name))
			 l.add(element);
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		return l;
		
	}
	
	@Override
	public List<ICisOwned> getListOfOwnedCis(){
		
		// add subscribed CIS to the list to be returned
		List<ICisOwned> l = new ArrayList<ICisOwned>();
		l.addAll(ownedCISs);
		
		return l;
	}

	@Override
	public List<ICis> getRemoteCis(){
			List<ICis> l = new ArrayList<ICis>();
			l.addAll(subscribedCISs);
			
			return l;
	}

	@Override
	public ICis[] getCisList(ICis arg0) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean requestNewCisOwner(String arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return false;
	}



	
	
	/**
	 * Get a CIS Record with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the CISRecord with the ID cisID, or null if no such CIS exists.
	 */
	@Override
	public ICis getCis(String cisId) {
		
		// first we check it on the owned CISs		
		Iterator<Cis> it = getOwnedCISs().iterator();
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisId().equals(cisId))
				 return element;
	     }
		
		// then we check on the subscribed CISs
		Iterator<CisSubscribedImp> iterator = this.subscribedCISs.iterator();
		while(iterator.hasNext()){
			CisSubscribedImp element = iterator.next();
			 if (element.getCisId().equals(cisId))
				 return element;
	     }
		
		
		return null;
	}



	@Override
	public ICisOwned getOwnedCis(String cisId) {
		// first we check it on the owned CISs		
		Iterator<Cis> it = getOwnedCISs().iterator();
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisId().equals(cisId))
				 return element;
	     }
		
		return null;
	}
	
	// session related methods

	private void persist(Object o){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(o);
			t.commit();
			LOG.info("Saving CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Saving CIS object failed, rolling back");
		}finally{
			if(session!=null){
				session.close();
				session = sessionFactory.openSession();
				LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				session.close();
			}
			
		}
	}
	
	private void updatePersisted(Object o){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(o);
			t.commit();
			LOG.info("Updated CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Updating CIS object failed, rolling back");
		}finally{
			if(session!=null){
				session.close();
				session = sessionFactory.openSession();
				LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				session.close();
			}
			
		}
	}
	
	private void deletePersisted(Object o){
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.delete(o);
			t.commit();
			LOG.info("Deleting object in CisManager succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Deleting object in CisManager failed, rolling back");
		}finally{
			if(session!=null){
				session.close();
				session = sessionFactory.openSession();
				LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				session.close();
			}
			
		}
	}
	
	public  SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		LOG.info("in setsessionfactory!! sessionFactory is: "+sessionFactory);
		//ActivityFeed.setStaticSessionFactory(sessionFactory);
		for(Cis cis : ownedCISs)
			cis.setSessionFactory(sessionFactory);
	}

	

	// getters and setters
	
	
	public List<Cis> getOwnedCISs() {
		return ownedCISs;
	}

	public List<CisSubscribedImp> getSubscribedCISs() {
		return subscribedCISs;
	}


// client methods
	
	@Override
	public void joinRemoteCIS(CisAdvertisementRecord adv, ICisManagerCallback callback) {
		
		LOG.debug("client call to join a RemoteCIS");

		// TODO: check with privacy
		
		// TODO: get qualifications
		

		IIdentity toIdentity;
		try {
			toIdentity = this.iCommMgr.getIdManager().fromJid(adv.getId());
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this);

			Community c = new Community();

			c.setJoin(new Join());

			try {
				LOG.info("Sending stanza with join");
				this.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the join");
			e1.printStackTrace();
		}
	}

	@Override
	public void leaveRemoteCIS(String cisId, ICisManagerCallback callback){
		LOG.debug("client call to leave a RemoteCIS");


		IIdentity toIdentity;
		try {
			toIdentity = this.iCommMgr.getIdManager().fromJid(cisId);
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this);

			Community c = new Community();

			c.setLeave(new Leave());
			try {
				LOG.info("Sending stanza with leave");
				this.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the join");
			e1.printStackTrace();
		}
	}

	
	public void UnRegisterCisManager(){
		// unregister all its CISs
		for(Cis c : ownedCISs ){
			c.unregisterCIS();
		}
		
	}

	
	/* ***********************************
	 *         Dependency Injection      *
	 *************************************/
	
	/**
	 * @param privacyPolicyManager the privacyPolicyManager to set
	 */
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[Dependency Injection] IPrivacyPolicyManager injected");
	}
	
	
	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == iCommMgr) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == iCommMgr.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (level >= 1) {
			if (null == privacyPolicyManager) {
				LOG.info("[Dependency Injection] Missing IPrivacyPolicyManager");
				return false;
			}
		}
		return true;
	}


}
