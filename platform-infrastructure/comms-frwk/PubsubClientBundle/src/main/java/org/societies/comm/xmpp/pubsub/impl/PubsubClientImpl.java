package org.societies.comm.xmpp.pubsub.impl;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jabber.protocol.pubsub.Create;
import org.jabber.protocol.pubsub.Item;
import org.jabber.protocol.pubsub.Items;
import org.jabber.protocol.pubsub.Publish;
import org.jabber.protocol.pubsub.Pubsub;
import org.jabber.protocol.pubsub.Retract;
import org.jabber.protocol.pubsub.Subscribe;
import org.jabber.protocol.pubsub.Unsubscribe;
import org.jabber.protocol.pubsub.owner.Affiliations;
import org.jabber.protocol.pubsub.owner.Delete;
import org.jabber.protocol.pubsub.owner.Purge;
import org.jabber.protocol.pubsub.owner.Subscriptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.api.comm.xmpp.pubsub.Affiliation;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.comm.xmpp.pubsub.SubscriptionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class PubsubClientImpl implements PubsubClient, ICommCallback {

	public static final int TIMEOUT = 10000;
	
	private final static List<String> NAMESPACES = Collections
			.singletonList("http://jabber.org/protocol/pubsub");
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("jabber.x.data",
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event"));
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubClientImpl.class);
	
	private ICommManager endpoint;
	private Map<String,Object> responses;
	private Map<Subscription,List<Subscriber>> subscribers;
	private IIdentityManager idm;
	private Marshaller contentMarshaller;
	private Unmarshaller contentUnmarshaller;
	private String packagesContextPath;
	
	@Autowired
	public PubsubClientImpl(ICommManager endpoint) {
		responses = new HashMap<String, Object>();
		subscribers = new HashMap<Subscription, List<Subscriber>>();
		this.endpoint = endpoint;
		idm = endpoint.getIdManager();
		packagesContextPath = "";
		try {
			JAXBContext jc = JAXBContext.newInstance();
			contentUnmarshaller = jc.createUnmarshaller();
			contentMarshaller = jc.createMarshaller();
			endpoint.register(this);
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		} catch (JAXBException e) {
			LOG.error(e.getMessage());
		}
	}
	
	public ICommManager getICommManager() {
		return endpoint;
	}
	
	/*
	 * CommCallback Impl
	 */
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if (payload instanceof org.jabber.protocol.pubsub.event.Event) {
			org.jabber.protocol.pubsub.event.Items items = ((org.jabber.protocol.pubsub.event.Event)payload).getItems();
			String node = items.getNode();
			Subscription sub = new Subscription(stanza.getFrom(), stanza.getTo(), node, null); // TODO may break due to mismatch between "to" and local identity
			org.jabber.protocol.pubsub.event.Item i = items.getItem().get(0); // TODO assume only one item per notification
			try {
				Object bean = null;
				synchronized (contentUnmarshaller) {
					bean = contentUnmarshaller.unmarshal((Element)i.getAny());
				}
				List<Subscriber> subscriberList = subscribers.get(sub);
				for (Subscriber subscriber : subscriberList)
					subscriber.pubsubEvent(stanza.getFrom(), node, i.getId(), bean);
			} catch (JAXBException e) {
				LOG.warn("JAXBException while unmarshalling pubsub payload",e);
			}
		}
	}
	// TODO subId
