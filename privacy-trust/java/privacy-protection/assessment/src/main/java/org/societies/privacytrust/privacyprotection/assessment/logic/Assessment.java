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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.PrivacyLogFilter;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * A wrapper around {@link DataTransferAnalyzer}
 * 
 * Parses the log and tries to find potential privacy breaches that occurred in the past.
 * This can be used for the a-posteriori assessment.
 * 
 * Estimates whether a particular data transmission is a potential privacy breach or not.
 * This can be used for the a-priori assessment.
 *
 * @author Mitja Vardjan
 *
 */
public class Assessment implements IAssessment {

	private static Logger LOG = LoggerFactory.getLogger(Assessment.class);

	private PrivacyLog privacyLog;
	private DataTransferAnalyzer dataTransferAnalyzer;
	private DataAccessAnalyzer dataAccessAnalyzer; 
	
	private HashMap<IIdentity, AssessmentResultIIdentity> assessmentById = new HashMap<IIdentity, AssessmentResultIIdentity>();
	private HashMap<String, AssessmentResultClassName> assessmentByClass = new HashMap<String, AssessmentResultClassName>();
	
	private Date resultsStart;
	private Date resultsEnd;
	
	private int autoPeriod = -1;
	
	public Assessment() {
		LOG.info("Constructor");
	}
	
	// Getters and setters for beans
	public PrivacyLog getPrivacyLog() {
		LOG.debug("getPrivacyLog()");
		return privacyLog;
	}
	public void setPrivacyLog(PrivacyLog privacyLog) {
		LOG.debug("setPrivacyLog()");
		this.privacyLog = privacyLog;
	}

	public void init() {
		LOG.debug("init()");
		dataTransferAnalyzer = new DataTransferAnalyzer(privacyLog);
		dataAccessAnalyzer = new DataAccessAnalyzer(privacyLog.getDataAccess());
		assessAllNow(null, null);
	}
	
	@Override
	public int getAutoPeriod() {
		return autoPeriod;
	}
	
	@Override
	public void setAutoPeriod(int seconds) {
		LOG.info("setAutoPeriod({})", seconds);
		this.autoPeriod = seconds;
	}
	
	@Override
	public void assessAllNow(Date start, Date end) {
		
		LOG.info("assessAllNow({}, {})", start, end);
		
		resultsStart = start;
		resultsEnd = end;
		
		start = nonNullStart(start);
		end = nonNullEnd(end);
		
		// For each sender identity: calculate result and update value in assessmentById
		for (IIdentity sender : privacyLog.getSenderIds()) {
			try {
				AssessmentResultIIdentity ass = dataTransferAnalyzer.estimatePrivacyBreach(sender, start, end);
				LOG.debug("assessAllNow(): updating for identity {}", sender);
				assessmentById.put(sender, ass);
			} catch (AssessmentException e) {
				LOG.warn("assessAllNow(): Skipped a sender identity", e);
			}
		}
		// For each sender class: calculate result and update value in assessmentByClass
		for (String sender : privacyLog.getSenderClassNames()) {
			try {
				AssessmentResultClassName ass = dataTransferAnalyzer.estimatePrivacyBreach(sender, start, end);
				LOG.debug("assessAllNow(): updating for class {}", sender);
				assessmentByClass.put(sender, ass);
			} catch (AssessmentException e) {
				LOG.warn("assessAllNow(): Skipped a sender class", e);
			}
		}
	}
	
	@Override
	public HashMap<IIdentity, AssessmentResultIIdentity> getAssessmentAllIds(Date start, Date end) {
		
		LOG.info("getAssessmentAllIds({}, {})", start, end);
		
		updateResultsIfNeeded(start, end);
		return assessmentById;
	}
	
	@Override
	public HashMap<String, AssessmentResultClassName> getAssessmentAllClasses(Date start, Date end) {

		LOG.info("getAssessmentAllClasses({}, {})", start, end);
		
		updateResultsIfNeeded(start, end);
		return assessmentByClass;
	}

