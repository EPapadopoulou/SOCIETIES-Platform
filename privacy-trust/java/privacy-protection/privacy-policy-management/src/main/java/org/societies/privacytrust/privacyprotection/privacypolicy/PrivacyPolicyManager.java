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
package org.societies.privacytrust.privacyprotection.privacypolicy;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyRegistryManager;
import org.societies.privacytrust.privacyprotection.privacypolicy.gui.PPNWindow;

/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManagerInternal {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManager.class.getName());

	ICommManager commManager;
	ICtxBroker ctxBroker;
	private PrivacyPolicyRegistryManager policyRegistryManager;


	public void init() {
		policyRegistryManager = new PrivacyPolicyRegistryManager(ctxBroker, commManager);
	}


	@Override
	public RequestPolicy getPrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Search
		RequestPolicy privacyPolicy = policyRegistryManager.getPrivacyPolicy(requestor);
		return privacyPolicy;
	}

	@Override
	public String getPrivacyPolicyFromLocation(String location) throws PrivacyException {
		return getPrivacyPolicyFromLocation(location, null);
	}

	@Override
	public String getPrivacyPolicyFromLocation(String location, Map<String, String> options) throws PrivacyException {
		// -- Read options (and create default options)
		String encodingField = "encoding";
		if (null == options) {
			options = new HashMap<String, String>();
		}
		if (!options.containsKey(encodingField)) {
			options.put(encodingField, "UTF-8");
		}

		// -- Retrieve the privacy policy file
		URL url;
		String privacyPolicy = null;
		try {
			url = new URL(location);
			InputStream privacyPolicyStream = url.openStream();
			privacyPolicy = IOUtils.toString(privacyPolicyStream, options.get(encodingField));
		} catch (MalformedURLException e) {
			throw new PrivacyException("Can't find the privacy policy file: \""+location+"", e);
		} catch (IOException e) {
			throw new PrivacyException("Can't read the privacy policy file: \""+location+"", e);
		}
		return privacyPolicy;
	}


	@Override
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException {
		// -- Verify
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		PPNWindow window = new PPNWindow(privacyPolicy);
		policyRegistryManager.updatePrivacyPolicy(privacyPolicy.getRequestor(), window.getRequestPolicy(), window.getConditionRanges());
		return privacyPolicy;
	}


	@Override
	public RequestPolicy updatePrivacyPolicy(String privacyPolicyXml, RequestorBean requestor) throws PrivacyException {
		// Retrieve the privacy policy
		RequestPolicy privacyPolicy = PrivacyPolicyUtils.fromXacmlString(privacyPolicyXml);
		
		if (null == privacyPolicy) {
			throw new PrivacyException("This XML formatted string of the privacy policy can not be parsed as a privacy policy.");
		}
		// Fill the requestor id
		privacyPolicy.setRequestor(requestor);
		
		//Ask user to edit privacy policy:
		
		
		return updatePrivacyPolicy(privacyPolicy);
		
		
	}


	@Override
	public boolean deletePrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyManager not ready");
		}

		// -- Delete
		return policyRegistryManager.deletePrivacyPolicy(requestor);
	}
	@Deprecated
	@Override
	public boolean deletePrivacyPolicy(Requestor requestor) throws PrivacyException {
		return deletePrivacyPolicy(RequestorUtils.toRequestorBean(requestor));
	}

	@Override
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException {
		return PrivacyPolicyUtils.inferPrivacyPolicy(privacyPolicyType, configuration);
	}




	// -- Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.debug("[DependencyInjection] ICommManager injected");
	}
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
		LOG.debug("[DependencyInjection] ICtxBroker injected");
	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (level == 0) {

			if (null == policyRegistryManager) {
				LOG.error("[Manual Dependency Injection] Missing PolicyRegistryManager");
				return false;
			}
		}
		if (level == 0 || level == 1) {
			if (null == ctxBroker) {
				LOG.error("[Dependency Injection] Missing ICtxBorker");
				return false;
			}
			if (null == commManager) {
				LOG.error("[Dependency Injection] Missing ICommManager");
				return false;
			}
			if (null == commManager.getIdManager()) {
				LOG.error("[Dependency Injection] Missing IIdentityManager");
				return false;
			}
		}
		return true;
	}


	@Override
	public IPrivacyPolicyRegistryManager getPolicyRegistryManager() {
		return policyRegistryManager;
	}


}
