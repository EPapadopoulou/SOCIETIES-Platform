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
package org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationAgent;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationClient;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponsePolicyUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentityOption;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.identityCreation.gui.IdentitySelectionWindow;
import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client.data.DataHelper;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.ClientResponseChecker;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.ClientResponsePolicyGenerator;

/**
 * Describe your class here...
 * 
 * @author Eliza
 * 
 */
public class NegotiationClient implements INegotiationClient {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final INegotiationAgent negotiationAgentRemote;
	private RequestPolicy requestPolicy;
	private ICtxBroker ctxBroker;
	private IEventMgr eventMgr;
	private PrivacyPolicyNegotiationManager policyMgr;
	private Hashtable<RequestorBean, ResponsePolicy> myPolicies;
	private Hashtable<RequestorBean, Agreement> agreements;
	private IPrivacyAgreementManagerInternal policyAgreementMgr;
	private IPrivacyDataManagerInternal privacyDataManager;
	private IPrivacyPolicyManager privacyPolicyManager;
	private IIdentitySelection idS;
	private IPrivacyPreferenceManager privPrefMgr;
	private IIdentityManager idm;
	private IIdentity userIdentity;
	private IUserFeedback userFeedback;
	private NegotiationDetailsBean details;

	public NegotiationClient(INegotiationAgent negotiationAgent,
			PrivacyPolicyNegotiationManager privacyPolicyNegotiationManager) {
		this.negotiationAgentRemote = negotiationAgent;
		this.policyMgr = privacyPolicyNegotiationManager;
		this.privacyPolicyManager = privacyPolicyNegotiationManager
				.getPrivacyPolicyManager();
		this.ctxBroker = privacyPolicyNegotiationManager.getCtxBroker();
		this.eventMgr = privacyPolicyNegotiationManager.getEventMgr();
		this.policyAgreementMgr = privacyPolicyNegotiationManager
				.getPrivacyAgreementManagerInternal();
		this.privacyDataManager = privacyPolicyNegotiationManager
				.getPrivacyDataManagerInternal();
		this.idS = privacyPolicyNegotiationManager.getIdentitySelection();
		this.privPrefMgr = privacyPolicyNegotiationManager
				.getPrivacyPreferenceManager();
		this.idm = privacyPolicyNegotiationManager.getIdm();
		this.myPolicies = new Hashtable<RequestorBean, ResponsePolicy>();
		this.agreements = new Hashtable<RequestorBean, Agreement>();
		this.userIdentity = idm.getThisNetworkNode();
		this.userFeedback = privacyPolicyNegotiationManager.getUserFeedback();

	}

