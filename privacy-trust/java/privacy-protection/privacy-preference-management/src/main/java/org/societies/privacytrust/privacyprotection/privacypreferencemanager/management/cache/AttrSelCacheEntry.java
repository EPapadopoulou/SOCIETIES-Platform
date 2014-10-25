package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class AttrSelCacheEntry {

	private final AttributeSelectionPreferenceDetailsBean details;
	private AttributeSelectionPreferenceTreeModel model;
	private final CtxAttributeIdentifier locationCtxID;

	public AttrSelCacheEntry(AttributeSelectionPreferenceDetailsBean details, AttributeSelectionPreferenceTreeModel model, CtxAttributeIdentifier locationCtxID ){
		this.details = details;
		this.model = model;
		this.locationCtxID = locationCtxID;
	}

	public AttributeSelectionPreferenceDetailsBean getDetails() {
		return details;
	}

	public AttributeSelectionPreferenceTreeModel getModel() {
		return model;
	}

	public void setModel(AttributeSelectionPreferenceTreeModel model) {
		this.model = model;
	}

	public CtxAttributeIdentifier getLocationCtxID() {
		return locationCtxID;
	}
	
	public boolean equalsDetails(AttributeSelectionPreferenceDetailsBean bean){
		return PrivacyPreferenceUtils.equals(details, bean);
	}
}
