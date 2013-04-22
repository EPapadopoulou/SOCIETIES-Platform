package org.societies.webapp.controller.privacy;

import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ManagedBean(name = "ppNegotiationTest")
@SessionScoped
public class PrivacyPolicyTestController extends BasePageController {

    private class PubSubListener implements Subscriber {

        public void registerForEvents() {
//            if (log.isTraceEnabled())
//                log.trace("registerForEvents()");

            if (getPubsubClient() == null) {
                log.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
                getPubsubClient().subscriberSubscribe(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        this);

                log.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + " events");
            } catch (Exception e) {
                addGlobalMessage("Error subscribing to pubsub notifications",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error subscribing to pubsub notifications (id="
                        + getUserService().getIdentity()
                        + " event=" + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, e);
            }
        }

        public void sendPpnEvent(String itemId, ResponsePolicy responsePolicy, NegotiationDetailsBean negotiationDetails) {
            UserFeedbackPrivacyNegotiationEvent payload = new UserFeedbackPrivacyNegotiationEvent();
            payload.setResponsePolicy(responsePolicy);
            payload.setNegotiationDetails(negotiationDetails);

            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION,
                        itemId,
                        payload);

            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of new negotiation",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of new negotiation", e);
            }
        }

        public void sendSimpleEvent(String requestID, String proposalText) {
            //create user feedback bean to fire in pubsub event
            UserFeedbackBean ufBean = new UserFeedbackBean();
            ufBean.setRequestId(requestID);
            ufBean.setProposalText(proposalText);
            ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

            //send pubsub event to all user agents
            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(), UserFeedbackEventTopics.REQUEST, requestID, ufBean);
            } catch (Exception e) {
                addGlobalMessage("Error publishing user feedback event",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing user feedback event", e);
            }
        }

        public void sendExpFBEvent(String requestID, int type, String proposalText, String[] options) {
            //create user feedback bean to fire in pubsub event
            UserFeedbackBean ufBean = new UserFeedbackBean();
            ufBean.setRequestId(requestID);
            ufBean.setType(type);
            ufBean.setProposalText(proposalText);
            List<String> optionsList = new ArrayList<String>();
            Collections.addAll(optionsList, options);
            ufBean.setOptions(optionsList);
            ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

            //send pubsub event to all user agents
            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(), UserFeedbackEventTopics.REQUEST, requestID, ufBean);
            } catch (Exception e) {
                addGlobalMessage("Error publishing user feedback event",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing user feedback event", e);
            }
        }

        public void sendImpFBEvent(String requestID, int type, String proposalText, int timeout) {
            //create user feedback bean to fire in pubsub event
            UserFeedbackBean ufBean = new UserFeedbackBean();
            ufBean.setRequestId(requestID);
            ufBean.setType(type);
            ufBean.setProposalText(proposalText);
            ufBean.setTimeout(timeout);
            ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

            //send pubsub event to all user agents
            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(), UserFeedbackEventTopics.REQUEST, requestID, ufBean);
            } catch (Exception e) {
                addGlobalMessage("Error publishing user feedback event",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing user feedback event", e);
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (log.isTraceEnabled())
                log.debug("pubsubEvent(): node=" + node + " item=" + item);

        }

    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
//            if (log.isTraceEnabled())
//                log.trace("userLoggedIn()");

            pubSubListener.registerForEvents();
        }

        @Override
        public void userLoggedOut() {
//            if (log.isTraceEnabled())
//                log.trace("userLoggedOut()");
        }
    }

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();
    private static int req_counter = 0;

    public PrivacyPolicyTestController() {
        log.trace("PrivacyPolicyTestController ctor()");
    }

    public PubsubClient getPubsubClient() {
        return pubsubClient;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPubsubClient(PubsubClient pubsubClient) {
        this.pubsubClient = pubsubClient;
    }

    public UserService getUserService() {
        return userService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserService(UserService userService) {
//        if (log.isTraceEnabled())
//            log.trace("setUserService() = " + userService);

        if (this.userService != null) {
            this.userService.removeLoginListener(loginListener);
        }

        this.userService = userService;
        this.userService.addLoginListener(loginListener);
    }

    public void sendPpnEvent() {
        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId("req" + ++req_counter);

        SecureRandom random = new SecureRandom();
        String guid = new BigInteger(130, random).toString(32);

        ResponsePolicy responsePolicy = buildResponsePolicy(guid, requestorBean);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(new BigInteger(130, random).intValue());

        pubSubListener.sendPpnEvent(guid, responsePolicy, negotiationDetails);
    }

    public void sendSimpleNotifiationEvent() {
        String requestID = UUID.randomUUID().toString();

        String proposalText = "This is just a simple alert";

        pubSubListener.sendSimpleEvent(requestID, proposalText);
    }

    public void sendAckNackEvent() {
        String requestID = UUID.randomUUID().toString();

        String proposalText = "Pick a button";
        String[] options = new String[]{"btn1", "btn2"}; // this actually has no effect for acknack

        pubSubListener.sendExpFBEvent(requestID, ExpProposalType.ACKNACK, proposalText, options);
    }

    public void sendSelectOneEvent() {
        String requestID = UUID.randomUUID().toString();

        String proposalText = "Pick ONE option";
        String[] options = new String[]{"Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"};

        pubSubListener.sendExpFBEvent(requestID, ExpProposalType.RADIOLIST, proposalText, options);
    }

    public void sendSelectManyEvent() {
        String requestID = UUID.randomUUID().toString();

        String proposalText = "Pick MANY options";
        String[] options = new String[]{"red", "orange", "yellow", "green", "blue", "indigo", "violet"};

        pubSubListener.sendExpFBEvent(requestID, ExpProposalType.CHECKBOXLIST, proposalText, options);
    }

    public void sendTimedAbortEvent(long sec) {
        String requestID = UUID.randomUUID().toString();

        String proposalText = "This is a timed abort";

        pubSubListener.sendImpFBEvent(requestID, ImpProposalType.TIMED_ABORT, proposalText, (int) sec * 1000);
    }

    private static ResponsePolicy buildResponsePolicy(String guid, RequestorBean requestorBean) {


        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("http://this.is.a.win/", "winning - " + guid));
//        responseItems.add(buildResponseItem("http://paddy.rules/", "paddy"));
//        responseItems.add(buildResponseItem("http://something.something.something/", "dark side"));

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);
        return responsePolicy;
    }

    private static ResponseItem buildResponseItem(String uri, String dataType) {
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action2 = new Action();
        action2.setActionConstant(ActionConstants.DELETE);
        action2.setOptional(false);
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("1");
        condition1.setOptional(false);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("2");
        condition2.setOptional(true);
        Condition condition3 = new Condition();
        condition3.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
        condition3.setValue("3");
        condition3.setOptional(false);
        Condition condition4 = new Condition();
        condition4.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
        condition4.setValue("4");
        condition4.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri(uri);
        resource.setDataType(dataType);

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action2);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);
        requestItem.getConditions().add(condition3);
        requestItem.getConditions().add(condition4);

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);
        return responseItem;
    }

}
