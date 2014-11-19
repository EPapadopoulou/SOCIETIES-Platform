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
package org.societies.privacytrust.privacyprotection.identity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentityOption;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.identity.gui.IdentityCreationGUIDialog;
import org.societies.privacytrust.privacyprotection.identity.gui.IdentityWindow;

/**
 * To manage identity selection between the multiple identities of the user
 * Skeleton implementation to manage only one identity
 * 
 * @author Olivier Maridat (Trialog)
 * 
 */
public class IdentitySelection implements IIdentitySelection {
	private static final String IDENTITY_MAPPINGS = "identity_Mappings";
	private static final String HAS_IDENTITY_INFORMATION = "has_Identity_Information";
	private static final String IDENTITY_INFORMATION = "identity_Information";

	private static Logger logging = LoggerFactory.getLogger(IdentitySelection.class);

	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private IIdentityManager idManager;	
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private List<IIdentity> identities;
	private Hashtable<IIdentity, List<CtxIdentifier>> identityMappings;
	private IIdentity userIdentity;
	private IndividualCtxEntity person;
	private CtxEntityIdentifier identity_Information_EntityID;
	private boolean initialised;

	public IdentitySelection() {
		this.identities = new ArrayList<IIdentity>();
		this.identityMappings = new Hashtable<IIdentity, List<CtxIdentifier>>();

	}

	public void init() {
		this.userIdentity = idManager.getThisNetworkNode();
		this.initialised = false;
		this.initialiseAfterContext();
	}

