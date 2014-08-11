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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;



/**
 * @author Elizabeth
 *
 */
public class PreferenceStorer {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker ctxBroker;
	private final IIdentityManager idMgr;


	public PreferenceStorer(ICtxBroker broker, IIdentityManager idMgr){
		this.ctxBroker = broker;
		this.idMgr = idMgr;	
	}


	public void deletePreference(CtxIdentifier id){
		CtxAttribute attrPreference;
		try {
			System.out.println("Deleting: "+id.toUriString());
			attrPreference = (CtxAttribute) ctxBroker.retrieve(id).get();
			if (attrPreference == null){
				this.logging.debug("Cannot delete preference. Doesn't exist: "+id.toUriString());
				
			}else{
				ctxBroker.remove(id).get();
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public boolean storeExisting(CtxIdentifier id, PrivacyPreferenceTreeModelBean p){
		this.logging.debug("Request to store preference to id:"+id.toUriString());
		try {
			Future<CtxAttribute> futureAttr;
			CtxAttribute attr = null;	
			futureAttr = ctxBroker.updateAttribute(((CtxAttributeIdentifier) id), SerialisationHelper.serialise(p));
			attr = futureAttr.get();

			if (null==attr){
				this.logging.debug("Id doesn't exist in DB. Returning error");
				return false;	
			}
			this.logging.debug("Updated attribute in DB for id: "+id.toUriString());
			return true;

		} catch (CtxException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			this.logging.debug("Error while updating preference in db for id"+id.toUriString());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	public CtxAttributeIdentifier storeNewPreference(PrivacyPreferenceTreeModelBean bean, String key) throws PrivacyException{

		IIdentity userId = this.idMgr.getThisNetworkNode();
		try {
			
			IndividualCtxEntity personEntity = ctxBroker.retrieveIndividualEntity(userId).get();
			Set<CtxAssociationIdentifier> associations = personEntity.getAssociations(CtxTypes.HAS_PRIVACY_PREFERENCES);
			if (associations.size()==0){
				this.logging.debug(CtxTypes.HAS_PRIVACY_PREFERENCES+" association doesn't exist in DB. Creating it");
				//create association and child entity
				CtxAssociation association = ctxBroker.createAssociation(userId, CtxTypes.HAS_PRIVACY_PREFERENCES).get();
				CtxEntity preferenceEntity = ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE).get();
				association.addChildEntity(preferenceEntity.getId());
				association = (CtxAssociation) ctxBroker.update(association).get();
				//create attribute under preference entity and store it
				CtxAttribute ctxAttribute = ctxBroker.createAttribute(preferenceEntity.getId(), key).get();
				ctxAttribute.setBinaryValue(SerialisationHelper.serialise(bean));
				 ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();	
				 return ctxAttribute.getId();
			}else{
				//get association
				CtxAssociationIdentifier ctxAssociationIdentifier = associations.iterator().next();
				CtxAssociation association = (CtxAssociation) this.ctxBroker.retrieve(ctxAssociationIdentifier).get();
				Set<CtxEntityIdentifier> childEntityIDs = association.getChildEntities(CtxTypes.PRIVACY_PREFERENCE);
				if (childEntityIDs.size()==0){
					this.logging.debug(CtxTypes.HAS_PRIVACY_PREFERENCES+" association found in DB but Entity "+CtxTypes.PRIVACY_PREFERENCE+" was not found. Creating it");
					//create preference entity and attach it to the association
					CtxEntity preferenceEntity = ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE).get();
					association.addChildEntity(preferenceEntity.getId());
					association = (CtxAssociation) ctxBroker.update(association).get();
					
					//create attribute under preference entity and store it
					CtxAttribute ctxAttribute = ctxBroker.createAttribute(preferenceEntity.getId(), key).get();
					ctxAttribute.setBinaryValue(SerialisationHelper.serialise(bean));
					ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();
					return ctxAttribute.getId();
				}else{
					this.logging.debug(CtxTypes.PRIVACY_PREFERENCE+" found in DB.");
					//store the attribute
					CtxEntity preferenceEntity = (CtxEntity) ctxBroker.retrieve(childEntityIDs.iterator().next()).get();
					CtxAttribute ctxAttribute = ctxBroker.createAttribute(preferenceEntity.getId(), key).get();
					ctxAttribute.setBinaryValue(SerialisationHelper.serialise(bean));
					ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();
					return ctxAttribute.getId();
				}
			}
		} catch (CtxException e) {
			this.logging.debug("Unable to store preference: "+key);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.logging.debug("Unable to store preference. Find exception in the logs. Returning null");
		return null;
	}

	public void storeRegistry(Registry registry) throws PrivacyException{
		this.logging.debug("Storing registry");
		IIdentity userId = this.idMgr.getThisNetworkNode();
		try {
			IndividualCtxEntity personEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
			
			Set<CtxAttribute> attributes = personEntity.getAttributes(CtxTypes.PRIVACY_PREFERENCE_REGISTRY);
			if (attributes.size()==0){
				this.logging.debug("Registry not found in DB, storing registry in new attribute");
				CtxAttribute attr = ctxBroker.createAttribute(personEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY).get();
				//RegistryBean bean = registry.toRegistryBean();
				attr.setBinaryValue(SerialisationHelper.serialise(registry));
				ctxBroker.update(attr).get();
				return;
			}
			
			this.logging.debug("Registry found in DB. Updating ctx attribute.");
			CtxAttribute ctxAttribute = attributes.iterator().next();
			ctxAttribute.setBinaryValue(SerialisationHelper.serialise(registry));
			ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();
			
		} catch (CtxException e) {
			this.logging.debug("Exception while storing PreferenceRegistry to DB for private DPI");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

