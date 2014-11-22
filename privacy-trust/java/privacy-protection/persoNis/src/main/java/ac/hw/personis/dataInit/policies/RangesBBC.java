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
public class RangesBBC {
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
				List<Condition> conditionsInPolicy = item.getConditions();
				String dataType = item.getResource().getDataType();
				if (dataType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){
					List<Condition> conditions = new ArrayList<Condition>();
					DataRetentionRange dataRetentionRange = null;
					ShareDataRange shareDataRange = null;
					BooleanRange mayInferBooleanRange = null;
					BooleanRange storeSecureBooleanRange = null;
					BooleanRange accessHeldBooleanRange = null;
					BooleanRange correctDataBooleanRange = null;
					BooleanRange rightToOptOutBooleanRange = null;

					for (Condition conditionInPolicy : conditionsInPolicy){
						switch (conditionInPolicy.getConditionConstant()){
						case DATA_RETENTION:
							Condition dataRetention = new Condition();
							dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
							dataRetention.setValue(dataRetentionValues[0]);
							conditions.add(dataRetention);
							dataRetentionRange = new DataRetentionRange(dataRetentionValues[0], conditionInPolicy.getValue()); //1h - 12h
							break;
						case SHARE_WITH_3RD_PARTIES:
							Condition shareData = new Condition();
							shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
							shareData.setValue(shareValues[0]);
							conditions.add(shareData);
							shareDataRange = new ShareDataRange(shareValues[0], conditionInPolicy.getValue());//no sharing
							break;
						case MAY_BE_INFERRED:
							Condition mayInfer = new Condition();
							mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
							mayInfer.setValue(booleanValues[0]);
							conditions.add(mayInfer);
							mayInferBooleanRange = new BooleanRange(booleanValues[0], conditionInPolicy.getValue(), ConditionConstants.MAY_BE_INFERRED);// no-yes
							break;
						case STORE_IN_SECURE_STORAGE:
							Condition storeSecure = new Condition();
							storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
							storeSecure.setValue(booleanValues[1]);
							conditions.add(storeSecure);
							storeSecureBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.STORE_IN_SECURE_STORAGE); //no
							break;
						case RIGHT_TO_ACCESS_HELD_DATA:
							Condition access = new Condition();
							access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
							access.setValue(booleanValues[1]);
							conditions.add(access);
							accessHeldBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //no-yes
							break;
						case RIGHT_TO_CORRECT_INCORRECT_DATA:
							Condition correct = new Condition();
							correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							correct.setValue(booleanValues[1]);
							conditions.add(correct);
							correctDataBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							break;
						case RIGHT_TO_OPTOUT:
							Condition optOut = new Condition();
							optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
							optOut.setValue(booleanValues[0]);
							conditions.add(optOut);
							rightToOptOutBooleanRange = new BooleanRange(booleanValues[0], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_OPTOUT);	
							break;
						}

						ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
						table.put(CtxAttributeTypes.LOCATION_SYMBOLIC, ranges);
					}
					item.setConditions(conditions);

				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
					List<Condition> conditions = new ArrayList<Condition>();

					DataRetentionRange dataRetentionRange = null;
					ShareDataRange shareDataRange = null;
					BooleanRange mayInferBooleanRange = null;
					BooleanRange storeSecureBooleanRange = null;
					BooleanRange accessHeldBooleanRange = null;
					BooleanRange correctDataBooleanRange = null;
					BooleanRange rightToOptOutBooleanRange = null;

