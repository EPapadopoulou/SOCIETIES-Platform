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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol;

import java.io.Serializable;
import java.util.UUID;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

/**
 * This class represents a Rule in XACML format. The PPNPOutcome class contains the following:
 * Effect : PrivacyOutcomeConstants (ALLOW, BLOCK)
 * RuleTarget: A target specifies:
 * 		the Subject: by Identity and if applicable an IServiceResourceIdentifier
 * 		the Resource: by CtxAttributeIdentifier
 * 		the Action: READ,WRITE,CREATE,DELETE
 * Conditions: a list of conditions that have to be satisfied by the other party. These are processed during the negotiation phase 
 * and not during the PPN preference evaluation phase.
 * 
 * @author Elizabeth
 *
 */
public class AccessControlOutcome implements IPrivacyOutcome, Serializable {

	private static final int MIN = 0;
	private static final int MAX = 100;
	private final String uuid;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final PrivacyOutcomeConstantsBean effect;

	private int confidenceLevel;

	private Stage currentStage;



	public AccessControlOutcome(PrivacyOutcomeConstantsBean effect){
		this.effect = effect;
		this.confidenceLevel = 50;
		this.currentStage = Stage.START;
		this.uuid = UUID.randomUUID().toString();
	}
	
	public AccessControlOutcome(AccessControlOutcomeBean bean){
		this.effect = bean.getEffect();
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
		this.uuid = bean.getUuid();
	}

	public AccessControlOutcomeBean toBean() {
		AccessControlOutcomeBean bean = new AccessControlOutcomeBean();
		bean.setConfidenceLevel(confidenceLevel);
		bean.setCurrentStage(currentStage);
		bean.setEffect(effect);
		bean.setUuid(uuid);
		return bean;
	}

	public PrivacyOutcomeConstantsBean getEffect(){
		return this.effect;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.ACCESS_CONTROL;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getConfidenceLevel()
	 */
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}

	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((effect == null) ? 0 : effect.hashCode());

		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccessControlOutcome [effect=");
		builder.append(effect);
		builder.append(", confidenceLevel=");
		builder.append(confidenceLevel);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		AccessControlOutcome other = (AccessControlOutcome) obj;
		if (effect != other.effect) {
			return false;
		}

		return true;
	}

/*	@Override
	public AccessControlOutcome clone(){
		AccessControlOutcome outcome = new AccessControlOutcome(this.effect);
		outcome.setConfidenceLevel(this.confidenceLevel);
		return outcome;
	}*/

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
