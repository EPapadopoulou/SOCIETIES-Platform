/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.policy;

import java.io.Serializable;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

/**
 * @author PUMA
 *
 */
public class ShareDataRange implements Serializable{

	private String value2;
	private String value1;

	public ShareDataRange(String value1, String value2) throws PrivacyException{

		if ((!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.SHARE_WITH_3RD_PARTIES).contains(value1)) || (!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.SHARE_WITH_3RD_PARTIES).contains(value2))){
			throw new PrivacyException("Ranges: "+value1+" or "+value2+" are not acceptable values for condition: "+ConditionConstants.SHARE_WITH_3RD_PARTIES.name());
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
		if (!PrivacyConditionsConstantValues.getValuesAsList(ConditionConstants.SHARE_WITH_3RD_PARTIES).contains(value)){
			return false;
		}
		
		try {
			String betterShareValue1 = PrivacyConditionsConstantValues.getBetterSharedValue(value, value1);
			String betterShareValue2 = PrivacyConditionsConstantValues.getBetterSharedValue(value, value2);
			
			return betterShareValue1.equalsIgnoreCase(value1) || (betterShareValue2.equalsIgnoreCase(value2));
				
			
			
			
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public String getBestShareDataAvailable(){
		try {
			return PrivacyConditionsConstantValues.getBetterSharedValue(value1, value2);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PrivacyConditionsConstantValues.getBetterConditionValue(ConditionConstants.SHARE_WITH_3RD_PARTIES);
	}
	
	public static void main(String[] args){
		String userValue = PrivacyConditionsConstantValues.getValues(ConditionConstants.SHARE_WITH_3RD_PARTIES)[4];
		String value1 = PrivacyConditionsConstantValues.getValues(ConditionConstants.SHARE_WITH_3RD_PARTIES)[1];
		String value2 = PrivacyConditionsConstantValues.getValues(ConditionConstants.SHARE_WITH_3RD_PARTIES)[3];
		
		ShareDataRange range;
		try {
			range = new ShareDataRange(value1, value2);
			System.out.println(range.isInRange(userValue));
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
