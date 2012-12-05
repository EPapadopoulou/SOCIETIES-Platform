package org.societies.android.api.internal.comms;

public interface Callback extends ICallback {
	void receiveResult(String xml);

	void receiveError(String xml); 
	
	void receiveItems(String xml);
	
	void receiveMessage(String xml);
}