					for (Condition conditionInPolicy : conditionsInPolicy){
						switch (conditionInPolicy.getConditionConstant()){
						case DATA_RETENTION:
							Condition dataRetention = new Condition();
							dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
							dataRetention.setValue(dataRetentionValues[10]);
							conditions.add(dataRetention);
							dataRetentionRange = new DataRetentionRange(dataRetentionValues[10], conditionInPolicy.getValue()); 
							break;
						case SHARE_WITH_3RD_PARTIES:
							Condition shareData = new Condition();
							shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
							shareData.setValue(shareValues[0]);
							conditions.add(shareData);
							shareDataRange = new ShareDataRange(shareValues[0], conditionInPolicy.getValue());//no sharing - affiliated
							break;
						case MAY_BE_INFERRED:
							Condition mayInfer = new Condition();
							mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
							mayInfer.setValue(booleanValues[1]);
							conditions.add(mayInfer);
							mayInferBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.MAY_BE_INFERRED);// no-yes
							break;
						case STORE_IN_SECURE_STORAGE:
							Condition storeSecure = new Condition();
							storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
							storeSecure.setValue(booleanValues[1]);
							conditions.add(storeSecure);
							storeSecureBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.STORE_IN_SECURE_STORAGE); //no
							break;
						case RIGHT_TO_ACCESS_HELD_DATA:
							Condition access = new Condition();
							access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
							access.setValue(booleanValues[1]);
							conditions.add(access);
							accessHeldBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //no-yes
							break;
						case RIGHT_TO_CORRECT_INCORRECT_DATA:
							Condition correct = new Condition();
							correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							correct.setValue(booleanValues[1]);
							conditions.add(correct);
							correctDataBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);	
							break;
						case RIGHT_TO_OPTOUT:
							Condition optOut = new Condition();
							optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
							optOut.setValue(booleanValues[1]);
							conditions.add(optOut);
							rightToOptOutBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_OPTOUT);
							break;

						}
					}

					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.NAME, ranges);

					item.setConditions(conditions);
				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
					List<Condition> conditions = new ArrayList<Condition>();
					DataRetentionRange dataRetentionRange = null;
					ShareDataRange shareDataRange = null;
					BooleanRange mayInferBooleanRange = null;
					BooleanRange storeSecureBooleanRange = null;
					BooleanRange accessHeldBooleanRange = null;
					BooleanRange correctDataBooleanRange = null;
					BooleanRange rightToOptOutBooleanRange = null;
					
					for (Condition conditionInPolicy : conditionsInPolicy){
						switch (conditionInPolicy.getConditionConstant()){
						case DATA_RETENTION:
							Condition dataRetention = new Condition();
							dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
							dataRetention.setValue(dataRetentionValues[10]);
							conditions.add(dataRetention);
							dataRetentionRange = new DataRetentionRange(dataRetentionValues[10], conditionInPolicy.getValue()); //until...
							break;
						case SHARE_WITH_3RD_PARTIES:
							Condition shareData = new Condition();
							shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
							shareData.setValue(shareValues[0]);
							conditions.add(shareData);
							shareDataRange = new ShareDataRange(shareValues[0], conditionInPolicy.getValue());//no sharing - affiliated	
							break;
						case MAY_BE_INFERRED:
							Condition mayInfer = new Condition();
							mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
							mayInfer.setValue(booleanValues[1]);
							conditions.add(mayInfer);
							mayInferBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.MAY_BE_INFERRED);// no-yes	
							break;
						case STORE_IN_SECURE_STORAGE:
							Condition storeSecure = new Condition();
							storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
							storeSecure.setValue(booleanValues[1]);
							conditions.add(storeSecure);
							storeSecureBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.STORE_IN_SECURE_STORAGE); //no
							break;
						case RIGHT_TO_ACCESS_HELD_DATA:
							Condition access = new Condition();
							access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
							access.setValue(booleanValues[1]);
							conditions.add(access);
							accessHeldBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //no-yes
							break;
						case RIGHT_TO_CORRECT_INCORRECT_DATA:
							Condition correct = new Condition();
							correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							correct.setValue(booleanValues[1]);
							conditions.add(correct);
							correctDataBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							break;
						case RIGHT_TO_OPTOUT:
							Condition optOut = new Condition();
							optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
							optOut.setValue(booleanValues[1]);
							conditions.add(optOut);
							rightToOptOutBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_OPTOUT);	
							break;
						}
					}


					ConditionRanges ranges = new ConditionRanges(dataRetentionRange, shareDataRange, mayInferBooleanRange, storeSecureBooleanRange, accessHeldBooleanRange, correctDataBooleanRange, rightToOptOutBooleanRange);
					table.put(CtxAttributeTypes.EMAIL, ranges);

					item.setConditions(conditions);
				}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
					List<Condition> conditions = new ArrayList<Condition>();
					DataRetentionRange dataRetentionRange = null;
					ShareDataRange shareDataRange = null;
					BooleanRange mayInferBooleanRange = null;
					BooleanRange storeSecureBooleanRange = null;
					BooleanRange accessHeldBooleanRange = null;
					BooleanRange correctDataBooleanRange = null;
					BooleanRange rightToOptOutBooleanRange = null;
					for (Condition conditionInPolicy : conditionsInPolicy){
						switch (conditionInPolicy.getConditionConstant()){
						case DATA_RETENTION:
							Condition dataRetention = new Condition();
							dataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION);
							dataRetention.setValue(dataRetentionValues[10]);
							conditions.add(dataRetention);
							dataRetentionRange = new DataRetentionRange(dataRetentionValues[10], conditionInPolicy.getValue()); //until...
							break;
						case SHARE_WITH_3RD_PARTIES:
							Condition shareData = new Condition();
							shareData.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
							shareData.setValue(shareValues[0]);
							conditions.add(shareData);
							shareDataRange = new ShareDataRange(shareValues[0], conditionInPolicy.getValue());//no sharing - affiliated	
							break;
						case MAY_BE_INFERRED:
							Condition mayInfer = new Condition();
							mayInfer.setConditionConstant(ConditionConstants.MAY_BE_INFERRED);
							mayInfer.setValue(booleanValues[1]);
							conditions.add(mayInfer);
							mayInferBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.MAY_BE_INFERRED);// no-yes	
						break;
						case STORE_IN_SECURE_STORAGE:
							Condition storeSecure = new Condition();
							storeSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
							storeSecure.setValue(booleanValues[1]);
							conditions.add(storeSecure);
							storeSecureBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.STORE_IN_SECURE_STORAGE); //no	
							break;
						case RIGHT_TO_ACCESS_HELD_DATA:
							Condition access = new Condition();
							access.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
							access.setValue(booleanValues[1]);
							conditions.add(access);
							accessHeldBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA); //no-yes	
							break;
						case RIGHT_TO_CORRECT_INCORRECT_DATA:
							Condition correct = new Condition();
							correct.setConditionConstant(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							correct.setValue(booleanValues[1]);
							conditions.add(correct);
							correctDataBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA);
							break;
						case RIGHT_TO_OPTOUT:
							Condition optOut = new Condition();
							optOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
							optOut.setValue(booleanValues[1]);
							conditions.add(optOut);
							rightToOptOutBooleanRange = new BooleanRange(booleanValues[1], conditionInPolicy.getValue(), ConditionConstants.RIGHT_TO_OPTOUT);	
							break;							
						}
					}


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
