package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

public class AttrSelPreferenceManager {

	private final static Logger logging = LoggerFactory.getLogger(AttrSelPreferenceManager.class);
	private PrivacyPreferenceManager privPrefMgr;
	private PrivatePreferenceCache prefCache;

	public AttrSelPreferenceManager(PrivacyPreferenceManager privPrefMgr, PrivatePreferenceCache prefCache){
		this.privPrefMgr = privPrefMgr;
		this.prefCache = prefCache;
	}

	public List<AttributeSelectionPreferenceDetailsBean> getAttrSelPreferenceDetails() {
		return this.prefCache.getAttrSelPreferenceDetails();
	}

	public boolean deleteAttSelPreference(AttributeSelectionPreferenceDetailsBean details) {
		return this.prefCache.removeAttSelPreference(details);
	}


	public Hashtable<Resource, CtxIdentifier> evaluateAttributeSelectionPreferences(Agreement agreement){
		
		Hashtable<Resource, CtxIdentifier> results = new Hashtable<Resource, CtxIdentifier>();
		return results;
	}
	public boolean storeAttrSelPreference(AttributeSelectionPreferenceDetailsBean details, AttributeSelectionPreferenceTreeModel model) {
		return this.prefCache.addAttrSelPreference(details, model);
	}

	public AttributeSelectionPreferenceTreeModel getAttrSelPreference(
			AttributeSelectionPreferenceDetailsBean details) {
		return this.prefCache.getAttrSelPreference(details);
	}


	
}
