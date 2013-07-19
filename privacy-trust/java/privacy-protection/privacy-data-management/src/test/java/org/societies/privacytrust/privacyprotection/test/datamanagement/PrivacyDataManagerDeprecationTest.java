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
package org.societies.privacytrust.privacyprotection.test.datamanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.NameUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author olivierm
 *
 */
//Run this test case using Spring jUnit
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "PrivacyDataManagerInternalTest-context.xml" })
public class PrivacyDataManagerDeprecationTest {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerDeprecationTest.class.getSimpleName());

	@Autowired
	IPrivacyDataManager privacyDataManager;
	@Autowired
	IPrivacyDataManagerInternal privacyDataManagerInternal;

	// -- Mocked data
	private DataIdentifier dataId;
	private DataIdentifier cisDataId;
	private Requestor requestor;
	private Requestor requestorCis;



	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Requestor
		IIdentity myCssId = new MockIdentity(IdentityType.CSS, "mycss","societies.local");
		IIdentity otherCssId = new MockIdentity(IdentityType.CSS, "othercss","societies.local");
		IIdentity cisId = new MockIdentity(IdentityType.CIS, "cis-one", "societies.local");
		requestor = new Requestor(otherCssId);
		requestorCis = new RequestorCis(otherCssId, cisId);

		// Data Id
		try {
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId.getJid()+"/ENTITY/person/1/ATTRIBUTE/name/13");
			cisDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisId.getJid()+"/cis-member-list");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		} 

		// Comm Manager
		ICommManager commManager = Mockito.mock(ICommManager.class);
		IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
		INetworkNode myCssNetworkNode = Mockito.mock(INetworkNode.class);
		Mockito.when(myCssNetworkNode.getJid()).thenReturn(myCssId.getJid());
		Mockito.when(idManager.getThisNetworkNode()).thenReturn(myCssNetworkNode);
		Mockito.when(idManager.fromJid(myCssId.getJid())).thenReturn(myCssId);
		Mockito.when(idManager.fromJid(otherCssId.getJid())).thenReturn(otherCssId);
		Mockito.when(idManager.fromJid(cisId.getJid())).thenReturn(cisId);
		Mockito.when(commManager.getIdManager()).thenReturn(idManager);

		// CIS Manager
		ICisManager cisManager = Mockito.mock(ICisManager.class);

		// Privacy Policy Manager
		((PrivacyDataManager) privacyDataManager).setCommManager(commManager);
		((PrivacyDataManager) privacyDataManager).setCisManager(cisManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/* --- CHECK PERMISSION CSS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAdded() {
		String testTitle = new String("CheckPermission: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestor, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAddedRequestorCis() {
		String testTitle = new String("CheckPermission: previously added permission, the requestor is a CIS");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyAddedRequestorCisError() {
		String testTitle = new String("CheckPermission: the requestor is a CIS, previously added permission with a CSS requestor");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, dataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		catch (Exception e) {
			LOG.error("[Test Exception] "+testTitle, e);
			fail("[Error "+testTitle+"] error: "+e);
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeleted() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, dataId);
			permissions = privacyDataManager.checkPermission(requestor, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e);
		}
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}
	@Test
	@Rollback(true)
	public void testCheckPermissionPreviouslyDeletedRequestorCis() {
		String testTitle = new String("CheckPermission: permission previously deleted, it is sure that it doesn't exist. The requestor is a CIS.");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action read = new Action(ActionConstants.READ);
			Action write = new Action(ActionConstants.WRITE, true);
			List<Action> actions = new ArrayList<Action>();
			actions.add(read);
			actions.add(write);
			dataUpdated = privacyDataManagerInternal.updatePermission(requestorCis, dataId, actions, Decision.PERMIT);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestorCis, dataId);
			permissions = privacyDataManager.checkPermission(requestorCis, dataId, actions);
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertTrue("Data permission not deleted", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyAdded() {
		String testTitle = new String("CheckPermissionCis: previously added permission");
		LOG.info("[TEST] "+testTitle);
		boolean dataUpdated = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			Decision decision = Decision.PERMIT;
			dataUpdated = privacyDataManagerInternal.updatePermission(requestor, cisDataId, actions, decision);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataUpdated);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	@Rollback(true)
	public void testCheckPermissionCisPreviouslyDeleted() {
		String testTitle = new String("CheckPermissionCis: permission previously deleted, it is sure that it doesn't exist");
		LOG.info("[TEST] "+testTitle);
		boolean dataDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			Action action = new Action(ActionConstants.READ);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			dataDeleted = privacyDataManagerInternal.deletePermissions(requestor, cisDataId);
			permissions = privacyDataManager.checkPermission(requestor, cisDataId, actions);
		} catch (PrivacyException e) {
			LOG.info("[Test PrivacyException] "+testTitle, e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		}
		assertTrue("Data permission not updated", dataDeleted);
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		LOG.debug("Permission retrieved: "+ResponseItemUtils.toString(ResponseItemUtils.toResponseItemBeans(permissions)));
		assertNotNull("No permission decision retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions.get(0).getDecision().name());
	}

	/* --- OBFUSCATION --- */

	@Test
	public void testObfuscateData() {
		String testTitle = new String("testObfuscateData");
		LOG.info("[TEST] "+testTitle);
		DataWrapper wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
		DataWrapper obfuscatedDataWrapper = null;
		try {
			IIdentity requestorId = Mockito.mock(IIdentity.class);
			Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
			RequestorBean requestor = RequestorUtils.create(requestorId.getJid());
			Future<DataWrapper> obfuscatedDataWrapperAsync = privacyDataManager.obfuscateData(requestor, wrapper);
			obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle+": "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (InterruptedException e) {
			LOG.error("[Test InterruptedException] "+testTitle+": Async interrupted error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async interrupted error "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.error("[Test ExecutionException] "+testTitle+": Async execution error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async execution error "+e.getMessage());
		}

		// Verify
		LOG.info("### Orginal name:\n"+NameUtils.toString((Name) wrapper.getData()));
		LOG.info("### Obfuscated name:\n"+NameUtils.toString((Name) obfuscatedDataWrapper.getData()));
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
	}

	@Test
	public void testObfuscateCtxData() {
		String testTitle = new String("testObfuscateCtxData");
		LOG.info("[TEST] "+testTitle);
		// -- Prepare data
		// Values
		String ownerId = "fooCss";
		String firstnameStr = "Olivier";
		String lastnameStr = "Maridat";
		double latitude = 45.255;
		double longitude = 2.45;
		double accuracy = 100.5;
		// Ids
		DataIdentifier nameId;
		DataIdentifier firstnameId = null;
		DataIdentifier lastnameId = null;
		DataIdentifier actionId = null;
		DataIdentifier locationCoordinatesId = null;
		// Data
		List<CtxModelObject> ctxDataListName;
		List<CtxModelObject> ctxDataListAction;
		List<CtxModelObject> ctxDataListLocationCoordinates;
		List<CtxModelObject> ctxDataListMix;
		CtxAttribute firstname;
		CtxAttribute lastname;
		CtxAttribute action;
		CtxAttribute locationCoordinates;
		// Generate Ids
		try {
			nameId = DataIdentifierFactory.create(DataIdentifierScheme.CONTEXT, ownerId, CtxAttributeTypes.NAME);
			firstnameId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_FIRST+"/33");
			lastnameId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_LAST+"/38");
			actionId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.ACTION+"/42");
			locationCoordinatesId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.LOCATION_COORDINATES+"/76");
		} catch (MalformedCtxIdentifierException e) {
			fail("Faillure during data id creation from URI: "+e);
		}
		// Create list of CtxModelObject lists
		ctxDataListName = new ArrayList<CtxModelObject>();
		firstname = new CtxAttribute((CtxAttributeIdentifier) firstnameId);
		firstname.setStringValue(firstnameStr);
		firstname.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		firstname.setValueType(CtxAttributeValueType.STRING);
		ctxDataListName.add(firstname);
		lastname = new CtxAttribute((CtxAttributeIdentifier) lastnameId);
		lastname.setStringValue(lastnameStr);
		lastname.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		lastname.setValueType(CtxAttributeValueType.STRING);
		ctxDataListName.add(lastname);

		ctxDataListAction = new ArrayList<CtxModelObject>();
		action = new CtxAttribute((CtxAttributeIdentifier) actionId);
		action.setStringValue("Do this !");
		action.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		action.setValueType(CtxAttributeValueType.STRING);
		ctxDataListAction.add(action);

		ctxDataListLocationCoordinates = new ArrayList<CtxModelObject>();
		locationCoordinates = new CtxAttribute((CtxAttributeIdentifier) locationCoordinatesId);
		locationCoordinates.setStringValue(latitude+","+longitude);
		locationCoordinates.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		locationCoordinates.getQuality().setPrecision(accuracy);
		action.setValueType(CtxAttributeValueType.STRING);
		ctxDataListLocationCoordinates.add(locationCoordinates);
		
		ctxDataListMix = new ArrayList<CtxModelObject>();
		ctxDataListMix.addAll(ctxDataListAction);
		ctxDataListMix.addAll(ctxDataListLocationCoordinates);
		ctxDataListMix.addAll(ctxDataListName);
		
		// Requestor
		IIdentity requestorId = Mockito.mock(IIdentity.class);
		Mockito.when(requestorId.getJid()).thenReturn("otherCss@societies.local");
		RequestorBean requestor = RequestorUtils.create(requestorId.getJid());
		
		// Mock Privacy preference manager
		IPrivacyPreferenceManager privacyPreferencesManagerMocked = Mockito.mock(IPrivacyPreferenceManager.class);
		Mockito.when(privacyPreferencesManagerMocked.evaluateDObfPreference((DObfPreferenceDetailsBean) Matchers.anyObject())).thenReturn(0.5);
		((PrivacyDataManager)privacyDataManager).setPrivacyPreferenceManager(privacyPreferencesManagerMocked);
		
		// -- Launch obfuscation
		List<CtxModelObject> obfuscatedNameDataList = null;
		List<CtxModelObject> obfuscatedActionDataList = null;
		List<CtxModelObject> obfuscatedLocationCoordinatesDataList = null;
		List<CtxModelObject> obfuscatedMixDataList = null;
		try {
			obfuscatedNameDataList = privacyDataManager.obfuscateData(requestor, ctxDataListName).get();
			obfuscatedActionDataList = privacyDataManager.obfuscateData(requestor, ctxDataListAction).get();
			obfuscatedLocationCoordinatesDataList = privacyDataManager.obfuscateData(requestor, ctxDataListLocationCoordinates).get();
			obfuscatedMixDataList = privacyDataManager.obfuscateData(requestor, ctxDataListMix).get();
		} catch (PrivacyException e) {
			LOG.error("[Test PrivacyException] "+testTitle+": "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: "+e.getMessage());
		} catch (InterruptedException e) {
			LOG.error("[Test InterruptedException] "+testTitle+": Async interrupted error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async interrupted error "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.error("[Test ExecutionException] "+testTitle+": Async execution error "+e.getMessage()+"\n", e);
			fail("[Error "+testTitle+"] Privacy error: Async execution error "+e.getMessage());
		}

		// -- Verify
		// Name
		assertNotNull("Retrieved name obfuscated data should not be null", obfuscatedNameDataList);
		assertTrue("Name data list size should not have been modified", ctxDataListName.size() == obfuscatedNameDataList.size());
		int lastNameIndex = 0;
		if (CtxAttributeTypes.NAME_LAST.equals(obfuscatedNameDataList.get(1).getId().getType())) {
			lastNameIndex = 1;
		}
		assertEquals("Name data should have been obfuscated", "M.", ((CtxAttribute)obfuscatedNameDataList.get(lastNameIndex)).getStringValue());
		LOG.info("Original result 1: "+((CtxAttribute)ctxDataListName.get(0)).getStringValue()+"|"+((CtxAttribute)ctxDataListName.get(0)).getQuality().getOriginType());
		LOG.info("Original result 2: "+((CtxAttribute)ctxDataListName.get(1)).getStringValue()+"|"+((CtxAttribute)ctxDataListName.get(1)).getQuality().getOriginType());
		LOG.info("Obfuscated result 1: "+((CtxAttribute)obfuscatedNameDataList.get(0)).getStringValue()+"|"+((CtxAttribute)obfuscatedNameDataList.get(0)).getQuality().getOriginType());
		LOG.info("Obfuscated result 2: "+((CtxAttribute)obfuscatedNameDataList.get(1)).getStringValue()+"|"+((CtxAttribute)obfuscatedNameDataList.get(1)).getQuality().getOriginType());
		
		// Action
		assertNotNull("Retrieved action obfuscated data should not be null", obfuscatedActionDataList);
		assertTrue("Action data list size should not have been modified", ctxDataListAction.size() == obfuscatedActionDataList.size());
		assertEquals("Unobfuscable data should not be obfuscated", ctxDataListAction, obfuscatedActionDataList);
		
		// Location coordinates
		assertNotNull("Retrieved obfuscated data should not be null", obfuscatedLocationCoordinatesDataList);
		assertTrue("Location Coordinates Data list size should not have been modified", ctxDataListLocationCoordinates.size() == obfuscatedLocationCoordinatesDataList.size());
		LOG.info("Original result: "+((CtxAttribute)ctxDataListLocationCoordinates.get(0)).getStringValue()+","+((CtxAttribute)ctxDataListLocationCoordinates.get(0)).getQuality().getPrecision()+"|"+((CtxAttribute)ctxDataListLocationCoordinates.get(0)).getQuality().getOriginType());
		LOG.info("Obfuscated result: "+((CtxAttribute)obfuscatedLocationCoordinatesDataList.get(0)).getStringValue()+","+((CtxAttribute)obfuscatedLocationCoordinatesDataList.get(0)).getQuality().getPrecision()+"|"+((CtxAttribute)obfuscatedLocationCoordinatesDataList.get(0)).getQuality().getOriginType());
		
		// Mix
		assertNotNull("Retrieved mix obfuscated data should not be null", obfuscatedMixDataList);
		assertTrue("Mix Data list size should not have been modified", ctxDataListMix.size() == obfuscatedMixDataList.size());
		
		// TODO: check a little bit more
	}


	/* --- Data Id --- */
	@Test
	public void testFromUriString() {
		String testTitle = new String("testFromUriString: multiple test of DataId parsing");
		LOG.info("[TEST] "+testTitle);

		String ownerId = "owner.domain.com";
		String dataId1 = DataIdentifierScheme.CIS+"://"+ownerId+"/locationSymbolic/";
		String dataId1b = "CIS://"+ownerId+"/locationSymbolic/";
		String dataId2 = DataIdentifierScheme.CIS+"://"+ownerId+"/locationSymbolic";
		String dataId3 = DataIdentifierScheme.CIS+":///locationSymbolic/";
		String dataId4 = DataIdentifierScheme.CIS+":///locationSymbolic";
		String dataId5 = DataIdentifierScheme.CIS+":///";
		String dataId6 = DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/name/13";
		String dataId6b = "CONTEXT://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/name/13";
		try {
			// CIS
			assertNotNull("Data id from "+dataId1+" should not be null", DataIdentifierFactory.fromUri(dataId1));
			assertEquals("Owner id from "+dataId1+" not retrieved", ownerId, DataIdentifierFactory.fromUri(dataId1).getOwnerId());
			assertNotNull("Data id from "+dataId1b+" should not be null", DataIdentifierFactory.fromUri(dataId1b));
			assertEquals("Owner id from "+dataId1b+" not retrieved", ownerId, DataIdentifierFactory.fromUri(dataId1b).getOwnerId());

			assertNotNull("Data id from "+dataId2+" should not be null", DataIdentifierFactory.fromUri(dataId2));
			assertEquals("Owner id from "+dataId2+" not retrieved", ownerId, DataIdentifierFactory.fromUri(dataId2).getOwnerId());

			assertNotNull("Data id from "+dataId3+" should not be null", DataIdentifierFactory.fromUri(dataId3));
			assertEquals("Owner id from "+dataId3+" not retrieved", "", DataIdentifierFactory.fromUri(dataId3).getOwnerId());

			assertNotNull("Data id from "+dataId4+" should not be null", DataIdentifierFactory.fromUri(dataId4));
			assertEquals("Owner id from "+dataId4+" not retrieved", "", DataIdentifierFactory.fromUri(dataId4).getOwnerId());
			assertEquals("Data type from "+dataId4+" not retrieved", "locationSymbolic", DataIdentifierFactory.fromUri(dataId4).getType());

			assertNotNull("Data id from "+dataId5+" should not be null", DataIdentifierFactory.fromUri(dataId5));
			assertEquals("Owner id from "+dataId5+" not retrieved", "", DataIdentifierFactory.fromUri(dataId5).getOwnerId());
			assertEquals("Data type from "+dataId5+" not retrieved", "", DataIdentifierFactory.fromUri(dataId5).getType());

			// Context
			assertNotNull("Data id from "+dataId6+" should not be null", DataIdentifierFactory.fromUri(dataId6));
			assertEquals("Owner id from "+dataId6+" not retrieved", ownerId, DataIdentifierFactory.fromUri(dataId6).getOwnerId());
			assertEquals("Data type from "+dataId6+" not retrieved", "name", DataIdentifierFactory.fromUri(dataId6).getType());

			assertNotNull("Data id from "+dataId6b+" should not be null", DataIdentifierFactory.fromUri(dataId6b));
			assertEquals("Owner id from "+dataId6b+" not retrieved", ownerId, DataIdentifierFactory.fromUri(dataId6b).getOwnerId());
			assertEquals("Data type from "+dataId6b+" not retrieved", "name", DataIdentifierFactory.fromUri(dataId6b).getType());
		}
		catch(MalformedCtxIdentifierException e) {
			LOG.info("[Error MalformedCtxIdentifierException] "+testTitle, e);
			fail("[Error MalformedCtxIdentifierException] "+testTitle+":"+e.getMessage());
		}
	}

	@Test
	public void testSchemes() {
		String testTitle = new String("testSchemes: multiple test on DataIdentifierScheme");
		LOG.info("[TEST] "+testTitle);

		DataIdentifierScheme schemeCtx1 = DataIdentifierScheme.CONTEXT;
		DataIdentifierScheme schemeCtx2 = DataIdentifierScheme.CONTEXT;
		DataIdentifierScheme schemeCis = DataIdentifierScheme.CIS;

		assertEquals("Schemes should be equals", schemeCtx1, schemeCtx2);
		assertEquals("Schemes name should be equals", schemeCtx1.name(), schemeCtx2.name());
		assertTrue("Schemes should not be equals", !schemeCtx1.equals(schemeCis));
		assertTrue("Schemes name should not be equals", !schemeCtx1.name().equals(schemeCis.name()));
	}

	// -- Dependency Injection
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("[Dependency Injection] IPrivacyDataManager injected");
	}
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}
}