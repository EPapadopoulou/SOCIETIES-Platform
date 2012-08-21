/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.integration.test.bit.privacydatamanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.DataIdentifierFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerTest
{
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class.getSimpleName());
	public static Integer testCaseNumber = 0;

	private DataIdentifier dataId;
	private IIdentity ownerId;
	private RequestorCis requestorCis;
	private RequestorService requestorService;


	@Before
	public void setUp() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase1266.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyDataManagerTest not ready");
		}
		// Data
		ownerId = TestCase1266.commManager.getIdManager().getThisNetworkNode();
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		// Data Id
		try {
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://john@societies.local/ENTITY/person/1/ATTRIBUTE/name/13");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		} 
	}

	@After
	public void tearDown() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::tearDown");
	}


	@Test
	public void CheckPermissionFirstTime()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		boolean dataUpdated = false;
		ResponseItem permission = null;
		try {
			Action read = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			permission = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permission);
		assertNotNull("No (real) permission retrieved", permission.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission.getDecision().name());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager#checkPermission(org.societies.api.internal.mock.DataIdentifier, IIdentity, IIdentity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)}.
	 */
	@Test
	public void CheckPermissionPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		boolean dataUpdated = false;
		ResponseItem permission1 = null;
		ResponseItem permission2 = null;
		try {
			Action read = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			Decision decision = Decision.PERMIT;
			permission1 = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actions);
			permission2 = TestCase1266.privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permission1);
		assertNotNull("No (real) permission retrieved", permission1.getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permission1.getDecision().name());
		assertNotNull("No permission retrieved", permission2);
		assertNotNull("No (real) permission retrieved", permission2.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permission2.getDecision().name());
		assertEquals("Two requests, not the same answer", permission1, permission2);
	}

	@Test
	public void ObfuscateData()
	{
		String testTitle = new String("ObfuscateData");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		IDataWrapper<Name> wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
		Future<IDataWrapper> obfuscatedDataWrapperAsync = null;
		IDataWrapper<Name> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapperAsync = TestCase1266.privacyDataManager.obfuscateData(requestorCis, wrapper);
			obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		} catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async error] "+testTitle, e);
			fail("InterruptedException async error ("+e.getMessage()+") "+testTitle);
		} catch (ExecutionException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async exec error] "+testTitle, e);
			fail("InterruptedException async exec error ("+e.getMessage()+") "+testTitle);
		}

		// Verify
		LOG.debug("### Orginal name:\n"+wrapper.getData().toString());
		LOG.debug("### Obfuscated name:\n"+obfuscatedDataWrapper.getData().toString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
	}

	@Test
	public void testHasObfuscatedVersion()
	{
		String testTitle = new String("HasObfuscatedVersion");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		IDataWrapper<Name> wrapper = new DataWrapper<Name>(dataId, null);
		LOG.info("[#"+testCaseNumber+"] "+wrapper);
		IDataWrapper actual = null;
		try {
			actual = TestCase1266.privacyDataManager.hasObfuscatedVersion(requestorCis, wrapper);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("Expected data id is not null", actual);
		assertEquals("Retrieved data id is not same as the first", dataId.getUri(), actual.getDataId().getUri());
	}



	/* ****************************
	 *            Tools           *
	 ******************************/

	private RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException{
		IIdentity requestorId = TestCase1266.commManager.getIdManager().fromJid("red@societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
		serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		IIdentity otherCssId = TestCase1266.commManager.getIdManager().fromJid("red@societies.local");
		IIdentity cisId = TestCase1266.commManager.getIdManager().fromJid("onecis.societies.local");
		return new RequestorCis(otherCssId, cisId);
	}
}
