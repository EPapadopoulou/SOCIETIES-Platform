/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.policy;

import java.io.Serializable;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

/**
 * @author PUMA
 *
 */
public class ConditionRanges implements Serializable{

	private final DataRetentionRange dataRetentionRange;
	private final ShareDataRange shareDataRange;
	private final BooleanRange mayInferBooleanRange;
	private final BooleanRange storeSecureBooleanRange;
	private final BooleanRange accessHeldBooleanRange;
	private final BooleanRange correctDataBooleanRange;
	private final BooleanRange rightToOptOutBooleanRange;
	
	public ConditionRanges(DataRetentionRange dataRetentionRange, ShareDataRange shareDataRange, BooleanRange mayInferBooleanRange, BooleanRange storeSecureBooleanRange, BooleanRange accessHeldBooleanRange, BooleanRange correctDataBooleanRange, BooleanRange rightToOptOutBooleanRange){
		this.dataRetentionRange = dataRetentionRange;
		this.shareDataRange = shareDataRange;
		this.mayInferBooleanRange = mayInferBooleanRange;
		this.storeSecureBooleanRange = storeSecureBooleanRange;
		this.accessHeldBooleanRange = accessHeldBooleanRange;
		this.correctDataBooleanRange = correctDataBooleanRange;
		this.rightToOptOutBooleanRange = rightToOptOutBooleanRange;	
	}

	public DataRetentionRange getDataRetentionRange() {
		return dataRetentionRange;
	}



	public ShareDataRange getShareDataRange() {
		return shareDataRange;
	}
	public BooleanRange getMayInferBooleanRange() {
		return mayInferBooleanRange;
	}


	public BooleanRange getStoreSecureBooleanRange() {
		return storeSecureBooleanRange;
	}

	public BooleanRange getAccessHeldBooleanRange() {
		return accessHeldBooleanRange;
	}


	public BooleanRange getCorrectDataBooleanRange() {
		return correctDataBooleanRange;
	}

	public BooleanRange getRightToOptOutBooleanRange() {
		return rightToOptOutBooleanRange;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n"+ConditionConstants.DATA_RETENTION.name()+" value1: "+dataRetentionRange.getValue1()+" - value2: "+dataRetentionRange.getValue2());
		sb.append("\n"+ConditionConstants.MAY_BE_INFERRED.name()+" value1: "+mayInferBooleanRange.getValue1()+" - value2: "+mayInferBooleanRange.getValue2());
		sb.append("\n"+ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA.name()+" value1: "+accessHeldBooleanRange.getValue1()+" - value2: "+accessHeldBooleanRange.getValue2());
		sb.append("\n"+ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA.name()+" value1: "+correctDataBooleanRange.getValue1()+" - value2: "+correctDataBooleanRange.getValue2());
		sb.append("\n"+ConditionConstants.RIGHT_TO_OPTOUT.name()+" value1: "+rightToOptOutBooleanRange.getValue1()+" - value2: "+rightToOptOutBooleanRange.getValue2());
		sb.append("\n"+ConditionConstants.SHARE_WITH_3RD_PARTIES.name()+" value1: "+shareDataRange.getValue1()+" - value2: "+shareDataRange.getValue2());
		sb.append("\n"+ConditionConstants.STORE_IN_SECURE_STORAGE.name()+" value1: "+storeSecureBooleanRange.getValue1()+" - value2: "+storeSecureBooleanRange.getValue2());
		
		return sb.toString();
	}
	
}
