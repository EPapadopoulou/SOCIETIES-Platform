package org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

@Deprecated
public class HistoryOfConditionValue {

	private final ConditionConstants conditionConstant;
	private Hashtable<String, Integer> history = new Hashtable<String, Integer>();
	
	private String mostCommonValue;
	
	public HistoryOfConditionValue(ConditionConstants cc){
		this.conditionConstant = cc;
		List<String> conValues = PrivacyConditionsConstantValues.getValuesAsList(cc);
		
		for (String conValue : conValues){
			this.history.put(conValue, 0);
		}
	}
	
	public String getMostCommonValue(){
		return mostCommonValue;
	}
	
	public Condition getMostCommonValueAsCondition(){
		Condition con = new Condition();
		con.setConditionConstant(conditionConstant);
		con.setValue(mostCommonValue);
		return con;
	}
	
	public void addUserInput(String value) throws PrivacyException{
		if ((value!=null) && (history.get(value)!=null)){
			Integer occurrences = history.get(value);
			occurrences++;
			if (this.mostCommonValue==null){
				this.mostCommonValue = value;
			}else{
				if (history.get(mostCommonValue)<=occurrences){
					this.mostCommonValue = value;
				}
			}
		}else{
			throw new PrivacyException("User input: \""+value+"\" not acceptable as privacy condition value of "+conditionConstant.name());
		}
	}
	
	
	public static void main(String args[]) {
		HistoryOfConditionValue history = new HistoryOfConditionValue(ConditionConstants.DATA_RETENTION);
		System.out.println(history.getMostCommonValue());
		try {
			history.addUserInput("garbage");
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(history.getMostCommonValue());
		try {
			history.addUserInput(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[0]);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(history.getMostCommonValue());
		
		try {
			history.addUserInput(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[1]);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(history.getMostCommonValue());

		try {
			history.addUserInput(PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION)[0]);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(history.getMostCommonValue());

	}
	
	
}
