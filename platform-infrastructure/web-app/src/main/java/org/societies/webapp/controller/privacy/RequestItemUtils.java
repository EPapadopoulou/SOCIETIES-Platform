package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestItemUtils {
	/**
	 * Instantiate a mandatory request item
	 * @param resource
	 * @param actions
	 * @param conditions
	 * @return
	 */
	public static RequestItem create(Resource resource, List<Action> actions, List<Condition> conditions) {
		return create(resource, actions, conditions, false);
	}

	public static RequestItem create(Resource resource, List<Action> actions, List<Condition> conditions, boolean optional) {
		RequestItem requestItem = new RequestItem();
		requestItem.setResource(resource);
		requestItem.setActions(actions);
		requestItem.setConditions(conditions);
		requestItem.setOptional(optional);
		return requestItem;
	}


	public static String toXmlString(RequestItem requestItem){
		StringBuilder sb = new StringBuilder();
		if (null != requestItem) {
			sb.append("\n<Target>\n");
			sb.append(ResourceUtils.toXmlString(requestItem.getResource()));
			sb.append(ActionUtils.toXmlString(requestItem.getActions()));
			sb.append(ConditionUtils.toXmlString(requestItem.getConditions()));
			sb.append("\t<optional>"+requestItem.isOptional()+"</optional>\n");
			sb.append("</Target>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<RequestItem> requestItems){
		StringBuilder sb = new StringBuilder();
		if (null != requestItems) {
			for(RequestItem requestItem : requestItems) {
				sb.append(toXmlString(requestItem));
			}
		}
		return sb.toString();
	}

	public static String toString(RequestItem value){
		StringBuilder builder = new StringBuilder();
		builder.append("RequestItem [");
		if (null != value) {
			builder.append("getActions()=");
			builder.append(ActionUtils.toString(value.getActions()));
			builder.append(", getConditions()=");
			builder.append(ConditionUtils.toString(value.getConditions()));
			builder.append(", isOptional()=");
			builder.append(value.isOptional());
			builder.append(", getResource()=");
			builder.append(ResourceUtils.toString(value.getResource()));
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<RequestItem> values){
		StringBuilder sb = new StringBuilder();
		if (null != values) {
			for(RequestItem value : values) {
				sb.append(toString(value));
			}
		}
		return sb.toString();
	}



	@Deprecated
	public static boolean equals(RequestItem o1, Object o2) {
		return equal(o1, o2);
	}
	public static boolean equal(RequestItem o1, Object o2) {
		return equal(o1, o2, false);
	}
	public static boolean equal(RequestItem o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		RequestItem ro2 = (RequestItem) o2;
		return (ActionUtils.equal(o1.getActions(), ro2.getActions())
				&& ConditionUtils.equal(o1.getConditions(), ro2.getConditions())
				&& ResourceUtils.equal(o1.getResource(), ro2.getResource())
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional())
				);
	}

	public static boolean equal(List<RequestItem> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { 
			return false;
		}
		// -- Verify obj type
		List<RequestItem> ro2 = (List<RequestItem>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(RequestItem o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(RequestItem needle, List<RequestItem> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(RequestItem entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}



}
