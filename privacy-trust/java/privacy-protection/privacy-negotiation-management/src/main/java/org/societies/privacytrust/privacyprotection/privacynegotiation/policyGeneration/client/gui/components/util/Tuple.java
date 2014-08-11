/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui.components.util;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * @author PUMA
 *
 */
public class Tuple {

	private ResponseItem respItem;
	private RequestItem reqItem;

	public Tuple (RequestItem reqItem, ResponseItem respItem){
		this.setReqItem(reqItem);
		this.setRespItem(respItem);
		
	}

	public ResponseItem getRespItem() {
		return respItem;
	}

	public void setRespItem(ResponseItem respItem) {
		this.respItem = respItem;
	}

	public RequestItem getReqItem() {
		return reqItem;
	}

	public void setReqItem(RequestItem reqItem) {
		this.reqItem = reqItem;
	}
}
