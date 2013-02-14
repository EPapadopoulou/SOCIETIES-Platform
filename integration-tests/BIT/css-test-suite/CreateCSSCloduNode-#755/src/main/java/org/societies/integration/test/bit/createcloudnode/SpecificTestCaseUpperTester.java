package org.societies.integration.test.bit.createcloudnode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.integration.test.IntegrationTestUtils;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.internal.css.ICSSInternalManager;

public class SpecificTestCaseUpperTester {
	
private static Logger LOG = LoggerFactory.getLogger(SpecificTestCaseUpperTester.class);
	
	public IntegrationTestUtils integrationTestUtils;
	
	CssRecord profile = null;
	CssRecord cssDetails = null;
	Future<CssInterfaceResult> interfaceResult = null;
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	public static final String TEST_IDENTITY_1 = "node55";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "cloud";
	public static final String TEST_EMAIL = "Liam@tssg.org";
	public static final String TEST_FORENAME = "Liam";
	public static final String TEST_NAME = "Cloud CSS";
	public static final String TEST_POSITION = "LOSER";
	public static final String TEST_WORKPLACE = "Lake";
	
	public SpecificTestCaseUpperTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}
	
	@BeforeClass
	public static void initialization() {
		LOG.info("[#755] Initialization");
		LOG.info("[#755] Prerequisite: The PLAN file is executed");
		LOG.info("[#755] Prerequisite: The CSS has an identity");
	}
	
	@Before
	public void setUp() {
		LOG.info("[#755] SpecificTestCaseUpperTester::setUp");

		Future<CssInterfaceResult> interfaceResult = null;
		//CssRecord profile = null;
		
		profile = createCSSRecord();
		LOG.info("[#755] SpecificTestCaseUpperTester CSS Record: profile:" +profile);

			// -- Create the CSS Record
		
			LOG.info("[#755] SpecificTestCaseUpperTester Preamble: Create the CSS Record");
	}
	
	private CssRecord createCSSRecord() {
		
		
		
    	CssNode cssNode_1;

		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Cloud.ordinal());

		//cssNode_2 = new CssNode();
		//cssNode_2.setIdentity(TEST_IDENTITY_2);
		//cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		//cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		

		CssRecord cssProfile = new CssRecord();
		cssProfile.getCssNodes().add(cssNode_1);
		//cssProfile.getCssNodes().add(cssNode_2);
		//cssProfile.getArchiveCSSNodes().add(cssNode_1);
		//cssProfile.getArchiveCSSNodes().add(cssNode_2);
		
		cssProfile.setCssIdentity(TEST_IDENTITY);
		cssProfile.setEmailID(TEST_EMAIL);
		cssProfile.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		cssProfile.setForeName(TEST_FORENAME);
		cssProfile.setName(TEST_NAME);
		cssProfile.setSex(CSSManagerEnums.genderType.Male.ordinal());
		cssProfile.setWorkplace(TEST_WORKPLACE);
		cssProfile.setPosition(TEST_POSITION);
		
		return cssProfile;
	}
	
	/**
	 * This method is called after every @Test methods
	 * 
	 */
	@After
	public void tearDown() {
		LOG.info("[#755] SpecificTestCaseUpperTester tearDown");
	}
	
	
	@Test
	public void bodyCreateNodetwice() {
		LOG.info("[#755] SpecificTestCaseUpperTester create CSS Cloud Node");
		String Name = null;
		Name ="liam";
		String compareName;
		assertTrue(null != Name);
		
		interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		assertTrue(null != interfaceResult);
		try {
			cssDetails = TestCase755.cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.info("[#755] SpecificTestCaseUpperTester CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		assertTrue(null != cssDetails);
		
		try {
			Name = TestCase755.cssRegistry.getCssRecord().getName();
			LOG.info("[#755] SpecificTestCaseUpperTester Name of CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#755] SpecificTestCaseUpperTester CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		//assertEquals(Name, compareName);
		
		interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		LOG.info("[#755] SpecificTestCaseUpperTester CreateNodetwice END.................");
	}
	
	@Test
	public void bodydeleteNon_existnode() {
		LOG.info("[#755] SpecificTestCaseUpperTester create CSS Cloud Node");
		String Name = null;
		Name ="liam";
		String compareName;
		assertNotNull(Name);
		
		interfaceResult = TestCase755.cssLocalManager.registerCSSNode(profile);
		assertNotNull(interfaceResult);
		
		try {
			cssDetails = TestCase755.cssRegistry.getCssRecord();
		} catch (CssRegistrationException e) {
			LOG.info("[#755] SpecificTestCaseUpperTester CssRegistrationException - Could not get the CSS Record from the CSS Registry");
			e.printStackTrace();
		}
		
		assertNotNull(cssDetails);
		
		TestCase755.cssLocalManager.unregisterCSSNode(cssDetails);
		
		try {
			Name = TestCase755.cssRegistry.getCssRecord().getName();
			LOG.info("[#755] Name of this CSS is .................:" +Name);
		} catch (CssRegistrationException e) {
			LOG.info("[#755] SpecificTestCaseUpperTester CssRegistrationException - Could not get the CSS Record Name from the CSS Registry");
			e.printStackTrace();
		}
		compareName = profile.getName();
		LOG.info("[#755] CompareName of this CSS is .................:" +compareName);
		

		
		//assertNull(cssDetails);
		TestCase755.cssLocalManager.unregisterCSSNode(cssDetails);
		
		LOG.info("[#755] SpecificTestCaseUpperTester Delete Non_exist Node END.................");
		
		
	}

}
