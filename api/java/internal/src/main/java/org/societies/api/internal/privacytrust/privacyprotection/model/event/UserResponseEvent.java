package org.societies.api.internal.privacytrust.privacyprotection.model.event;

import java.io.Serializable;

public class UserResponseEvent implements Serializable{

	public enum Response {ABORT, CONTINUE};
	private final String uuid;
	private final Response response;

	public UserResponseEvent(String uuid, Response response){
		this.uuid = uuid;
		this.response = response;
		
	}
	public String getUuid() {
		return uuid;
	}
	public Response getResponse() {
		return response;
	}
	
}
