package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.util.Hashtable;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

public class ConditionConstantsFriendly {

	
	
	public static String getFriendlyName(ConditionConstants constant){
		switch (constant){
			case DATA_RETENTION: 
				return "Data will be kept for: ";
			case MAY_BE_INFERRED:
				return "Allow this data to be used to infer further information about you";
			case RIGHT_TO_ACCESS_HELD_DATA:
				return "Demand to access this data from the requestor";
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				return "Demand to correct this data held by the requestor if incorrect";
			case RIGHT_TO_OPTOUT:
				return "Demand the right to opt out of disclosing this data at any time";
			case SHARE_WITH_3RD_PARTIES:
				return "Allow sharing of this data with :";
			case STORE_IN_SECURE_STORAGE:
				return "Demand that this data be stored securely by the requestor";
	
			default: return "Condition not recognised";
		}
	}
}
