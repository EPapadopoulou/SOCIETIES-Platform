/**
 * 
 */
package org.societies.api.internal.context.model;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * @author PUMA
 *
 */
public class CtxIDChanger {
	public static CtxAttributeIdentifier changeOwner(String newOwnerID, CtxAttributeIdentifier ctxID){
		
		CtxEntityIdentifier ctxEntityID = new CtxEntityIdentifier(newOwnerID, ctxID.getScope().getType(), ctxID.getScope().getObjectNumber());
		return new CtxAttributeIdentifier(ctxEntityID, ctxID.getType(), ctxID.getObjectNumber());
	}

}
