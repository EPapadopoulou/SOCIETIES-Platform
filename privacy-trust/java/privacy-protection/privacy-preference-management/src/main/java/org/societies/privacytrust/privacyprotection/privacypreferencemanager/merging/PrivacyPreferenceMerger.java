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


package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;


public class PrivacyPreferenceMerger {

	private PrivacyPreferenceManager privPrefMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public PrivacyPreferenceMerger(PrivacyPreferenceManager ppMgr){
		
		this.privPrefMgr = ppMgr;
		

	}

	public void addIDSDecision(IIdentity selectedDPI, RequestorBean requestor){
		ContextSnapshot snapshot = this.takeSnapshot();
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedDPI.getJid());
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel existingModel = privPrefMgr.getIDSPreference(details);
		if (existingModel==null){
			IDSPrivacyPreferenceTreeModel model;
			try {
			
				model = new IDSPrivacyPreferenceTreeModel(details, this.createIDSPreference(snapshot, details));
				this.privPrefMgr.storeIDSPreference(details, model);
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			PrivacyPreference mergedPreference;
			try {
				mergedPreference = this.mergeIDSPreference(details, existingModel.getRootPreference(), snapshot);
				if (mergedPreference!=null){
					IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, mergedPreference);
					this.privPrefMgr.storeIDSPreference(details, model);
				}
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}



	public PrivacyPreference mergeIDSPreference(IDSPreferenceDetailsBean d, PrivacyPreference node, ContextSnapshot snapshot) throws InvalidFormatException{
		logging.debug("MERGING IDS PREFERENCES - START");

		if (node.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			
			PrivacyPreference p = new PrivacyPreference();
			p.add(this.createIDSPreference(snapshot, d));
			p = p.getRoot();
			p.add(node);
			return p;
		}


		ArrayList<SingleRule> singleRules = this.convertToSingleRules(snapshot);


		PrivacyPreference mergedTree = node;
		for (int i = 0; i< singleRules.size(); i++){

			SingleRule sr = singleRules.get(i);
			//this.logging.debug("Merging new Single Rule: "+sr.toString());
			//this.logging.debug("\twith: "+mergedTree.toTreeString());
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp; //in the MergingManager if this method returns null, it means we have to request a full learning cycle

		}
		logging.debug("MERGING IDS PREFERENCES - END");
		return mergedTree;
	}

	public PrivacyPreference mergeAccCtrlPreference(AccessControlPreferenceDetailsBean d, PrivacyPreference existingPreference, PrivacyPreference newPreference){
		logging.debug("MERGING ACCESS CONTROL PREFERENCES - START");
		if (existingPreference.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			if (newPreference.isLeaf()){
				AccessControlOutcome newOutcome = (AccessControlOutcome) newPreference.getOutcome();
				AccessControlOutcome existingOutcome = (AccessControlOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingPreference;
				}
			}
			newPreference = newPreference.getRoot();
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			return p;
		}
		
		if (newPreference.isLeaf()){
			this.logging.debug("newPreference node does not contain context condition. merging as leaf");
			if (existingPreference.isLeaf()){
				AccessControlOutcome newOutcome = (AccessControlOutcome) newPreference.getOutcome();
				AccessControlOutcome existingOutcome = (AccessControlOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingPreference;
				}else{
					PrivacyPreference newRoot = new PrivacyPreference();
					newRoot.add(existingPreference);
					newRoot.add(newPreference);
					return newRoot;
				}
			}
			
			if (existingPreference.getUserObject()==null){
				existingPreference.add(newPreference);
				return existingPreference;
			}
			
			PrivacyPreference newRoot = new PrivacyPreference();
			
			newRoot.add(existingPreference);
			newRoot.add(newPreference);
			return newRoot;
		}
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		PrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		logging.debug("MERGING ACCESS CONTROL PREFERENCES - END");
		return mergedTree;
	}


	public PrivacyPreference mergeAttrSelPreference(AttributeSelectionPreferenceDetailsBean d, PrivacyPreference existingPreference, PrivacyPreference newPreference){
		logging.debug("MERGING ATTRIBUTE SELECTION PREFERENCES - START");

		if (existingPreference.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			if (newPreference.isLeaf()){
				AttributeSelectionOutcome newOutcome = (AttributeSelectionOutcome) newPreference.getOutcome();
				AttributeSelectionOutcome existingOutcome = (AttributeSelectionOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingPreference;
				}
			}
			newPreference = newPreference.getRoot();
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			return p;
		}
		if (newPreference.isLeaf()){
			this.logging.debug("newPreference node does not contain context condition. merging as leaf");
			if (existingPreference.isLeaf()){
				AttributeSelectionOutcome newOutcome = (AttributeSelectionOutcome) newPreference.getOutcome();
				AttributeSelectionOutcome existingOutcome = (AttributeSelectionOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingPreference;
				}else{
					PrivacyPreference newRoot = new PrivacyPreference();
					newRoot.add(existingPreference);
					newRoot.add(newPreference);
					return newRoot;
				}
			}
			
			if (existingPreference.getUserObject()==null){
				existingPreference.add(newPreference);
				return existingPreference;
			}
			
			PrivacyPreference newRoot = new PrivacyPreference();
			
			newRoot.add(existingPreference);
			newRoot.add(newPreference);
			return newRoot;
		}
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		PrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		logging.debug("MERGING ATTRIBUTE SELECTION PREFERENCES - END");

		return mergedTree;
	}

	private PrivacyPreference recreateFromSingleRule(ArrayList<SingleRule> singleRules){
		if (singleRules.size() == 0){
			this.logging.debug("Can't recreate tree, singleRules.size()=0");
			return null;
		}
		
		PrivacyPreference privacyPreference = singleRules.get(0).toPrivacyPreference();
		if (singleRules.size()==1){
			this.logging.debug("Only one single rule found:\n{}\n returning singleRule.toPrivacyPreference()", privacyPreference.toTreeString());
			return privacyPreference;
		}
		
		
		
		for (int i = 1; i<singleRules.size(); i++){
			SingleRule sr = singleRules.get(i);
			this.logging.debug("Recreating tree, merging tree:\n {} \n with singlerule: \n{}", privacyPreference.toTreeString(), sr.toString());
			PrivacyPreference commonNode = this.findCommonNode(privacyPreference, sr);
			this.logging.debug("Found commonNode (recreate): "+commonNode);			
			if (null==commonNode){
				this.logging.debug("Did not find commonNode (recreate), adding to root of tree");
				
				PrivacyPreference root = privacyPreference.getRoot();
				if (null==root.getUserObject()){
					privacyPreference = addToNode(root, sr);
				}else{
					PrivacyPreference newEmptyRoot = new PrivacyPreference();
					newEmptyRoot.add(root);
					privacyPreference = addToNode(newEmptyRoot, sr);
				}
			}else{
				privacyPreference = addToNode(commonNode, sr).getRoot();
			}
		}
		
		this.logging.debug("Merging complete. returning preference: \n{} \n", privacyPreference.getRoot().toTreeString());
		return privacyPreference.getRoot();
		
	}
	private PrivacyPreference merge(PrivacyPreference oldTree, SingleRule sr){
		logging.debug("\nMerging: \n{} \n with SingleRule: \n{} \n\n", oldTree.toTreeString(), sr.toString());
		//IPreference newTree = null;
		ArrayList<SingleRule> oldRules = this.convertToSingleRules(oldTree);

		logging.debug("Checking if situation 2 (100% match)");
		//check if we're in Situation 2 (100% match)
		ArrayList<SingleRule> temp = this.checkMatches(oldRules, sr);
		if (temp.size()>0){
			//need to recreate the tree from the arraylist
			logging.debug("Situation 2, Match 100%, recreating tree and returning");
			return recreateFromSingleRule(oldRules);
		}
		this.logging.debug("Not in Situation 2");
		this.logging.debug("Checking conflicts");
		//check if we're in Situation 1 (same conditions different outcomes)
		temp = this.checkConflicts(oldRules, sr);
		if (temp.size()>0){
			this.logging.debug("Situation 1, conflict can't be resolved, updated confidence levels");
			this.logging.debug("Recreating tree from single rules: \n {} \n", temp);
			//TODO: update confidence levels
			//re-create the tree from temp
			return recreateFromSingleRule(temp);
		}
		this.logging.debug("Not in situation 1");



		//we're going to find a branch that has the most common conditions with this rule.
		PrivacyPreference commonNode = this.findCommonNode(oldTree, sr);
		
		this.logging.debug("Found commonNode: "+commonNode);
		if (null==commonNode){
			this.logging.debug("Did not find commonNode, adding to root of tree");
			PrivacyPreference root = (PrivacyPreference ) oldTree.getRoot();
			if (null==root.getUserObject()){
				return this.addToNode((PrivacyPreference ) oldTree.getRoot(),sr);
			}
			PrivacyPreference newRoot = new PrivacyPreference();
			newRoot.add(root);
			return this.addToNode(newRoot, sr);
		}

		return this.addToNode(commonNode, sr);

		//ArrayList<SingleRule> sortedRules = sortTree(oldRules);
		//newTree = createTree(sortedRules);
		//return newTree;


	}
	
	private ArrayList<SingleRule> checkConflicts(ArrayList<SingleRule> oldRules, SingleRule newRule){

		for (int i=0; i< oldRules.size(); i++){
			SingleRule sr = oldRules.get(i);
			if (sr.conflicts(newRule)){
				this.logging.debug("single rule (from preference): \n{} \nconflicts with single rule (new preference): \n{}", sr.toString(), newRule.toString());
				sr.getOutcome().updateConfidenceLevel(false);
				//oldRules.set(i, this.resolveConflict(sr, newRule));
				oldRules.add(newRule);
				return oldRules;
			}

		}

		return new ArrayList<SingleRule>();
	}
	private SingleRule resolveConflict(SingleRule oldRule, SingleRule newRule){
		//resolve
		return oldRule;
	}


	private ArrayList<SingleRule> checkMatches(ArrayList<SingleRule> oldRules, SingleRule newRule){

		for (int i=0; i< oldRules.size(); i++){

			SingleRule sr = oldRules.get(i);
			if (sr.equals(newRule)){
				sr.getOutcome().updateConfidenceLevel(true);
				oldRules.set(i, sr);
				return oldRules;
			}
		}
		return new ArrayList<SingleRule>();
	}


	private PrivacyPreference findCommonNode(PrivacyPreference ptn, SingleRule sr){

		CommonNodeCounter cnc = new CommonNodeCounter();

		//if it's an empty root, we have to repeat with all its children
		if (ptn.getUserObject() == null){
			this.logging.debug("current node is empty root");
			Enumeration<PrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				PrivacyPreference p = e.nextElement();
				this.logging.debug("processing child :"+p.toString()+" which is child of: "+ptn.toString());
				cnc = findCommonNode(p,sr, cnc);
			}
		}else{

			cnc = findCommonNode(ptn,sr,cnc);
		}

		return cnc.getMostCommonNode();
	}

