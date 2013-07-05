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
package org.societies.integration.test.bit.context.hierarchy;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * The test {@link #setUp()} method creates the following data:
 * <ol>
 * <li>A {@link CtxAttributeTypes#NAME_FIRST NAME_FIRST} attribute under the
 *     context entity of the CSS owner (if not exists).</li>
 * <li>A {@link CtxAttributeTypes#NAME_LAST NAME_LAST} attribute under the
 *     context entity of the CSS owner (if not exists).</li>
 * </ol>
 * 
 * The internal Context Broker is used to retrieve the context data above by
 * looking up the generic attribute type {@link CtxAttributeTypes#NAME NAME}.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 2.0
 */
public class TestLocalInternalRetrieveByType {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalInternalRetrieveByType.class);
	
	private static final String USER_NAME_FIRST = "John";
	private static final String USER_NAME_LAST = "Do";
	
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgr;

	/** The identifiers of the context model objects created in this test. */
	private Set<CtxIdentifier> testCtxIds = new HashSet<CtxIdentifier>();

	/** The CSS owner ID. */
	private IIdentity userId;
	
	private CtxAttributeIdentifier userNameFirstCtxAttrId;
	private String userNameFirstCtxAttrValue;
	private CtxAttributeIdentifier userNameLastCtxAttrId;
	private String userNameLastCtxAttrValue;

	@Before
	public void setUp() throws Exception {
		
		this.internalCtxBroker = CtxDataHierarchyTestCase.getInternalCtxBroker();
		this.commMgr = CtxDataHierarchyTestCase.getCommManager();
		
		this.userId = this.commMgr.getIdManager().fromJid(
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid());
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userId=" + this.userId);
		
		final IndividualCtxEntity userCtxEnt = 
				this.internalCtxBroker.retrieveIndividualEntity(this.userId).get();
		
		// Create NAME_FIRST user attribute if not exists
		CtxAttribute userNameFirstCtxAttr = this.createCtxAttributeIfNotExists(
				userCtxEnt, CtxAttributeTypes.NAME_FIRST);
		if (userNameFirstCtxAttr.getStringValue() == null) {
			userNameFirstCtxAttr.setStringValue(USER_NAME_FIRST);
			this.internalCtxBroker.update(userNameFirstCtxAttr);
		}
		this.userNameFirstCtxAttrId = userNameFirstCtxAttr.getId();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameFirstCtxAttrId=" + this.userNameFirstCtxAttrId);
		this.userNameFirstCtxAttrValue = userNameFirstCtxAttr.getStringValue();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameFirstCtxAttrValue=" + this.userNameFirstCtxAttrValue);
		
		// Create NAME_LAST user attribute if not exists
		CtxAttribute userNameLastCtxAttr = this.createCtxAttributeIfNotExists(
				userCtxEnt, CtxAttributeTypes.NAME_LAST);
		if (userNameLastCtxAttr.getStringValue() == null) {
			userNameLastCtxAttr.setStringValue(USER_NAME_LAST);	
			this.internalCtxBroker.update(userNameLastCtxAttr);
		}
		this.userNameLastCtxAttrId = userNameLastCtxAttr.getId();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameLastCtxAttrId=" + this.userNameLastCtxAttrId);
		this.userNameLastCtxAttrValue = userNameLastCtxAttr.getStringValue();
		if (LOG.isInfoEnabled())
			LOG.info("*** setUp: userNameLastCtxAttrValue=" + this.userNameLastCtxAttrValue);
	}
	
	@After
	public void tearDown() throws Exception {

		this.userId = null;
		this.userNameFirstCtxAttrId = null;
		this.userNameFirstCtxAttrValue = null;
		this.userNameLastCtxAttrId = null;
		this.userNameLastCtxAttrValue = null;
		
		if (LOG.isInfoEnabled())
			LOG.info("tearDown: Removing '" + this.testCtxIds + "' from Context DB");
		for (final CtxIdentifier ctxId : this.testCtxIds)
			this.internalCtxBroker.remove(ctxId);
	}

	@Test
	public void testRetrieveUserName() throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info("*** Starting testRetrieveUserName");

		final List<CtxIdentifier> userNameCtxIds = this.internalCtxBroker.lookup(
				this.userId, CtxAttributeTypes.NAME).get();
		assertNotNull(userNameCtxIds);
		if (LOG.isInfoEnabled())
			LOG.info("*** testRetrieveUserName: userNameCtxIds=" + userNameCtxIds);
		assertFalse(userNameCtxIds.isEmpty());
		assertEquals(2, userNameCtxIds.size());
		assertTrue(userNameCtxIds.contains(this.userNameFirstCtxAttrId));
		assertTrue(userNameCtxIds.contains(this.userNameLastCtxAttrId));
		
		final List<CtxModelObject> userNames = 
				this.internalCtxBroker.retrieve(userNameCtxIds).get();
		assertNotNull(userNames);
		if (LOG.isInfoEnabled())
			LOG.info("*** testRetrieveUserName: userNames=" + userNames);
		assertFalse(userNames.isEmpty());
		assertEquals(2, userNames.size());
		for (final CtxModelObject userName : userNames) {
			assertTrue(userName instanceof CtxAttribute);
			assertTrue(CtxAttributeTypes.NAME_FIRST.equals(userName.getType())
					|| CtxAttributeTypes.NAME_LAST.equals(userName.getType()));
			if (CtxAttributeTypes.NAME_FIRST.equals(userName.getType())) {
				assertEquals(this.userNameFirstCtxAttrId, userName.getId());
				assertEquals(this.userNameFirstCtxAttrValue,
						((CtxAttribute) userName).getStringValue());
			} else { // if (CtxAttributeTypes.NAME_LAST.equals(userName.getType()))
				assertEquals(this.userNameLastCtxAttrId, userName.getId());
				assertEquals(this.userNameLastCtxAttrValue,
						((CtxAttribute) userName).getStringValue());
			}
		}
	}
	
	@Test
	public void testIllegalRetrieve() throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info("*** Starting testIllegalRetrieve");

		
		boolean nullTargetExceptionCaught = false;
		try {
			this.internalCtxBroker.lookup((IIdentity) null, CtxAttributeTypes.NAME).get();
		} catch (NullPointerException npe) {
			nullTargetExceptionCaught = true;
		}
		assertTrue(nullTargetExceptionCaught);
		
		boolean nullTypeExceptionCaught = false;
		try {
			this.internalCtxBroker.lookup(this.userId, null).get();
		} catch (NullPointerException npe) {
			nullTypeExceptionCaught = true;
		}
		assertTrue(nullTypeExceptionCaught);
		
		boolean nullCtxIdListExceptionCaught = false;
		try {
			this.internalCtxBroker.retrieve((List<CtxIdentifier>) null).get();
		} catch (NullPointerException npe) {
			nullCtxIdListExceptionCaught = true;
		}
		assertTrue(nullCtxIdListExceptionCaught);
	}
	
	private CtxAttribute createCtxAttributeIfNotExists(
			final CtxEntity scope, String attrType) throws Exception {
		
		final CtxAttribute result;
		if (scope.getAttributes(attrType).size() == 0) {
			
			// Create new attribute
			result = this.internalCtxBroker.createAttribute(scope.getId(), attrType).get();
			// Add to test context data IDs
			this.testCtxIds.add(result.getId());
			
		} else if (scope.getAttributes(attrType).size() == 1) {
			
			// Return existing attribute
			result = scope.getAttributes(attrType).iterator().next();
			
		} else {
			
			// There can only be one!
			throw new IllegalStateException("Found multiple context attributes of type '"
					+ attrType + "' under entity '" + scope + "'");
		}
		
		return result;
	}
}