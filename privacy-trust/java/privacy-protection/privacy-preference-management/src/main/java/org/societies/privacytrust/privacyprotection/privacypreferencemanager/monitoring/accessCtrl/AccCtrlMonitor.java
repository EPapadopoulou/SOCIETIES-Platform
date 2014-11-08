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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.accessCtrl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.IMonitor;
/**
 * @author Eliza
 *
 */
public class AccCtrlMonitor  extends EventListener implements IMonitor, CtxChangeEventListener{


	private Logger logging = LoggerFactory.getLogger(this.getClass());

	/* this hashtable holds the context identifiers that appear as PreferenceCondition objs as keys
	 * and the list of details of the preferences affected by these PreferenceCondition objs as values 
	 */
	private Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> monitoringTable = new Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>>();

	private IIdentity userIdentity;

	private PrivacyPreferenceManager privPrefMgr;



	public AccCtrlMonitor(PrivacyPreferenceManager privPrefMgr) {
		this.privPrefMgr = privPrefMgr;
		
		userIdentity = this.privPrefMgr.getIdm().getThisNetworkNode();

		this.privPrefMgr.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, null);
		registerForInstalledApps();

	}


	private void registerForInstalledApps() {
		
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails = privPrefMgr.getAccessControlPreferenceManager().getAccCtrlPreferenceDetails();
		for (AccessControlPreferenceDetailsBean detail : accCtrlPreferenceDetails){
			try {
				CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(detail.getResource().getDataIdUri());
				if (this.monitoringTable.containsKey(ctxAttrID)){
					this.monitoringTable.get(ctxAttrID).add(detail);
				}else{
					this.privPrefMgr.getCtxBroker().registerForChanges(this, ctxAttrID);
					ArrayList<AccessControlPreferenceDetailsBean> list = new ArrayList<AccessControlPreferenceDetailsBean>();
					list.add(detail);
					this.monitoringTable.put(ctxAttrID, list);
				}
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public void monitorThisContext(Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> newDetails){

		Enumeration<CtxIdentifier> keys = newDetails.keys();

		while (keys.hasMoreElements()){
			CtxIdentifier nextElement = keys.nextElement();
			if (this.monitoringTable.containsKey(nextElement)){
				this.monitoringTable.get(nextElement).addAll(newDetails.get(nextElement));
			}else{
				try {
					this.privPrefMgr.getCtxBroker().registerForChanges(this, nextElement);
					this.monitoringTable.put(nextElement, newDetails.get(nextElement));
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//privPrefMgr.getContextCache().getContextCacheUpdater().registerForContextEvent((CtxAttributeIdentifier) nextElement, this);
				
			}
		}

	}

	private void processChangedContext(CtxChangeEvent event) {
		CtxIdentifier ctxIdentifier = event.getId();
		ArrayList<AccessControlPreferenceDetailsBean> details = this.monitoringTable.get(ctxIdentifier);
		for (AccessControlPreferenceDetailsBean detail : details){
			try {
				
				ResponseItem evaluateAccCtrlPreference = this.privPrefMgr.getAccessControlPreferenceManager().evaluateAccCtrlPreference(detail, new ArrayList<Condition>());
				logging.debug("evaluating preference due to context change event: detail={} evalResult={}", detail, evaluateAccCtrlPreference);
				if (evaluateAccCtrlPreference!=null){
					boolean updatePermission = this.privPrefMgr.getprivacyDataManagerInternal().updatePermission(detail.getRequestor(), evaluateAccCtrlPreference);
					if(updatePermission){
						logging.debug("Updated permission: {}", evaluateAccCtrlPreference);
					}else{
						logging.error("Error updating permission {}", evaluateAccCtrlPreference);
					}
				}else{
					logging.debug("Preferences did not yield an outcome. Removing permissions");
					boolean deletePermissions = this.privPrefMgr.getprivacyDataManagerInternal().deletePermissions(detail.getRequestor(), ctxIdentifier);
					if(deletePermissions){
						logging.debug("Deleted permission for : "+ctxIdentifier+" and req: "+ detail.getRequestor().getRequestorId());
					}else{
						logging.error("Error deleting permission for : "+ctxIdentifier+" and req: "+ detail.getRequestor().getRequestorId());
					}
					
				}
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}


/*	private void processChangedContext1(CtxChangeEvent event) throws PrivacyException, InvalidFormatException, MalformedCtxIdentifierException{
		//JOptionPane.showMessageDialog(null, "Received context event: " + event.getId().getType());

		Enumeration<CtxIdentifier> e = monitoringTable.keys();
		ArrayList<AccessControlPreferenceDetailsBean> arrayList = new ArrayList<AccessControlPreferenceDetailsBean>();
		while (e.hasMoreElements()){
			
			CtxIdentifier nextElement = e.nextElement();
			//JOptionPane.showMessageDialog(null, "inside keys loop: "+ nextElement.getUri() );
			if (nextElement.equals(event.getId())){
				arrayList = monitoringTable.get(nextElement);
				break;
			}
		}

		for (AccessControlPreferenceDetailsBean detail : arrayList){
			//JOptionPane.showMessageDialog(null, "inside for loop1 ");
			AgreementEnvelope agreement = this.agreementMgr.getAgreement(RequestorUtils.toRequestor(detail.getRequestor(), this.idMgr));
			//JOptionPane.showMessageDialog(null, "Retrieved agreement for requestor: "+RequestorUtils.toXmlString(detail.getRequestor())+agreement);
			List<ResponseItem> requestedItems = agreement.getAgreement().getRequestedItems();
			List<Condition> conditions = new ArrayList<Condition>();
			for (ResponseItem respItem : requestedItems){
				RequestItem requestItem = respItem.getRequestItem();
				if (requestItem.getResource().getDataType().equals(event.getId().getType())){
					conditions = requestItem.getConditions();
				}
			}
			AccessControlPreferenceTreeModel privPrefModel = this.accCtrlManager.getAccCtrlPreference(detail);
			//ResponseItem evaluateAccCtrlPreference = this.accCtrlManager.evaluateAccCtrlPreference(detail, conditions);
			IPrivacyOutcome evaluateAccCtrlPreference = this.accCtrlManager.evaluatePreference(privPrefModel.getPref(), conditions, detail.getRequestor());
			AccessControlOutcome outcome = (AccessControlOutcome) evaluateAccCtrlPreference;
			if (evaluateAccCtrlPreference==null){
				List<Action> actions = new ArrayList<Action>();
				actions.add(detail.getAction());
				
				boolean deletePermission = this.privDataManager.deletePermissions(detail.getRequestor(), ResourceUtils.getDataIdentifier(detail.getResource()), actions);
				//JOptionPane.showMessageDialog(null, "Deleted permission on privDataManager");

				this.logging.debug("Deleted permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId());
			}else{
				List<Action> actions = new ArrayList<Action>();
				actions.add(detail.getAction());
				if(outcome.getEffect().equals(PrivacyOutcomeConstantsBean.ALLOW)){
					
					this.privDataManager.updatePermission(detail.getRequestor(), ResourceUtils.getDataIdentifier(detail.getResource()), actions, Decision.PERMIT);
					//JOptionPane.showMessageDialog(null, "Updated permission on privDataManager");
					this.logging.debug("Updated permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId()+" with decision: "+Decision.PERMIT);	
				}else{
					this.privDataManager.updatePermission(detail.getRequestor(), ResourceUtils.getDataIdentifier(detail.getResource()), actions, Decision.DENY);
					//JOptionPane.showMessageDialog(null, "Updated permission on privDataManager");
					this.logging.debug("Updated permission on privDataManager for dataUri: "+detail.getResource().getDataIdUri()+" and action: "+detail.getAction()+"requestor: "+detail.getRequestor().getRequestorId()+" with decision: "+Decision.DENY);
				}
				
			}
		}
	}
*/





	@Override
	public void handleInternalEvent(InternalEvent event) {
		
/*		PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();

		List<ResponseItem> requestedItems = ppnEvent.getAgreement().getRequestedItems();
		List<DataIdentifier> resources = this.getDataIdentifiers(requestedItems);
		RequestorBean requestor = ppnEvent.getAgreement().getRequestor();
		try {
			Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> preferenceConditions = this.privPrefMgr.getAccessControlPreferenceManager().getContextConditions(RequestorUtils.toRequestor(requestor, idMgr), resources);
			//JOptionPane.showMessageDialog(null, "Retrieved preferenceConditions: "+preferenceConditions.size());
			this.monitorThisContext(preferenceConditions);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}


	private List<DataIdentifier> getDataIdentifiers(List<ResponseItem> requestedItems) {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		
		for (ResponseItem respItem : requestedItems){
			try {
				dataIds.add(ResourceUtils.getDataIdentifier(respItem.getRequestItem().getResource()));
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return dataIds;
	}


	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onModification(CtxChangeEvent event) {
		this.processChangedContext(event);
		
	}









	@Override
	public String getMonitorID() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}






	@Override
	public void onCreation(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}






	@Override
	public void onUpdate(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}






	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}


}
