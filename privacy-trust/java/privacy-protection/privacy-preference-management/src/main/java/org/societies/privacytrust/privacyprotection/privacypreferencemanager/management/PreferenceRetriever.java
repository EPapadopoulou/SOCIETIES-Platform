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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;

/**
 * @author Elizabeth
 * 
 */
public class PreferenceRetriever {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker;
	private final IIdentityManager idMgr; 

	public PreferenceRetriever(ICtxBroker ctxBroker, IIdentityManager idMgr){
		this.ctxBroker = ctxBroker;
		this.idMgr = idMgr;
	}

	public Registry retrieveRegistry(){
		IIdentity userId = this.idMgr.getThisNetworkNode();

		this.logging.debug("Retrieving registry");
		try {
			IndividualCtxEntity entityPerson = ctxBroker.retrieveIndividualEntity(userId).get();
			Set<CtxAttribute> attributes = entityPerson.getAttributes(CtxTypes.PRIVACY_PREFERENCE_REGISTRY);
			if (attributes.size()==0){
				return new Registry();
			}
			CtxAttribute ctxAttribute = attributes.iterator().next();
			Registry registry = (Registry) SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), this.getClass().getClassLoader());
			return registry;
		} catch (CtxException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
		} catch (InterruptedException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
		} catch (ExecutionException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.logging.debug("Error retrieving registry from DB, creating new registry");
		return new Registry();
	}


	/*
	 * retrieves a preference object using that preference object's context identifier to find it
	 * @param id
	 * @return
	 */
	public IPrivacyPreferenceTreeModel retrievePreference(CtxIdentifier id){
		try{
			//retrieve directly the attribute in context that holds the preference as a blob value
			CtxAttribute attrPref = (CtxAttribute) ctxBroker.retrieve(id).get();
			//cast the blob value to type IPreference and return it
			Object obj = SerialisationHelper.deserialise(attrPref.getBinaryValue(), this.getClass().getClassLoader());

			if (obj instanceof PPNPrivacyPreferenceTreeModelBean){
				this.logging.debug("Returning ppn preference");
				return PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModel((PPNPrivacyPreferenceTreeModelBean) obj, this.idMgr);
			}

			if (obj instanceof IDSPrivacyPreferenceTreeModelBean){
				this.logging.debug("Returning ids preference");
				return PrivacyPreferenceUtils.toIDSPrivacyPreferenceTreeModel((IDSPrivacyPreferenceTreeModelBean) obj, idMgr);
			}

			if (obj instanceof DObfPrivacyPreferenceTreeModelBean){
				this.logging.debug("Returning dobf preference");
				return PrivacyPreferenceUtils.toDObfPreferenceTreeModel((DObfPrivacyPreferenceTreeModelBean) obj, idMgr);
			}

			if (obj instanceof AccessControlPreferenceTreeModelBean){
				this.logging.debug("Returning accCtrl preference");
				return PrivacyPreferenceUtils.toAccCtrlPreferenceTreeModel((AccessControlPreferenceTreeModelBean) obj, idMgr);
			}
			
			if (obj instanceof AttributeSelectionPreferenceTreeModelBean){
				this.logging.debug("Returning attrSel preference");
				return PrivacyPreferenceUtils.toAccCtrlPreferenceTreeModel((AccessControlPreferenceTreeModelBean) obj, idMgr);
			}

		}
		catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//returns null if no preference is found in the database.
		this.logging.debug("Could not retrieve ctxAttribute with id: "+id.toUriString()+" from DB. returning null");
		return null;
	}







}

