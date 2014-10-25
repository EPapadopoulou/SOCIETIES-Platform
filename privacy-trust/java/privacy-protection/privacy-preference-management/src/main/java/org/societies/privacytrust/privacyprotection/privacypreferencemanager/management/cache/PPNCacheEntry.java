/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

/**
 * @author PUMA
 *
 */
public class PPNCacheEntry {

	private final PPNPreferenceDetailsBean details;
	private final CtxAttributeIdentifier locationCtxID;
	private PPNPrivacyPreferenceTreeModel model;

	public PPNCacheEntry(PPNPreferenceDetailsBean details, PPNPrivacyPreferenceTreeModel model, CtxAttributeIdentifier locationCtxID ){
		this.details = details;
		this.model = model;
		this.locationCtxID = locationCtxID;
		
	}

	public PPNPreferenceDetailsBean getDetails() {
		return details;
	}


	public PPNPrivacyPreferenceTreeModel getModel() {
		return model;
	}

	public void setModel(PPNPrivacyPreferenceTreeModel model) {
		this.model = model;
	}

	public CtxAttributeIdentifier getLocationCtxID() {
		return locationCtxID;
	}
	
	public boolean equalsDetails(PPNPreferenceDetailsBean bean){
		return PrivacyPreferenceUtils.equals(details, bean);
	}
}
