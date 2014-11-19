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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn;

import java.io.Serializable;
import java.util.UUID;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

/**
 * @author Eliza
 *
 */
public class PPNPOutcome implements IPrivacyOutcome, Serializable{
	private static final int MIN = 0;
	private static final int MAX = 100;
	private int confidenceLevel;
	private final Condition condition;
	private Stage currentStage;
	private final String uuid;
	
	public PPNPOutcome(Condition condition) throws PrivacyException {
		if (condition.getValue()==null){
			throw new PrivacyException("Condition value cannot be null in new PPNPOutcome object");
		}
		if (!PrivacyConditionsConstantValues.getValuesAsList(condition.getConditionConstant()).contains(condition.getValue())){
			throw new PrivacyException("Condition value for condition type: "+condition.getConditionConstant()+" is not valid: "+condition.getValue());
		}
		this.condition = condition;
		this.currentStage = Stage.START;
		this.confidenceLevel = 50;
		this.uuid = UUID.randomUUID().toString();
	}

	public PPNPOutcome(PPNPOutcomeBean bean){
		this.condition = bean.getCondition();
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
		this.uuid = bean.getUuid();
	}
	
	public PPNPOutcomeBean toBean(){
		PPNPOutcomeBean bean = new PPNPOutcomeBean();
		bean.setCondition(condition);
		bean.setConfidenceLevel(confidenceLevel);
		bean.setCurrentStage(currentStage);
		bean.setUuid(uuid);
		return bean;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PPNPOutcome [decision=");
		builder.append(ConditionUtils.toXmlString(condition));
		builder.append("]");
		return builder.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		PPNPOutcome other = (PPNPOutcome) obj;
		if (condition == null) {
			if (other.condition != null) {
				return false;
			}
		} else if (!ConditionUtils.equal(condition, other.getCondition())) {
			return false;
		}
		return true;
	}



	public int getConfidenceLevel() {
		return confidenceLevel;
	}


	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}


	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION;
	}


	public Condition getCondition() {
		return condition;
	}



	public Stage getCurrentStage() {
		// TODO Auto-generated method stub
		return this.currentStage;
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
