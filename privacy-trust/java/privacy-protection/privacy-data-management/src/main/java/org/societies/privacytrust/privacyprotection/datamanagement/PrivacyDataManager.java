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
package org.societies.privacytrust.privacyprotection.datamanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfOutcome;
import org.societies.privacytrust.privacyprotection.dataobfuscation.DataObfuscationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager implements IPrivacyDataManager {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManager.class.getSimpleName());

	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private IDataObfuscationManager dataObfuscationManager;

	public PrivacyDataManager()  {
		dataObfuscationManager = new DataObfuscationManager();
	}


	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.internal.mock.CtxIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("[Parameters] Not enought information: requestor or owner id is missing");
		}
		if (null == dataId) {
			throw new NullPointerException("[Parameters] Not enought information: data id is missing. At least the data type is expected.");
		}
		if (null == actions || actions.size() <= 0) {
			throw new NullPointerException("[Parameters] Actions are missing");
		}
		if (!atLeast1MandatoryAction(actions)) {
			throw new PrivacyException("[Parameters] At least one mandatory action is required, they can't be all optional.");
		}
		if (!isDepencyInjectionDone(1)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}


		// -- Create useful values for default result
		List<Condition> conditions = new ArrayList<Condition>();
		Resource resource = new Resource(dataId);
		RequestItem requestItemNull = new RequestItem(resource, actions, conditions);

		// -- Retrieve a stored permission
		ResponseItem permission = privacyDataManagerInternal.getPermission(requestor, dataId, actions);

		// -- Permission not available: ask to PrivacyPreferenceManager
		if (null == permission || null == permission.getRequestItem()) {
			LOG.info("No Permission retrieved");
			permission = null;
			try {
				permission = privacyPreferenceManager.checkPermission(requestor, dataId, actions);
			} catch (Exception e) {
				LOG.error("Error when retrieving permission from PrivacyPreferenceManager", e);
			}
			// Permission still not available: deny access
			if (null == permission || null == permission.getRequestItem()) {
				permission = new ResponseItem(requestItemNull, Decision.DENY);
			}
			// Store new permission retrieved from PrivacyPreferenceManager
			privacyDataManagerInternal.updatePermission(requestor, permission);
		}
		return permission;
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId, Action action) throws PrivacyException {
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return checkPermission(requestor, dataId, actions);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.internal.mock.CtxIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, IIdentity ownerId, CtxIdentifier dataId, Action action) throws PrivacyException {
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return checkPermission(requestor, dataId, actions);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Async
	@Override
	public Future<IDataWrapper> obfuscateData(Requestor requestor, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("Not enought information: requestor or owner id is missing");
		}
		if (null == dataWrapper || null == dataWrapper.getData()) {
			throw new PrivacyException("Not enought information: data missing");
		}
		if (null == dataWrapper.getDataId()) {
			throw new PrivacyException("Not enought information: data id missing. At least the data type is expected");
		}
		if (!isDepencyInjectionDone(2)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}

		// -- Retrieve the obfuscation level
		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, dataWrapper.getDataId().getType());
		double obfuscationLevel = 1;
		if (null != dataObfuscationPreferences) {
			obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		}
		// If no obfuscation is required: return directly the wrapped data
		if (obfuscationLevel >= 1) {
			return new AsyncResult<IDataWrapper>(dataWrapper);
		}

		// -- Obfuscate the data
		IDataWrapper obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(dataWrapper, obfuscationLevel);
		return new AsyncResult<IDataWrapper>(obfuscatedDataWrapper);
	}
	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Async
	@Override
	public Future<IDataWrapper> obfuscateData(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		return obfuscateData(requestor, dataWrapper);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public IDataWrapper hasObfuscatedVersion(Requestor requestor, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("Not enought information: requestor or owner id is missing");
		}
		if (null == dataWrapper || null == dataWrapper.getDataId()) {
			throw new PrivacyException("Not enought information: data id is missing. At least the data type is expected.");
		}
		return dataWrapper;
		// Not use at the moment
		//		if (!isDepencyInjectionDone(2)) {
		//			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		//		}
		//		
		//		// -- Retrieve the obfuscation level
		//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, dataWrapper.getDataId().getType());
		//		double obfuscationLevel = 1;
		//		if (null != dataObfuscationPreferences) {
		//			obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		//		}
		//		
		//		// -- Check if an obfuscated version is available
		//		return dataObfuscationManager.hasObfuscatedVersion(dataWrapper, obfuscationLevel);
	}
	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public String hasObfuscatedVersion(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		return hasObfuscatedVersion(requestor, dataWrapper).getDataId().getUri();
	}


	// -- Private methods

	/**
	 * Simple method to check if a list of actions has, at least, one action
	 * which is not optional
	 * @param actions List of action
	 * @return True if the list is ok
	 */
	private boolean atLeast1MandatoryAction(List<Action> actions) {
		boolean oneMandatory = false;
		for(Action action : actions) {
			if (!action.isOptional()) {
				oneMandatory = true;
				break;
			}
		}
		return oneMandatory;
	}

	// --- Dependency Injection
	public void setPrivacyPreferenceManager(
			IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
		LOG.info("[Dependency Injection] privacyPreferenceManager injected");
	}
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}


	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == privacyPreferenceManager) {
			LOG.info("[Dependency Injection] Missing PrivacyPreferenceManager");
			return false;
		}
		if (level == 0 || level == 1) {
			if (null == privacyDataManagerInternal) {
				LOG.info("[Dependency Injection] Missing PrivacyDataManagerInternal");
				return false;
			}
		}
		if (level == 0 || level == 2) {
			if (null == dataObfuscationManager) {
				LOG.info("[Dependency Injection] Missing DataObfuscationManager");
				return false;
			}
		}
		return true;
	}
}
