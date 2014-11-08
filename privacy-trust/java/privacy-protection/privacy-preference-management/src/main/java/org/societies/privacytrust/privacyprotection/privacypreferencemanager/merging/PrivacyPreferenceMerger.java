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


		if (node.isLeaf()){
			System.out.println("existing node does not contain context condition. merging as leaf");
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
			//System.out.println("Merging new Single Rule: "+sr.toString());
			//System.out.println("\twith: "+mergedTree.toTreeString());
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp; //in the MergingManager if this method returns null, it means we have to request a full learning cycle

		}

		return mergedTree;
	}

	public PrivacyPreference mergeAccCtrlPreference(AccessControlPreferenceDetailsBean d, PrivacyPreference existingPreference, PrivacyPreference newPreference){
		if (existingPreference.isLeaf()){
			System.out.println("existing node does not contain context condition. merging as leaf");
			newPreference = newPreference.getRoot();
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			return p;
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
		return mergedTree;
	}


	public PrivacyPreference mergeAttrSelPreference(AttributeSelectionPreferenceDetailsBean d, PrivacyPreference existingPreference, PrivacyPreference newPreference){
		if (existingPreference.isLeaf()){
			System.out.println("existing node does not contain context condition. merging as leaf");
			newPreference = newPreference.getRoot();
			PrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			return p;
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
		return mergedTree;
	}

	private PrivacyPreference merge(PrivacyPreference oldTree, SingleRule sr){
		//IPreference newTree = null;
		ArrayList<SingleRule> oldRules = this.convertToSingleRules(oldTree);

		//check if we're in Situation 1 (same conditions different outcomes)
		ArrayList<SingleRule> temp = this.checkConflicts(oldRules, sr);
		if (temp.size()>0){
			System.out.println("Situation 1, conflict can't be resolved");
			return null;
		}
		System.out.println("Not in situation 1");

		//check if we're in Situation 2 (100% match)
		temp = this.checkMatches(oldRules, sr);
		if (temp.size()>0){
			//the confidence level of the matching outcome has been updated in the SingleRule. This should be reflected in the tree by reference
			return oldTree;
		}
		System.out.println("Not in Situation 2");

		//we're going to find a branch that has the most common conditions with this rule.
		PrivacyPreference commonNode = this.findCommonNode(oldTree, sr);
		
		System.out.println("Found commonNode: "+commonNode);
		if (null==commonNode){
			System.out.println("Did not find commonNode, adding to root of tree");
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

	private SingleRule increaseConfidenceLevel(SingleRule sr){
		//need to increase the confidence level by running the algorithm
		return sr;
	}





	private PrivacyPreference findCommonNode(PrivacyPreference ptn, SingleRule sr){

		CommonNodeCounter cnc = new CommonNodeCounter();

		//if it's an empty root, we have to repeat with all its children
		if (ptn.getUserObject() == null){
			System.out.println("current node is empty root");
			Enumeration<PrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				PrivacyPreference p = e.nextElement();
				System.out.println("processing child :"+p.toString()+" which is child of: "+ptn.toString());
				cnc = findCommonNode(p,sr, cnc);
			}
		}else{

			cnc = findCommonNode(ptn,sr,cnc);
		}

		return cnc.getMostCommonNode();
	}

	private CommonNodeCounter findCommonNode(PrivacyPreference ptn, SingleRule sr, CommonNodeCounter cnc){

		//unlikely
		if (ptn.isLeaf()){
			System.out.println("current node is leaf. returning common node counter");
			return cnc;
		}

		IPrivacyPreferenceCondition pc = (IPrivacyPreferenceCondition) ptn.getUserObject();
		//if they have a common condition, go to the children, otherwise, return and continue with siblings
		if (sr.hasCondition(pc)){
			System.out.println("Single rule: "+sr.toString()+" has common node: "+pc.toString());
			cnc.add(ptn, ptn.getLevel());
			Enumeration<PrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				cnc = findCommonNode(e.nextElement(),sr,cnc);
			}
		}
		return cnc;
	}


	private ArrayList<SingleRule> checkConflicts(ArrayList<SingleRule> oldRules, SingleRule newRule){

		for (int i=0; i< oldRules.size(); i++){
			SingleRule sr = oldRules.get(i);
			if (sr.conflicts(newRule)){
				System.out.println("single rule: "+sr.toString()+" conflicts with: "+newRule.toString());
				oldRules.set(i, this.resolveConflict(sr, newRule));
				return oldRules;
			}

		}

		return new ArrayList<SingleRule>();
	}
	private SingleRule resolveConflict(SingleRule oldRule, SingleRule newRule){
		//resolve
		return oldRule;
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
			System.out.println("::"+singleRules.get(i).toString());
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

		System.out.println("BEFORE REMOVAL: "+sr.toString());
		if (null!=ptn.getUserObject()){
			System.out.println(" found common node: "+ptn.getUserObject().toString());
			//IPreferenceCondition[] cons = new IPreferenceCondition[ptn.getLevel()];
			Object[] objs = ptn.getUserObjectPath();

			for (int i = 0; i< objs.length; i++){
				if (objs[i] instanceof ContextPreferenceCondition){
					ContextPreferenceCondition con = (ContextPreferenceCondition) objs[i];
					System.out.println(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						System.out.println(" REMOVED "+con.toString());
					}
				}else 
				if (objs[i] instanceof TrustPreferenceCondition){
					TrustPreferenceCondition con = (TrustPreferenceCondition) objs[i];
					System.out.println(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						System.out.println(" REMOVED "+con.toString());
					}
				}else if (objs[i] instanceof PrivacyCondition){
					PrivacyCondition con = (PrivacyCondition) objs[i];
					System.out.println(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						System.out.println(" REMOVED "+con.toString());
					}
				}
					
			}
			if (ptn.getUserObject() instanceof ContextPreferenceCondition){
				if (sr.hasCondition((ContextPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((ContextPreferenceCondition) ptn.getUserObject());
				}else if (sr.hasCondition((TrustPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((TrustPreferenceCondition) ptn.getUserObject());
				}else if (sr.hasCondition((PrivacyCondition) ptn.getUserObject())){
					sr.removeCondition((PrivacyCondition) ptn.getUserObject());
				}
				
			}
		}else{
			System.out.println(" not found common node");
		}


		System.out.println("AFTER REMOVAL: "+sr.toString());
		PrivacyPreference leaf = new PrivacyPreference(sr.getOutcome());
		for (int i = 0; i< sr.getConditions().size(); i++){
			/*ContextPreferenceCondition pc = (ContextPreferenceCondition) ptn.getUserObject();
			if (null==pc){
				System.out.println("weird");
			}
			if (sr.getConditions().get(i) == null){
				System.out.println("even weirder");
			}
			*/
			//log("pc: "+pc.toString());
			System.out.println("sr con: "+sr.getConditions().get(i).toString());
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
		
		PrivacyPreference existingPreference = existingModel.getRootPreference();
		PrivacyPreference newPreference = newModel.getRootPreference();
		if (existingPreference.isLeaf()){
			System.out.println("existing node does not contain context condition. merging as leaf");
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
		
		
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		System.out.println(" [Merging] new tree is: "+newSingleRules.toString()+" and old tree is: "+this.convertToSingleRules(existingPreference).toString());
		
		PrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			PrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		
		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(existingModel.getDetails(), mergedTree);
		return model;
	}

	public DObfPreferenceTreeModel mergeDObfPreference(DObfPreferenceTreeModel existingDObfModel,
			DObfPreferenceTreeModel newDObfModel) {
		PrivacyPreference existingPreference = existingDObfModel.getRootPreference();
		PrivacyPreference newPreference = newDObfModel.getRootPreference();
		
		if (existingPreference.isLeaf()){
			System.out.println("existing node does not contain context condition merging as leaf");
			if (newPreference.isLeaf()){
				System.out.println("existing and new preferences are leaf prefs");
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
			return model;
		}
		
		
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		System.out.println(" [Merging] new tree is: "+newSingleRules.toString()+" and old tree is: "+this.convertToSingleRules(existingPreference).toString());
		
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