	private CommonNodeCounter findCommonNode(PrivacyPreference ptn, SingleRule sr, CommonNodeCounter cnc){

		
		if (ptn.isLeaf()){
			this.logging.debug("current node is leaf. returning common node counter");
			return cnc;
		}

		IPrivacyPreferenceCondition pc = (IPrivacyPreferenceCondition) ptn.getUserObject();
		//if they have a common condition, go to the children, otherwise, return and continue with siblings
		if (sr.hasCondition(pc)){
			this.logging.debug("Single rule: \n"+sr.toString()+"\n has common node: "+pc.toString()+" with \n"+ptn.toTreeString());
			cnc.add(ptn, ptn.getLevel());
			Enumeration<PrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				cnc = findCommonNode(e.nextElement(),sr,cnc);
			}
		}
		return cnc;
	}



	public ArrayList<SingleRule> convertToSingleRules(PrivacyPreference ptn){
		ArrayList<SingleRule> singleRules = new ArrayList<SingleRule>();
		//Enumeration<IPreference> newNodeEnum = ptn.depthFirstEnumeration();
		Enumeration<PrivacyPreference> newNodeEnum = ptn.preorderEnumeration();
		//we're going to construct SingleRule objects from the new tree to use as input to merge with the old tree
		while (newNodeEnum.hasMoreElements()){
			PrivacyPreference temp = (PrivacyPreference ) newNodeEnum.nextElement();
			if (temp.isLeaf()){
				Object[] userObjs = temp.getUserObjectPath();
				SingleRule sr = new SingleRule();
				for (int i=0; i<userObjs.length; i++){
					if (userObjs!=null){
						if (userObjs[i] instanceof IPrivacyPreferenceCondition){
							sr.addConditions((IPrivacyPreferenceCondition) userObjs[i]); 
						}else {
							sr.setOutcome((IPrivacyOutcome) userObjs[i]);
						}
					}
				}
				singleRules.add(sr);
			}

		}	

		for (int i=0; i<singleRules.size(); i++){
			this.logging.debug("::"+singleRules.get(i).toString());
		}
		return singleRules;
	}

	public ArrayList<SingleRule> convertToSingleRules(ContextSnapshot snapshot){
		ArrayList<SingleRule> srlist = new ArrayList<SingleRule>();
		List<SingleContextAttributeSnapshot> slist = snapshot.getList();
		SingleRule sr = new SingleRule();

		for (SingleContextAttributeSnapshot s : slist){
			IPrivacyPreferenceCondition con = this.getContextConditionPreference(s);
			sr.addConditions(con);
		}
		srlist.add(sr);
		return srlist;
	}
	private PrivacyPreference createIDSPreference(ContextSnapshot snapshot, IDSPreferenceDetailsBean details) throws InvalidFormatException{
		IdentitySelectionPreferenceOutcome outcome = new IdentitySelectionPreferenceOutcome(this.privPrefMgr.getIdm().fromJid(details.getAffectedIdentity()));
		PrivacyPreference p = new PrivacyPreference(outcome);
		List<SingleContextAttributeSnapshot> list = snapshot.getList();
		for (SingleContextAttributeSnapshot s : list){
			PrivacyPreference temp = new PrivacyPreference(this.getContextConditionPreference(s));
			temp.add(p);
			p = temp;

		}
		return p;
	}


	private IPrivacyPreferenceCondition getContextConditionPreference(SingleContextAttributeSnapshot attrSnapshot){
		ContextPreferenceCondition condition = new ContextPreferenceCondition(attrSnapshot.getId(),OperatorConstants.EQUALS, attrSnapshot.getValue());
		//PrivacyPreference pref = new PrivacyPreference(condition); 
		return condition;
	}
	private ContextSnapshot takeSnapshot(){
		ContextSnapshot snapshot = new ContextSnapshot();
		SingleContextAttributeSnapshot attrSnapshot = this.takeAttributeSnapshot(CtxTypes.SYMBOLIC_LOCATION);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		attrSnapshot = this.takeAttributeSnapshot(CtxTypes.STATUS);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		attrSnapshot = this.takeAttributeSnapshot(CtxTypes.ACTIVITY);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		return snapshot;
	}

	private SingleContextAttributeSnapshot takeAttributeSnapshot(String type){
		CtxIdentifier id;
		try {
			List<CtxIdentifier> l = this.privPrefMgr.getCtxBroker().lookup(CtxModelType.ATTRIBUTE, type).get();
			if (l.size()==0){
				return null;
			}
			id = l.get(0);
			CtxAttribute attr = (CtxAttribute) this.privPrefMgr.getCtxBroker().retrieve(id);
			SingleContextAttributeSnapshot attrSnapshot = new SingleContextAttributeSnapshot(attr);
			return attrSnapshot;
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private PrivacyPreference addToNode(PrivacyPreference ptn, SingleRule sr){

		this.logging.debug("BEFORE REMOVAL: "+sr.toString());
		if (null!=ptn.getUserObject()){
			this.logging.debug(" found common node: "+ptn.getUserObject().toString());
			//IPreferenceCondition[] cons = new IPreferenceCondition[ptn.getLevel()];
			Object[] objs = ptn.getUserObjectPath();

			for (int i = 0; i< objs.length; i++){
				if (objs[i] instanceof ContextPreferenceCondition){
					ContextPreferenceCondition con = (ContextPreferenceCondition) objs[i];
					this.logging.debug(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						this.logging.debug(" REMOVED "+con.toString());
					}
				}else 
				if (objs[i] instanceof TrustPreferenceCondition){
					TrustPreferenceCondition con = (TrustPreferenceCondition) objs[i];
					this.logging.debug(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						this.logging.debug(" REMOVED "+con.toString());
					}
				}else if (objs[i] instanceof PrivacyCondition){
					PrivacyCondition con = (PrivacyCondition) objs[i];
					this.logging.debug(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						this.logging.debug(" REMOVED "+con.toString());
					}
				}
					
			}
			if (ptn.getUserObject() instanceof ContextPreferenceCondition){
				if (sr.hasCondition((ContextPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((ContextPreferenceCondition) ptn.getUserObject());
				}
			}else if (ptn.getUserObject() instanceof TrustPreferenceCondition){
				if (sr.hasCondition((TrustPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((TrustPreferenceCondition) ptn.getUserObject());
				}
			}else if (ptn.getUserObject() instanceof PrivacyCondition){
				if (sr.hasCondition((PrivacyCondition) ptn.getUserObject())){
					sr.removeCondition((PrivacyCondition) ptn.getUserObject());
				}
				
			}
		}else{
			this.logging.debug(" not found common node");
		}


		this.logging.debug("AFTER REMOVAL: "+sr.toString());
		PrivacyPreference leaf = new PrivacyPreference(sr.getOutcome());
		for (int i = 0; i< sr.getConditions().size(); i++){
			/*ContextPreferenceCondition pc = (ContextPreferenceCondition) ptn.getUserObject();
			if (null==pc){
				this.logging.debug("weird");
			}
			if (sr.getConditions().get(i) == null){
				this.logging.debug("even weirder");
			}
			*/
			//log("pc: "+pc.toString());
			this.logging.debug("sr con: "+sr.getConditions().get(i).toString());
			PrivacyPreference temp = new PrivacyPreference(sr.getConditions().get(i));
			ptn.add(temp);
			ptn = temp;


		}

		ptn.add(leaf);
		return (PrivacyPreference ) ptn.getRoot();
	}

	public PPNPrivacyPreferenceTreeModel mergePPNPreference(
			PPNPrivacyPreferenceTreeModel newModel,
			PPNPrivacyPreferenceTreeModel existingModel) {
		logging.debug("MERGING PPN PREFERENCES - START");
		PrivacyPreference existingPreference = existingModel.getRootPreference();
		PrivacyPreference newPreference = newModel.getRootPreference();
		if (existingPreference.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			if (newPreference.isLeaf()){
				PPNPOutcome newOutcome = (PPNPOutcome) newPreference.getOutcome();
				PPNPOutcome existingOutcome = (PPNPOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingModel;
				}
			}
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(), p);
			return model;
		}
		if (newPreference.isLeaf()){
			this.logging.debug("newPreference node does not contain context condition. merging as leaf");
			if (existingPreference.isLeaf()){
				PPNPOutcome newOutcome = (PPNPOutcome) newPreference.getOutcome();
				PPNPOutcome existingOutcome = (PPNPOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(), existingPreference);
				}else{
					PrivacyPreference newRoot = new PrivacyPreference();
					newRoot.add(existingPreference);
					newRoot.add(newPreference);
					return  new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(),newRoot);
				}
			}
			
			if (existingPreference.getUserObject()==null){
				existingPreference.add(newPreference);
				return new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(),existingPreference);
			}
			
			PrivacyPreference newRoot = new PrivacyPreference();
			
			newRoot.add(existingPreference);
			newRoot.add(newPreference);
			return new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(),newRoot);
		}
		
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		this.logging.debug(" [Merging] new tree is: "+newSingleRules.toString()+" and old tree is: "+this.convertToSingleRules(existingPreference).toString());
		
		PrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		
		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(), mergedTree);
		logging.debug("MERGING PPN PREFERENCES - END");
		return model;
	}

	public DObfPreferenceTreeModel mergeDObfPreference(DObfPreferenceTreeModel existingDObfModel,
			DObfPreferenceTreeModel newDObfModel) {
		logging.debug("MERGING DOBF PREFERENCES - START");

		PrivacyPreference existingPreference = existingDObfModel.getRootPreference();
		PrivacyPreference newPreference = newDObfModel.getRootPreference();
		
		if (existingPreference.isLeaf()){
			this.logging.debug("existing node does not contain context condition merging as leaf");
			if (newPreference.isLeaf()){
				this.logging.debug("existing and new preferences are leaf prefs");
				DObfOutcome newOutcome = (DObfOutcome) newPreference.getOutcome();
				DObfOutcome existingOutcome = (DObfOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return existingDObfModel;
				}
			}
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			DObfPreferenceTreeModel model = new DObfPreferenceTreeModel(existingDObfModel.getDetails(), p);
			logging.debug("MERGING DOBF PREFERENCES - END");
			return model;
		}
		if (newPreference.isLeaf()){
			this.logging.debug("newPreference node does not contain context condition. merging as leaf");
			if (existingPreference.isLeaf()){
				DObfOutcome newOutcome = (DObfOutcome) newPreference.getOutcome();
				DObfOutcome existingOutcome = (DObfOutcome) existingPreference.getOutcome();
				if (newOutcome.equals(existingOutcome)){
					existingOutcome.updateConfidenceLevel(true);
					return new DObfPreferenceTreeModel(existingDObfModel.getDetails(), existingPreference);
				}else{
					PrivacyPreference newRoot = new PrivacyPreference();
					newRoot.add(existingPreference);
					newRoot.add(newPreference);
					return  new DObfPreferenceTreeModel(existingDObfModel.getDetails(),newRoot);
				}
			}
			
			if (existingPreference.getUserObject()==null){
				existingPreference.add(newPreference);
				return new DObfPreferenceTreeModel(existingDObfModel.getDetails(),existingPreference);
			}
			
			PrivacyPreference newRoot = new PrivacyPreference();
			
			newRoot.add(existingPreference);
			newRoot.add(newPreference);
			return new DObfPreferenceTreeModel(existingDObfModel.getDetails(),newRoot);
		}
		
		
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		this.logging.debug(" [Merging] new tree is: "+newSingleRules.toString()+" and old tree is: "+this.convertToSingleRules(existingPreference).toString());
		
		PrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		
		DObfPreferenceTreeModel model = new  DObfPreferenceTreeModel(existingDObfModel.getDetails(), mergedTree);
		return model;
		
	}





}
