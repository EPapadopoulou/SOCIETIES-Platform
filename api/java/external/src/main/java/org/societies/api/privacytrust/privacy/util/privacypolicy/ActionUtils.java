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

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;

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
	public static Action create(ActionConstants actionConstant) {
		return create(actionConstant, false);
	}

	public static Action create(ActionConstants actionConstant, boolean optional) {
		Action action = new Action();
		action.setActionConstant(actionConstant);
		action.setOptional(optional);
		return action;
	}
	
	public static Action create(String actionConstantValue, boolean optional) {
		return create(ActionConstants.fromValue(actionConstantValue), optional);
	}

	/**
	 * Create a list of mandatory actions
	 * 
	 * @param actionConstants Array of actions
	 * @return List of mandatory actions
	 */
	public static List<Action> createList(ActionConstants... actionConstants) {
		List<Action> actions = new ArrayList<Action>();
		for (ActionConstants actionConstant : actionConstants) {
			actions.add(create(actionConstant));
		}
		return actions;
	}

	/**
	 * To retrieve the friendly name
	 * @param entry Action
	 * @return Action friendly name
	 */
	public static String getFriendlyName(Action entry) {
		if (null == entry || null == entry.getActionConstant()) {
			return "";
		}
		if (null == map2FriendlyName || map2FriendlyName.size() <= 0) {
			map2FriendlyName = new HashMap<String, String>();
			map2FriendlyName.put(ActionConstants.READ.name(), "access");
			map2FriendlyName.put(ActionConstants.WRITE.name(), "update");
			map2FriendlyName.put(ActionConstants.CREATE.name(), "create");
			map2FriendlyName.put(ActionConstants.DELETE.name(), "delete");
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
	public static List<String> getFriendlyName(List<Action> haystack) {
		List<String> friendlyNameList = new ArrayList<String>();
		if (null != haystack && haystack.size() > 0) {
			// Sort
			Collections.sort(haystack, new ActionComparator());
			// Retrieve friendly names
			for(Action entry : haystack) {
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
	public static String getFriendlyDescription(List<Action> haystack) {
		return getFriendlyDescription(haystack, false);
	}
	/**
	 * Return a friendly description of a list of actions
	 * @param haystack
	 * @param displayOptionString To display " (optional)" after an option action
	 * @return "action1, action2 and action3"
	 */
	public static String getFriendlyDescription(List<Action> haystack, boolean displayOptionalString) {
		if (null == haystack || haystack.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int size = haystack.size();
		// Sort
		Collections.sort(haystack, new ActionComparator());
		// Retrieve friendly names
		for (Action entry : haystack) {
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



	public static String toXmlString(Action action){
		StringBuilder sb = new StringBuilder();
		if (null != action) {
			sb.append("\t\t<Action>\n");
			sb.append("\t\t\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\""+action.getActionConstant().getClass().getName()+"\">\n");
			sb.append("\t\t\t\t<AttributeValue>"+action.getActionConstant().name()+"</AttributeValue>\n");
			sb.append("\t\t\t</Attribute>\n");
			sb.append("\t\t\t<optional>"+action.isOptional()+"</optional>\n");
			sb.append("\t\t</Action>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<Action> actions){
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(Action action : actions) {
				sb.append(toXmlString(action));
			}
		}
		return sb.toString();
	}

	public static String toString(Action action){
		StringBuilder builder = new StringBuilder();
		
		if (null != action) {
			builder.append("Action: ");
			builder.append(action.getActionConstant());
			builder.append(" optional: ");
			builder.append(action.isOptional()+" ");
		}
		
		return builder.toString();
	}

	public static String toString(List<Action> actions){
		StringBuilder sb = new StringBuilder();
		sb.append("Actions: \n");
		if (null != actions) {
			for(Action action : actions) {
				sb.append(toString(action));
				
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
	/**
	 * Simple method to check if a list of actions has, at least, one action
	 * which is not optional
	 * @param actions List of action
	 * @return True if the list is ok
	 */
	public static boolean atLeast1MandatoryAction(List<Action> actions) {
		boolean oneMandatory = false;
		for(Action action : actions) {
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
	public static boolean equal(Action o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Action ro2 = (Action) o2;
		return (ActionConstantsUtils.equal(o1.getActionConstant(), ro2.getActionConstant()))
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional());
	}
	@Deprecated
	public static boolean equals(Action o1, Object o2) {
		return equal(o1, o2);
	}
	/**
	 * Equal all action fields (including optional)
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equal(Action o1, Object o2) {
		return equal(o1, o2, false);
	}

	public static boolean equal(List<Action> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<Action> ro2 = (List<Action>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(Action o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}
	@Deprecated
	public static boolean equals(List<Action> o1, Object o2) {
		return equal(o1, o2);
	}

	public static class ActionComparator implements Comparator<Action> { 
		private Map<ActionConstants, Integer> map2Order = null;

		/**
		 * Order: READ, WRITE, CREATE, DELETE
		 */
		@Override
		public int compare(
				Action o1,
				Action o2) {
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
				map2Order = new HashMap<ActionConstants, Integer>();
				map2Order.put(ActionConstants.READ, 1);
				map2Order.put(ActionConstants.WRITE, 2);
				map2Order.put(ActionConstants.CREATE, 3);
				map2Order.put(ActionConstants.DELETE, 4);
			}
			int o1Value = map2Order.get(o1.getActionConstant());
			int o2Value = map2Order.get(o2.getActionConstant());
			if (o1Value < o2Value) {
				return -1;
			}
			return 1;
		}
	}


	public static boolean contain(Action needle, List<Action> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(Action entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
	public static boolean contains(Action needle, List<Action> haystack){
		return contain(needle, haystack);
	}

	/**
	 * All mandatory requested elements of the haystack are in the needles list
	 * The needle list may contain other elements
	 * @param needles
	 * @param haystack
	 * @return
	 */
	public static boolean containAllMandotory(List<Action> needles, List<Action> haystack) {
		if (null == haystack || haystack.size() <= 0) {
			return true;
		}
		for (Action entry : haystack){
			if (entry.isOptional()) {
				continue;
			}
			if (!contain(entry, needles)) {
				return false;
			}
		}
		return true;
	}


	public static boolean contains(List<Action> actionsToCheck, List<Action> actions) {
		return contains(actionsToCheck, actions, null);
	}
	/**
	 * 
	 * @param actionsToCheck
	 * @param actions
	 * @param intersection Will be filled with the intersection of the two list. It will works only if actions contains actionsToCheck
	 * @return
	 */
	public static boolean contains(List<Action> actionsToCheck, List<Action> actions, List<Action> intersection) {
		if (null == actions || actions.size() <= 0 || null == actionsToCheck || actionsToCheck.size() <= 0 || actions.size() < actionsToCheck.size()) {
			return false;
		}
		for(Action actionToCheck : actionsToCheck) {
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
		
		for(Action actionToCheck : actionsToCheck) {
			if (!contains(actionToCheck, actions)) {
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
		
		for(Action actionToCheck : actionsToCheck) {
			if (!contains(actionToCheck, actions)) {
				result.add(actionToCheck);
			}
		}
		return result;
	}

	public static Action copyOf(Action action1){
		Action action = new Action();
		action.setActionConstant(action1.getActionConstant());
		action.setOptional(action1.isOptional());
		action.setActionId(action1.getActionId());
		return action;
	}
	
	public static List<Action> copyOf(List<Action> actions1){
		List<Action> actions = new ArrayList<Action>();
		for (Action action: actions1){
			actions.add(copyOf(action));
		}
		
		return actions;
	}


	/*	public static void main(String[] args){
		List<Action> actions = new ArrayList<Action>();
		List<Action> actions1 = new ArrayList<Action>();
		Action actionREAD = new Action();
		actionREAD.setActionConstant(ActionConstants.READ);
		Action actionWRITE = new Action();
		actionWRITE.setActionConstant(ActionConstants.WRITE);
		Action actionCREATE = new Action();
		actionCREATE.setActionConstant(ActionConstants.CREATE);
		Action actionDELETE = new Action();
		actionDELETE.setActionConstant(ActionConstants.DELETE);

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