//	<message from='pubsub.shakespeare.lit' to='francisco@denmark.lit' id='foo'>
//	  <event xmlns='http://jabber.org/protocol/pubsub#event'>
//	    <items node='princely_musings'>
//	      <item id='ae890ac52d0df67ed7cfdf51b644e901'/>
//	    </items>
//	  </event>
//	  <headers xmlns='http://jabber.org/protocol/shim'>
//	    <header name='SubID'>123-abc</header>
//	    <header name='SubID'>004-yyy</header>
//	  </headers>
//	</message>

	
	@Override
	public void receiveResult(Stanza stanza, Object payload) {
		synchronized (responses) {
			LOG.info("receiveResult 4 id "+stanza.getId());
			responses.put(stanza.getId(), payload);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		synchronized (responses) {
			LOG.info("receiveError 4 id "+stanza.getId());
			responses.put(stanza.getId(), error);
			responses.notifyAll();
		}
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		SimpleEntry<String, List<String>> mapSimpleEntry = new AbstractMap.SimpleEntry<String, List<String>>(node, items);
		synchronized (responses) {
			LOG.info("receiveItems 4 id "+stanza.getId());
			responses.put(stanza.getId(), mapSimpleEntry);
			responses.notifyAll();
		}
	}

	/*
	 * PubsubClient Impl - emulates synchronous
	 */
	
	private Object blockingIQ(Stanza stanza, Object payload) throws CommunicationException, XMPPError  {
		endpoint.sendIQSet(stanza, payload, this);
		return waitForResponse(stanza.getId());
	}
	
	private Object waitForResponse(String id) throws XMPPError {
		Object response = null;
		synchronized (responses) {				
			while (!responses.containsKey(id)) {
				try {
					LOG.info("waiting response 4 id "+id);
					responses.wait(TIMEOUT);
				} catch (InterruptedException e) {
					LOG.info(e.getMessage());
				}
				LOG.info("checking response 4 id "+id+" in "+Arrays.toString(responses.keySet().toArray()));
			}
			response = responses.remove(id);
			LOG.info("got response 4 id "+id);
		}
		if (response instanceof XMPPError)
			throw (XMPPError)response;
		return response;
	}

	@Override
	public List<String> discoItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		String id = endpoint.getItems(pubsubService, node, this);
		Object response = waitForResponse(id);
		
		// TODO node check
//		String returnedNode = ((SimpleEntry<String, List<XMPPNode>>)response).getKey();
//		if (returnedNode != node)
//			throw new CommunicationException("");
		return ((SimpleEntry<String, List<String>>)response).getValue();
	}

	@Override
	public Subscription subscriberSubscribe(Identity pubsubService, String node,
			Subscriber subscriber) throws XMPPError, CommunicationException {
		Subscription subscription = new Subscription(pubsubService, endpoint.getIdentity(), node, null);
		List<Subscriber> subscriberList = subscribers.get(subscription);
		
		if (subscriberList==null) {
			subscriberList = new ArrayList<Subscriber>();
			
			Stanza stanza = new Stanza(pubsubService);
			Pubsub payload = new Pubsub();
			Subscribe sub = new Subscribe();
			sub.setJid(endpoint.getIdentity().getJid());
			sub.setNode(node);
			payload.setSubscribe(sub);
	
			Object response = blockingIQ(stanza, payload);
			
			String subId = ((Pubsub)response).getSubscription().getSubid();
			subscription = new Subscription(pubsubService, endpoint.getIdentity(), node, subId);
			subscribers.put(subscription, subscriberList);
		}
		
		subscriberList.add(subscriber);
		
		return subscription;
	}

	@Override
	public void subscriberUnsubscribe(Identity pubsubService, String node,
			Subscriber subscriber) throws XMPPError,
			CommunicationException {
		
		Subscription subscription = new Subscription(pubsubService, endpoint.getIdentity(), node, null);
		List<Subscriber> subscriberList = subscribers.get(subscription);
		subscriberList.remove(subscriber);
		
		if (subscriberList.size()==0) {
			Stanza stanza = new Stanza(pubsubService);
			Pubsub payload = new Pubsub();
			Unsubscribe unsub = new Unsubscribe();
			unsub.setJid(endpoint.getIdentity().getJid());
			unsub.setNode(node);
			payload.setUnsubscribe(unsub);
	
			Object response = blockingIQ(stanza, payload);
		}
	}

	

	@Override
	public List<Element> subscriberRetrieveLast(Identity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		// TODO max items... in the server also!
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Element> returnList = new ArrayList<Element>();
		for (Item i : itemList)
			returnList.add((Element) i.getAny());
		
		return returnList;
	}

	@Override
	public List<Element> subscriberRetrieveSpecific(Identity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Items items = new Items();
		items.setNode(node);
		if (subId!=null)
			items.setSubid(subId);
		
		for(String itemId : itemIdList) {
			Item item = new Item();
			item.setId(itemId);
			items.getItem().add(item);
		}
		
		payload.setItems(items);
		
		Object response = blockingIQ(stanza, payload);
		
		List<Item> itemList = ((Pubsub)response).getItems().getItem();
		List<Element> returnList = new ArrayList<Element>();
		for (Item i : itemList)
			returnList.add((Element) i.getAny());
		
		return returnList;
	}

	@Override
	public String publisherPublish(Identity pubsubService, String node,
			String itemId, Object item) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Publish p = new Publish();
		p.setNode(node);
		Item i = new Item();
		if (itemId!=null)
			i.setId(itemId);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			synchronized (contentMarshaller) {
				contentMarshaller.marshal(item, doc);
			}
			i.setAny(doc.getDocumentElement());
			
			p.setItem(i);
			payload.setPublish(p);
			
			Object response = blockingIQ(stanza, payload);
			
			return ((Pubsub)response).getPublish().getItem().getId();
		} catch (ParserConfigurationException e) {
			throw new CommunicationException("ParserConfigurationException while marshalling item to publish", e);
		} catch (JAXBException e) {
			throw new CommunicationException("JAXBException while marshalling item to publish", e);
		}
	}

	@Override
	public void publisherDelete(Identity pubsubService, String node,
			String itemId) throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		
		Retract retract = new Retract();
		retract.setNode(node);
		Item i = new Item();
		i.setId(itemId);
		retract.getItem().add(i);
		payload.setRetract(retract);
		
		Object response = blockingIQ(stanza, payload);
	}

	@Override
	public void ownerCreate(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		Pubsub payload = new Pubsub();
		Create c = new Create();
		c.setNode(node);
		payload.setCreate(c);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public void ownerDelete(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Delete delete = new Delete();
		delete.setNode(node);
		payload.setDelete(delete);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public void ownerPurgeItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Purge purge = new Purge();
		purge.setNode(node);
		payload.setPurge(purge);
		
		blockingIQ(stanza, payload);
	}

	@Override
	public Map<Identity, SubscriptionState> ownerGetSubscriptions(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Subscription> subList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getSubscriptions().getSubscription();
		Map<Identity, SubscriptionState> returnMap = new HashMap<Identity, SubscriptionState>();
		for (org.jabber.protocol.pubsub.owner.Subscription s : subList)
			returnMap.put(idm.fromJid(s.getJid()), SubscriptionState.valueOf(s.getSubscription()));
		
		return returnMap;
	}

	@Override
	public Map<Identity, Affiliation> ownerGetAffiliations(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		blockingIQ(stanza, payload);
		
		List<org.jabber.protocol.pubsub.owner.Affiliation> affList = ((org.jabber.protocol.pubsub.owner.Pubsub)payload).getAffiliations().getAffiliation();
		Map<Identity, Affiliation> returnMap = new HashMap<Identity, Affiliation>();
		for (org.jabber.protocol.pubsub.owner.Affiliation a : affList)
			returnMap.put(idm.fromJid(a.getJid()), Affiliation.valueOf(a.getAffiliation()));
		
		return returnMap;
	}

	@Override
	public void ownerSetSubscriptions(Identity pubsubService, String node,
			Map<Identity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Subscriptions subs = new Subscriptions();
		subs.setNode(node);
		payload.setSubscriptions(subs);
		
		for (Identity subscriber : subscriptions.keySet()) {
			org.jabber.protocol.pubsub.owner.Subscription s = new org.jabber.protocol.pubsub.owner.Subscription();
			s.setJid(subscriber.getJid());
			s.setSubscription(subscriptions.get(subscriber).toString());
			subs.getSubscription().add(s);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple subscription changes
	}

	@Override
	public void ownerSetAffiliations(Identity pubsubService, String node,
			Map<Identity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		Stanza stanza = new Stanza(pubsubService);
		org.jabber.protocol.pubsub.owner.Pubsub payload = new org.jabber.protocol.pubsub.owner.Pubsub();
		Affiliations affs = new Affiliations();
		affs.setNode(node);
		payload.setAffiliations(affs);
		
		for (Identity subscriber : affiliations.keySet()) {
			org.jabber.protocol.pubsub.owner.Affiliation a = new org.jabber.protocol.pubsub.owner.Affiliation();
			a.setJid(subscriber.getJid());
			a.setAffiliation(affiliations.get(subscriber).toString());
			affs.getAffiliation().add(a);
		}
		
		blockingIQ(stanza, payload);
		
		// TODO error handling on multiple affiliation changes
		
	}

	@Override
	public synchronized void addJaxbPackages(List<String> packageList) throws JAXBException {
		if (packagesContextPath.length()==0) {
			// TODO first run!
		}
		
		StringBuilder contextPath = new StringBuilder(packagesContextPath);
		for (String pack : packageList)
			contextPath.append(":" + pack);

		JAXBContext jc = JAXBContext.newInstance(contextPath.toString(),
				this.getClass().getClassLoader());
		contentUnmarshaller = jc.createUnmarshaller();
		contentMarshaller = jc.createMarshaller();
		
		packagesContextPath = contextPath.toString();
	}
	
}
