/**
 * 
 */
package ac.hw.personis.dataInit.policies;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.policy.BooleanRange;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;
import org.societies.privacytrust.privacyprotection.api.policy.DataRetentionRange;
import org.societies.privacytrust.privacyprotection.api.policy.ShareDataRange;

/**
 * @author PUMA
 *
 */
public class RangesITunes {
//  private static final String[] timeValues = new String[]{"30 minutes", "1 hour", "2 hours", "6 hours", "12 hours", "1 day", "2 days", "3 days", "4 days", "a week", "until account is deactivated"};
	static String[] dataRetentionValues = PrivacyConditionsConstantValues.getValues(ConditionConstants.DATA_RETENTION);
	
//      private static final String[] shareValues = new String[]{"No sharing", "affiliated services", "3rd parties", "anyone"};
	static String[] shareValues = PrivacyConditionsConstantValues.getValues(ConditionConstants.SHARE_WITH_3RD_PARTIES);
	
//  private static final String[] boolValues = new String[]{"yes", "no"};
	static String[] booleanValues = PrivacyConditionsConstantValues.getValues(ConditionConstants.RIGHT_TO_OPTOUT);
	
	public static Hashtable<String, ConditionRanges> getRanges(RequestPolicy policy){
		try {
			Hashtable<String, ConditionRanges> table = new Hashtable<String, ConditionRanges>();

			for (RequestItem item : policy.getRequestItems()){
				String dataType = item.getResource().getDataType();
				if (dataType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){
					List<Condition> conditions = new ArrayList<Condition>();

					Condition dataRetention = new Condition();
					dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
					dataRetention.setValue(dataRetentionValues[9]);
					conditions.add(dataRetention);
					DataRetentionRange dataRetentionRange = new DataRetentionRange(dataRetentionValues[0], dataRetentionValues[9]); //1h - 12h

					Condition shareData = new Condition();
					shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
					shareData.setValue(shareValues[0]);
					conditions.add(shareData);
					ShareDataRange shareDataRange = new ShareDataRange(shareValues[0], shareValues[3]);//no sharing - 3rd parties

					Condition mayInfer = new Condition();
					mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
					mayInfer.setValue(booleanValues[0]);
					conditions.add(mayInfer);
					BooleanRange mayInferBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.MAY_BE_INFERRED);// no-yes

					Condition storeSecure = new Condition();
					storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
					storeSecure.setValue(booleanValues[0]);
					conditions.add(storeSecure);
					BooleanRange storeSecureBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.STORE_IN_SECURE_STORAGE); //yes

					Condition access = new Condition();
					access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
					access.setValue(booleanValues[0]);
					conditions.add(access);
					BooleanRange accessHeldBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //yes

					Condition correct = new Condition();
					correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
					correct.setValue(booleanValues[0]);
					conditions.add(correct);
					BooleanRange correctDataBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);//yes

					Condition optOut = new Condition();
					optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
					optOut.setValue(booleanValues[0]);
					conditions.add(optOut);
					BooleanRange rightToOptOutBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_OPTOUT);
					
					
					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.LOCATION_SYMBOLIC, ranges);
					
					item.setConditions(conditions);
					
				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
					List<Condition> conditions = new ArrayList<Condition>();

					Condition dataRetention = new Condition();
					dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
					dataRetention.setValue(dataRetentionValues[10]);
					conditions.add(dataRetention);
					DataRetentionRange dataRetentionRange = new DataRetentionRange(dataRetentionValues[10], dataRetentionValues[10]); //until...

					Condition shareData = new Condition();
					shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
					shareData.setValue(shareValues[0]);
					conditions.add(shareData);
					ShareDataRange shareDataRange = new ShareDataRange(shareValues[0], shareValues[3]);//no sharing - 3rd parties

					Condition mayInfer = new Condition();
					mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
					mayInfer.setValue(booleanValues[0]);
					conditions.add(mayInfer);
					BooleanRange mayInferBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.MAY_BE_INFERRED);// no-yes

					Condition storeSecure = new Condition();
					storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
					storeSecure.setValue(booleanValues[0]);
					conditions.add(storeSecure);
					BooleanRange storeSecureBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.STORE_IN_SECURE_STORAGE); //yes

					Condition access = new Condition();
					access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
					access.setValue(booleanValues[0]);
					conditions.add(access);
					BooleanRange accessHeldBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //yes

					Condition correct = new Condition();
					correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
					correct.setValue(booleanValues[0]);
					conditions.add(correct);
					BooleanRange correctDataBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);//yes

					Condition optOut = new Condition();
					optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
					optOut.setValue(booleanValues[0]);
					conditions.add(optOut);
					BooleanRange rightToOptOutBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_OPTOUT);
					
					
					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.NAME, ranges);
					
					item.setConditions(conditions);
				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
					List<Condition> conditions = new ArrayList<Condition>();

					Condition dataRetention = new Condition();
					dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
					dataRetention.setValue(dataRetentionValues[10]);
					conditions.add(dataRetention);
					DataRetentionRange dataRetentionRange = new DataRetentionRange(dataRetentionValues[10], dataRetentionValues[10]); //until...

					Condition shareData = new Condition();
					shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
					shareData.setValue(shareValues[0]);
					conditions.add(shareData);
					ShareDataRange shareDataRange = new ShareDataRange(shareValues[0], shareValues[3]);//no sharing - 3rd parties

					Condition mayInfer = new Condition();
					mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
					mayInfer.setValue(booleanValues[0]);
					conditions.add(mayInfer);
					BooleanRange mayInferBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.MAY_BE_INFERRED);// no-yes

					Condition storeSecure = new Condition();
					storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
					storeSecure.setValue(booleanValues[0]);
					conditions.add(storeSecure);
					BooleanRange storeSecureBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.STORE_IN_SECURE_STORAGE); //yes

					Condition access = new Condition();
					access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
					access.setValue(booleanValues[0]);
					conditions.add(access);
					BooleanRange accessHeldBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //yes

					Condition correct = new Condition();
					correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
					correct.setValue(booleanValues[0]);
					conditions.add(correct);
					BooleanRange correctDataBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);//yes

					Condition optOut = new Condition();
					optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
					optOut.setValue(booleanValues[0]);
					conditions.add(optOut);
					BooleanRange rightToOptOutBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_OPTOUT);
					
					
					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.EMAIL, ranges);
					
					item.setConditions(conditions);
				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
					List<Condition> conditions = new ArrayList<Condition>();

					Condition dataRetention = new Condition();
					dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
					dataRetention.setValue(dataRetentionValues[10]);
					conditions.add(dataRetention);
					DataRetentionRange dataRetentionRange = new DataRetentionRange(dataRetentionValues[0], dataRetentionValues[10]); //30min

					Condition shareData = new Condition();
					shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
					shareData.setValue(shareValues[0]);
					conditions.add(shareData);
					ShareDataRange shareDataRange = new ShareDataRange(shareValues[0], shareValues[3]);//no sharing - 3rd parties

					Condition mayInfer = new Condition();
					mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
					mayInfer.setValue(booleanValues[0]);
					conditions.add(mayInfer);
					BooleanRange mayInferBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.MAY_BE_INFERRED);// no-yes

					Condition storeSecure = new Condition();
					storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
					storeSecure.setValue(booleanValues[0]);
					conditions.add(storeSecure);
					BooleanRange storeSecureBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.STORE_IN_SECURE_STORAGE); //yes

					Condition access = new Condition();
					access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
					access.setValue(booleanValues[0]);
					conditions.add(access);
					BooleanRange accessHeldBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //yes

					Condition correct = new Condition();
					correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
					correct.setValue(booleanValues[0]);
					conditions.add(correct);
					BooleanRange correctDataBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);//yes
					
					Condition optOut = new Condition();
					optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
					optOut.setValue(booleanValues[0]);
					conditions.add(optOut);
					BooleanRange rightToOptOutBooleanRange = new BooleanRange(booleanValues[0], booleanValues[0], ConditionConstants.RIGHT_TO_OPTOUT);
					
					
					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.BIRTHDAY, ranges);
					
					item.setConditions(conditions);
				}
			}

			return table;
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Hashtable<String, ConditionRanges>();
	}
}
