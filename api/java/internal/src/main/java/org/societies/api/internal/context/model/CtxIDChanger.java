/**
 * 
 */
package org.societies.api.internal.context.model;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * @author PUMA
 *
 */
public class CtxIDChanger {
	public static CtxAttributeIdentifier changeIDOwner(String newOwnerID, CtxAttributeIdentifier ctxID){
		
		CtxEntityIdentifier ctxEntityID = new CtxEntityIdentifier(newOwnerID, ctxID.getScope().getType(), ctxID.getScope().getObjectNumber());
		return new CtxAttributeIdentifier(ctxEntityID, ctxID.getType(), ctxID.getObjectNumber());
	}
	
	public static List<CtxAttributeIdentifier> changeIDOwner(String newOwnerID, List<CtxAttributeIdentifier> ctxIDs){
		List<CtxAttributeIdentifier> list = new ArrayList<CtxAttributeIdentifier>();
		for (CtxAttributeIdentifier ctxID : ctxIDs){
			list.add(changeIDOwner(newOwnerID, ctxID));
		}
		return list;
	}

	public static CtxAttribute changeAttrOwner(String newOwnerID, CtxAttribute ctxAttribute){
		CtxAttributeIdentifier ctxID = changeIDOwner(newOwnerID, ctxAttribute.getId());
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
	
	
	public static List<CtxAttribute> changeAttrOwner(String newOwnerID, List<CtxAttribute> ctxAttributes){
		
		List<CtxAttribute> list = new ArrayList<CtxAttribute>();
		for (CtxAttribute ctxAttribute : ctxAttributes){
			list.add(changeAttrOwner(newOwnerID, ctxAttribute));
		}
		return list;
	}
	
	
	
}
