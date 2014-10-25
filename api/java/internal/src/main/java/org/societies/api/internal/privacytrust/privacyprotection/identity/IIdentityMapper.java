package org.societies.api.internal.privacytrust.privacyprotection.identity;

import java.util.List;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;

public interface IIdentityMapper {

	public List<CtxIdentifier> getLinkedAttributes(IIdentity identity);
}
