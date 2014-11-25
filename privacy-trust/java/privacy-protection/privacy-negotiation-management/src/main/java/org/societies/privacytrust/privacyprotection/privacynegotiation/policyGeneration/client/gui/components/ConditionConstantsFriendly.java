package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components;

import java.util.Hashtable;

import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

public class ConditionConstantsFriendly {

	
	
	public static String getFriendlyName(ConditionConstants constant, RequestorBean requestor, String dataType){
		if (dataType.equalsIgnoreCase("locationSymbolic")){
			dataType = "location";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		switch (constant){
			case DATA_RETENTION: 
				sb.append("Your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> will be kept by "+requestor.getRequestorId()+" for: ");
				break;
			case MAY_BE_INFERRED:
				sb.append("Allow your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> to be used to infer further information about you");
				break;
			case RIGHT_TO_ACCESS_HELD_DATA:
				sb.append("Demand access to view your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> from "+requestor.getRequestorId());
				break;
			case RIGHT_TO_CORRECT_INCORRECT_DATA:
				sb.append("Demand to correct your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> held by "+requestor.getRequestorId()+" if incorrect");
				break;
			case RIGHT_TO_OPTOUT:
				sb.append("Demand the right to opt out of disclosing your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> at any time");
				break;
			case SHARE_WITH_3RD_PARTIES:
				sb.append("Allow sharing of your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> with :");
				break;
			case STORE_IN_SECURE_STORAGE:
				sb.append("Demand that your <span style=\"color:blue; font-weight:bold;\">"+dataType+"</span> be stored securely by "+requestor.getRequestorId());
				break;
		}
		sb.append("</html>");
		return sb.toString();
	}
	
}
