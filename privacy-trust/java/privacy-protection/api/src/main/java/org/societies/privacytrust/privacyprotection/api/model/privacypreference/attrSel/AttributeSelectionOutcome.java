/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel;

import java.io.Serializable;
import java.util.UUID;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.Stage;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ConfidenceCalculator;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

/**
 * @author PUMA
 *
 */
public class AttributeSelectionOutcome implements IPrivacyOutcome, Serializable  {

	
	/**
	 * 
	 */
	private static final int MIN = 0;
	private static final int MAX = 100;
	private static final long serialVersionUID = 1L;

	private CtxIdentifier ctxID;

	private int confidenceLevel;

	private Stage currentStage;
	private final String uuid;
		
	public AttributeSelectionOutcome(CtxIdentifier ctxID){
		this.ctxID = ctxID;
		this.currentStage = Stage.START;
		this.confidenceLevel = 50;
		this.uuid = UUID.randomUUID().toString();
	}
	
	public AttributeSelectionOutcome(AttributeSelectionOutcomeBean bean) throws MalformedCtxIdentifierException{
		this.ctxID = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(bean.getCtxID());
		this.confidenceLevel = bean.getConfidenceLevel();
		this.currentStage = bean.getCurrentStage();
		uuid = bean.getUuid();
	}
	
	public AttributeSelectionOutcomeBean toBean() {
		AttributeSelectionOutcomeBean bean = new  AttributeSelectionOutcomeBean();
		bean.setConfidenceLevel(confidenceLevel);
		bean.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(ctxID));
		bean.setCurrentStage(currentStage);
		bean.setUuid(uuid);
		return bean;
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

	public String getUuid() {
		return uuid;
	}

	public void updateConfidenceLevel(boolean positive){
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
		
	}

	
}
