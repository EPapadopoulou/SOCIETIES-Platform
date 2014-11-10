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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf;

import java.io.Serializable;
import java.util.UUID;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define the level of obfuscation that has to be applied to
 * a context attribute before being disclosed to an external entity.
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 17:06:54
 */
public class DObfOutcome implements IPrivacyOutcome, Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MIN = 0;
	private static final int MAX = 100;
	private Integer obfuscationLevel;
	private Stage currentStage;
	private int confidenceLevel;
	private final String uuid;

	public DObfOutcome(Integer obfuscationLevel){
		this.obfuscationLevel = obfuscationLevel;
		this.confidenceLevel = 50;
		this.currentStage = Stage.START;
		this.uuid = UUID.randomUUID().toString();
	}


	public DObfOutcome (DObfOutcomeBean bean){
		this.obfuscationLevel = bean.getObfuscationLevel();
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
		this.uuid = bean.getUuid();
	}
	

	public DObfOutcomeBean toBean() {
		DObfOutcomeBean bean = new DObfOutcomeBean();
		bean.setConfidenceLevel(confidenceLevel);
		bean.setCurrentStage(currentStage);
		bean.setObfuscationLevel(obfuscationLevel);
		bean.setUuid(uuid);
		return bean;
	}

	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((obfuscationLevel == null) ? 0 : obfuscationLevel.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DObfOutcome other = (DObfOutcome) obj;
		if (obfuscationLevel == null) {
			if (other.obfuscationLevel != null) {
				return false;
			}
		} else if (!obfuscationLevel.equals(other.obfuscationLevel)) {
			return false;
		}
		return true;
	}


	public PrivacyPreferenceTypeConstants getOutcomeType(){
		return PrivacyPreferenceTypeConstants.DATA_OBFUSCATION;
	}

	public Integer getObfuscationLevel(){
		return obfuscationLevel;
	}

	public Stage getCurrentStage() {
		return this.currentStage;
	}

	public int getConfidenceLevel() {
		return confidenceLevel;
	}


	public String getUuid() {
		return uuid;
	}


	public void updateConfidenceLevel(boolean positive){
		if (positive){
			switch (currentStage){
			case POSITIVE_1:
				confidenceLevel+=20; 
				currentStage = Stage.POSITIVE_2;
				break;
			case POSITIVE_2:
				confidenceLevel+=30;
				currentStage = Stage.POSITIVE_3;
				break;
			default: 
				confidenceLevel+=10;
				currentStage = Stage.POSITIVE_1;
				break;
			}
			if (confidenceLevel>MAX){
				confidenceLevel = 100;
			}
		}else{
			switch (currentStage){
			case NEGATIVE_1:
				confidenceLevel-=20;
				currentStage = Stage.NEGATIVE_2;
				break;
			case NEGATIVE_2:
				confidenceLevel-=30;
				currentStage = Stage.NEGATIVE_3;
				break;
			default:
				confidenceLevel-=10;
				currentStage = Stage.NEGATIVE_1;
				break;
			}
			if (confidenceLevel<MIN){
				confidenceLevel = 0;
			}
		}
		
	}
	
}