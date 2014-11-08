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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.SingleRule;






/**
 * @author Elizabeth
 *
 */
public class ConflictResolver {
	
	public ConflictResolver(){
		
	}
	
	public  PrivacyPreference resolveConflictsConfidenceOnly(List<PrivacyPreference> outcomes){
		int confLevel = outcomes.get(0).getOutcome().getConfidenceLevel();
		int index = -1; 
		for (int i = 1; i < outcomes.size(); i++){
			if (confLevel<=outcomes.get(i).getOutcome().getConfidenceLevel()){
				confLevel = outcomes.get(i).getOutcome().getConfidenceLevel();
				index = i;
			}
		}
		if (index == -1){
			return null;
		}else{
			return outcomes.get(index);
		}
		
	}

	
	public PrivacyPreference resolveConflicts(List<PrivacyPreference> outcomes){
		
		Hashtable<Double, List<PrivacyPreference>> table = new Hashtable<Double, List<PrivacyPreference>>(); 
		double trustValue = 0.0;

		//we must find the highest trust
		for (PrivacyPreference pref : outcomes){
			if (pref.isBranch()){
				System.err.println("OUTCOMES ONLY PLEASE");
			}
			TreeNode[] path = pref.getPath();
			for (TreeNode node: path){
				if (node instanceof PrivacyPreference){
					PrivacyPreference privacyPreference = (PrivacyPreference) node;
					if (privacyPreference.isBranch()){
						if (privacyPreference.getUserObject() instanceof TrustPreferenceCondition){
							TrustPreferenceCondition trustCondition = (TrustPreferenceCondition) privacyPreference.getCondition();
							if (trustCondition.getTrustThreshold()>trustValue){
								trustValue = trustCondition.getTrustThreshold();
							}
							
							if (table.containsKey(trustCondition.getTrustThreshold())){
								table.get(trustCondition.getTrustThreshold()).add(pref);
							}else{
								
								List<PrivacyPreference> list = new ArrayList<PrivacyPreference>();
								list.add(pref);
								table.put(trustCondition.getTrustThreshold(), list);
							}
							
						}
					}
				}
			}
		}
		
		if (table.containsKey(trustValue)){
			List<PrivacyPreference> list = table.get(trustValue);
			if (list.size()==1){
				return list.get(0);
			}else if (list.size()==0){
				System.err.println("ERRORRRRR");
				return null;
			}else{
				return resolveConflictsConfidenceOnly(list);
			}
		}
		return null;
	}
}

