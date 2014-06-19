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
package org.societies.personalisation.UserPreferenceManagement.impl.evaluation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.OperatorConstants;



public class PreferenceEvaluator {

	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private UserPreferenceConditionMonitor monitor;

	public PreferenceEvaluator(PrivateContextCache cache, UserPreferenceConditionMonitor monitor){

		this.contextCache = cache;
		this.monitor = monitor;
	}

	public Hashtable<IPreferenceOutcome,List<CtxIdentifier>> evaluatePreference(IPreference ptn, String uuid){
		Hashtable<IPreferenceOutcome,List<CtxIdentifier>> temp = new Hashtable<IPreferenceOutcome,List<CtxIdentifier>>();
		IPreference p = this.evaluatePreferenceInternal(ptn);
		if (p!=null){
			ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();

			Object[] objs = p.getUserObjectPath();
			for (Object obj : objs){
				if (obj instanceof IPreferenceCondition){
					ctxIds.add( ((IPreferenceCondition) obj).getCtxIdentifier());
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

			this.monitor.addEvaluationResult(uuid, p);
			temp.put(p.getOutcome(), ctxIds);
			return temp;
		}else{
			return new Hashtable<IPreferenceOutcome,List<CtxIdentifier>>();
		}
	}

	public Hashtable<IPreferenceOutcome,List<CtxIdentifier>> evaluatePreference(IPreference ptn){
		Hashtable<IPreferenceOutcome,List<CtxIdentifier>> temp = new Hashtable<IPreferenceOutcome,List<CtxIdentifier>>();
		IPreference p = this.evaluatePreferenceInternal(ptn);
		if (p!=null){
			ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();

			Object[] objs = p.getUserObjectPath();
			for (Object obj : objs){
				if (obj instanceof IPreferenceCondition){
					ctxIds.add( ((IPreferenceCondition) obj).getCtxIdentifier());
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
			return temp;
		}else{
			return new Hashtable<IPreferenceOutcome,List<CtxIdentifier>>();
		}
	}
	
	private IPreference evaluatePreferenceInternal(IPreference ptn){
		System.out.println("Evaluating node: "+ptn.getUserObject());
		if (ptn.isBranch()){
			if (ptn.getUserObject()!=null){
				if (!evaluatesToTrue(ptn.getCondition())){
					System.out.println("Returning null, Condition evaluates to false: "+ptn.getCondition().toString());
					return null;
				}else{
					System.out.println("Condition evaluates to true, continuing "+ptn.getCondition());
				}
			}
			
			List<IPreference> preferenceList = new ArrayList<IPreference>();
			IPreference defaultOutcome = null;
			
			Enumeration children = ptn.children();
			if (ptn.getChildCount()==1){
				IPreference childAt0 = (IPreference) ptn.getChildAt(0);
				if (childAt0.isLeaf()){
					System.out.println("Returning only leaf outcome. "+childAt0.getOutcome());
					return childAt0;
				}
			}
			while (children.hasMoreElements()){
				IPreference childNode = (IPreference) children.nextElement();
				if (childNode.isLeaf()){
					
					defaultOutcome = childNode;
				}else{
					IPreference outcomeNode = evaluatePreferenceInternal(childNode);
					if (outcomeNode!=null)
					{
						preferenceList.add(outcomeNode);
					}
				}				
			}			
			if (preferenceList.size()==1){
				System.out.println("Returning 1 outcome: "+preferenceList.get(0));
				return preferenceList.get(0);
			}
			
			if (preferenceList.size()==0){
				if (defaultOutcome==null){
					System.out.println("Not found anything, returning null");
				}else{
					System.out.println("Returning default outcome "+defaultOutcome.toString());
				}
				return defaultOutcome;
			}
			
			ConflictResolver cr = new ConflictResolver();
			IPreference io = cr.resolveConflicts(preferenceList);
			if(this.logging.isDebugEnabled()){
				logging.debug("PrefEvaluator> Returning: "+io.toString());
			}
			System.out.println("Resolved conflict, returning: "+io.toString());
			return io;
		}
		
		return ptn;
	}



	public boolean evaluatesToTrue(IPreferenceCondition cond){
		if (cond instanceof ContextPreferenceCondition){
			String currentContextValue = this.getValueFromContext(cond.getCtxIdentifier());
			OperatorConstants operator = cond.getoperator();

			if(this.logging.isDebugEnabled()){
				logging.debug("evaluating cond: "+cond.toString()+" against current value: "+currentContextValue);
			}
			if (operator.equals(OperatorConstants.EQUALS)){
				//JOptionPane.showMessageDialog(null, "Comparing: "+cond.getvalue()+" with current context value: "+currentContextValue);
				return currentContextValue.equalsIgnoreCase(cond.getvalue());
			}
			else
				return this.evaluateInt(parseString(cond.getvalue()), parseString(currentContextValue), operator);
		}else{
			if (logging.isErrorEnabled()){
				this.logging.error("Catastrophic failure. Request to evaluate a node that is not a conditional node");
			}
			return false;
		}

	}

	public boolean evaluateInt(int valueInPreference, int valueInContext, OperatorConstants operator){
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
		default: if(this.logging.isDebugEnabled()){
			logging.debug("Invalid Operator");
		}
		}

		return result;
	}

	public String getValueFromContext(CtxIdentifier id){
		if (id==null){
			if(this.logging.isDebugEnabled()){
				this.logging.debug("can't get context value from null id");
			}
		}
		if (this.contextCache==null){
			if(this.logging.isDebugEnabled()){
				this.logging.debug("ContextCache is null. PrefEvaluator not initialised properly");
			}
		}
		return this.contextCache.getContextValue(id);

	}

	public int parseString(String str){
		try{
			return Integer.parseInt(str);
		}catch (NumberFormatException nbe){
			if(this.logging.isDebugEnabled()){
				logging.debug("Could not parse String to int");
			}
			return 0;
		}

	}


	/*	public static void main(String[] args){
		ICtxBroker sbroker = new StubCtxBroker();

		try {

			ICtxEntity entity = sbroker.createEntity("Person");
			if (entity==null){
				System.out.println("entity is null");
			}
			ICtxEntityIdentifier entityID = entity.getCtxIdentifier();
			ICtxAttribute symlocAttr = sbroker.createAttribute(entityID, CtxAttributeTypes.SYMBOLIC_LOCATION);
			//ICtxEntityIdentifier entityID = new StubCtxEntityIdentifier("AliceSecret1234%5Bbe94bf83-1264-4f36-b339-53894b0a376f%5D,","ac3fdc679c58235326","person",1L);
			//ICtxAttributeIdentifier symlocAttrID = new StubCtxAttributeIdentifier(entityID, CtxAttributeTypes.SYMBOLIC_LOCATION, 62L);
			//ICtxAttribute symlocAttr = new StubCtxAttribute(symlocAttrID, entityID);
			symlocAttr.setStringValue("home");
			sbroker.update(symlocAttr);

			IPreference condition1 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "home", CtxAttributeTypes.SYMBOLIC_LOCATION));
			IPreference condition2 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "work", CtxAttributeTypes.SYMBOLIC_LOCATION));

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

