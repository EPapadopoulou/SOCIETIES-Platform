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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;



public class PreferenceEvaluator {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private List<Condition> conditions;
	private RequestorBean requestor;
	private IIdentity userIdentity;
	private PrivacyPreferenceManager privPrefMgr;
	public PreferenceEvaluator(PrivacyPreferenceManager privPrefMgr, RequestorBean requestor, IIdentity userIdentity){

		this.privPrefMgr = privPrefMgr;
		this.requestor = requestor;
		this.userIdentity = userIdentity;
	}

	public Hashtable<IPrivacyOutcome,List<CtxIdentifier>> evaluatePreference(PrivacyPreference ptn){
		Hashtable<IPrivacyOutcome,List<CtxIdentifier>> temp = new Hashtable<IPrivacyOutcome,List<CtxIdentifier>>();
		PrivacyPreference p = this.evaluatePreferenceInternal(ptn);
		if (p!=null){
			ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();

			Object[] objs = p.getUserObjectPath();
			for (Object obj : objs){
				if (obj instanceof ContextPreferenceCondition){
					ctxIds.add( ((ContextPreferenceCondition) obj).getCtxIdentifier());
				}
			}

			/*IPreference[] prefs = (IPreference[]) p.getUserObjectPath();
			for (int i = 0; i<prefs.length; i++){
				if (null!=prefs[i].getUserObject()){
					if (prefs[i].isBranch()){
						IPreferenceCondition condition = prefs[i].getCondition();
						ctxIds.add(condition.getCtxIdentifier());
					}
				}
			}*/


			temp.put(p.getOutcome(), ctxIds);
			////JOptionPane.showMessageDiaSystem.out.println(null, "Evaluation: returning outcome: "+p.getOutcome().toString());
			return temp;
		}else{
			return new Hashtable<IPrivacyOutcome,List<CtxIdentifier>>();
		}
	}

