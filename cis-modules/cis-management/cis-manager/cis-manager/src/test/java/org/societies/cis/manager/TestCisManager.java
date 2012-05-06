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

package org.societies.cis.manager;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.societies.activity.ActivityFeed;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRecord;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.persistance.IPersistanceManager;
import org.societies.identity.NetworkNodeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import static org.mockito.Mockito.*;

/**
 * Junit and Mockito Test for CIS
 *
 * @author Thomas Vilarinho (Sintef)
 *
 */
//@RunWith(PowerMockRunner.class)
@PrepareForTest( { ActivityFeed.class })
@ContextConfiguration(locations = { "../../../../CisManagerTest-context.xml" })
public class TestCisManager extends AbstractTransactionalJUnit4SpringContextTests {
	
	//@Autowired
	private CisManager cisManagerUnderTest;
	@Autowired
	private SessionFactory sessionFactory;
	private ICISCommunicationMgrFactory mockCcmFactory;
	private ICommManager mockCSSendpoint;
	private IPersistanceManager mockPM;
	private ICommManager mockCISendpoint1;
	private ICommManager mockCISendpoint2;
	private ICommManager mockCISendpoint3;
	
	public static final String TEST_GOOD_JID = "testXcmanager.societies.local";
	public static final String TEST_CSSID = "juca@societies.local";
	public static final String TEST_CSS_PWD = "password";
	public static final String TEST_CIS_NAME_1 = "Flamengo Futebol Clube";
	public static final String TEST_CIS_TYPW = "futebol";
	public static final int TEST_CIS_MODE = 0;
	
	public static final String TEST_CISID_1 = "flamengo.societies.local";
	public static final String TEST_CISID_2 = "santos.societies.local";
	public static final String TEST_CIS_NAME_2 = "Santos Futebol Clube";
	public static final String TEST_CISID_3 = "palmeiras.societies.local";
	public static final String TEST_CIS_NAME_3 = "Palmeiras Futebol Clube";
	
	
	IIdentityManager mockIICisManagerId;
	INetworkNode testCisManagerId;
	INetworkNode testCisId_1;
	INetworkNode testCisId_2;
	INetworkNode testCisId_3;
	IIdentityManager mockIICisId_1;
	IIdentityManager mockIICisId_2;
	IIdentityManager mockIICisId_3;
	
	void setUpFactory() throws Exception {
		System.out.println("in setupFactory!");
		mockCcmFactory = mock(ICISCommunicationMgrFactory.class);
		
		// mocking the IcomManagers
		mockCISendpoint1 = mock (ICommManager.class);
		mockCISendpoint2 = mock (ICommManager.class);
		mockCISendpoint3 = mock (ICommManager.class);

		// mocking their Identity Manager
		mockIICisId_1 = mock (IIdentityManager.class);
		mockIICisId_2 = mock (IIdentityManager.class);
		mockIICisId_3 = mock (IIdentityManager.class);

		
		// creating a NetworkNordImpl for each Identity Manager		
		testCisId_1 = new NetworkNodeImpl(TEST_CISID_1);
		testCisId_2 = new NetworkNodeImpl(TEST_CISID_2);
		testCisId_3 = new NetworkNodeImpl(TEST_CISID_3);
		when(mockCISendpoint1.getIdManager()).thenReturn(mockIICisId_1);
		when(mockCISendpoint2.getIdManager()).thenReturn(mockIICisId_2);
		when(mockCISendpoint3.getIdManager()).thenReturn(mockIICisId_3);
		
		when(mockIICisId_1.getThisNetworkNode()).thenReturn(testCisId_1);
		when(mockIICisId_2.getThisNetworkNode()).thenReturn(testCisId_2);
		when(mockIICisId_3.getThisNetworkNode()).thenReturn(testCisId_3);
		
		
		when(mockCcmFactory.getNewCommManager()).thenReturn(mockCISendpoint1,mockCISendpoint2,mockCISendpoint3);
		
		
		
	}
	
