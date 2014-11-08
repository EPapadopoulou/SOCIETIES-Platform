package org.societies.privacytrust.privacyprotection.api.dataobfuscation;

import org.societies.api.internal.context.model.CtxAttributeTypes;

public class ObfuscationLevels {

	public static Integer getApplicableObfuscationLevels(String attributeType){
		if (attributeType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			return new Integer(9);
		}else if (attributeType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
			return new Integer(5);
		}else if (attributeType.equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
			return new Integer(4);
		}else if (attributeType.equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
			return new Integer(2);
		}
		
		return new Integer(0);
	}
}
