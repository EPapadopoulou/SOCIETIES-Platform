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
	
	public static RequestItem copyOf(RequestItem item){
		RequestItem rItem = new RequestItem();
		rItem.setActions(ActionUtils.copyOf(item.getActions()));
		rItem.setConditions(ConditionUtils.copyOf(item.getConditions()));
		rItem.setOptional(item.isOptional());
		rItem.setPurpose(item.getPurpose());
		rItem.setRequestItemId(item.getRequestItemId());
		rItem.setResource(ResourceUtils.copyOf(item.getResource()));
		return rItem;
	}
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

	public static String toString(RequestItem requestItem){
		StringBuilder builder = new StringBuilder();
		builder.append("RequestItem ");
		if (null != requestItem) {
			builder.append(" (optional: "+requestItem.isOptional()+")");
			builder.append("\n");
			builder.append(ResourceUtils.toString(requestItem.getResource()));
			
			builder.append(ActionUtils.toString(requestItem.getActions()));
			
			builder.append(ConditionUtils.toString(requestItem.getConditions()));


		}

		return builder.toString();
	}

	public static String toString(List<RequestItem> values){
		StringBuilder sb = new StringBuilder();
		sb.append("RequestItems\n");
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
