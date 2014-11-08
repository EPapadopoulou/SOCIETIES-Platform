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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache.PreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.AccessControlPreferenceCreator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.AttrSelPreferenceCreator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.DObfPreferenceCreator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.IDSPreferenceCreator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.NegotiationListener;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.PPNPreferenceMerger;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.PrivacyPreferenceConditionMonitor;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.accessCtrl.AccCtrlMonitor;

/**
 * @author Elizabeth
 *
 */

public class PrivacyPreferenceManager implements IPrivacyPreferenceManager{

	private PreferenceCache prefCache;
	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker ctxBroker;

	private ITrustBroker trustBroker;

	private PrivacyPreferenceConditionMonitor privacyPCM;

	private IPrivacyDataManagerInternal privacyDataManagerInternal;

	private IIdentityManager idm;

	private ICommManager commsMgr;

	private boolean test = false;


	private IUserFeedback userFeedback;
	private AccessControlPreferenceManager accCtrlMgr;
	private IPrivacyAgreementManager agreementMgr;
	private PPNegotiationPreferenceManager ppnMgr;
	private IDSPreferenceManager idsMgr;
	private AttrSelPreferenceManager attrSelPrefMgr;
	private IEventMgr eventMgr;

	private AccCtrlMonitor accCtrlMonitor;


	private AccessControlPreferenceCreator accCtrlPreferenceCreator;
	private DObfPreferenceCreator dobfPreferenceCreator;
	private PPNPreferenceMerger ppnPreferenceMerger;
	private IDSPreferenceCreator idsPreferenceCreator;
	private AttrSelPreferenceCreator attrSelPreferenceCreator;
	private NegotiationListener negotiationListener;
	private IIdentity userIdentity;

	public PrivacyPreferenceManager(){

	}


	public void initialisePrivacyPreferenceManager(ICtxBroker ctxBroker, ITrustBroker trustBroker){
		this.ctxBroker = ctxBroker;
		this.trustBroker = trustBroker;
		this.initialisePrivacyPreferenceManager();

	}

	public void initialisePrivacyPreferenceManager(){
		this.userIdentity = this.idm.getThisNetworkNode();
		prefCache = new PreferenceCache(this);

		//this.privacyPCM = new PrivacyPreferenceConditionMonitor(this);
		this.contextCache = new PrivateContextCache(this);
		this.accCtrlPreferenceCreator = new AccessControlPreferenceCreator(this);
		this.ppnPreferenceMerger = new PPNPreferenceMerger(this);
		this.idsPreferenceCreator = new IDSPreferenceCreator(this);
		this.attrSelPreferenceCreator = new AttrSelPreferenceCreator(this);
		this.negotiationListener = new NegotiationListener(this);
		this.dobfPreferenceCreator = new DObfPreferenceCreator(this);
		this.accCtrlMonitor = new AccCtrlMonitor(this);
		logging.debug("Initialised {}", this.getClass().getName());
	}



	public AccessControlPreferenceManager getAccessControlPreferenceManager(){

		if (this.accCtrlMgr==null){
			
			accCtrlMgr = new AccessControlPreferenceManager(this);
		}
		return accCtrlMgr;
	}

	private PPNegotiationPreferenceManager getPPNegotiationPreferenceManager(){
		if (this.ppnMgr==null){
			ppnMgr = new PPNegotiationPreferenceManager(this);
		}
		return ppnMgr;
	}

	private DObfPrivacyPreferenceManager getDObfPreferenceManager(){
		DObfPrivacyPreferenceManager dobfMgr = new DObfPrivacyPreferenceManager(this);
		return dobfMgr;
	}

	private IDSPreferenceManager getIDSPreferenceManager(){
		if (idsMgr ==null){
			idsMgr = new IDSPreferenceManager(this);
		}
		return idsMgr;
	}

	private AttrSelPreferenceManager getAttrSelPreferenceManager(){
		if (attrSelPrefMgr==null){
			attrSelPrefMgr = new AttrSelPreferenceManager(this);
		}
		return attrSelPrefMgr;
	}
	public AccCtrlMonitor getAccCtrlMonitor() {
		return accCtrlMonitor;
	}



