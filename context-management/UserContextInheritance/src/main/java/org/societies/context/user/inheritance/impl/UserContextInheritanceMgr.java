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
package org.societies.context.user.inheritance.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.context.api.user.inheritance.ConflictResolutionAlgorithm;
import org.societies.context.api.user.inheritance.IUserCtxInheritanceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContextInheritanceMgr implements IUserCtxInheritanceMgr {

	private static final Logger LOG = LoggerFactory.getLogger(UserContextInheritanceMgr.class);
	private ICtxBroker ctxBroker;
	private ICommManager commMngr;
	
	/*@Autowired
	private final IIdentity ownerId;
	@Autowired
	private final String ccsIdString;*/

	@Autowired (required=true)
	public UserContextInheritanceMgr(ICtxBroker ctxBroker, ICommManager commMngr) throws Exception {	
		if (LOG.isDebugEnabled()){
			LOG.info(this.getClass() + "instantiated ");
		}
		this.ctxBroker = ctxBroker;
		this.commMngr = commMngr;
		//fetch this CSS Entity
		//final String ownerIdStr = commMngr.getIdManager().getThisNetworkNode().getBareJid();
		//this.ownerId = commMngr.getIdManager().fromJid(ownerIdStr);
		//return a (static) list with the inheritable attributes

	}

	public CtxAttributeIdentifier inferTypes(ArrayList<String> inferrableTypes) {
		//TODO 
		//CtxAttributeIdentifier attributeTypes = (CtxAttributeIdentifier) ctxBroker.retrieve(this, ctxAttId);

		return null;

	}
	
	public void communityInheritance(CtxAttributeIdentifier ctxAttrId) throws InvalidFormatException, InterruptedException, ExecutionException, CtxException {

		CtxAttribute ctxAttributeObjToInherit = (CtxAttribute) ctxBroker.retrieve(ctxAttrId).get();
		
		//Given the css entity, fetch a set of association ids, type "isMemberOf"
		String cssIdString = commMngr.getIdManager().getThisNetworkNode().getBareJid();
		IIdentity ownerId = commMngr.getIdManager().fromJid(cssIdString);
		IndividualCtxEntity cssEntity = ctxBroker.retrieveIndividualEntity(ownerId).get();
					
		//Use the broker to get the association ids of type is_Member_Of and then the objects
		ArrayList<CtxAssociation> setOfCtxAssociationsObj = new ArrayList<CtxAssociation>();		
		Set<CtxAssociationIdentifier> setOfCSSAssocIds = cssEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
	
		for (CtxAssociationIdentifier cssAssocId:setOfCSSAssocIds){
			 CtxAssociation assocObj = (CtxAssociation) ctxBroker.retrieve(cssAssocId).get();
			 setOfCtxAssociationsObj.add(assocObj);
		}
						
		//from the association objects, get the CIS ids (getParent method) (for each .getChildEntities get the entities where the getOwnerId = jid of the CIS (by using the comm manager))
		ArrayList<IndividualCtxEntity> setOfParentCISsEntities = new ArrayList<IndividualCtxEntity>();
		ArrayList<CtxIdentifier> setOfCISsIds = new ArrayList<CtxIdentifier>();
		for (CtxAssociation assocObj:setOfCtxAssociationsObj){
			CtxEntityIdentifier assocParentId = assocObj.getParentEntity();
			setOfCISsIds.add(assocParentId);
			IndividualCtxEntity cisEntity = (IndividualCtxEntity) ctxBroker.retrieve(assocParentId).get();
			setOfParentCISsEntities.add(cisEntity);			
		}
		//use the  lookup (requestor, targerid, attribute, attId.getType()) to retrieve a set of att ids
		ArrayList<CtxAttributeIdentifier> setOfCISCtxAttributeIds = new ArrayList<CtxAttributeIdentifier>();

		for (CtxIdentifier cisEntityId:setOfCISsIds){
			String cisString = commMngr.getIdManager().getThisNetworkNode().getBareJid();
			IIdentity cisIdentity = commMngr.getIdManager().fromJid(cisString);
			 CtxAttributeIdentifier cisCtxAttributeId = (CtxAttributeIdentifier) ctxBroker.lookup(cisIdentity, CtxModelType.ATTRIBUTE, ctxAttrId.getType()).get();
			 setOfCISCtxAttributeIds.add(cisCtxAttributeId);
		}
 		// through the broker, retrieve the attribute objects
		ArrayList<CtxAttribute> listWithCtxAttributeObjs = new ArrayList<CtxAttribute>();
		for (CtxAttributeIdentifier attId:setOfCISCtxAttributeIds){
			CtxAttribute attrEntity = (CtxAttribute) ctxBroker.retrieve(attId).get();
			listWithCtxAttributeObjs.add(attrEntity);	
		}
		//if the attributes are more than one, then run the compareQoC method
		if (listWithCtxAttributeObjs.size() >= 2) {
			CtxAttribute currAtt = listWithCtxAttributeObjs.get(0);
			for (int i=1; i<listWithCtxAttributeObjs.size(); i++){
				CtxAttribute currentAtt = (CtxAttribute)compareQoC(currAtt, listWithCtxAttributeObjs.get(i));
				currAtt=currentAtt;
			}
		}
	
	}
	/**
	 *
	 * 
	 * @param 
	 * @param 
	 * @param attrType
	 *            
	 * @throws CtxException 
	 *             if the unregistration process fails
	 * 
	 * @since 0.0.3
	 */
	public CtxAttribute compareQoC(CtxAttribute ctxAtt1, CtxAttribute ctxAtt2) {
		DateTime dt = new DateTime();
		Calendar cale = GregorianCalendar.getInstance();
		
		long freshnessOfFirstAttribute = ctxAtt1.getQuality().getFreshness();
		long freshnessOfSecondAttribute = ctxAtt2.getQuality().getFreshness();

		Date lastUpdatedFirstAttribute = (Date) ctxAtt1.getQuality().getLastUpdated();
		Date lastUpdatedSecondAttribute = (Date) ctxAtt2.getQuality().getLastUpdated();

		Double precisionOfFirstAttribute = ctxAtt1.getQuality().getPrecision();
		Double precisionOfSecondtAttribute = ctxAtt2.getQuality().getPrecision();
		
		
		/*qoc1 = 50*(lastUpdatedFirstAttribute/DateTime.now()) + 50*precisionOfFirstAttribute;
		qoc2 = 50*(lastUpdatedSecondAttribute/DateTime.now()) + 50*precisionOfSecondAttribute;*/
		

		//if (lastUpdatedFirstAttribute.after(lastUpdatedSecondAttribute) && dt.get lastUpdatedFirstAttribute.getMinutes())
			
			return null;
		
	}
	
	
	
	

	@Override
	public void getCIS(IIdentity arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getContextAttribute(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1, IIdentity arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inheritContextAttribute(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolveConflicts(ConflictResolutionAlgorithm arg0) {
		// TODO Auto-generated method stub
		
	}
}