	public void initialiseAfterContext(){
		this.logging.debug("Lazy Initialisation -  IdentitySelection component");
		try {

			person = this.ctxBroker.retrieveIndividualEntity(userIdentity)
					.get();

			Set<CtxAssociationIdentifier> associations = person
					.getAssociations(HAS_IDENTITY_INFORMATION);
			if (!associations.isEmpty()) {
				CtxAssociationIdentifier associationID = associations
						.iterator().next();
				CtxAssociation ctxAssociation = (CtxAssociation) this.ctxBroker
						.retrieve(associationID).get();

				Set<CtxEntityIdentifier> childEntities = ctxAssociation
						.getChildEntities(IDENTITY_INFORMATION);
				if (!childEntities.isEmpty()) {
					identity_Information_EntityID = childEntities.iterator()
							.next();
					CtxEntity entity = (CtxEntity) this.ctxBroker.retrieve(
							identity_Information_EntityID).get();
					Set<CtxAttribute> attributes = entity
							.getAttributes(IDENTITY_MAPPINGS);
					if (attributes.size() > 0) {
						CtxAttribute identityMappingsAttribute = attributes
								.iterator().next();
						if (identityMappingsAttribute.getBinaryValue()!=null){
							this.identityMappings = (Hashtable<IIdentity, List<CtxIdentifier>>) SerialisationHelper.deserialise(identityMappingsAttribute.getBinaryValue(), this.getClass().getClassLoader());
							
						}
						if (this.identityMappings != null) {
							Hashtable<String, List<CtxIdentifier>> forCtxBroker = new Hashtable<String, List<CtxIdentifier>>();
							Enumeration<IIdentity> keys = this.identityMappings
									.keys();
							while (keys.hasMoreElements()) {
								IIdentity id = keys.nextElement();
								this.identities.add(this.idManager.addAlias(id.getIdentifier()));
								forCtxBroker.put(id.getBareJid(), this.identityMappings.get(id));
								
							}
							ctxBroker.setLinkedAttributes(forCtxBroker);
						}
					}

				}
			} else {
				setupContext();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (this.identityMappings == null) {
			this.identityMappings = new Hashtable<IIdentity, List<CtxIdentifier>>();
		}
		if (this.identities == null) {
			this.identities = new ArrayList<IIdentity>();
		}
		this.initialised = true;
	}
	private void setupContext() {
		try {
			CtxAssociation ctxAssociation = this.ctxBroker.createAssociation(
					HAS_IDENTITY_INFORMATION).get();
			ctxAssociation.setParentEntity(this.person.getId());

			CtxEntity ctxEntity = this.ctxBroker.createEntity(
					IDENTITY_INFORMATION).get();
			this.identity_Information_EntityID = ctxEntity.getId();

			ctxAssociation.addChildEntity(identity_Information_EntityID);
			ctxAssociation = (CtxAssociation) this.ctxBroker.update(
					ctxAssociation).get();

			CtxAttribute ctxAttribute = this.ctxBroker.createAttribute(
					identity_Information_EntityID, IDENTITY_MAPPINGS).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void storeIdentityMappings() {
		if (this.identities != null && this.identityMappings != null) {
			try {
				if (this.identity_Information_EntityID == null) {
					if (this.person == null) {
						this.person = this.ctxBroker.retrieveIndividualEntity(
								userIdentity).get();
						this.setupContext();
					}
				}
				CtxEntity ctxEntity = (CtxEntity) this.ctxBroker.retrieve(
						identity_Information_EntityID).get();
				Set<CtxAttribute> attributes = ctxEntity
						.getAttributes(IDENTITY_MAPPINGS);
				CtxAttribute ctxAttribute;
				if (attributes.size() > 0) {
					ctxAttribute = attributes.iterator().next();
				} else {
					ctxAttribute = ctxBroker.createAttribute(
							identity_Information_EntityID, IDENTITY_MAPPINGS)
							.get();
				}
				ctxAttribute.setBinaryValue(SerialisationHelper
						.serialise(this.identityMappings));
				this.ctxBroker.update(ctxAttribute).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection
	 * #processIdentityContext(org.societies.api.internal.privacytrust.
	 * privacyprotection.model.privacypolicy.IAgreement)
	 */
	@Override
	public List<IIdentityOption> processIdentityContext(Agreement agreement) {
		if (!this.initialised){
			this.initialiseAfterContext();
		}
		List<IIdentityOption> identityList = new ArrayList<IIdentityOption>();

		List<ResponseItem> agreedResources = agreement.getRequestedItems();

		Enumeration<IIdentity> keys = this.identityMappings.keys();

		// for all identities
		while (keys.hasMoreElements()) {
			IIdentity identity = keys.nextElement();
			boolean isIdentityValid = true;
			List<CtxIdentifier> list = this.identityMappings.get(identity);

			// for all response items
			for (ResponseItem item : agreedResources) {
				Resource resource = item.getRequestItem().getResource();
				boolean found = false;
				// for all data types associated with this identity
				for (CtxIdentifier ctxID : list) {
					// if resource data type is found in the mappings list, set
					// data type found
					if (resource.getDataType().equalsIgnoreCase(ctxID.getType())) {
						this.logging.debug("Identity: "+identity.getBareJid()+" has item: "+resource.getDataType());						
						found = true;
						break;
					}
				}
				if (!found) {
					this.logging.debug("Identity: "+identity.getBareJid()+" does not have item: "+resource.getDataType());
					isIdentityValid = false;
					break;
				}
			}

			if (isIdentityValid) {
				IdentityOption option = new IdentityOption(identity);
				identityList.add(option);
			}

		}

		this.logging.debug("Returning "+identityList.size()+" identities: "+identityList);
		return identityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection
	 * #evaluateLinkability(org.societies.api.identity.IIdentity,
	 * org.societies.api.identity.IIdentity,
	 * org.societies.api.internal.privacytrust
	 * .privacyprotection.model.privacypolicy.Resource)
	 */
	@Override
	public IIdentityOption evaluateLinkability(IIdentity remoteEntity,
			IIdentity userOwnedEntity, Resource dataToBeReleased) {
		if (!this.initialised){
			this.initialiseAfterContext();
		}
		return new IdentityOption(userOwnedEntity);
	}

	@Override
	public IIdentity createIdentity(String name, List<CtxIdentifier> idList) {
		if (!this.initialised){
			this.initialiseAfterContext();
		}
		
		IIdentity impl = this.idManager.addAlias(name);
		List<CtxIdentifier> newList = new ArrayList<CtxIdentifier>();
		for (CtxIdentifier ctxID : idList){
			CtxIdentifier newCtxID =CtxIDChanger.changeIDOwner(impl.getBareJid(), (CtxAttributeIdentifier) ctxID);
			newList.add(newCtxID);
		}
		logging.debug("Creating new identity"+impl.getBareJid()+" with context list: {}", newList);
		this.identityMappings.put(impl, newList);
		this.identities.add(impl);
		this.ctxBroker.addIdentityLinkedAttributes(impl.getBareJid(), newList);
		this.storeIdentityMappings();
		return impl;
	}

	@Override
	public List<CtxIdentifier> getLinkedAttributes(IIdentity identity) {
		if (!this.initialised){
			this.initialiseAfterContext();
		}
		if (this.identityMappings.containsKey(identity)){
			return this.identityMappings.get(identity);
		}
		
		return new ArrayList<CtxIdentifier>();
	}
	
	
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		idManager = this.commsMgr.getIdManager();
	}

	@Override
	public List<IIdentity> getAllIdentities() {
		if (!this.initialised){
			this.initialiseAfterContext();
		}
		// TODO Auto-generated method stub
		return this.identities;
	}

	public IPrivacyPreferenceManager getPrivacyPreferenceManager() {
		return privacyPreferenceManager;
	}

	public void setPrivacyPreferenceManager(IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
	}

	@Override
	public Hashtable<String, List<CtxIdentifier>> showIdentityCreationGUI(Agreement agreement) {
		IdentityWindow window = new IdentityWindow(agreement, this, userIdentity, this.identities);
		return window.getIdentityInformation();
	}

	@Override
	public IIdentity showIdentitySelectionGUI(String providerFriendlyName, List<IIdentity> ids, IIdentity recommendedIdentity) {
		IdentityWindow window = new IdentityWindow(providerFriendlyName, ids, recommendedIdentity);
		return window.getSelectedIdentity();
	}



}
