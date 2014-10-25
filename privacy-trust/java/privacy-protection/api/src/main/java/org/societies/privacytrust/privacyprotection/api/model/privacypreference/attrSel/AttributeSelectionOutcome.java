/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel;

import java.io.Serializable;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

/**
 * @author PUMA
 *
 */
public class AttributeSelectionOutcome extends IPrivacyOutcome implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CtxIdentifier ctxID;

	private int confidenceLevel;

	private Stage currentStage;
		
	public AttributeSelectionOutcome(CtxIdentifier ctxID){
		this.ctxID = ctxID;
		this.currentStage = Stage.START;
		this.confidenceLevel = 50;
		
	}
	
	public AttributeSelectionOutcome(AttributeSelectionOutcomeBean bean) throws MalformedCtxIdentifierException{
		this.ctxID = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(bean.getCtxID());
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
	}
	
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		// TODO Auto-generated method stub
		return PrivacyPreferenceTypeConstants.ATTRIBUTE_SELECTION;
	}
	
	public CtxIdentifier getCtxID() {
		return ctxID;
	}

	public void setCtxID(CtxIdentifier ctxID) {
		this.ctxID = ctxID;
	}

	
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}

	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ctxID == null) ? 0 : ctxID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj==null){
			return false;
		}
		
		if (!(obj instanceof AttributeSelectionOutcome)){
			return false;
		}
		
		AttributeSelectionOutcome outcome = (AttributeSelectionOutcome) obj;
		
		if (outcome.getCtxID().equals(this.getCtxID())){
			return true;
		}
		
		return false;
	}

	@Override
	public AttributeSelectionOutcome clone(){
		
		AttributeSelectionOutcome outcome = new AttributeSelectionOutcome(ctxID);
		outcome.setConfidenceLevel(getConfidenceLevel());
		return outcome;
	}

	@Override
	public String toString() {
		return "AttributeSelectionOutcome [ctxID=" + ctxID.getUri() + "]";
	}

	public Stage getCurrentStage() {
		// TODO Auto-generated method stub
		return this.currentStage;
	}

	public void updateConfidenceLevel(boolean positive){
		this.confidenceLevel = ConfidenceCalculator.updateConfidence(currentStage, confidenceLevel, positive);
	}
	
}
