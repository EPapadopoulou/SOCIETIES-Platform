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
package org.societies.personalisation.UserPreferenceManagement.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hsqldb.lib.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.management.CtxModelTypes;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.CisEventListener;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestCommunityDownload {


	private Community community;
	private MockIdentity cisID;
	private MockIdentity userId;
	private UserPreferenceConditionMonitor upcm;
	private ICommManager commManager;
	private ICommunityPreferenceManager communityPreferenceMgr;
	private ICtxBroker ctxBroker;
	private IEventMgr eventMgr;
	private IUserFeedback userFeedbackMgr;
	private IC45Learning userPrefLearning;
	private IInternalPersonalisationManager persoMgr;
	private ServiceResourceIdentifier serviceID;
	private String parameterName;
	private IndividualCtxEntity personCtxEntity;
	private CtxAttribute locationAttribute;
	private PreferenceDetails preferenceDetails;
	private IPreference preference;
	private CtxAssociation ctxPreferenceAssoc;
	private CtxEntity preferenceCtxEntity;
	private CtxAttribute ctxPreferenceAttribute1;
	private CtxAttribute ctxPreferenceRegistryAttribute;
	private CtxAttribute ctxPreferenceAttribute2;
	private final static String preferenceKey1 = "preference_1";
	private final static String preferenceKey2 = "preference_2";
	@Before
	public void setup(){
		cisID = new MockIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		userId = new MockIdentity(IdentityType.CSS, "user", "ict-societies.eu");

		community = new Community();
		community.setCommunityJid(cisID.getJid());

		community.setOwnerJid(userId.getJid());
		upcm = new UserPreferenceConditionMonitor();


		commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idm = Mockito.mock(IIdentityManager.class);
		upcm.setCommManager(commManager);
		communityPreferenceMgr = Mockito.mock(ICommunityPreferenceManager.class);
		upcm.setCommunityPreferenceMgr(communityPreferenceMgr);
		ctxBroker = Mockito.mock(ICtxBroker.class);
		upcm.setCtxBroker(ctxBroker);
		eventMgr = Mockito.mock(IEventMgr.class);
		upcm.setEventMgr(eventMgr);
		persoMgr = Mockito.mock(IInternalPersonalisationManager.class);
		upcm.setPersoMgr(persoMgr);
		userFeedbackMgr = Mockito.mock(IUserFeedback.class);
		upcm.setUserFeedbackMgr(userFeedbackMgr);
		userPrefLearning = Mockito.mock(IC45Learning.class);
		upcm.setUserPrefLearning(userPrefLearning);
		upcm.initialisePreferenceManagement();
		uploadPreferenceToCommunity();


		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "PREFERENCE_REGISTRY")).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(this.commManager.getIdManager()).thenReturn(idm);
			Mockito.when(this.commManager.getIdManager().fromJid(community.getCommunityJid())).thenReturn(cisID);
			Mockito.when(this.commManager.getIdManager().getThisNetworkNode()).thenReturn(userId);
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));

			Mockito.when(ctxBroker.retrieveIndividualEntity(userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(personCtxEntity));
			List<PreferenceDetails> details = new ArrayList<PreferenceDetails>();
			details.add(preferenceDetails);
			Mockito.when(this.communityPreferenceMgr.getCommunityPreferenceDetails(cisID)).thenReturn(details);
			String key = "ServiceID: "+ServiceModelUtils.serviceResourceIdentifierToString(this.preferenceDetails.getServiceID())+" - PreferenceName: "+this.preferenceDetails.getPreferenceName();
			List<String> tempList = new ArrayList<String>();
			tempList.add(key);
			Mockito.when(this.userFeedbackMgr.getExplicitFB(Mockito.eq(ExpProposalType.CHECKBOXLIST), (ExpProposalContent) Mockito.anyObject())).thenReturn(new AsyncResult<List<String>>(tempList));
			List<IPreferenceTreeModel> models = new ArrayList<IPreferenceTreeModel>();
			IPreferenceTreeModel model = new PreferenceTreeModel(preferenceDetails, preference);
			models.add(model);
			Mockito.when(this.communityPreferenceMgr.getCommunityPreferences(cisID, details)).thenReturn(models);
			ArrayList<CtxIdentifier> tempCtxPrefEntityList = new ArrayList<CtxIdentifier>();
			tempCtxPrefEntityList.add(this.preferenceCtxEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>(tempCtxPrefEntityList)));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) preferenceCtxEntity.getId(), preferenceKey1)).thenReturn(new AsyncResult<CtxAttribute>(ctxPreferenceAttribute1));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) preferenceCtxEntity.getId(), preferenceKey2)).thenReturn(new AsyncResult<CtxAttribute>(ctxPreferenceAttribute2));
			Mockito.when(ctxBroker.update(this.ctxPreferenceAttribute1)).thenReturn(new AsyncResult<CtxModelObject>(ctxPreferenceAttribute1));
			Mockito.when(ctxBroker.update(this.ctxPreferenceAttribute2)).thenReturn(new AsyncResult<CtxModelObject>(ctxPreferenceAttribute2));
			Mockito.when(ctxBroker.createAttribute(personCtxEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(ctxPreferenceRegistryAttribute));
			Mockito.when(ctxBroker.update(ctxPreferenceRegistryAttribute)).thenReturn(new AsyncResult<CtxModelObject>(ctxPreferenceRegistryAttribute));
			Mockito.when(ctxBroker.retrieve(ctxPreferenceAttribute1.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.ctxPreferenceAttribute1));
			Mockito.when(ctxBroker.retrieve(ctxPreferenceAttribute2.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.ctxPreferenceAttribute2));
			this.storeUserPreference();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test(){
		//with empty preference DB:
		CisEventListener cisEventListener = upcm.getCisEventListener();
		InternalEvent iEvent = new InternalEvent(EventTypes.CIS_SUBS, "subscription of CIS", this.cisID.getBareJid(), community);
		cisEventListener.handleInternalEvent(iEvent);

		//adding a preference

		//cisEventListener.handleInternalEvent(iEvent);
	}

	private void uploadPreferenceToCommunity(){

		try {
			this.serviceID = new ServiceResourceIdentifier();
			this.serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
			this.serviceID.setServiceInstanceIdentifier("MediaPlayer");		
			IPreference preferenceLeaf = new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", parameterName, "10"));
			CtxEntityIdentifier personCtxEntityId = new CtxEntityIdentifier(this.userId.getBareJid(), CtxEntityTypes.PERSON, new Long(1));
			personCtxEntity = new IndividualCtxEntity(personCtxEntityId);
			CtxAttributeIdentifier locationAttributeId = new CtxAttributeIdentifier(personCtxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
			locationAttribute = new CtxAttribute(locationAttributeId);
			preference = new PreferenceTreeNode(new ContextPreferenceCondition(locationAttributeId, OperatorConstants.EQUALS, "home", CtxAttributeTypes.LOCATION_SYMBOLIC));
			preference.add(preferenceLeaf);

			preferenceDetails = new PreferenceDetails("", serviceID, parameterName);

			CtxEntityIdentifier preferenceCtxEntityId = new CtxEntityIdentifier(this.userId.getBareJid(), CtxEntityTypes.PREFERENCE, new Long(2));
			preferenceCtxEntity = new CtxEntity(preferenceCtxEntityId);
			CtxAssociationIdentifier ctxPreferenceAssocId = new CtxAssociationIdentifier(this.userId.getBareJid(), CtxModelTypes.HAS_PREFERENCES, new Long(2));
			this.ctxPreferenceAssoc = new CtxAssociation(ctxPreferenceAssocId);
			this.ctxPreferenceAssoc.setParentEntity(personCtxEntityId);
			CtxAttributeIdentifier preferenceCtxAttributeId1 = new CtxAttributeIdentifier(preferenceCtxEntityId, preferenceKey1, new Long(3));

			this.ctxPreferenceAttribute1  = new CtxAttribute(preferenceCtxAttributeId1);

			CtxAttributeIdentifier preferenceCtxAttributeId2 = new CtxAttributeIdentifier(preferenceCtxEntityId, preferenceKey2, new Long(4));

			this.ctxPreferenceAttribute2  = new CtxAttribute(preferenceCtxAttributeId2);


			CtxAttributeIdentifier ctxPreferenceRegistryAttributeId = new CtxAttributeIdentifier(personCtxEntityId, CtxModelTypes.PREFERENCE_REGISTRY, new Long(5));
			ctxPreferenceRegistryAttribute = new CtxAttribute(ctxPreferenceRegistryAttributeId);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void storeUserPreference(){
		IPreference preferenceLeaf = new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", parameterName, "100"));
		IPreference preferenceCondition = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "home", CtxAttributeTypes.LOCATION_SYMBOLIC));
		this.upcm.getPreferenceManager().storePreference(userId, preferenceDetails, preferenceCondition);
	}
}
