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
package org.societies.privacytrust.trust.impl;

import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * Implementation of the external {@link org.societies.api.privacytrust.trust.
 * ITrustBroker ITrustBroker} interface.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@Service
@Lazy(value = false)
public class TrustBroker implements org.societies.api.privacytrust.trust.ITrustBroker, ITrustBroker {

	Hashtable<String, Double> trusts;
	public TrustBroker(){
		trusts = new Hashtable<String, Double>();
		trusts.put("google", 50.0);
		trusts.put("hwu", 50.0);
		trusts.put("bbc", 50.0);
		trusts.put("itunes", 50.0);
	}
	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<ExtTrustRelationship>> retrieveExtTrustRelationships(
			TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<TrustRelationship> retrieveTrustRelationship(TrustQuery query)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<ExtTrustRelationship> retrieveExtTrustRelationship(
			TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Double> retrieveTrustValue(TrustQuery query)
			throws TrustException {
		TrustedEntityId trusteeId = query.getTrusteeId();
		if (trusteeId.getEntityType().equals(TrustedEntityType.SVC)){
			
			String entityId = trusteeId.getEntityId();
			if (entityId==null){
				return new AsyncResult<Double>(0.0);
			}
			if (entityId.toLowerCase().contains("google")){
				return new AsyncResult<Double>(this.trusts.get("google"));
			}else if (entityId.toLowerCase().contains("hwu")){
				return new AsyncResult<Double>(this.trusts.get("hwu"));
			}else if (entityId.toLowerCase().contains("bbc")){
				return new AsyncResult<Double>(this.trusts.get("bbc"));
			}else if (entityId.toLowerCase().contains("itunes")){
				return new AsyncResult<Double>(this.trusts.get("itunes"));
			}else{
				if (this.trusts.containsKey(entityId)){
					return new AsyncResult<Double>(this.trusts.get(entityId));
				}
				return new AsyncResult<Double>(50.0);
			}
		}else{
			throw new TrustInvalidArgumentException("Trust entity type : "+trusteeId.getEntityType()+" is not allowed");
		}
	}

	@Override
	public void updateTrustValue(TrustQuery query, Double trustValue) throws TrustException{
		if (query==null){
			throw new TrustInvalidArgumentException("TrustQuery is null");
		}
		if (trustValue == null){
			throw new TrustInvalidArgumentException("Trust value is null");
		}
		TrustedEntityId trusteeId = query.getTrusteeId();
		if (trusteeId.getEntityType().equals(TrustedEntityType.SVC)){
			
			String entityId = trusteeId.getEntityId();
			if (entityId!=null){
				if (entityId.toLowerCase().contains("google")){
					this.trusts.put("google", trustValue);
				}
				else if (entityId.toLowerCase().contains("hwu")){
					this.trusts.put("hwu", trustValue);
				}else if (entityId.toLowerCase().contains("bbc")){
					this.trusts.put("bbc", trustValue);
				}else if (entityId.toLowerCase().contains("itunes")){
					this.trusts.put("itunes", trustValue);
				}else{
					this.trusts.put(entityId, trustValue);
				}
			}
		}else{
			throw new TrustInvalidArgumentException("Trust entity type : "+trusteeId.getEntityType()+" is not allowed");
		}
	}
	@Override
	public Future<Boolean> removeTrustRelationships(TrustQuery query)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerTrustUpdateListener(ITrustUpdateEventListener listener,
			TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterTrustUpdateListener(
			ITrustUpdateEventListener listener, TrustQuery query)
			throws TrustException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<Double> retrieveTrust(TrustedEntityId trusteeId)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<TrustRelationship> retrieveTrustRelationship(
			Requestor requestor, TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Double> retrieveTrustValue(Requestor requestor,
			TrustQuery query) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerTrustUpdateListener(Requestor requestor,
			ITrustUpdateEventListener listener, TrustQuery query)
			throws TrustException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterTrustUpdateListener(Requestor requestor,
			ITrustUpdateEventListener listener, TrustQuery query)
			throws TrustException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustedEntityId trustorId)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustedEntityId trustorId,
			TrustedEntityId trusteeId) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<TrustRelationship> retrieveTrustRelationship(
			Requestor requestor, TrustedEntityId trustorId,
			TrustedEntityId trusteeId, TrustValueType trustValueType)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Double> retrieveTrustValue(Requestor requestor,
			TrustedEntityId trustorId, TrustedEntityId trusteeId,
			TrustValueType trustValueType) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustedEntityId trustorId,
			TrustedEntityType trusteeType) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustedEntityId trustorId,
			TrustValueType trustValueType) throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<TrustRelationship>> retrieveTrustRelationships(
			Requestor requestor, TrustedEntityId trustorId,
			TrustedEntityType trusteeType, TrustValueType trustValueType)
			throws TrustException {
		// TODO Auto-generated method stub
		return null;
	}
	
}