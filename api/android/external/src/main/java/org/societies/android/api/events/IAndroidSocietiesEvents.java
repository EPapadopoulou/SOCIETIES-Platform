package org.societies.android.api.events;

/**
 * Interface defines how 3rd party and optionally, platform components, can 
 * subscribe/publish to Societies platform events. The base eventing system used in Societies
 * is based on XMPP Pubsub event nodes. These event nodes form the basis of inter-node
 * messaging. 
 * 
 * This interface allows local Android components to subscribe/publish XMPP events using a 
 * an Android Intents/ XMPP Pubsub events translation.
 * 
 * This interface should not be used for Android intra-node eventing - use Intents.  
 *
 */
public interface IAndroidSocietiesEvents {
	
	/**
	 * Societies Events intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.events.ReturnValue";

	public static final String PUBLISH_EVENT = "org.societies.android.platform.events.PUBLISH_EVENT";
	public static final String SUBSCRIBE_TO_ALL_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_ALL_EVENTS";
	public static final String SUBSCRIBE_TO_EVENT = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENT";
	public static final String SUBSCRIBE_TO_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENTS";
	public static final String UNSUBSCRIBE_FROM_ALL_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_ALL_EVENTS";
	public static final String UNSUBSCRIBE_FROM_EVENT = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENT";
	public static final String UNSUBSCRIBE_FROM_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENTS";

	/**
	 * Android intent Societies platform events. The Pubsub event nodes are the final part of the intent 
	 * 
	 */
	//Device Manager events
	String DEVICE_MANAGER_DEVICE_REGISTERED = "org.societies.android.device.manager.DEVICE_REGISTERED";

	//CSS Manager events
	String CSS_MANAGER_ADD_CSS_NODE = "org.societies.android.css.manager.addCSSNode";
	String CSS_MANAGER_DEPART_CSS_NODE = "org.societies.android.css.manager.departCSSNode";
	
	//Array of Societies events
	String societiesEvents [] = {DEVICE_MANAGER_DEVICE_REGISTERED, 
								CSS_MANAGER_ADD_CSS_NODE,
								CSS_MANAGER_DEPART_CSS_NODE
			};
	
	//Array of interface method signatures
	String methodsArray [] = {"subscribeToEvent(String client, String societiesIntent)",
			"subscribeToEvents(String client, String intentFilter)",
			"subscribeToAllEvents(String client)",
			"unSubscribeFromEvent(String client, String societiesIntent)",
			"unSubscribeFromEvents(String client, String intentFilter)",
			"unSubscribeFromAllEvents(String client)",
			"publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass)"
	};

	String GENERIC_INTENT_PAYLOAD_KEY = "Pubsub_Payload_Key";
	
	/**
	 * Subscribe to a specified Societies platform event (Android Intent)
	 * 
	 * @param client app package 
	 * @param societiesIntent specific event intent
	 * @return boolean - returned via Android intent
	 */
	boolean subscribeToEvent(String client, String societiesIntent);
	
	/**
	 * Subscribe to Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be subscribed to.
	 * 
	 * @param client app package 
	 * @param intentFilter event filter
	 * @return boolean - returned via Android intent
	 */
	boolean subscribeToEvents(String client, String intentFilter);
	
	/**
	 * Subscribe to all platform events. This should only be used if really required.
	 * 
	 * @param client app package 
	 * @return boolean - returned via Android intent
	 */
	boolean subscribeToAllEvents(String client);
	
	/**
	 * Un-subscribe from a specified Societies platform event (Android Intent)
	 * 
	 * @param client app package 
	 * @param societiesIntent specific event intent
	 * @return boolean - returned via Android intent
	 */
	boolean unSubscribeFromEvent(String client, String societiesIntent);
	
	/**
	 * Un-subscribe from Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be un-subscribed from.
	 * 
	 * @param client app package 
	 * @param intentFilter event filter
	 * @return boolean - returned via Android intent
	 */
	boolean unSubscribeFromEvents(String client, String intentFilter);
	
	/**
	 * Un-subscribe from all current platform event subscriptions.
	 * 
	 * @param client app package 
	 * @return boolean - returned via Android intent
	 */
	boolean unSubscribeFromAllEvents(String client);
	
	/**
	 * Publish an event to the Societies platform for consumption by other CSS nodes
	 * 
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @param eventPayload event object
	 * @param eventClass class of event object
	 * @return boolean - returned via Android intent
	 */
	boolean publishEvent(String client, String societiesIntent, Object eventPayload, Class eventClass);
}
