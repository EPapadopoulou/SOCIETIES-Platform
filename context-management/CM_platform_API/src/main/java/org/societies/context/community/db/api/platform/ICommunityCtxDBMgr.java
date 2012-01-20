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

package org.societies.context.community.db.api.platform;

import org.societies.context.model.api.CtxEntityIdentifier;


/**
 * ICommunityCtxDBMgr platform interface. This interface provides access to community context database. 
 * 
 * @author nlia
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxDBMgr {

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community identifier
	 * @param callback 
	 * @since 0.0.1
	 */
	public void retrieveBonds(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param community identifier
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieveChildCommunities(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * Retrieves a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieveCommunityMembers(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * Retrieves communities characterized as parent for the community specified by the Community CtxEntityIdentifier  
	 * 
	 * @param community
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieveParentCommunities(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

}