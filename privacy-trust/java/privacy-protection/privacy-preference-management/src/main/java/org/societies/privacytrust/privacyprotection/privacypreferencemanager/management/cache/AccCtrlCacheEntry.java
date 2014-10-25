package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;

public class AccCtrlCacheEntry {

	private final AccessControlPreferenceDetailsBean details;
	private AccessControlPreferenceTreeModel model;
	private final CtxAttributeIdentifier locationCtxID;

	public AccCtrlCacheEntry(AccessControlPreferenceDetailsBean details, AccessControlPreferenceTreeModel model, CtxAttributeIdentifier locationCtxID){
		this.details = details;
		this.model = model;
		this.locationCtxID = locationCtxID;
		
	}

	public AccessControlPreferenceDetailsBean getDetails() {
		return details;
	}

	public AccessControlPreferenceTreeModel getModel() {
		return model;
	}

	public void setModel(AccessControlPreferenceTreeModel model) {
		this.model = model;
	}

	public CtxAttributeIdentifier getLocationCtxID() {
		return locationCtxID;
	}

	public boolean equalsDetails(AccessControlPreferenceDetailsBean bean){
		return PrivacyPreferenceUtils.equals(details, bean);
	}

}
