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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids;


import java.io.Serializable;
import java.util.UUID;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define that a CSS IIdentity should be used in a specific transaction if the preceding IPrivacyPreferenceConditions are true. 
 * The format of the IIdentity will be defined by the IIdentity Management component
 * @author Elizabeth
 *
 */
public class IdentitySelectionPreferenceOutcome implements IPrivacyOutcome, Serializable {
	private static final int MIN = 0;
	private static final int MAX = 100;
	private boolean shouldUseIdentity;
	private IIdentity userIdentity;
	private Stage currentStage; 
	private int confidenceLevel;
	private final String uuid;
	
	public IdentitySelectionPreferenceOutcome(IIdentity userIdentity) {
		this.userIdentity = userIdentity;
		this.confidenceLevel = 50;
		this.currentStage = Stage.START;
		this.uuid = UUID.randomUUID().toString();
	}
	public IdentitySelectionPreferenceOutcome(IDSOutcomeBean bean, IIdentityManager idMgr) throws InvalidFormatException {
		this.userIdentity = idMgr.fromJid(bean.getUserIdentity());
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
		uuid = bean.getUuid();
	}
	
	public IDSOutcomeBean toBean() {
		IDSOutcomeBean bean = new IDSOutcomeBean();
		bean.setConfidenceLevel(confidenceLevel);
		bean.setCurrentStage(currentStage);
		bean.setShouldUseIdentity(shouldUseIdentity);
		bean.setUserIdentity(userIdentity.getBareJid());
		bean.setUuid(uuid);
		return bean;
	
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.IDENTITY_SELECTION;
	}


	public void setIdentity(IIdentity userId){
		this.userIdentity = userId;
	}

	public IIdentity getIdentity(){
		return this.userIdentity;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userIdentity == null) ? 0 : userIdentity.hashCode());
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
		IdentitySelectionPreferenceOutcome other = (IdentitySelectionPreferenceOutcome) obj;
		if (userIdentity == null) {
			if (other.userIdentity != null) {
				return false;
			}
		} else if (!userIdentity.getBareJid().equalsIgnoreCase(other.userIdentity.getBareJid())) {
			return false;
		}
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "Select: "+this.userIdentity.toString();
	}


	public boolean isShouldUseIdentity() {
		return shouldUseIdentity;
	}


	public void setShouldUseIdentity(boolean shouldUseIdentity) {
		this.shouldUseIdentity = shouldUseIdentity;
	}
	public Stage getCurrentStage() {
		// TODO Auto-generated method stub
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