	@Override
	public List<ResponseItem> checkPermission(RequestorBean requestor,
			List<DataIdentifier> dataIds, Action action)
					throws PrivacyException {

		AccessControlPreferenceManager  accCtrlMgr = getAccessControlPreferenceManager();
		if (dataIds.size()==1){
			List<ResponseItem> permissions = new ArrayList<ResponseItem>();

			permissions.add(this.checkPermission(requestor, dataIds.get(0), action));
			return permissions;
		}else{
			List<ResponseItem> checkPermission = accCtrlMgr.checkPermission(requestor, dataIds, action);
			for (ResponseItem item : checkPermission){
				this.logging.info("checkPermission for requestor: "+org.societies.api.identity.util.RequestorUtils.toString(requestor)+" on : "+item.getRequestItem().getResource().getDataType()+" for action: "+item.getRequestItem().getActions().get(0)+". Returning Decision: "+item.getDecision());
			}
			return checkPermission;
		}
	}

	@Override
	public ResponseItem checkPermission(RequestorBean requestor,
			DataIdentifier dataIds, Action action)
					throws PrivacyException {
		AccessControlPreferenceManager  accCtrlMgr = getAccessControlPreferenceManager();
		ResponseItem item = accCtrlMgr.checkPermission(requestor, dataIds, action);

		this.logging.info("checkPermission for requestor: "+org.societies.api.identity.util.RequestorUtils.toString(requestor)+" on : "+item.getRequestItem().getResource().getDataType()+" for action: "+item.getRequestItem().getActions().get(0)+". Returning Decision: "+item.getDecision());

		return item;
	}

	@Override
	@Deprecated
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		// TODO Auto-generated method stub
		AccessControlPreferenceManager  accCtrlMgr = getAccessControlPreferenceManager();
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		for (Action action: actions){
			permissions.addAll(accCtrlMgr.checkPermission(requestor, dataIds, action));
		}

