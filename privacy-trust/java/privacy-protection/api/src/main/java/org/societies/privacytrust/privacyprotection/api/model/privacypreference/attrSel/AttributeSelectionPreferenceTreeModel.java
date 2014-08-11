package org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel;

import java.io.Serializable;

import javax.swing.tree.DefaultTreeModel;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

public class AttributeSelectionPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IPrivacyPreference preference;
	private AttributeSelectionPreferenceDetailsBean details;

	public AttributeSelectionPreferenceTreeModel(AttributeSelectionPreferenceDetailsBean details, IPrivacyPreference preference){
		super(preference);
		this.details = details;
		this.preference = preference;
		
		
	}
	@Override
	public IPrivacyPreference getRootPreference() {
		// TODO Auto-generated method stub
		return this.preference;
	}

	@Override
	public PrivacyPreferenceTypeConstants getPrivacyType() {
		// TODO Auto-generated method stub
		return PrivacyPreferenceTypeConstants.ATTRIBUTE_SELECTION;
	}
	public AttributeSelectionPreferenceDetailsBean getDetails() {
		return details;
	}
	public IPrivacyPreference getPreference() {
		return preference;
	}
	public void setPreference(IPrivacyPreference preference) {
		this.preference = preference;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result
				+ ((preference == null) ? 0 : preference.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AttributeSelectionPreferenceTreeModel other = (AttributeSelectionPreferenceTreeModel) obj;
		if (details == null) {
			if (other.details != null) {
				return false;
			}
		} else if (!details.equals(other.details)) {
			return false;
		}
		if (preference == null) {
			if (other.preference != null) {
				return false;
			}
		} else if (!preference.equals(other.preference)) {
			return false;
		}
		return true;
	}

	

}
