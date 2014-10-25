package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class IDSCacheEntry {

	private final IDSPreferenceDetailsBean details;
	private IDSPrivacyPreferenceTreeModel model;
	private final CtxAttributeIdentifier locationCtxID;

	public IDSCacheEntry(IDSPreferenceDetailsBean details, IDSPrivacyPreferenceTreeModel model, CtxAttributeIdentifier locationCtxID){
		this.details = details;
		this.model = model;
		this.locationCtxID = locationCtxID;
		
	}

	public IDSPreferenceDetailsBean getDetails() {
		return details;
	}

	public IDSPrivacyPreferenceTreeModel getModel() {
		return model;
	}

	public void setModel(IDSPrivacyPreferenceTreeModel model) {
		this.model = model;
	}

	public CtxAttributeIdentifier getLocationCtxID() {
		return locationCtxID;
	}
	
	public boolean equalsDetails(IDSPreferenceDetailsBean bean){
		return PrivacyPreferenceUtils.equals(details, bean);
	}
}
