package org.societies.privacytrust.privacyprotection.api.policy;

import java.io.Serializable;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

public class BooleanRange implements Serializable{


	private String value2;
	private String value1;
	private ConditionConstants conditionConstant;
	
	public BooleanRange(String value1, String value2, ConditionConstants conditionConstant) throws PrivacyException{
		
		
		this.conditionConstant = conditionConstant;
		//Unless it's DATA_RETENTION or SHARE_WITH_3RD_PARTIES, all other constants use the same String list ["yes","no"]
		if ((!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.MAY_BE_INFERRED).contains(value1)) || (!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.MAY_BE_INFERRED).contains(value2))){
			throw new PrivacyException("Ranges: "+value1+" or "+value2+" are not acceptable values for condition: "+ConditionConstants.MAY_BE_INFERRED.name());
		}

		this.value1 = value1;
		this.value2 = value2;

	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public boolean isInRange(String value){


		//first check that the value is in boolean range
		if (!PrivacyConditionsConstantValues.getValuesAsList(
				ConditionConstants.MAY_BE_INFERRED).contains(value)) {
			return false;
		}


		
		try {
			String betterConditionValue1 = PrivacyConditionsConstantValues.getBetterConditionValue(this.conditionConstant, value, value1);
			String betterConditionValue2 = PrivacyConditionsConstantValues.getBetterConditionValue(this.conditionConstant, value, value2);
			
			return betterConditionValue1.equalsIgnoreCase(value1) || (betterConditionValue2.equalsIgnoreCase(value2));
				
			
			
			
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
	
	public String getBestConditionValueAvailable(){
		try {
			return PrivacyConditionsConstantValues.getBetterConditionValue(conditionConstant, value1, value2);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return PrivacyConditionsConstantValues.getBetterConditionValue(conditionConstant);
	}
	
	public static void main(String[] args){
		try {
			BooleanRange range = new BooleanRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[0], PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[0], ConditionConstants.MAY_BE_INFERRED);
			System.out.println(range.isInRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[1]));
			System.out.println(range.isInRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.MAY_BE_INFERRED)[0]));
			
			BooleanRange range1 = new BooleanRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.STORE_IN_SECURE_STORAGE)[1], PrivacyConditionsConstantValues.getValues(ConditionConstants.STORE_IN_SECURE_STORAGE)[1], ConditionConstants.STORE_IN_SECURE_STORAGE);
			System.out.println(range1.isInRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.STORE_IN_SECURE_STORAGE)[1]));
			System.out.println(range1.isInRange(PrivacyConditionsConstantValues.getValues(ConditionConstants.STORE_IN_SECURE_STORAGE)[0]));
			
			
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
