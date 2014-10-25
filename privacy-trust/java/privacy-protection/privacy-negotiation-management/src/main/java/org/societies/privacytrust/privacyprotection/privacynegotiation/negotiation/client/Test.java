package org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.schema.identity.DataIdentifier;

public class Test {

	public static void main(String[] args){
		String eliza = "eliza";
		String panagiotis = "panagiotis";
		
		CtxEntityIdentifier entityID = new CtxEntityIdentifier(eliza, "PERSON", new Long(1));
		CtxAttributeIdentifier ctxAttributeID = new CtxAttributeIdentifier(entityID, CtxAttributeTypes.NAME, new Long(1));
		System.out.println(entityID);
		System.out.println(ctxAttributeID);
		ctxAttributeID = changeOwner(panagiotis, ctxAttributeID);
		entityID = ctxAttributeID.getScope();
		System.out.println(entityID);
		System.out.println(ctxAttributeID);
		
	}
	
	public static CtxAttributeIdentifier changeOwner(String newOwnerID, CtxAttributeIdentifier ctxID){
		
		CtxEntityIdentifier ctxEntityID = new CtxEntityIdentifier(newOwnerID, ctxID.getScope().getType(), ctxID.getScope().getObjectNumber());
		return new CtxAttributeIdentifier(ctxEntityID, ctxID.getType(), ctxID.getObjectNumber());
	}
}
