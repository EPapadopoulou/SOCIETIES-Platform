/**
 * 
 */
package org.societies.api.internal.personalisation.preference.util;

import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

/**
 * @author Eliza
 *
 */
public class PreferenceDetailsUtil {

	public PPNPreferenceDetailsBean copyOf(PPNPreferenceDetailsBean bean){
		PPNPreferenceDetailsBean copy = new PPNPreferenceDetailsBean();
		copy.setRequestor(RequestorUtils.copyOf(bean.getRequestor()));
		copy.setResource(ResourceUtils.copyOf(bean.getResource()));
		return copy ;
	}
	
	public AttributeSelectionPreferenceDetailsBean copyOf (AttributeSelectionPreferenceDetailsBean bean){
		
		AttributeSelectionPreferenceDetailsBean copy = new AttributeSelectionPreferenceDetailsBean();
		copy.setRequestor(RequestorUtils.copyOf(bean.getRequestor()));
		copy.setDataType(bean.getDataType());
		copy.setActions(ActionUtils.copyOf(bean.getActions()));
		return copy;
	}
	
	public IDSPreferenceDetailsBean copyOf (IDSPreferenceDetailsBean bean){
		
		IDSPreferenceDetailsBean copy = new IDSPreferenceDetailsBean();
		copy.setAffectedIdentity(bean.getAffectedIdentity());
		copy.setRequestor(RequestorUtils.copyOf(bean.getRequestor()));
		return copy;
	}
	
	public AccessControlPreferenceDetailsBean copyOf (AccessControlPreferenceDetailsBean bean){
		
		AccessControlPreferenceDetailsBean copy = new AccessControlPreferenceDetailsBean();
		copy.setAction(ActionUtils.copyOf(bean.getAction()));
		copy.setRequestor(RequestorUtils.copyOf(bean.getRequestor()));
		copy.setResource(ResourceUtils.copyOf(bean.getResource()));
		return copy;
	}
	
	public DObfPreferenceDetailsBean copyOf(DObfPreferenceDetailsBean bean){
		
		DObfPreferenceDetailsBean copy = new DObfPreferenceDetailsBean();
		copy.setRequestor(RequestorUtils.copyOf(bean.getRequestor()));
		copy.setResource(ResourceUtils.copyOf(bean.getResource()));
		return copy;
	}
}
