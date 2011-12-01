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

package org.societies.api.internal.security.policynegotiator;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;

/**
 * Interface for invoking the requester side (either the policy negotiator or
 * some other component).
 * To be used by policy negotiator from the provider side (from some other node).
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequesterCallback {

	/**
	 * Async return for
	 * {@link INegotiationRequester#getPolicyOptions(INegotiationRequesterCallback)}.
	 * 
	 * @param sops All available options for policy, embedded in a single XML document.
	 */
	public void onGetPolicyOptions(String sops);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#negotiatePolicy(int, ResponsePolicy, INegotiationRequesterCallback)}.
	 * 
	 * @param modifiedPolicy Policy possibly modified by provider side.
	 * Based on the policy sent before by the requester side.
	 */
	public void onNegotiatePolicy(ResponsePolicy modifiedPolicy);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#acceptPolicy(int, ResponsePolicy, INegotiationRequesterCallback)}.
	 * 
	 * @param policy XML-based final policy signed by both parties.
	 */
	public void onAcceptPolicy(String policy);
}
