package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class DObfCacheEntry {

	private final CtxAttributeIdentifier locationCtxID;
	private final DObfPreferenceDetailsBean details;
	private DObfPreferenceTreeModel model;

	public DObfCacheEntry(DObfPreferenceDetailsBean details, DObfPreferenceTreeModel model, CtxAttributeIdentifier locationCtxID) {
		this.details = details;
		this.model = model;
		this.locationCtxID = locationCtxID;
	
	}

	public CtxAttributeIdentifier getLocationCtxID() {
		return locationCtxID;
	}

	public DObfPreferenceDetailsBean getDetails() {
		return details;
	}

	public DObfPreferenceTreeModel getModel() {
		return model;
	}

	public void setModel(DObfPreferenceTreeModel model) {
		this.model = model;
	}
	
	public boolean equalsDetails(DObfPreferenceDetailsBean bean){
		return PrivacyPreferenceUtils.equals(details, bean);
	}
}
