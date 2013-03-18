/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.integration.test.userfeedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Rafik
 * @author Olivier Maridat (Trialog)
 */
public class UserFeedbackMocker implements Subscriber {
	private static Logger LOG = LoggerFactory.getLogger(UserFeedbackMocker.class);

	private IUserFeedback userFeedback;
	private ICommManager commManager;
	private PubsubClient pubsub;
	private boolean enabled;
	private IIdentity cloodNodeJid;
	/**
	 * Map of pre-selected result for user feedback requests
	 * <feedback type : result >
	 */
	private Map<UserFeedbackType, UserFeedbackMockResult> mockResults;

	public UserFeedbackMocker() {
		mockResults = new HashMap<UserFeedbackType, UserFeedbackMockResult>();
		enabled = false;
	}

	public void onCreate() throws Exception {
		if (!isDepencyInjectionDone()) {
			throw new Exception("[Dependency Injection] UserFeedbackMocker is not ready. Missing dependencies.");
		}
		LOG.info("### [UserFeedbackMock] init method");
		// -- Rettrieve cloud node JID
		cloodNodeJid = commManager.getIdManager().getThisNetworkNode();
		LOG.debug("### [UserFeedbackMock] Got my cloud ID: "+cloodNodeJid);

		// -- Register for events from created pubsub node
		LOG.debug("### [UserFeedbackMock] Registering for user feedback pubsub node");
		pubsub.subscriberSubscribe(cloodNodeJid, "org/societies/useragent/feedback/event/REQUEST", this);
		//pubsub.subscriberSubscribe(myCloudID, "org/societies/useragent/feedback/event/EXPLICIT_RESPONSE", this);
		//pubsub.subscriberSubscribe(myCloudID, "org/societies/useragent/feedback/event/IMPLICIT_RESPONSE", this);
		LOG.debug("### [UserFeedbackMock] Pubsub registration complete!");
	}

	public void onDestroy() throws XMPPError, CommunicationException {
		// -- Unregister to events
		LOG.debug("### [UserFeedbackMock] Unregistering for user feedback pubsub node");
		pubsub.subscriberUnsubscribe(cloodNodeJid, "org/societies/useragent/feedback/event/REQUEST", this);
	}


	@Override
	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		// -- UserFeedbackMocker disabled
		if (!isEnabled()) {
			return;
		}
		// -- Not a relevant event
		if(!eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST")) {
			return;
		}

		// -- Retrieve data
		UserFeedbackBean ufBean = (UserFeedbackBean)item;
		LOG.debug("+++ [UserFeedbackMock] Received pubsub event with topic: "+eventTopic);
		LOG.debug("+++ [UserFeedbackMock] "+ufBean.getMethod().name());
		for (String string : ufBean.getOptions()) {
			LOG.debug("+++ [UserFeedbackMock] option: " + string);
		}

		// -- Find in configuration
		UserFeedbackType feedbackType = UserFeedbackType.fromValue(ufBean.getMethod().value()+":"+ufBean.getType());
		boolean userfeedbackReplied = false;
		if (mockResults.containsKey(feedbackType)) {
			LOG.debug("+++ [UserFeedbackMock] Configuration found for this explicit request: "+ feedbackType);
			UserFeedbackMockResult mockResult = mockResults.get(feedbackType);
			// - Send result
			if (null != mockResult.getResult() || mockResult.getResult().size() >= 0) {
				userfeedbackReplied = true;
				userFeedback.submitExplicitResponse(ufBean.getRequestId(), mockResult.getResult());
			}
			// - Send result using option indexes
			else if (null != mockResult.getResult() || mockResult.getResult().size() >= 0) {
				userfeedbackReplied = true;
				for(Integer resultIndex : mockResult.getResultIndexes()) {
					if (ufBean.getOptions().contains(resultIndex)) {
						mockResult.addResult(ufBean.getOptions().get(resultIndex));
					}
				}
				userFeedback.submitExplicitResponse(ufBean.getRequestId(), mockResult.getResult());
			}

			// - Manage usage of this result value
			mockResult.incrNbOfusage(-1);
			if (!mockResult.isUsable()) {
				mockResults.remove(ufBean.getType());
			}
		}

		// -- Default behaviour
		if (!userfeedbackReplied) {
			LOG.debug("+++ [UserFeedbackMock] Use default configuration for this request: "+ feedbackType);
			if (FeedbackMethodType.GET_EXPLICIT_FB.equals(ufBean.getMethod())) {
				List<String> result = new ArrayList<String>();
				result.add(ufBean.getOptions().size() > 0 ? ufBean.getOptions().get(0) : "Ouch!");
				userFeedback.submitExplicitResponse(ufBean.getRequestId(), result);
			}
			else if (FeedbackMethodType.GET_IMPLICIT_FB.equals(ufBean.getMethod())) {
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
			}
			else if (FeedbackMethodType.SHOW_NOTIFICATION.equals(ufBean.getMethod())) {
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
			}
		}
	}

	/**
	 * Add a pre-selected reply to a user feedback request
	 * @param feedbackType
	 * @param reply
	 */
	public void addReply(UserFeedbackType feedbackType, UserFeedbackMockResult reply) {
		mockResults.put(feedbackType, reply);
	}
	public void removeReply(UserFeedbackType feedbackType) {
		if (mockResults.containsKey(feedbackType)) {
			mockResults.remove(feedbackType);
		}
	}
	public void removeAllReplies() {
		mockResults.clear();
	}

	/* -- Dependency injection --- */
	@Autowired
	public void setCommManager(ICommManager commManager){
		this.commManager = commManager;
		LOG.info("[DependencyInjection] ICommManager injected");
	}
	@Autowired
	public void setPubsub(PubsubClient pubsub){
		this.pubsub = pubsub;
		LOG.info("[DependencyInjection] PubsubClient injected");
	}
	@Autowired
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
		LOG.info("[DependencyInjection] IUserFeedback injected");
	}
	@Value("${userfeedback.mocked:0}")
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		LOG.info("[DependencyInjection] Userfeedback mocker is "+(isEnabled() ? "en" : "dis")+"abled");
	}
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	public boolean isDepencyInjectionDone(int level) {
		if (null == commManager) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commManager.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == pubsub) {
			LOG.info("[Dependency Injection] Missing PubsubClient");
			return false;
		}
		if (null == userFeedback) {
			LOG.info("[Dependency Injection] Missing IUserFeedback");
			return false;
		}
		return true;
	}
}
