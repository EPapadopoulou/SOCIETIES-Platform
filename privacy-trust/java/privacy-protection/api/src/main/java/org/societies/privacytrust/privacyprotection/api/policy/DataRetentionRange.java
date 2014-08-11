package org.societies.privacytrust.privacyprotection.api.policy;

import java.io.Serializable;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

public class DataRetentionRange implements Serializable{

	private String value2;
	private String value1;

	public DataRetentionRange(String value1, String value2) throws PrivacyException{

		if ((!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.DATA_RETENTION).contains(value1)) || (!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.DATA_RETENTION).contains(value2))){
			throw new PrivacyException("Ranges: "+value1+" or "+value2+" are not acceptable values for condition: "+ConditionConstants.DATA_RETENTION.name());
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
	
	public String getBestDataRetentionAvailable() {

			try {
				return PrivacyConditionsConstantValues.getBetterDataRetention(value1, value2);
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return PrivacyConditionsConstantValues.getBetterConditionValue(ConditionConstants.DATA_RETENTION);
	}
	public boolean isInRange(String value){
		if (!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.DATA_RETENTION).contains(value)){
			return false;
		}
		
		try {
			String betterDataRetention1 = PrivacyConditionsConstantValues.getBetterDataRetention(value, value1);
			String betterDataRetention2 = PrivacyConditionsConstantValues.getBetterDataRetention(value, value2);
			
			return betterDataRetention1.equalsIgnoreCase(value1) || (betterDataRetention2.equalsIgnoreCase(value2));
				
			
			
			
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args){
		String userValue = PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[0];
		String value1 = PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[1];
		String value2 = PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[3];
		
		DataRetentionRange range;
		try {
			range = new DataRetentionRange(value1, value2);
			System.out.println(range.isInRange(userValue));
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
