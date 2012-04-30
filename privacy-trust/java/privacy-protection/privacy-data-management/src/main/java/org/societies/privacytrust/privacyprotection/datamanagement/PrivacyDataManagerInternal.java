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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyOutcomeConstants;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.model.PrivacyPermission;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerInternal implements IPrivacyDataManagerInternal {
	private static Logger log = LoggerFactory.getLogger(PrivacyDataManagerInternal.class.getSimpleName());

	private SessionFactory sessionFactory;

	public PrivacyDataManagerInternal() {
		
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public ResponseItem getPermission(Requestor requestor, IIdentity ownerId,
			CtxIdentifier dataId) throws PrivacyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier, java.util.List, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyOutcomeConstants)
	 */
	@Override
	public boolean updatePermission(Requestor requestor, IIdentity ownerId, CtxIdentifier dataId, List<Action> actions, PrivacyOutcomeConstants permission) throws PrivacyException {
		Session session = sessionFactory.openSession();
		PrivacyPermission privacyPermissionEntry = null;
		Transaction t = session.beginTransaction();
		try {
			privacyPermissionEntry = new PrivacyPermission("me@societies.local", "myData", permission);
			session.save(privacyPermissionEntry);
			t.commit();
			log.debug("PrivacyPermission saved.");
		} catch (Exception e) {
			t.rollback();
			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)
	 */
	@Override
	public boolean updatePermission(Requestor requestor, ResponseItem permission)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public boolean deletePermission(Requestor requestor, IIdentity ownerId,
			CtxIdentifier dataId) throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	// --- Dependency Injection
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		log.info("sessionFactory injected");
		this.sessionFactory = sessionFactory;
	}

}
