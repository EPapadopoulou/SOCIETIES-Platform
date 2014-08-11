package org.societies.privacytrust.trust.impl;

import org.societies.api.privacytrust.trust.TrustException;

public class TrustInvalidArgumentException extends TrustException {

	public TrustInvalidArgumentException(){
		super();
	}
	
	public TrustInvalidArgumentException(String message){
		super(message);
	}
	
	public TrustInvalidArgumentException(String message, Throwable t){
		super(message, t);
	}
	
	public TrustInvalidArgumentException(Throwable t){
		super(t);
	}
	
	
	
}
