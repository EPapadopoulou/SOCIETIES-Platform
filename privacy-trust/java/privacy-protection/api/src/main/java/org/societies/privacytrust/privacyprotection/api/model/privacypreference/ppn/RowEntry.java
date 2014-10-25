package org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

@Deprecated
public class RowEntry {
	private final double trustThreshold;
	private Hashtable<ConditionConstants, HistoryOfConditionValue> histories;

	public RowEntry(final double trustThreshold){
		this.trustThreshold = trustThreshold;
		this.histories = new Hashtable<ConditionConstants, HistoryOfConditionValue>();
		for (ConditionConstants cc : ConditionConstants.values()){
			histories.put(cc, new HistoryOfConditionValue(cc));
		}
	}

	public double getTrustThreshold() {
		return trustThreshold;
	}

	public String getMostCommonValue (ConditionConstants cc){
		return this.histories.get(cc).getMostCommonValue();
	}

	private void addUserInput(Condition condition) throws PrivacyException{
		this.histories.get(condition.getConditionConstant()).addUserInput(condition.getValue());
	}

	public void addUserInput(List<Condition> conditions) throws PrivacyException{
		if (conditions.size()!=ConditionConstants.values().length){
			throw new PrivacyException("Not all conditions are present in the user input");
		}else{
			for (Condition con: conditions){
				this.addUserInput(con);
			}
		}
	}
	
	public String toString(){
		return ""+this.trustThreshold;
	}

	public List<Condition> getMostCommonValues() {
		List<Condition> list = new ArrayList<Condition>();
		Enumeration<ConditionConstants> keys = this.histories.keys();
		while (keys.hasMoreElements()){
			list.add(histories.get(keys.nextElement()).getMostCommonValueAsCondition());
		}
		return list;
	}
}
