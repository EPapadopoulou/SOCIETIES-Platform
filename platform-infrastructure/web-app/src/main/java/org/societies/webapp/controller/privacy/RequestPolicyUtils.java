package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;


/**
 * Utility class to manage RequestPolicy bean
 * * conversion between bean and old Java class RequestPolicy
 * * display method
 * * comparaison method
 * @author Olivier Maridat (Trialog)
 */
public class RequestPolicyUtils {

	public static RequestPolicy create(PrivacyPolicyTypeConstants privacyPolicyType, RequestorBean requestor, List<RequestItem> requestItems) {
		RequestPolicy requestPolicy = new RequestPolicy();
		requestPolicy.setPrivacyPolicyType(privacyPolicyType);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(requestItems);
		return requestPolicy;
	}

	public static RequestPolicy create(RequestorBean requestor, List<RequestItem> requestItems) {
		RequestPolicy requestPolicy = new RequestPolicy();
		requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.OTHER);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(requestItems);
		return requestPolicy;
	}

	public static RequestPolicy createList(RequestorBean requestor, RequestItem... requestItems) {
		RequestPolicy requestPolicy = new RequestPolicy();
		requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.OTHER);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(Arrays.asList(requestItems));
		return requestPolicy;
	}


	/**
	 * Create a XACML string representing the Privacy Policy from a Java RequestPolicy
	 * @param privacyPolicy Privacy policy as a Java object
	 * @return A string containing the XACML version the privacy policy
	 */
	public static String toXmlString(RequestPolicy requestPolicy){
		StringBuilder sb = new StringBuilder();
		if (null != requestPolicy) {
			sb.append("<RequestPolicy>");
			sb.append(RequestorUtils.toXmlString(requestPolicy.getRequestor()));
			sb.append(RequestItemUtils.toXmlString(requestPolicy.getRequestItems()));
			sb.append("</RequestPolicy>");
		}
		return sb.toString();
	}


	public static boolean equal(RequestPolicy o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		RequestPolicy ro2 = (RequestPolicy) o2;
		return (/*PrivacyPolicyTypeConstantsUtils.equal(o1.getPrivacyPolicyType(), ro2.getPrivacyPolicyType())
				&& */RequestItemUtils.equal(o1.getRequestItems(), ro2.getRequestItems())
				&& RequestorUtils.equal(o1.getRequestor(), ro2.getRequestor())
				);
	}
	/**
	 * Use equal instead
	 */
	@Deprecated
	public static boolean equals(RequestPolicy o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<RequestPolicy> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<RequestPolicy> ro2 = (List<RequestPolicy>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(RequestPolicy o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(RequestPolicy needle, List<RequestPolicy> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(RequestPolicy entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
	//
	//	public static RequestItem getRequestItem(RequestPolicy privacyPolicy, Resource resourceTarger) {
	//		
	//	}

	/**
	 * Use PrivacyPolicyUtils.getDataTypes instead
	 */
	@Deprecated
	public static List<String> getDataTypes(RequestPolicy privacyPolicy) {
		return PrivacyPolicyUtils.getDataTypes(privacyPolicy);
	}
	/**
	 * Use PrivacyPolicyUtils.getDataTypes instead
	 */
	@Deprecated
	public static List<String> getDataTypes(DataIdentifierScheme schemeFilter, RequestPolicy privacyPolicy) {
		return PrivacyPolicyUtils.getDataTypes(schemeFilter, privacyPolicy);
	}


}