	@Override
	public AssessmentResultIIdentity getAssessment(IIdentity sender, Date start, Date end) {
		
		LOG.info("getAssessment({})", sender);
		
		if (sender == null || sender.getJid() == null) {
			LOG.warn("getAssessment({}): invalid argument", sender);
			return null;
		}
		updateResultsIfNeeded(start, end);
		return assessmentById.get(sender);
	}

	@Override
	public AssessmentResultClassName getAssessment(String sender, Date start, Date end) {

		LOG.info("getAssessment({})", sender);

		if (sender == null) {
			LOG.warn("getAssessment({}): invalid argument", sender);
			return null;
		}
		updateResultsIfNeeded(start, end);
		return assessmentByClass.get(sender);
	}
	
	@Override
	public long getNumDataTransmissionEvents(Date start, Date end) {

		LOG.info("getNumDataTransmissionEvents()");

		PrivacyLogFilter filter = new PrivacyLogFilter();
		filter.setStart(start);
		filter.setEnd(end);
		return privacyLog.search(filter).size();
	}
	
	@Override
	public long getNumDataAccessEvents(Date start, Date end) {

		LOG.info("getNumDataAccessEvents()");
		
		start = nonNullStart(start);
		end = nonNullEnd(end);
		
		List<DataAccessLogEntry> allDataAccessEvents = privacyLog.getDataAccess();
		int count = 0;
		for (DataAccessLogEntry da : allDataAccessEvents) {
			if (da.getTime().after(start) && da.getTime().before(end)) {
				++count;
			}
		}
		return count;
	}
	
	@Override
	public List<IIdentity> getDataAccessRequestors() {
		return dataAccessAnalyzer.getDataAccessRequestors();
	}
	
	@Override
	public List<String> getDataAccessRequestorClasses() {
		return dataAccessAnalyzer.getDataAccessRequestorClasses();
	}

	@Override
	public int getNumDataAccessEvents(IIdentity requestor, Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataAccessAnalyzer.getNumDataAccessEvents(requestor, start, end);
	}

	@Override
	public int getNumDataAccessEvents(String requestorClass, Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataAccessAnalyzer.getNumDataAccessEvents(requestorClass, start, end);
	}
	
	@Override
	public Map<IIdentity, Integer> getNumDataAccessEventsForAllIdentities(Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataAccessAnalyzer.getNumDataAccessEventsForAllIdentities(start, end);
	}
	
	@Override
	public Map<String, Integer> getNumDataAccessEventsForAllClasses(Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataAccessAnalyzer.getNumDataAccessEventsForAllClasses(start, end);
	}
	
	@Override
	public List<IIdentity> getDataTransmissionReceivers() {
		return dataTransferAnalyzer.getDataTransmissionReceivers();
	}
	
	@Override
	public int getNumDataTransmissionEvents(IIdentity receiver, Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataTransferAnalyzer.getNumDataTransmissionEvents(receiver, start, end);
	}

	@Override
	public Map<IIdentity, Integer> getNumDataTransmissionEventsForAllReceivers(Date start, Date end) {
		start = nonNullStart(start);
		end = nonNullEnd(end);
		return dataTransferAnalyzer.getNumDataTransmissionEventsForAllReceivers(start, end);
	}
	
	private Date nonNullStart(Date d) {
		if (d == null) {
			return new Date(0);
		}
		else {
			return d;
		}
	}
	
	private Date nonNullEnd(Date d) {
		if (d == null) {
			return new Date();
		}
		else {
			return d;
		}
	}
	
	private boolean areDatesEqual(Date a, Date b) {
		if (a == null) {
			return b == null;
		}
		else {
			return a.equals(b);
		}
	}
	
	private void updateResultsIfNeeded(Date start, Date end) {
		if (!areDatesEqual(start, resultsStart) || !areDatesEqual(end, resultsEnd)) {
			LOG.debug("Previous results are for different time interval: from {} to {}. Updating...",
					resultsStart, resultsEnd);
			assessAllNow(start, end);
		}
	}
}
