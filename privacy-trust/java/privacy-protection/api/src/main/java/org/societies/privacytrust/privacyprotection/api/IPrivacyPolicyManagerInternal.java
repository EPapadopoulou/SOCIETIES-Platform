package org.societies.privacytrust.privacyprotection.api;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;

public interface IPrivacyPolicyManagerInternal extends IPrivacyPolicyManager{

	public IPrivacyPolicyRegistryManager getPolicyRegistryManager();
}