	@Override
	public void receiveProviderPolicy(RequestPolicy policy) {


		this.logging.debug("Received Provider RequestPolicy!");
		this.logging.debug("Request policy contains: "
				+ policy.getRequestItems().size() + " requestItems");
		this.logging.debug("############## Original RequestPolicy  "
				+ RequestPolicyUtils.toXmlString(policy));
		// this.logging.debug(policy.toString());
		this.requestPolicy = policy;
		List<String> notFoundTypes = this.dataTypesExist(policy);
		String str = "";
		for (String s : notFoundTypes) {
			str = str.concat(s + "\n");
		}
		if (notFoundTypes.size() > 0) {
			this.logging.debug("Service requires these contextTypes\n" + str
					+ "which don't exist");
			this.userFeedback
			.showNotification("Error starting service\n Service requires these data types\n"
					+ str + "which don't exist in your profile");
			// JOptionPane.showMessageDialog(null,
			// "Service requires these data types\n"+str+"which don't exist",
			// "Error Starting service", JOptionPane.ERROR_MESSAGE);
			InternalEvent evt = this.createFailedNegotiationEvent();
			try {
				this.eventMgr.publishInternalEvent(evt);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		/*What happens now:
		 * First, we use the ClientResponsePolicyGenerator to generate the user's ResponsePolicy
		 * which retrieves the evaluated PPN preferences 
		 * and shows the RequestPolicy  to the user using the PPNUIWindow GUI
		 * After the user edits the policy, we get the ResponsePolicy and send it back to the provider.
		 */
		//JOptionPane.showMessageDialog(null, "Generating response policy");
		ClientResponsePolicyGenerator gen = new ClientResponsePolicyGenerator();
		ResponsePolicy responsePolicyGenerated = gen.generatePolicy(this.policyMgr,details, policy);
		if (responsePolicyGenerated==null){
			if (details.getRequestor() instanceof RequestorServiceBean){
				RequestorServiceBean requestor = (RequestorServiceBean) details.getRequestor();
				this.userFeedback.showNotification("Installing service: "+requestor.getRequestorServiceId().getServiceInstanceIdentifier()+" was aborted by user.");
				this.logging.debug("User aborted negotiation with : "+requestor.getRequestorId()+" for service: "+ServiceModelUtils.serviceResourceIdentifierToString(requestor.getRequestorServiceId()));
			}else if (details.getRequestor() instanceof RequestorCisBean){
				RequestorCisBean requestor = (RequestorCisBean) details.getRequestor();
				this.userFeedback.showNotification("Joining CIS : (find the title) has been aborted by user's request");
				this.logging.debug("User aborted negotiation with : "+requestor.getRequestorId()+" for CIS: "+requestor.getCisRequestorId());
			}
			this.policyMgr.setNegotiationAborted(details);
			return;
		}

		this.logging.debug("############## Provided ResponsePolicy" + ResponsePolicyUtils.toString(responsePolicyGenerated));
		this.myPolicies.put(responsePolicyGenerated.getRequestor(), responsePolicyGenerated);
		this.logging
		.debug("Generated ResponsePolicy. Negotiating with other party");
		Future<ResponsePolicy> responsePolicy = this.negotiationAgentRemote
				.negotiate(policy.getRequestor(), responsePolicyGenerated);
		this.logging.debug("Received reply ResponsePolicy");
		try {
			ResponsePolicy responsePolicyGet = responsePolicy.get();
			/*
			 * received response policy from provider
			 */
			this.receiveNegotiationResponse(responsePolicyGet);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void receiveNegotiationResponse(ResponsePolicy policy) {
		this.logging.debug("Received response policy from Provider!");
		// this.logging.debug(policy.toString());
		if (policy.getNegotiationStatus().equals(NegotiationStatus.FAILED)) {
			this.logging.debug("Negotiation Failed by provider");
			String requestorStr = "";
			if (policy.getRequestor() instanceof RequestorCisBean) {
				requestorStr = "Cis administrator";
			} else {
				requestorStr = "Service Provider";
			}
			this.userFeedback.showNotification("Negotiation Status: "
					+ NegotiationStatus.FAILED + "\n" + requestorStr
					+ " did not accept your privacy terms & conditions");
			/*
			 * JOptionPane.showMessageDialog(null,
			 * "Negotiation Status: "+NegotiationStatus.FAILED+"\n"
			 * +"Provider did not accept my terms & conditions");
			 */
			InternalEvent evt = this.createFailedNegotiationEvent();
			try {
				this.eventMgr.publishInternalEvent(evt);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ResponsePolicy myResponsePolicy = this.findMyResponsePolicy(policy);
			if (myResponsePolicy == null) {
				// new
				// TimedNotificationGUI().showGUI("Ignoring an invalid response policy",
				// "message from: Privacy Policy Negotiation Client");
				this.logging.debug("Ignoring invalid response policy");
				return;
			}
			this.logging.debug("Checking other party's response policy against user's response policy");
			this.logging.debug("&&&&&&& Requested "+ ResponsePolicyUtils.toString(myResponsePolicy));
			this.logging.debug("&&&&&&& Provided "+ ResponsePolicyUtils.toString(policy));
			ClientResponseChecker checker = new ClientResponseChecker(this.policyMgr);
			ResponsePolicy finalResponsePolicy = checker.checkResponse(myResponsePolicy, policy);
			if (null!=finalResponsePolicy) {
				this.logging.debug("ResponsePolicy is OK, creating agreement");
				
				this.setFinalIdentity(policy, policy.getRequestor());

			} else {
				this.logging
				.debug("Received ResponsePolicy does not match user's ResponsePolicy, failing negotiation");
				InternalEvent evt = this.createFailedNegotiationEvent();
				try {
					this.eventMgr.publishInternalEvent(evt);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}

	}

	private IIdentity selectIdentity(List<IIdentityOption> idOptions, Agreement agreement) {

		List<IIdentity> list = new ArrayList<IIdentity>();
		for (IIdentityOption option: idOptions){
			list.add(option.getReferenceIdentity());
		}
		IIdentity recommendedIdentity = this.privPrefMgr.evaluateIDSPreferences(agreement, list );
		
		if (idOptions.size()==0){
			
			//create new identity
			Hashtable<String, List<CtxIdentifier>> identityInformation = this.idS.showIdentityCreationGUI(agreement);
			
			Enumeration<String> keys = identityInformation.keys();

			String idName = keys.nextElement();
			List<CtxIdentifier> ctxIDList = identityInformation.get(idName);

			IIdentity identity = this.idS.createIdentity(idName, ctxIDList);

			
			for (CtxIdentifier ctxID : ctxIDList){
				CtxIdentifier newCtxID = CtxIDChanger.changeOwner(identity.getBareJid(), (CtxAttributeIdentifier) ctxID);
				this.logging.debug("Replaced owner in ctxID:"+newCtxID.getOwnerId()+" full ID: "+newCtxID.getUri());
				for (ResponseItem item : agreement.getRequestedItems()){
					if (item.getRequestItem().getResource().getDataType().equalsIgnoreCase(newCtxID.getType())){
						item.getRequestItem().getResource().setDataIdUri(newCtxID.getUri());
					}
				}
			}
			agreement.setUserIdentity(identity.getJid());
			InternalEvent event = new InternalEvent(
					EventTypes.IDENTITY_CREATED, "",
					INegotiationClient.class.getName(), AgreementUtils.copyOf(agreement));
			try {
				this.eventMgr.publishInternalEvent(event);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return identity;
		}else{
			//select from identity list
			List<IIdentity> identities = new ArrayList<IIdentity>();
			for (IIdentityOption option : idOptions){
				identities.add(option.getReferenceIdentity());
			}
			
			
			IIdentity identity = this.idS.showIdentitySelectionGUI(identities, recommendedIdentity);
			if(identity==null){
				return selectIdentity(new ArrayList<IIdentityOption>(), agreement);
			}
			
			List<CtxIdentifier> ctxIDList = this.idS.getLinkedAttributes(identity);
			for (CtxIdentifier ctxID : ctxIDList){
				for (ResponseItem item : agreement.getRequestedItems()){
					if (item.getRequestItem().getResource().getDataType().equalsIgnoreCase(ctxID.getType())){
						item.getRequestItem().getResource().setDataIdUri(ctxID.getUri());
					}
				}
			}
			

			
			
			return identity;
		}
	}


	private String getMessage(RequestorBean requestor) {
		if (requestor instanceof RequestorCisBean) {
			return "Please select one of the following identities to join CIS: "
					+ ((RequestorCisBean) requestor).getCisRequestorId();
		} else if (requestor instanceof RequestorServiceBean) {
			return "Please select one of the following identities to use service: "
					+ ((RequestorServiceBean) requestor)
					.getRequestorServiceId();
		} else {
			return "Please select one of the following identities to interact with: "
					+ requestor.getRequestorId();
		}
	}

	public void setFinalIdentity(ResponsePolicy policy, RequestorBean requestor) {
		try {


			// this.privacyCallback.setInitialAgreement(agreement);
			
			Agreement agreement = new Agreement();
			
			agreement.setRequestedItems(policy.getResponseItems());
			agreement.setRequestor(policy.getRequestor());
			List<IIdentityOption> idOptions = this.idS.processIdentityContext(agreement);
			IIdentity selectedIdentity = this.selectIdentity(idOptions,	agreement);
			agreement.setUserIdentity(selectedIdentity.getBareJid());
			this.logging.debug("Identity selected: "+ selectedIdentity.getJid());
			this.agreements.put(policy.getRequestor(), agreement);
			
			//Agreement agreement = this.agreements.get(requestor);
			agreement.setUserIdentity(selectedIdentity.getJid());
			// agreement.setUserPublicDPI(this.IDM.getPublicDigitalPersonalIdentifier());
			AgreementFinaliser finaliser = new AgreementFinaliser();
			byte[] signature = finaliser.signAgreement(agreement);
			Key publicKey = finaliser.getPublicKey();
			AgreementEnvelope envelope = new AgreementEnvelope();
			envelope.setAgreement(AgreementUtils.copyOf(agreement));

			envelope.setPublicKey(SerialisationHelper.serialise(publicKey));
			envelope.setSignature(signature);
			this.logging.debug("Sending agreement to other party");
			Future<Boolean> ack = this.negotiationAgentRemote
					.acknowledgeAgreement(envelope);
			this.logging
			.debug("Received acknowledgement of agreement from other party");
			if (null == ack) {
				this.userFeedback
				.showNotification("Negotiation error: Did not receive acknowledgement for receiving Negotiation Agreement from service or CIS");
				// JOptionPane.showMessageDialog(null, "ack is null");
			}
			// TODO: is this line needed? shouldn't this be called by the
			// remoteComm when it receives the result of the
			// acknowledgeAgreement(envelope) method?
			this.acknowledgeAgreement(requestor, envelope, ack.get());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void acknowledgeAgreement(RequestorBean requestor,
			AgreementEnvelope envelope, boolean b) {
		if (b) {
			this.logging
			.debug("Acknowledged Agreement - creating access control objects");
			try {
				// TODO: uncomment lines

				this.policyAgreementMgr.updateAgreement(RequestorUtils
						.toRequestor((envelope.getAgreement().getRequestor()),
								this.idm), AgreementEnvelopeUtils
								.toAgreementEnvelope(envelope, this.idm));

				this.logging.debug("Agreement stored");
				List<ResponseItem> requests = envelope.getAgreement()
						.getRequestedItems();
				for (ResponseItem responseItem : requests) {
					privacyDataManager.updatePermission(
							requestor, 
							responseItem);
				}
				this.logging.debug("Permissions updated");
				InternalEvent event = this
						.createSuccessfulNegotiationEvent(envelope);
				try {
					this.logging.debug("Sending successful negotiation event");
					this.eventMgr.publishInternalEvent(event);
					this.logging.debug("Successful negotiation event sent");
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.logging.debug("Unable to post " + event.geteventType()
							+ " event");
				}
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.logging.debug("Provider DID NOT Acknowledge Agreement");
			InternalEvent event = this.createFailedNegotiationEvent();
			try {
				this.logging.debug("Sending failed negotiation event");
				this.eventMgr.publishInternalEvent(event);
				this.logging.debug("Failed negotiation event sent");
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.logging.debug("Unable to post " + event.geteventType()
						+ " event");
			}
		}

	}

	private void startNegotiation() {

		RequestorBean requestor = details.getRequestor();
		this.logging.debug("Starting negotiation. NegotiationID: "
				+ details.getNegotiationID() + " with: "
				+ RequestorUtils.toString(requestor));
		try {
			if (this.negotiationAgentRemote == null) {
				this.logging.debug("negAgentRemote is NULL");
				//JOptionPane.showMessageDialog(null, "NegotiationAgentRemote is null");
			}
			RequestPolicy requestorPolicy = this.negotiationAgentRemote.getPolicy(requestor).get();

			//JOptionPane.showMessageDialog(null, "Got requestor policy, size of items: "+requestorPolicy.getRequestItems().size());
			this.receiveProviderPolicy(requestorPolicy);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 * 
	 * HELPER METHODS BELOW
	 */

	private List<String> dataTypesExist(RequestPolicy policy) {
		List<String> notFoundContextTypes = new ArrayList<String>();
		List<RequestItem> items = policy.getRequestItems();
		for (RequestItem item : items) {
			if (!item.isOptional()) {
				String dataType = item.getResource().getDataType();

				DataIdentifierScheme scheme = item.getResource().getScheme();
				if (scheme.equals(DataIdentifierScheme.CONTEXT)) {
					DataHelper helper = new DataHelper(ctxBroker, userIdentity);
					if (!helper.dataTypeExists(item)) {
						notFoundContextTypes.add(dataType);
					}
				} else if (scheme.equals(DataIdentifierScheme.ACTIVITY)) {
					// TODO
				} else if (scheme.equals(DataIdentifierScheme.CIS)) {
					// TODO
				} else if (scheme.equals(DataIdentifierScheme.CSS)) {
					// TODO
				} else if (scheme.equals(DataIdentifierScheme.DEVICE)) {
					// TODO
				}
			}
		}
		return notFoundContextTypes;
	}

	private InternalEvent createFailedNegotiationEvent() {
		FailedNegotiationEvent event = new FailedNegotiationEvent(details);
		InternalEvent iEvent = new InternalEvent(
				EventTypes.FAILED_NEGOTIATION_EVENT, "",
				INegotiationClient.class.getName(), event);
		this.logging.debug("Failed negotiation Event: \n" + "EventName: "
				+ iEvent.geteventName() + "\nEventSource:"
				+ iEvent.geteventSource() + "\nEventType"
				+ iEvent.geteventType() + "\nEventInfo:"
				+ iEvent.geteventInfo());
		return iEvent;
	}

	private InternalEvent createSuccessfulNegotiationEvent(
			AgreementEnvelope envelope) throws InvalidFormatException {
		PPNegotiationEvent event = new PPNegotiationEvent(
				envelope.getAgreement(),
				NegotiationStatus.SUCCESSFUL, details);
		InternalEvent iEvent = new InternalEvent(
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT, "",
				INegotiationClient.class.getName(), event);
		this.logging.debug("Successfull negotiation Event: \n" + "EventName: "
				+ iEvent.geteventName() + "\nEventSource:"
				+ iEvent.geteventSource() + "\nEventType"
				+ iEvent.geteventType() + "\nEventInfo:"
				+ iEvent.geteventInfo());
		return iEvent;
	}

	private ResponsePolicy findMyResponsePolicy(ResponsePolicy providerPolicy) {
		RequestorBean requestor = providerPolicy.getRequestor();
		if (this.myPolicies.containsKey(requestor)) {
			return this.myPolicies.get(requestor);
		}

		Enumeration<RequestorBean> en = this.myPolicies.keys();

		while (en.hasMoreElements()) {
			RequestorBean provider = en.nextElement();
			if ((provider instanceof RequestorServiceBean)
					&& (requestor instanceof RequestorServiceBean)) {
				if (provider.getRequestorId()
						.equals(requestor.getRequestorId())) {
					ServiceResourceIdentifier serviceID = ((RequestorServiceBean) provider)
							.getRequestorServiceId();
					if (serviceID != null) {
						if (ServiceModelUtils.compare(serviceID,
								((RequestorServiceBean) requestor)
								.getRequestorServiceId())) {
							return this.myPolicies.get(provider);
						}
					}
				}
			} else if ((provider instanceof RequestorCisBean)
					&& (requestor instanceof RequestorCisBean)) {
				if (provider.getRequestorId()
						.equals(requestor.getRequestorId())) {
					String cisId = ((RequestorCisBean) provider)
							.getCisRequestorId();
					if (cisId != null) {
						if (cisId.equals(((RequestorCisBean) requestor)
								.getCisRequestorId())) {
							return this.myPolicies.get(provider);
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public void startPrivacyPolicyNegotiation(NegotiationDetailsBean details,
			RequestPolicy policy) {
		this.details = details;
		this.logging.debug("Starting new negotiation (id:"
				+ details.getNegotiationID() + ") with: "
				+ details.getRequestor().toString());
		if (policy == null) {
			this.logging
			.debug("RequestPolicy not provided, retrieving privacy policy from provider");
			this.startNegotiation();
		} else {
			this.logging.debug("RequestPolicy provided, processing policy");
			this.receiveProviderPolicy(policy);

		}

	}

}
