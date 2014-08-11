package ac.hw.personis.exception;

import org.societies.api.context.CtxException;

public class ContextException extends CtxException {

	public ContextException(String s){
		super(s);
	}
	
	public ContextException(String s, Throwable t){
		super(s,t);
	}
}