		for (ResponseItem item : permissions){

			try{
				this.logging.info("checkPermission for requestor: "+org.societies.api.identity.util.RequestorUtils.toString(requestor)+" on : "+item.getRequestItem().getResource().getDataType()+" for action: "+item.getRequestItem().getActions().get(0)+". Returning Decision: "+item.getDecision());
			}catch(NullPointerException npe){
				this.logging.error("NPE probably on actions list", npe);
			}
		}
		return permissions;
	}




	@Override
	public boolean deleteAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		try {
			DataIdentifier id = ResourceUtils.getDataIdentifier(details.getResource());
			if (id!=null){
				List<Action> actions = new ArrayList<Action>();
				actions.add(details.getAction());
				this.privacyDataManagerInternal.deletePermissions(details.getRequestor(), id, actions);
			}
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.getAccessControlPreferenceManager().deleteAccCtrlPreference(details);
	}

	@Override
	public boolean deleteAttSelPreference(
			AttributeSelectionPreferenceDetailsBean details) {

		return this.getAttrSelPreferenceManager().deleteAttSelPreference(details);

	}


	@Override
	public boolean deleteDObfPreference(DObfPreferenceDetailsBean details) {
		return this.getDObfPreferenceManager().deleteDObfPreference(details);
	}


	@Override
	public boolean deleteIDSPreference(IDSPreferenceDetailsBean details) {
		return this.getIDSPreferenceManager().deleteIDSPreference(details);
	}


	@Override
	public boolean deletePPNPreference(PPNPreferenceDetailsBean details) {
		return this.getPPNegotiationPreferenceManager().deletePPNPreference(details);
	}


	@Override
	public ResponseItem evaluateAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		List<Condition> conditions = new ArrayList<Condition>();
		try {
			AgreementEnvelope agreementEnv = this.agreementMgr.getAgreement(RequestorUtils.toRequestor(details.getRequestor(), this.idm));

			if (agreementEnv!=null){
				IAgreement agreement = agreementEnv.getAgreement();

				for (ResponseItem item: agreement.getRequestedItems()){
					if (item.getRequestItem().getResource().getDataType().equals(details.getResource().getDataType())){
						conditions  = item.getRequestItem().getConditions();

						//JOptionPane.showMessageDialog(null, "Found conditions in agreement");
					}
				}

			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return this.getAccessControlPreferenceManager().evaluateAccCtrlPreference(details, conditions);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.logging.debug("Could not evaluate AccCtrl preference for provided details");
			return null;
		}
	}


	@Override
	public Hashtable<Resource, CtxIdentifier> evaluateAttributeSelectionPreferences(Agreement agreement){
		return this.getAttrSelPreferenceManager().evaluateAttributeSelectionPreferences(agreement);
	}
	@Override
	public Integer evaluateDObfPreference(DObfPreferenceDetailsBean details) {
		return this.getDObfPreferenceManager().evaluateDObfPreference(details);
	}


	@Override
	public IIdentity evaluateIDSPreference(IDSPreferenceDetailsBean details) {
		return this.getIDSPreferenceManager().evaluateIDSPreference(details);
	}


	@Override
	public IIdentity evaluateIDSPreferences(Agreement agreement,
			List<IIdentity> identities) {
		return this.getIDSPreferenceManager().evaluateIDSPreferences(agreement, identities);

	}


	@Override
	public AccessControlPreferenceTreeModel getAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		return this.getAccessControlPreferenceManager().getAccCtrlPreference(details);
	}


	@Override
	public AttributeSelectionPreferenceTreeModel getAttrSelPreference(AttributeSelectionPreferenceDetailsBean details) {
		return this.getAttrSelPreferenceManager().getAttrSelPreference(details);
	}



	@Override
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails() {
		return this.getAccessControlPreferenceManager().getAccCtrlPreferenceDetails();
	}

	@Override
	public List<AttributeSelectionPreferenceDetailsBean> getAttrSelPreferenceDetails() {
		return this.getAttrSelPreferenceManager().getAttrSelPreferenceDetails();
	}

	@Override
	public DObfPreferenceTreeModel getDObfPreference(
			DObfPreferenceDetailsBean details) {
		return this.getDObfPreferenceManager().getDObfPreference(details);
	}


	@Override
	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails() {
		return this.getDObfPreferenceManager().getDObfPreferenceDetails();
	}


	@Override
	public IDSPrivacyPreferenceTreeModel getIDSPreference(
			IDSPreferenceDetailsBean details) {
		return this.getIDSPreferenceManager().getIDSPreference(details);
	}


	@Override
	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails() {
		return this.getIDSPreferenceManager().getIDSPreferenceDetails();
	}


	@Override
	public PPNPrivacyPreferenceTreeModel getPPNPreference(
			PPNPreferenceDetailsBean details) {
		return this.getPPNegotiationPreferenceManager().getPPNPreference(details);
	}


	@Override
	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails() {
		return this.getPPNegotiationPreferenceManager().getPPNPreferenceDetails();
	}


	@Override
	public boolean storeAccCtrlPreference(
			AccessControlPreferenceDetailsBean details,
			AccessControlPreferenceTreeModel model) {
		this.logging.debug("request to store access control preferences for: "+ResourceUtils.toXmlString(details.getResource())+" \nand requestor: "+RequestorUtils.toXmlString(details.getRequestor()));
		System.out.println("request to store access control preferences for: "+ResourceUtils.toXmlString(details.getResource())+" \nand requestor: "+RequestorUtils.toXmlString(details.getRequestor()));
		return this.getAccessControlPreferenceManager().storeAccCtrlPreference(details, model);
	}


	@Override
	public boolean storeAttrSelPreference(AttributeSelectionPreferenceDetailsBean details,	AttributeSelectionPreferenceTreeModel model) {
		return this.getAttrSelPreferenceManager().storeAttrSelPreference(details,model);
	}

	@Override
	public boolean storeDObfPreference(DObfPreferenceDetailsBean details,
			DObfPreferenceTreeModel model) {
		try {
			return this.getDObfPreferenceManager().storeDObfPreference(details, model);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean storeIDSPreference(IDSPreferenceDetailsBean details,
			IDSPrivacyPreferenceTreeModel model) {
		try {
			return this.getIDSPreferenceManager().storeIDSPreference(details, model);

		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean storePPNPreference(PPNPreferenceDetailsBean details,
			PPNPrivacyPreferenceTreeModel model) {

		try {
			return this.getPPNegotiationPreferenceManager().storePPNPreference(details, model);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}


	}

	@Override
	public HashMap<RequestItem,ResponseItem> evaluatePPNPreferences(RequestPolicy requestPolicy) {
		try {
			return this.getPPNegotiationPreferenceManager().evaluatePPNPreferences(requestPolicy);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashMap<RequestItem, ResponseItem>();
	}


	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/**
	 * @return the trustBroker
	 */
	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}
	/**
	 * @param trustBroker the trustBroker to set
	 */
	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}


	/**
	 * @return the privacyDataManager
	 */
	public IPrivacyDataManagerInternal getprivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}


	/**
	 * @param privacyDataManagerInternal the privacyDataManager to set
	 */
	public void setprivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}


	/**
	 * @return the test
	 */
	public boolean isTest() {
		return test;
	}


	/**
	 * @param test the test to set
	 */
	public void setTest(boolean test) {
		this.test = test;
	}




	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	public IIdentityManager getIdm(){
		return this.idm;
	}

	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		this.idm = commsMgr.getIdManager();
	}

	public void setIdMgr(IIdentityManager identityManager){
		this.idm = identityManager;
	}

	public PrivacyPreferenceConditionMonitor getPCM(){
		return privacyPCM;
	}

	public PrivateContextCache getContextCache(){
		return this.contextCache;
	}


	/**
	 * @return the userFeedback
	 */
	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}


	/**
	 * @param userFeedback the userFeedback to set
	 */
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}


	public IPrivacyAgreementManager getAgreementMgr() {
		return agreementMgr;
	}


	public void setAgreementMgr(IPrivacyAgreementManager agreementMgr) {
		this.agreementMgr = agreementMgr;
	}


	public IEventMgr getEventMgr() {
		return eventMgr;
	}


	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}


	public AccessControlPreferenceCreator getAccCtrlPreferenceCreator() {
		return accCtrlPreferenceCreator;
	}


	public PPNPreferenceMerger getPpnPreferenceMerger() {
		return ppnPreferenceMerger;
	}




	public AttrSelPreferenceCreator getAttrSelPreferenceCreator() {
		return attrSelPreferenceCreator;
	}



	public IDSPreferenceCreator getIdsPreferenceCreator() {
		return idsPreferenceCreator;
	}


	@Override
	public boolean deletePPNPreferences() {
		return this.ppnMgr.deletePPNPreferences();
	}


	@Override
	public boolean deleteIDSPreferences() {
		return this.idsMgr.deleteIDSPreferences();
	}


	@Override
	public boolean deleteDObfPreferences() {
		return this.getDObfPreferenceManager().deleteDObfPreferences();
	}


	@Override
	public boolean deleteAccCtrlPreferences() {
		return this.getAccessControlPreferenceManager().deleteAccCtrlPreferences();
	}


	@Override
	public boolean deleteAttSelPreferences() {
		return this.getAttrSelPreferenceManager().deleteAttSelPreferences();
	}


