package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;

public class tester {

	public static void main(String[] args){
		Condition condition1 = new Condition();
		condition1.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
		condition1.setValue(PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[1]);
		
		Condition condition2 = new Condition();
		condition2.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
		condition2.setValue(PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[1]);
		
		try {
			PPNPOutcome outcome1 = new PPNPOutcome(condition1);
			PPNPOutcome outcome2 = new PPNPOutcome(condition2);
			System.out.println(ConditionUtils.equal(condition1, condition2));
			System.out.println(outcome1.equals(outcome2));
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
