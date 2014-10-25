package org.societies.privacytrust.privacyprotection.api;

import java.util.Hashtable;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.policy.ConditionRanges;

public interface IPrivacyPolicyManagerInternal extends IPrivacyPolicyManager{

	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy, Hashtable<String, ConditionRanges> ranges) throws PrivacyException;
	public IPrivacyPolicyRegistryManager getPolicyRegistryManager();
}
