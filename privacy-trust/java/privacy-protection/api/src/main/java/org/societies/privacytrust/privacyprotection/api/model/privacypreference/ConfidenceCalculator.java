/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;

/**
 * @author PUMA
 *
 */
@Deprecated
public class ConfidenceCalculator {

	private static final int MIN = 0;
	private static final int MAX = 100;



	public static int updateConfidence(Stage currentStage, int confidenceLevel, boolean positive){
		if (positive){
			switch (currentStage){
			case POSITIVE_1:
				confidenceLevel+=20; 
				currentStage = Stage.POSITIVE_2;
				break;
			case POSITIVE_2:
				confidenceLevel+=30;
				currentStage = Stage.POSITIVE_3;
				break;
			default: 
				confidenceLevel+=10;
				currentStage = Stage.POSITIVE_1;
				break;
			}
			if (confidenceLevel>MAX){
				confidenceLevel = 100;
			}
		}else{
			switch (currentStage){
			case NEGATIVE_1:
				confidenceLevel-=20;
				currentStage = Stage.NEGATIVE_2;
				break;
			case NEGATIVE_2:
				confidenceLevel-=30;
				currentStage = Stage.NEGATIVE_3;
				break;
			default:
				confidenceLevel-=10;
				currentStage = Stage.NEGATIVE_1;
				break;
			}
			if (confidenceLevel<MIN){
				confidenceLevel = 0;
			}
		}
		return confidenceLevel;
	}

}