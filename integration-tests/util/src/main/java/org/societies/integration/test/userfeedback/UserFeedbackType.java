/**
 * 
 */
package org.societies.integration.test.userfeedback;


/**
 * @author Olivier Maridat (Trialog)
 *
 */
public enum UserFeedbackType {
	RADIOLIST("getExplicitFB:0"), CHECKBOXLIST("getExplicitFB:1"), ACKNACK("getExplicitFB:2"), ABORT("getImplicitFB:0");

	private final String value;

	UserFeedbackType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static UserFeedbackType fromValue(String v) {
		for (UserFeedbackType c: UserFeedbackType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
