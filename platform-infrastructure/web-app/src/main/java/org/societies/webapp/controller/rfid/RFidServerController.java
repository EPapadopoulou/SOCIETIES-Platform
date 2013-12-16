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

package org.societies.webapp.controller.rfid;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.webapp.controller.BasePageController;
import org.societies.api.css.devicemgmt.rfid.RfidReader;
import org.societies.api.css.devicemgmt.rfid.RfidWakeupUnit;
import org.societies.webapp.service.UserService;

@ManagedBean(name = "rfidServerController")
@SessionScoped
public class RFidServerController extends BasePageController {

	private static final String RFID_SERVER_EVENT_TYPE = "org/societies/rfid/server";

	@ManagedProperty(value = "#{userService}")
	private UserService userService; // NB: MUST include public getter/setter

	@ManagedProperty(value= "#{eventManager}")
	private IEventMgr eventManager; 

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commManager;

	private List<RfidBean> rfidBeans;
	private RfidBean selectedRfidBean;
	private RfidBean addRfidBean;

	private RfidWakeupUnit selectedWakeupUnit;


	private ArrayList<RfidWakeupUnit> rfidWakeupUnits;
	private ContextRetriever contextRetriever;
	
	private RfidWakeupUnit newRfidWakeupUnit;

	private IIdentityManager idManager;

	private IIdentity serverIdentity;

	private String newWakeUpLocDropDown;
	private String newWakeUpLocText;


