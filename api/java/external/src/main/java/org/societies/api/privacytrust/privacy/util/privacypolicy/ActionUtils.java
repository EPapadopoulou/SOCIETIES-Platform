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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ActionUtils {
	public static Map<String, String> map2FriendlyName;

	/**
	 * Create a mandatory action
	 * 
	 * @param actionConstant
	 * @return
	 */
	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants actionConstant) {
		return create(actionConstant, false);
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants actionConstant, boolean optional) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		action.setActionConstant(actionConstant);
		action.setOptional(optional);
		return action;
	}

	/**
	 * Create a list of mandatory actions
	 * 
	 * @param actionConstants Array of actions
	 * @return List of mandatory actions
	 */
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> createList(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants... actionConstants) {
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action>();
		for (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants actionConstant : actionConstants) {
			actions.add(create(actionConstant));
		}
		return actions;
	}

	/**
	 * To retrieve the friendly name
	 * @param entry Action
	 * @return Action friendly name
	 */
	public static String getFriendlyName(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry) {
		if (null == entry || null == entry.getActionConstant()) {
			return "";
		}
		if (null == map2FriendlyName || map2FriendlyName.size() <= 0) {
			map2FriendlyName = new HashMap<String, String>();
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.READ.name(), "access");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.WRITE.name(), "update");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.CREATE.name(), "create");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.DELETE.name(), "delete");
		}
		if (map2FriendlyName.containsKey(entry.getActionConstant().name())) {
			return map2FriendlyName.get(entry.getActionConstant().name());
		}
		return entry.getActionConstant().name();
	}

	/**
	 * To retrieve the friendly names
	 * @param haystack List of actions
	 * @return List of action friendly names
	 */
	public static List<String> getFriendlyName(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		List<String> friendlyNameList = new ArrayList<String>();
		if (null != haystack && haystack.size() > 0) {
			// Sort
			Collections.sort(haystack, new ActionComparator());
			// Retrieve friendly names
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack) {
				friendlyNameList.add(getFriendlyName(entry));
			}
		}
		return friendlyNameList;
	}

	/**
	 * Return a friendly description of a list of actions
	 * @param haystack
	 * @return "action1, action2 and action3"
	 */
	public static String getFriendlyDescription(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		return getFriendlyDescription(haystack, false);
	}
	/**
	 * Return a friendly description of a list of actions
	 * @param haystack
	 * @param displayOptionString To display " (optional)" after an option action
	 * @return "action1, action2 and action3"
	 */
	public static String getFriendlyDescription(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack, boolean displayOptionalString) {
		if (null == haystack || haystack.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int size = haystack.size();
		// Sort
		Collections.sort(haystack, new ActionComparator());
		// Retrieve friendly names
		for (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack) {
			sb.append(getFriendlyName(entry));
			if (displayOptionalString && entry.isOptional()) {
				sb.append(" (optional)");
			}
			if (i != (size-1)) {
				if (i == (size-2)) {
					sb.append(" and ");
				}
				else {
					sb.append(", ");
				}
			}
			i++;
		}
		return sb.toString();
	}



	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action){
		StringBuilder sb = new StringBuilder();
		if (null != action) {
			sb.append("\n<Action>\n");
			sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\""+action.getActionConstant().getClass().getName()+"\">\n");
			sb.append("\t\t<AttributeValue>"+action.getActionConstant().name()+"</AttributeValue>\n");
			sb.append("\t</Attribute>\n");
			sb.append("\t<optional>"+action.isOptional()+"</optional>\n");
			sb.append("</Action>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions){
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action : actions) {
				sb.append(toXmlString(action));
			}
		}
		return sb.toString();
	}

	public static String toString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action){
		StringBuilder builder = new StringBuilder();
		builder.append("Action [");
		if (null != action) {
			builder.append("getActionConstant()=");
			builder.append(action.getActionConstant());
			builder.append(", isOptional()=");
			builder.append(action.isOptional());
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions){
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action : actions) {
				sb.append(toString(action));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Simple method to check if a list of actions has, at least, one action
	 * which is not optional
	 * @param actions List of action
	 * @return True if the list is ok
	 */
	public static boolean atLeast1MandatoryAction(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions) {
		boolean oneMandatory = false;
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action action : actions) {
			if (!action.isOptional()) {
				oneMandatory = true;
				break;
			}
		}
		return oneMandatory;
	}

	/**
	 * 
	 * @param o1
	 * @param o2
	 * @param dontCheckOptional At true, the optional field won't be checked
	 * @return
	 */
	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action ro2 = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action) o2;
		return (ActionConstantsUtils.equal(o1.getActionConstant(), ro2.getActionConstant()))
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional());
	}
	@Deprecated
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o1, Object o2) {
		return equal(o1, o2);
	}
	/**
	 * Equal all action fields (including optional)
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o1, Object o2) {
		return equal(o1, o2, false);
	}

	public static boolean equal(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> ro2 = (List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}
	@Deprecated
	public static boolean equals(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> o1, Object o2) {
		return equal(o1, o2);
	}

	public static class ActionComparator implements Comparator<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> { 
		private Map<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants, Integer> map2Order = null;

		/**
		 * Order: READ, WRITE, CREATE, DELETE
		 */
		@Override
		public int compare(
				org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o1,
				org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action o2) {
			if (equal(o1, o2)) {
				return 0;
			}
			if (null == o1) {
				return 1;
			}
			if (null == o2) {
				return -1;
			}
			// Create order
			if (null == map2Order || map2Order.size() <= 0) {
				map2Order = new HashMap<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants, Integer>();
				map2Order.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.READ, 1);
				map2Order.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.WRITE, 2);
				map2Order.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.CREATE, 3);
				map2Order.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.DELETE, 4);
			}
			int o1Value = map2Order.get(o1.getActionConstant());
			int o2Value = map2Order.get(o2.getActionConstant());
			if (o1Value < o2Value) {
				return -1;
			}
			return 1;
		}
	}


	public static boolean contain(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action needle, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
	public static boolean contains(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action needle, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack){
		return contain(needle, haystack);
	}

	/**
	 * All mandatory requested elements of the haystack are in the needles list
	 * The needle list may contain other elements
	 * @param needles
	 * @param haystack
	 * @return
	 */
	public static boolean containAllMandotory(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> needles, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		if (null == haystack || haystack.size() <= 0) {
			return true;
		}
		for (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack){
			if (entry.isOptional()) {
				continue;
			}
			if (!contain(entry, needles)) {
				return false;
			}
		}
		return true;
	}


	public static boolean contains(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionsToCheck, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions) {
		return contains(actionsToCheck, actions, null);
	}
	/**
	 * 
	 * @param actionsToCheck
	 * @param actions
	 * @param intersection Will be filled with the intersection of the two list. It will works only if actions contains actionsToCheck
	 * @return
	 */
	public static boolean contains(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionsToCheck, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> intersection) {
		if (null == actions || actions.size() <= 0 || null == actionsToCheck || actionsToCheck.size() <= 0 || actions.size() < actionsToCheck.size()) {
			return false;
		}
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionToCheck : actionsToCheck) {
			if (!contains(actionToCheck, actions)) {
				return false;
			}
			if (null != intersection) {
				intersection.add(actionToCheck);
			}
		}
		return true;
	}

	public static boolean containsOr(List<Action> actionsToCheck, List<Action> actions) {
		if (null == actions || actions.size() <= 0 || null == actionsToCheck || actionsToCheck.size() <= 0 || actions.size() < actionsToCheck.size()) {
			return false;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionBeans = toActionBeans(actions);
		for(Action actionToCheck : actionsToCheck) {
			if (!contains(toActionBean(actionToCheck), actionBeans)) {
				return true;
			}
		}
		return false;
	}

	public static List<Action> intersect(List<Action> actionsToCheck, List<Action> actions) {
		if (null == actions || actions.size() <= 0 || null == actionsToCheck || actionsToCheck.size() <= 0 || actions.size() < actionsToCheck.size()) {
			return null;
		}
		List<Action> result = new ArrayList<Action>();
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionBeans = toActionBeans(actions);
		for(Action actionToCheck : actionsToCheck) {
			if (!contains(toActionBean(actionToCheck), actionBeans)) {
				result.add(actionToCheck);
			}
		}
		return result;
	}

	public static Action toAction(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionBean)
	{
		if (null == actionBean) {
			return null;
		}
		ActionConstants actionConstant = ActionConstants.valueOf(actionBean.getActionConstant().name());
		return new Action(actionConstant, actionBean.isOptional());
	}
	public static List<Action> toActions(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionBeans)
	{
		if (null == actionBeans) {
			return null;
		}
		List<Action> actions = new ArrayList<Action>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionBean : actionBeans) {
			actions.add(ActionUtils.toAction(actionBean));
		}
		return actions;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action toActionBean(Action action)
	{
		if (null == action) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants actionConstant = org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.valueOf(action.getActionType().name());
		actionBean.setActionConstant(actionConstant);
		actionBean.setOptional(action.isOptional());
		return actionBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> toActionBeans(List<Action> actions)
	{
		if (null == actions) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actionBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action>();
		for(Action action : actions) {
			actionBeans.add(ActionUtils.toActionBean(action));
		}
		return actionBeans;
	}

	/*	public static void main(String[] args){
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action>();
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions1 = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action>();
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionREAD = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		actionREAD.setActionConstant(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.READ);
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionWRITE = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		actionWRITE.setActionConstant(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.WRITE);
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionCREATE = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		actionCREATE.setActionConstant(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.CREATE);
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action actionDELETE = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action();
		actionDELETE.setActionConstant(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.DELETE);

		actions.add(actionREAD);
		actions.add(actionDELETE);

		actions1.add(actionREAD);
		actions1.add(actionWRITE);

		System.out.println(equals(actions, actions1));

		actions1.remove(actionWRITE);
		actions1.add(actionDELETE);
		System.out.println(equals(actions, actions1));

		actions1.add(actionCREATE);
		System.out.println(equals(actions, actions1));
		actions.add(actionCREATE);
		System.out.println(equals(actions, actions1));
		actions.add(actionWRITE);
		System.out.println(equals(actions, actions1));
	}*/
}