	private PrivacyPreference evaluatePreferenceInternal(PrivacyPreference ptn){
		System.out.println("evaluatePreferenceInternal currentNode: "+ptn);
		//a non-context aware preference
		if (ptn.isLeaf()){
			System.out.println("preference is not context-dependent. returning IAction object"+ptn);
			//JOptionPane.showMessageDialog(null, "preference is not context-dependent. returning IAction object"+ptn.getOutcome().toString());
			return ptn;
		}
		//if the root object is null then the tree is split so we have to evaluate more than one tree
		//if is not empty root evaluate current node, else evaluate children nodes

		if (ptn.getUserObject()!=null){
			IPrivacyPreferenceCondition con = ptn.getCondition();
			try {
				if (!evaluatesToTrue(con)){
					System.out.println("Condition "+con+" is false. Returning null outcome");
					return null;		
				}
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		//current node condition evaluated to true, descending levels
		
		Enumeration<PrivacyPreference> e = ptn.children();
		ArrayList<PrivacyPreference> prefList = new ArrayList<PrivacyPreference>(); 
		while (e.hasMoreElements()){
			PrivacyPreference p = e.nextElement();
			PrivacyPreference outcomePreference = this.evaluatePreferenceInternal(p);
			if (outcomePreference!=null){
				prefList.add(outcomePreference);
			}

		}
		//if only one IOutcome is applicable with the current context return that
		if (prefList.size()==1){
			System.out.println("PrefEvaluator> Returning: "+ prefList.get(0).toString());
			//JOptionPane.showMessageDialog(null, "split: PrefEvaluator> Returning: "+ prefList.get(0).toString());
			return prefList.get(0);
		}
		//if no IOutcome is applicable, return a null object
		else if (prefList.size()==0){

			System.out.println("PrefEvaluator> No preference applicable");
			//JOptionPane.showMessageDialog(null, "split: PrefEvaluator> No preference applicable");
			return null;
		}
		//if more than one IOutcome objs is applicable, use conflict resolution and return the most applicable
		else{
			System.out.println("Resolving conflicts "+prefList);
			ConflictResolver cr = new ConflictResolver();
			PrivacyPreference io = cr.resolveConflicts(prefList);
			//JOptionPane.showMessageDialog(null, "split PrefEvaluator> Returning: "+io.toString());
			System.out.println("PrefEvaluator> Conflict Resolved Returning: "+io.toString());
			return io;
		}

	}
	/*	private IPrivacyPreference evaluatePreferenceInternal(IPrivacyPreference ptn){
		System.out.println("evaluatePreferenceInternal currentNode: "+ptn);
		//a non-context aware preference
		if (ptn.isLeaf()){
			System.out.println("preference is not context-dependent. returning IAction object"+ptn);
			//JOptionPane.showMessageDialog(null, "preference is not context-dependent. returning IAction object"+ptn.getOutcome().toString());
			return ptn;
		}
		//if the root object is null then the tree is split so we have to evaluate more than one tree
		if (ptn.getUserObject()==null){
			System.out.println("preference tree is split. we might have a conflict");
			//JOptionPane.showMessageDialog(null, "preference tree is split. we might have a conflict");
			Enumeration<IPrivacyPreference> e = ptn.children();
			ArrayList<IPrivacyPreference> prefList = new ArrayList<IPrivacyPreference>(); 
			while (e.hasMoreElements()){
				IPrivacyPreference p = e.nextElement();
				IPrivacyPreference outcomePreference = this.evaluatePreferenceInternal(p);
				if (outcomePreference!=null){
					prefList.add(outcomePreference);
				}

			}
			//if only one IOutcome is applicable with the current context return that
			if (prefList.size()==1){
				System.out.println("PrefEvaluator> Returning: "+ prefList.get(0).toString());
				//JOptionPane.showMessageDialog(null, "split: PrefEvaluator> Returning: "+ prefList.get(0).toString());
				return prefList.get(0);
			}
			//if no IOutcome is applicable, return a null object
			else if (prefList.size()==0){

				System.out.println("PrefEvaluator> No preference applicable");
				//JOptionPane.showMessageDialog(null, "split: PrefEvaluator> No preference applicable");
				return null;
			}
			//if more than one IOutcome objs is applicable, use conflict resolution and return the most applicable
			else{
				System.out.println("Resolving conflicts "+prefList);
				ConflictResolver cr = new ConflictResolver();
				IPrivacyPreference io = cr.resolveConflicts(prefList);
				//JOptionPane.showMessageDialog(null, "split PrefEvaluator> Returning: "+io.toString());
				System.out.println("PrefEvaluator> Conflict Resolved Returning: "+io.toString());
				return io;
			}
		}
		//if the root node is not empty
		else{
			System.out.println("preference tree is not split. no conflicts here");
			//JOptionPane.showMessageDialog(null, "preference tree is not split. no conflicts here");
			//and it's a condition
			if (ptn.isBranch()){
				//evaluate the condition
				IPrivacyPreferenceCondition con = ptn.getCondition();
				try {
					if (evaluatesToTrue(con)){
						////JOptionPane.showMessageDiaSystem.out.println(null, con.toString()+" evaluated to true");
						System.out.println(ptn+" is true - descending tree levels");
						//JOptionPane.showMessageDialog(null, con.toString()+" is true - descending tree levels");
						//traverse the tree in preorder traversal to evaluate all the conditions under this branch and find an Action 
						Enumeration<IPrivacyPreference> e = ptn.children();
						while (e.hasMoreElements()){
							IPrivacyPreference p = e.nextElement();
							IPrivacyPreference outcomePreference = this.evaluatePreferenceInternal(p);
							if(null != outcomePreference){
								return outcomePreference;
							}
						}		
					}else{
						////JOptionPane.showMessageDiaSystem.out.println(null, con.toString()+" evaluated to false");
						System.out.println(ptn+" is false - returning");
						//JOptionPane.showMessageDialog(null, con.toString()+" is false - returning");
					}
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//and it's not a condition but an Outcome (i.e. not a branch but a leaf)
			else{
				//JOptionPane.showMessageDialog(null, "PrefEvaluator> Returning: "+ptn.getOutcome());
				System.out.println("PrefEvaluator> Returning: "+ptn.getOutcome());
				return ptn;
			}
		}


		return null;
	}*/




	private boolean evaluatesToTrue(IPrivacyPreferenceCondition cond)throws PrivacyException{
		if (cond instanceof ContextPreferenceCondition){
			ContextPreferenceCondition contextCond = (ContextPreferenceCondition) cond;
			String currentContextValue = this.getValueFromContext(contextCond.getCtxIdentifier());
			OperatorConstants operator = contextCond.getOperator();

			System.out.println("evaluating cond: "+contextCond.toString()+" against current value: "+currentContextValue);
			if (operator.equals(OperatorConstants.EQUALS)){

				return currentContextValue.equalsIgnoreCase(contextCond.getValue());
			}
			else
				return this.evaluateInt(parseString(contextCond.getValue()), parseString(currentContextValue), operator);
		}else if (cond instanceof TrustPreferenceCondition){
			TrustPreferenceCondition trustCond = (TrustPreferenceCondition) cond;
			return this.evaluatesToTrueTrust(trustCond.getTrustThreshold());
		}else if (cond instanceof PrivacyCondition){
			return evaluatesToTrueCondition((PrivacyCondition) cond);
		}
		else{
			throw new PrivacyException("PM: Condition is not a Context, Trust or Privacy condition");
		}

	}

	private boolean evaluatesToTrueCondition(PrivacyCondition cond){
		System.out.println("evaluating condition: "+cond.toString());
		System.out.println("Conditions available from negotiation: "+conditions.size());
		for (Condition condition : conditions){
			if (((PrivacyCondition) cond).getCondition().getConditionConstant().equals(condition.getConditionConstant())){
				System.out.println("Found same condition. "+cond.getType());
				try {
					if (PrivacyConditionsConstantValues.getBetterConditionValue(cond.getCondition().getConditionConstant(), cond.getCondition().getValue(), condition.getValue()).equalsIgnoreCase(condition.getValue())){
						//logging.debug("Condition in agreement {} is better than condition in preference {}. returning true",  condition.getValue(), cond.getCondition().getValue());
						System.out.println("Condition in agreement "+condition.getValue()+" is better than condition in preference "+cond.getCondition().getValue()+". returning true" );
						return true;
					}
				} catch (PrivacyException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
					logging.error("PrivacyException: {}", e);
					return false;
				}

			}
		}
		logging.debug("evaluate condition returns false");
		return false;
	}
	private boolean evaluatesToTrueTrust(Double trustValue){


		TrustedEntityId trusteeID;
		try {
			trusteeID = new TrustedEntityId(TrustedEntityType.SVC, this.requestor.getRequestorId());
			TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CIS, this.userIdentity.getBareJid());
			TrustQuery trustQuery = new TrustQuery(trustorID);
			trustQuery.setTrusteeId(trusteeID);
			Double currentTrustValue = privPrefMgr.getTrustBroker().retrieveTrustValue(trustQuery).get();

			return currentTrustValue>=trustValue;

		} catch (MalformedTrustedEntityIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TrustException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	private boolean evaluateInt(int valueInPreference, int valueInContext, OperatorConstants operator){
		boolean result = false;
		switch (operator){
		case GREATER_OR_EQUAL_THAN:
			result = valueInContext >= valueInPreference;
			break;
		case GREATER_THAN:
			result = valueInContext > valueInPreference;
			break;
		case LESS_OR_EQUAL_THAN:
			result = valueInContext <= valueInPreference;
			break;
		case LESS_THAN:
			result = valueInContext < valueInPreference;
			break;
		default: System.out.println("Invalid Operator");
		}

		return result;
	}

	public String getValueFromContext(CtxIdentifier id){
		if (id==null){
			System.out.println("can't get context value from null id");
		}
		if (this.privPrefMgr.getContextCache()==null){
			System.out.println("ContextCache is null. PrefEvaluator not initialised properly");
		}
		return this.privPrefMgr.getContextCache().getContextValue(id);

	}

	public int parseString(String str){
		try{
			return Integer.parseInt(str);
		}catch (NumberFormatException nbe){
			System.out.println("Could not parse String to int");
			return 0;
		}

	}



	public Condition evaluatePPNPreferences(PPNPrivacyPreferenceTreeModel model){
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> evaluatePreference = this.evaluatePreference(model.getRootPreference());
		if (evaluatePreference.keySet().size() > 0){
			IPrivacyOutcome outcome = evaluatePreference.keys().nextElement();
			if (outcome instanceof PPNPOutcome){
				return ((PPNPOutcome) outcome).getCondition();
			}
		}

		return null;
	}
	/*	public ResponseItem evaluatePPNPreferences(RequestItem requestItem, PPNPrivacyPreferenceTreeModel model) {
		this.conditions = requestItem.getConditions();
		//need a custom method
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> evaluatePreference = this.evaluatePreference(model.getRootPreference());
		if (evaluatePreference.keySet().size()>0){
			IPrivacyOutcome outcome = evaluatePreference.keys().nextElement();
			if (outcome instanceof PPNPOutcome){
				ResponseItem responseItem = new ResponseItem();
				responseItem.setDecision(((PPNPOutcome) outcome).getDecision());
				responseItem.setRequestItem(RequestItemUtils.copyOf(requestItem));
				return responseItem;
			}
		}

		return null;

				Enumeration<RequestItem> items = modelsHashtable.keys();

		//for every requestItem, a list of responseItems will be created
		//for each action in the requestItem, there will be a ResponseItem
		while(items.hasMoreElements()){
			RequestItem item = items.nextElement();
			//set the conditions from the request policy
			this.conditions = item.getConditions();
			//evaluate all the preferences for this request item
			PPNPrivacyPreferenceTreeModel model = modelsHashtable.get(item);
				Hashtable<IPrivacyOutcome, List<CtxIdentifier>> outList = this.evaluatePreference(model.getRootPreference());
				//we actually don't care about the ctxIdentifiers in the case of ppn as there will be no proactive actions for these preferences
				IPrivacyOutcome temp = outList.keys().nextElement(); //there's only one key
				if (temp instanceof PPNPOutcome){
					PPNPOutcome outcome = (PPNPOutcome) temp;
					ResponseItem respItem = new ResponseItem();
					respItem.setDecision(outcome.getDecision());
					respItem.setRequestItem(item);
					toReturn.put(item, respItem);
				}



		}

		return toReturn;


	}*/

	public Hashtable<IPrivacyOutcome, List<CtxIdentifier>> evaluateAccessCtrlPreference(PrivacyPreference pref, List<Condition> conditions) {
		this.conditions = conditions;
		return this.evaluatePreference(pref);
	}

	public CtxIdentifier evaluateAttrPreference(AttributeSelectionPreferenceTreeModel attrSelPreference, List<Condition> conditions) {
		this.conditions = conditions;
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> evaluatePreference = this.evaluatePreference(attrSelPreference.getRootPreference());
		if (!evaluatePreference.isEmpty()){
			Enumeration<IPrivacyOutcome> keys = evaluatePreference.keys();
			while (keys.hasMoreElements()){
				AttributeSelectionOutcome outcome = (AttributeSelectionOutcome) keys.nextElement();
				return outcome.getCtxID();
			}
		}

		return null;
	}


	/*	public static void main(String[] args){
		ICtxBroker sbroker = new StubCtxBroker();

		try {

			ICtxEntity entity = sbroker.createEntity("Person");
			if (entity==null){
				System.out.println("entity is null");
			}
			ICtxEntityIdentifier entityID = entity.getCtxIdentifier();
			ICtxAttribute symlocAttr = sbroker.createAttribute(entityID, CtxTypes.SYMBOLIC_LOCATION);
			//ICtxEntityIdentifier entityID = new StubCtxEntityIdentifier("AliceSecret1234%5Bbe94bf83-1264-4f36-b339-53894b0a376f%5D,","ac3fdc679c58235326","person",1L);
			//ICtxAttributeIdentifier symlocAttrID = new StubCtxAttributeIdentifier(entityID, CtxTypes.SYMBOLIC_LOCATION, 62L);
			//ICtxAttribute symlocAttr = new StubCtxAttribute(symlocAttrID, entityID);
			symlocAttr.setStringValue("home");
			sbroker.update(symlocAttr);

			IPreference condition1 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "home", CtxTypes.SYMBOLIC_LOCATION));
			IPreference condition2 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "work", CtxTypes.SYMBOLIC_LOCATION));

			//define outcomes
			IPreference outcome1 = new PreferenceTreeNode(new PreferenceOutcome("volume", "100"));
			IPreference outcome2 = new PreferenceTreeNode(new PreferenceOutcome("volume", "0"));


			//build tree
			IPreference preference = new PreferenceTreeNode();
			preference.add(condition1); //branch 1
			condition1.add(outcome1);
			preference.add(condition2); //branch 2
			condition2.add(outcome2);

			PrivateContextCache cc = new PrivateContextCache(sbroker);

			PreferenceEvaluator ev = new PreferenceEvaluator(cc);
			Hashtable<IOutcome,List<CtxIdentifier>> result = ev.evaluatePreference(preference);
			if (result!=null && (!result.isEmpty())){
				System.out.println("got result. size: "+result.size());
				Enumeration<IOutcome> outcomes = result.keys();
				while (outcomes.hasMoreElements()){
					IOutcome o = outcomes.nextElement();
					System.out.println(o.toString());
				}
			}
		} catch (ContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}