	@Before
	public void setUp() throws Exception {
		// create mocked class
		System.out.println("in setup!");
		mockCSSendpoint = mock (ICommManager.class);
		mockPM = mock(IPersistanceManager.class);

		mockIICisManagerId = mock (IIdentityManager.class);
		
		testCisManagerId = new NetworkNodeImpl(TEST_GOOD_JID);
		
		// mocking the CISManager
		when(mockCSSendpoint.getIdManager()).thenReturn(mockIICisManagerId);
		when(mockIICisManagerId.getThisNetworkNode()).thenReturn(testCisManagerId);
		doNothing().when(mockCSSendpoint).register(any(org.societies.api.comm.xmpp.interfaces.IFeatureServer.class));
		
		// mocking the activity feed static methods
		PowerMockito.mockStatic(ActivityFeed.class);
		System.out.println("in setup! cisManagerUnderTest.getSessionFactory(): "+sessionFactory);
		ActivityFeed.setStaticSessionFactory(sessionFactory);
		cisManagerUnderTest.setSessionFactory(sessionFactory);
//		Mockito.when(ActivityFeed.startUp(anyString())).thenReturn(new ActivityFeed());
		setUpFactory();
		
	}

	@After
	public void tearDown() throws Exception {
		mockCcmFactory = null;
		mockCSSendpoint = null;
		mockPM = null;
		testCisManagerId = null;

	}

	@Test
	public void testConstructor() {

		cisManagerUnderTest = new CisManager(mockCcmFactory,mockCSSendpoint);
		assertEquals(TEST_GOOD_JID, cisManagerUnderTest.cisManagerId.getJid());
	}

	@Test
	public void testCreateCIS() {

		cisManagerUnderTest = new CisManager(mockCcmFactory,mockCSSendpoint);
		Future<ICisOwned> testCIS = cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE);
		try {
			assertNotNull(testCIS.get());
			assertNotNull(testCIS.get().getCisId());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	@Ignore
	@Test
	public void testListCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager(mockCcmFactory,mockCSSendpoint);
		
		
		ICisOwned[] ciss = new ICisOwned [3]; 
		int[] cissCheck = {0,0,0};
		
		ciss[0] =  (cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[1] = (cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_2, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[2] = (cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_3, TEST_CIS_TYPW , TEST_CIS_MODE)).get();

		List<ICisRecord> l = cisManagerUnderTest.getCisList();
		Iterator<ICisRecord> it = l.iterator();
		 
		while(it.hasNext()){
			 ICisRecord element = it.next();
			 assertEquals(element.getOwnerId(),TEST_CSSID);
			 for(int i=0;i<ciss.length;i++){
				 if(element.getName().equals(ciss[i].getName()) 
				&& 	element.getCisId().equals(ciss[i].getCisId())
				&& 	element.getCisType().equals(ciss[i].getCisType())
				&& 	(element.getMembershipCriteria() == ciss[i].getMembershipCriteria())		 
						 )
					 cissCheck[i] = 1; // found a matching CIS
					 
			 }
			 
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		// check if it found all matching CISs
		 for(int i=0;i<ciss.length;i++){
			 assertEquals(cissCheck[i], 1);
		 }
	
	}
	@Ignore
	@Test
	public void testdeleteCIS() throws InterruptedException, ExecutionException {

		cisManagerUnderTest = new CisManager(mockCcmFactory,mockCSSendpoint);

		ICisOwned[] ciss = new ICisOwned [2]; 
		String jidTobeDeleted = "";
		
		ciss[0] =  (cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_1, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		ciss[1] = (cisManagerUnderTest.createCis(TEST_CSSID, TEST_CSS_PWD,
				TEST_CIS_NAME_2, TEST_CIS_TYPW , TEST_CIS_MODE)).get();
		
		List<ICisRecord> l = cisManagerUnderTest.getCisList();
		Iterator<ICisRecord> it = l.iterator();
		ICisRecord element = it.next(); 
		jidTobeDeleted = element.getCisId();
		
		cisManagerUnderTest.deleteCis(jidTobeDeleted, "", "");
		
		// get a new iterator
		it = l.iterator();
		boolean presence = false;
		int interactions = 0;
		while(it.hasNext()){
			 element = it.next();
			 interactions++;
			 if(element.getCisId().equals(jidTobeDeleted))		 
						presence = true; // found a matching CIS
	     }
		
		//assertEquals(false,presence);
		//assertEquals(1,interactions);
		
	
	}
	
	
}
