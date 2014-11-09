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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;






/**
 * @author Elizabeth
 *
 */
public class ConflictResolver {
	private final static Logger logging = LoggerFactory.getLogger(ConflictResolver.class);
	public ConflictResolver(){
		
	}
	
	public  static PrivacyPreference resolveConflictsConfidenceOnly(List<PrivacyPreference> outcomes){
		int confLevel = outcomes.get(0).getOutcome().getConfidenceLevel();
		int index = 0; 
		
		for (int i = 1; i < outcomes.size(); i++){
			
			int confLevel2 = outcomes.get(i).getOutcome().getConfidenceLevel();
			logging.debug("comparing confLevel: {} with {}", confLevel, confLevel2);
			if (confLevel<=confLevel2){
				logging.debug("conflevel {} <= {} true", confLevel, confLevel2);
				confLevel = confLevel2;
				index = i;
			}
		}
		
		return outcomes.get(index);
		
		
	}

	private static boolean equals(IPrivacyOutcome outcome1, IPrivacyOutcome outcome2){
		if (outcome1 instanceof PPNPOutcome){
			
			boolean equals = ((PPNPOutcome) outcome1).equals(outcome2);
			logging.debug("PPNPOutcomes match {}", equals);
			return equals;
		}
		
		if (outcome1 instanceof AccessControlOutcome){
			boolean equals = ((AccessControlOutcome) outcome1).equals(outcome2);
			logging.debug("AccessControlOutcomes match {}", equals);
			return equals;
		}
		
		if (outcome1 instanceof AttributeSelectionOutcome){
			boolean equals = ((AttributeSelectionOutcome) outcome1).equals(outcome2);
			logging.debug("AttributeSelectionOutcomes match {}", equals);
			return equals;
		}
		
		if (outcome1 instanceof DObfOutcome){
			boolean equals = ((DObfOutcome) outcome1).equals(outcome2);
			logging.debug("DObfOutcomes match {}", equals);
			return equals;
		}
		
		if (outcome1 instanceof IdentitySelectionPreferenceOutcome){
			boolean equals = ((IdentitySelectionPreferenceOutcome) outcome1).equals(outcome2);
			logging.debug("IdentitySelectionPreferenceOutcomes match {}", equals);
			return equals;
		}
		
		logging.debug("Outcome1 {} not instance of any known outcome", outcome1);
		
		return false;
	}
	

	public static void main(String[] args){
		PrivacyPreference prefAllow = new PrivacyPreference(new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW));
		PrivacyPreference prefBLOCK = new PrivacyPreference(new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK));
		PrivacyPreference prefLocation  = new PrivacyPreference(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "home"));
		PrivacyPreference prefLocation2  = new PrivacyPreference(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "home"));
		prefLocation.add(prefAllow);
		prefLocation2.add(prefBLOCK);
		
		
		
		List<PrivacyPreference> prefs = new ArrayList<PrivacyPreference>();
		prefs.add(prefAllow);
		prefs.add(prefBLOCK);
		logging.debug(ConflictResolver.resolveConflicts(prefs).toTreeString());
		
	}
	public static PrivacyPreference resolveConflicts(List<PrivacyPreference> outcomes){
		//check to see if all outcomes contain conditions. 
		//if not, they should be removed from the list unless they are all conditionless 
		List<PrivacyPreference> conditionLessOutcomes = new ArrayList<PrivacyPreference>();
		for (PrivacyPreference privPreference: outcomes){
			PrivacyPreference parent = (PrivacyPreference) privPreference.getParent();
			
			if (parent==null || parent.getUserObject()==null){
				conditionLessOutcomes.add(privPreference);
			}
		}
		
		if (outcomes.size() == conditionLessOutcomes.size()){
			return resolveConflictsConfidenceOnly(outcomes);
		}
		//check to see if the outcomes conflict with each other
		if (outcomes.size()==1){
			return outcomes.get(0);
		}
		boolean conflicting = false;
		PrivacyPreference firstPref = outcomes.get(0);
		for (int i=1; i<outcomes.size(); i++){
			PrivacyPreference pref = outcomes.get(i);
			if (!equals(pref.getOutcome(), firstPref.getOutcome())){
				conflicting = true;
				break;
			}
		}
		//if not conflicting, return the one with the highest confidence level
		if (!conflicting){
			for (int i=1; i<outcomes.size(); i++){
				PrivacyPreference pref = outcomes.get(i);
				if (firstPref.getOutcome().getConfidenceLevel()<pref.getOutcome().getConfidenceLevel()){
					firstPref = pref;
				}
			}	
			logging.debug("Found that outcomes do not conflict, returning one with highest confidence level");
			return firstPref;
		}
		logging.debug("Found that there are actual conflicts between outcomes. Resolving conflicts");
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
				logging.debug("Resolved conflict through trust, not using confidence levels");
				return list.get(0);
			}else if (list.size()==0){
				logging.debug("Error in resolving conflict through trust, resolving only with confidence levels");
				return resolveConflictsConfidenceOnly(outcomes);
			}else{
				logging.debug("Could not resolve conflicts through trust *only*. Using confidence levels as well.");
				return resolveConflictsConfidenceOnly(list);
			}
		}
		logging.debug("no trust conditions in preference, resolving only with confidence levels");
		return resolveConflictsConfidenceOnly(outcomes);
	}
}

