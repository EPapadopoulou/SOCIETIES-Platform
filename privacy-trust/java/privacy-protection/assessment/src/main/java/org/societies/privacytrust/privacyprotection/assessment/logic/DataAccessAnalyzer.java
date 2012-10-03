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
package org.societies.privacytrust.privacyprotection.assessment.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * Parses the log and creates report about classes and identities that accessed local data.
 * The report should be suitable to be displayed in web browser.
 *
 * @author Mitja Vardjan
 *
 */
public class DataAccessAnalyzer {
	private static Logger LOG = LoggerFactory.getLogger(DataTransferAnalyzer.class);

	private PrivacyLog privacyLog;
	
	public DataAccessAnalyzer(PrivacyLog privacyLog) {
		LOG.info("Constructor");
		this.privacyLog = privacyLog;
	}

	public List<DataAccessLogEntry> getDataAccess(IIdentity requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = new ArrayList<DataAccessLogEntry>();
		String requestorJid;
		
		if (requestor == null || requestor.getJid() == null) {
			LOG.warn("getDataAccess({}): requestor or requestor JID is null", requestor);
			return matchedEntries;
		}
		
		requestorJid = requestor.getJid();
		for (DataAccessLogEntry da : privacyLog.getDataAccess()) {
			if (requestorJid.equals(da.getRequestor().getJid()) &&
					da.getTime().after(start) && da.getTime().before(end)) {
				matchedEntries.add(da);
			}
		}
		return matchedEntries;
	}
	
	public List<DataAccessLogEntry> getDataAccess(String requestor, Date start, Date end) {
		
		List<DataAccessLogEntry> matchedEntries = new ArrayList<DataAccessLogEntry>();

		if (requestor == null) {
			LOG.warn("getDataAccess({}): requestor is null", requestor);
			return matchedEntries;
		}
		
		for (DataAccessLogEntry da : privacyLog.getDataAccess()) {
			if (requestor.equals(da.getRequestorClass()) &&
					da.getTime().after(start) && da.getTime().before(end)) {
				matchedEntries.add(da);
			}
		}
		return matchedEntries;
	}
	
	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestor Identity of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getDataAccessSize(IIdentity requestor, Date start, Date end) {
		List<DataAccessLogEntry> matchedEntries = getDataAccess(requestor, start, end);
		return matchedEntries.size();
	}
	
	/**
	 * Get number of events in certain time period where given requestor accessed local data.
	 * 
	 * @param requestor Identity of the requestor (the one who requested data access)
	 * @param start Match only events after this time
	 * @param end Match only events before this time
	 * @return All events where requestor matches
	 */
	public int getDataAccessSize(String requestor, Date start, Date end) {
		List<DataAccessLogEntry> matchedEntries = getDataAccess(requestor, start, end);
		return matchedEntries.size();
	}
}
