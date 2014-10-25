/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

/**
 * @author PUMA
 *
 */
@Deprecated
public class PPNModel {

	List<RowEntry> rows ;
	private final double  gap = 0.2;
	private final String dataType;

	public PPNModel(String dataType){
		this.dataType = dataType;
		this.rows = new ArrayList<RowEntry>();

	}
	
	public List<Condition> getRecommendedConditions(double trustOfRequestor){
		if (this.rows.size()==0){
			return new ArrayList<Condition>();
		}
		
		for (int i=0; i<rows.size(); i++){
			RowEntry currentRow = rows.get(i);
			double nextRowThreshold = 100.0;
			if (i<rows.size()-1)
				nextRowThreshold = rows.get(1+i).getTrustThreshold();
			
			if (trustOfRequestor>=currentRow.getTrustThreshold() && trustOfRequestor < nextRowThreshold){
				return currentRow.getMostCommonValues();
			}
		}
		
		return new ArrayList<Condition>(); 
	}

	public void addUserInput(double trustThreshold, List<Condition> conditions) throws PrivacyException{
		if (this.rows.isEmpty()){
			System.out.println("Adding first row in the model.");
			RowEntry row = new RowEntry(trustThreshold);
			row.addUserInput(conditions);
			rows.add(row);
			return;
		}
		//row at index 0 is the lowest trust level
		//row at rows.size()-1 is the highest trust level


		for (int i = 0; i < rows.size(); i++){
			System.out.println("Adding: "+trustThreshold+" and current pointer at: "+rows.get(i).getTrustThreshold());
			RowEntry row = rows.get(i);
			double rowThreshold = row.getTrustThreshold();
			double nextRowThreshold = 100.0;
			if (i<rows.size()-1)
				nextRowThreshold = rows.get(1+i).getTrustThreshold();
			if ((trustThreshold<nextRowThreshold) && (nextRowThreshold-trustThreshold>gap)){
				System.out.println("newThreshold ("+trustThreshold+") less than nextRowThreshold ("+nextRowThreshold+")");
				if (trustThreshold > rowThreshold){
					System.out.println("newThreshold ("+trustThreshold+") greater  than rowThreshold ("+rowThreshold+")");
					System.out.println("trustThreshold - rowThreshold > gap: "+(trustThreshold - rowThreshold));
					if (trustThreshold - rowThreshold > gap){
						RowEntry newEntry = new RowEntry(trustThreshold);
						newEntry.addUserInput(conditions);
						
						rows.add(i+1, newEntry);
						break;
					}else{
						row.addUserInput(conditions);
						break;
					}
				}else 
				{
					System.out.println("newThreshold ("+trustThreshold+") less than rowThreshold ("+rowThreshold+")");
					System.out.println("rowThreshold - trustThreshold > gap: "+(rowThreshold - trustThreshold));
					if (rowThreshold - trustThreshold > gap){
						RowEntry newEntry = new RowEntry(trustThreshold);
						newEntry.addUserInput(conditions);
						rows.add(i,newEntry);
						break;
					}else{
						row.addUserInput(conditions);
						break;
					}
				}
			}
		}
	}
	
	public static void main (String[] args) throws PrivacyException{
		PPNModel model = new PPNModel(CtxAttributeTypes.LOCATION_SYMBOLIC);
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (ConditionConstants cc : ConditionConstants.values()){
			Condition con = new Condition();
			con.setConditionConstant(cc);
			con.setValue(PrivacyConditionsConstantValues.getValues(cc)[0]);
			conditions.add(con);
		}
		model.addUserInput(1.76, conditions);
		
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(5.15, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(4.85, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(5.63, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(5.10, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(3.10, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(8.10, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
		model.addUserInput(4.85, conditions);
		System.out.println(model.rows.size()+" -> "+model.rows.toString()+"\n");
	}

	public String getDataType() {
		return dataType;
	}
}
