/**
 * 
 */
package org.societies.api.internal.context.model;

import org.societies.api.context.model.CtxAttribute;
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

	public static CtxAttribute changeOwner(String newOwnerID, CtxAttribute ctxAttribute){
		CtxAttributeIdentifier ctxID = changeOwner(newOwnerID, ctxAttribute.getId());
		CtxAttribute changedCtxAttribute = new CtxAttribute(ctxID);
		 
		switch (ctxAttribute.getValueType()){
		case BINARY:
			changedCtxAttribute.setBinaryValue(ctxAttribute.getBinaryValue());
			break;
		case COMPLEX:
			changedCtxAttribute.setComplexValue(ctxAttribute.getComplexValue());
			break;
		case DOUBLE:
			changedCtxAttribute.setDoubleValue(ctxAttribute.getDoubleValue());
			break;
		case INTEGER:
			changedCtxAttribute.setIntegerValue(ctxAttribute.getIntegerValue());
			break;
		case STRING:
			changedCtxAttribute.setStringValue(ctxAttribute.getStringValue());
			break;
		case EMPTY:
			changedCtxAttribute.setStringValue(ctxAttribute.getStringValue());
			break;
		default:
			changedCtxAttribute.setStringValue(ctxAttribute.getStringValue());
			break;
		}
		return changedCtxAttribute;
		
	}
}