	public RFidServerController() {
		// controller constructor - called every time this page is requested!
		this.setRfidBeans(new ArrayList<RfidBean>());
		this.addRfidBean = new RfidBean();
		this.selectedRfidBean = new RfidBean();
		this.rfidWakeupUnits = new ArrayList<RfidWakeupUnit>();
		this.newRfidWakeupUnit = new RfidWakeupUnit();
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@PostConstruct
	public void retrieveRFIDRecords(){
		this.contextRetriever = new ContextRetriever(ctxBroker, serverIdentity);
		this.getRfidBeans();
		this.getWakeupUnits();

	}

	public void deleteWakeupWithLoc(String loc) {
		List<RfidWakeupUnit> matchedUnits = new ArrayList<RfidWakeupUnit>();
		for(RfidWakeupUnit unit : this.rfidWakeupUnits)
		{
			if(loc.equals(unit.getScreenID()))
			{
				matchedUnits.add(unit);
			}
				}
		if(matchedUnits.size()>0)
		{
			for(RfidWakeupUnit unitToDelete : matchedUnits)
			{
				deleteWakeupUnit(unitToDelete);
				//this.rfidWakeupUnits.remove(unitToDelete);
			}
		}
	}
	
	public void deleteWakeupUnit() {
		deleteWakeupUnit(this.selectedWakeupUnit);
	}
	
	public void deleteWakeupUnit(RfidWakeupUnit wakeupUnit){
		if(log.isDebugEnabled()) log.debug("Delete wakeup record");
		InternalEvent event = new InternalEvent(RFID_SERVER_EVENT_TYPE, "deleteWakeupUnit", this.getClass().getName(), wakeupUnit);
		try {
			this.eventManager.publishInternalEvent(event);
			if(log.isDebugEnabled()) log.debug("Published deletion event");
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.rfidWakeupUnits.remove(wakeupUnit);
	}

	
	public void getWakeupUnits() {
		this.rfidWakeupUnits=this.contextRetriever.getRfidWakeupUnits();

	}

	public void addRfidWakeupUnit() {
		this.log.debug("IN ADD WAKEUP");
		if(null != this.newRfidWakeupUnit.getScreenID() && !this.newRfidWakeupUnit.getScreenID().trim().isEmpty()
				&& null != this.newRfidWakeupUnit.getWakeupUnitNumber() && !this.newRfidWakeupUnit.getWakeupUnitNumber().trim().isEmpty())
		{
			//CHECK TO SEE IF ONE ALREADY EXISTS
			boolean wakeupFound = false;
			FacesMessage msg = null;
			for(RfidWakeupUnit wakeupUnit : this.rfidWakeupUnits)
			{
				if(wakeupUnit.getScreenID().equals(this.newRfidWakeupUnit.getScreenID())
						|| wakeupUnit.getWakeupUnitNumber().equals(this.newRfidWakeupUnit.getWakeupUnitNumber()))
				{
					wakeupFound = true;
					msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "RFID WakeupUnit", "A Wakeup Unit with similar properties already exist!");
					break;
				}
			}
			
			if(!wakeupFound)
			{
				this.rfidWakeupUnits.add(this.newRfidWakeupUnit);
				InternalEvent event = new InternalEvent(RFID_SERVER_EVENT_TYPE, "addNewWakeupUnit", this.getClass().getName(), this.newRfidWakeupUnit);
				try {
					this.eventManager.publishInternalEvent(event);
					if(log.isDebugEnabled()) log.debug("Published add new wakeupUnit event");
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RFID WakeupUnit", "Wakeup Unit has successfully been added!");

			}
			this.newRfidWakeupUnit = new RfidWakeupUnit();
			FacesContext.getCurrentInstance().addMessage(null, msg);
			RequestContext.getCurrentInstance().addCallbackParam("wakeupAdded", wakeupFound);
		}		

	}

	public void addTag(){

		if(log.isDebugEnabled()) log.debug("Add button clicked");
		FacesMessage msg = null;
		RequestContext context = RequestContext.getCurrentInstance();
		boolean tagAdded = false;
		if(addRfidBean.getRfidTag()!=null && !addRfidBean.getRfidTag().trim().isEmpty())
		{
			this.rfidBeans.add(addRfidBean);
			Hashtable<String, String> hash = new Hashtable<String, String>();
			hash.put("tag", this.addRfidBean.getRfidTag());
			if (!(this.addRfidBean.getPassword()==null || this.addRfidBean.getPassword()=="")){
				hash.put("password", this.addRfidBean.getPassword());
			}

			InternalEvent event = new InternalEvent(RFID_SERVER_EVENT_TYPE, "addNewTag", this.getClass().getName(), hash);
			try {
				this.eventManager.publishInternalEvent(event);
				if(log.isDebugEnabled()) log.debug("Published add new tag event");
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tagAdded = true;
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "RFID Tag", "RFID Tag successfully added!");
		}
		else
		{
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "RFID Tag", "Please enter a value RFID Tag!");
		}
		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("tagAdded", tagAdded);


	}


	public void deleteTag(){
		if(log.isDebugEnabled()) log.debug("Delete rfid record");
		Hashtable<String, String> hash = new Hashtable<String, String>();


		hash.put("tag", this.selectedRfidBean.getRfidTag());
		InternalEvent event = new InternalEvent(RFID_SERVER_EVENT_TYPE, "deleteTag", this.getClass().getName(), hash);
		try {
			this.eventManager.publishInternalEvent(event);
			if(log.isDebugEnabled()) log.debug("Published deletion event");
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}



	@PostConstruct
	public void retrieveRfidInfo(){

	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		idManager = commManager.getIdManager();
		this.commManager = commManager;
		this.serverIdentity = idManager.getThisNetworkNode();
	}

	public RfidBean getSelectedRfidBean() {
		if(log.isDebugEnabled()) log.debug("Get selectedRfidBean");
		return selectedRfidBean;

	}

	public void setSelectedRfidBean(RfidBean selectedRfidBean) {
		this.selectedRfidBean = selectedRfidBean;
		if(log.isDebugEnabled()) log.debug("Set selectedRfidBean");
	}

	public List<RfidBean> getRfidBeans() {
		this.rfidBeans = new ArrayList<RfidBean>();
		this.contextRetriever.getAllFromContext();
		Hashtable<String,String> tagToIdentity = contextRetriever.getTagToIdentity();
		Hashtable<String,String> tagToPassword = contextRetriever.getTagToPassword();
		Hashtable<String,String> tagToSymloc = contextRetriever.getTagToSymloc();
		Hashtable<String,String> tagToTime = contextRetriever.getTagToTime();

		if(log.isDebugEnabled()) log.debug("GOT BEANS: " + tagToSymloc.toString());

		Enumeration<String> rfidTags = tagToPassword.keys();

		while(rfidTags.hasMoreElements()){
			String rfidTag = rfidTags.nextElement();
			RfidBean bean = new RfidBean();
			bean.setRfidTag(rfidTag);
			bean.setPassword(tagToPassword.get(rfidTag));
			bean.setSymLoc(tagToSymloc.get(rfidTag));
			bean.setTime(tagToTime.get(rfidTag));
			if (tagToIdentity.containsKey(rfidTag)){
				bean.setUserIdentity(tagToIdentity.get(rfidTag));
			}
			this.rfidBeans.add(bean);
		}
		return rfidBeans;
	}

	public void setRfidBeans(List<RfidBean> rfidBeans) {
		this.rfidBeans = rfidBeans;
	}

	public RfidBean getAddRfidBean() {
		if(log.isDebugEnabled()) log.debug("Get addRfidBean");
		return addRfidBean;
	}

	public void setAddRfidBean(RfidBean addRfidBean) {
		this.addRfidBean = addRfidBean;
		if(log.isDebugEnabled()) log.debug("Set addRfidBean");

	}

	public RfidWakeupUnit getNewRfidWakeupUnit() {
		return newRfidWakeupUnit;
	}

	public void setNewRfidWakeupUnit(RfidWakeupUnit newRfidWakeupUnit) {
		this.newRfidWakeupUnit = newRfidWakeupUnit;
	}



	public ArrayList<RfidWakeupUnit> getRfidWakeupUnits() {
		return rfidWakeupUnits;
	}

	public String getNewWakeUpLocDropDown() {
		return newWakeUpLocDropDown;
	}

	public void setNewWakeUpLocDropDown(String newWakeUpLocDropDown) {
		this.newWakeUpLocDropDown = newWakeUpLocDropDown;
	}

	public String getNewWakeUpLocText() {
		return newWakeUpLocText;
	}

	public void setNewWakeUpLocText(String newWakeUpLocText) {
		this.newWakeUpLocText = newWakeUpLocText;
	}
	
	public RfidWakeupUnit getSelectedWakeupUnit() {
		return selectedWakeupUnit;
	}

	public void setSelectedWakeupUnit(RfidWakeupUnit selectedWakeupUnit) {
		this.selectedWakeupUnit = selectedWakeupUnit;
	}



}