/*	@Override
	public HashMap<CtxModelObject, Integer> getObfuscationLevel(
			RequestorBean requestor, List<CtxModelObject> ctxDataList) {
		HashMap<CtxModelObject, Integer> map = new HashMap<CtxModelObject, Integer>();
		for (CtxModelObject ctxModelObject : ctxDataList){
			DObfPreferenceDetailsBean details = new DObfPreferenceDetailsBean();
			details.setRequestor(requestor);
			CtxIdentifier ctxId = ctxModelObject.getId();
			details.setResource(ResourceUtils.create(ctxId));
			 Integer obfLevel = this.getDObfPreferenceManager().evaluateDObfPreference(details);
			if (obfLevel>=0){
				map.put(ctxModelObject, new Integer(obfLevel));
			}else{
				map.put(ctxModelObject, new Integer(0));
			}
		}

		return map;
	}

*/


	@Override
	public HashMap<CtxModelObject, Integer> getObfuscationLevel(
			RequestorBean requestor, List<CtxModelObject> ctxDataList) {
		return this.getDObfPreferenceManager().getObfuscationLevel(requestor, ctxDataList);
	}


	public void setAccCtrlMonitor(AccCtrlMonitor accCtrlMonitor) {
		this.accCtrlMonitor = accCtrlMonitor;
	}


	public DObfPreferenceCreator getDobfPreferenceCreator() {
		return dobfPreferenceCreator;
	}


	public void setDobfPreferenceCreator(DObfPreferenceCreator dobfPreferenceCreator) {
		this.dobfPreferenceCreator = dobfPreferenceCreator;
	}


	public void setContextCache(PrivateContextCache contextCache) {
		this.contextCache = contextCache;
	}


	public PreferenceCache getPrefCache() {
		return prefCache;
	}


	public void setPrefCache(PreferenceCache prefCache) {
		this.prefCache = prefCache;
	}
}

