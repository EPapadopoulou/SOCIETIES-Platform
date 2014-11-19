package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

public class AttrSelPreferenceCreator {

	private PrivacyPreferenceManager privPrefMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IIdentity userIdentity;

	public AttrSelPreferenceCreator(PrivacyPreferenceManager privPrefMgr){
		this.privPrefMgr = privPrefMgr;
		userIdentity = this.privPrefMgr.getCommsMgr().getIdManager().getThisNetworkNode();
	}
	public void notifyIdentityCreated(InternalEvent event){
		if (event.geteventInfo() instanceof Agreement){
			Agreement agreement = (Agreement) event.geteventInfo();
			try {
				this.logging.debug("Creating attribute selection preferences");
				this.createAttributeSelectionPreferences(agreement);
				this.logging.debug("Created attribute selection preferences");
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TrustException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createAttributeSelectionPreferences(Agreement agreement) throws InvalidFormatException, InterruptedException, ExecutionException, TrustException, MalformedCtxIdentifierException{

		RequestorBean requestor = agreement.getRequestor();

		ITrustBroker trustBroker = privPrefMgr.getTrustBroker();

		TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());

		TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CIS, this.privPrefMgr.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
		TrustQuery trustQuery = new TrustQuery(trustorID);
		trustQuery.setTrusteeId(trusteeID);
		Double trustValue = trustBroker.retrieveTrustValue(trustQuery).get();

		for (ResponseItem responseItem : agreement.getRequestedItems()){


			Resource resource = ResourceUtils.copyOf(responseItem.getRequestItem().getResource());
			CtxAttributeIdentifier ctxID = CtxIDChanger.changeIDOwner(this.userIdentity.getBareJid(),new CtxAttributeIdentifier(resource.getDataIdUri()));

			AttributeSelectionPreferenceDetailsBean detailsSpecific = new AttributeSelectionPreferenceDetailsBean();
			detailsSpecific.setRequestor(RequestorUtils.copyOf(requestor));
			detailsSpecific.setDataType(resource.getDataType());
			this.storeAndMergeAttrSelectionPreferenceRequestorSpecific(detailsSpecific, ConditionUtils.copyOf(responseItem.getRequestItem().getConditions()), ctxID);
			
			
			AttributeSelectionPreferenceDetailsBean detailsRequestorIdOnly = new AttributeSelectionPreferenceDetailsBean();
			RequestorBean requestorOnly = new RequestorBean();
			requestorOnly.setRequestorId(requestor.getRequestorId());
			detailsRequestorIdOnly.setRequestor(requestorOnly);
			detailsRequestorIdOnly.setDataType(resource.getDataType());
			this.storeAndMergeAttrSelectionPreferenceRequestorSpecific(detailsRequestorIdOnly, ConditionUtils.copyOf(responseItem.getRequestItem().getConditions()), ctxID);
			
			AttributeSelectionPreferenceDetailsBean detailsGeneric = new AttributeSelectionPreferenceDetailsBean();
			detailsGeneric.setDataType(resource.getDataType());
			
			this.storeAndMergeAttrSelectionPreference(detailsGeneric, ConditionUtils.copyOf(responseItem.getRequestItem().getConditions()), trustValue, ctxID);
		}
	}


	private void  storeAndMergeAttrSelectionPreference(AttributeSelectionPreferenceDetailsBean details, List<Condition> conditions, Double trustValue, CtxIdentifier ctxID){
		AttributeSelectionPreferenceTreeModel existingModel = privPrefMgr.getAttrSelPreference(details);

		AttributeSelectionOutcome outcome = new AttributeSelectionOutcome(ctxID);
		PrivacyPreference preference = new PrivacyPreference(outcome);


		for (Condition condition : conditions){
			PrivacyCondition privacyCondition = new PrivacyCondition(condition);
			PrivacyPreference conditionPreference = new PrivacyPreference(privacyCondition);
			conditionPreference.add(preference);
			preference = conditionPreference;

		}

		TrustPreferenceCondition condition = new TrustPreferenceCondition(trustValue);
		PrivacyPreference trustPreference = new PrivacyPreference(condition);
		trustPreference.add(preference);


		if (existingModel==null){
			privPrefMgr.storeAttrSelPreference(details, new AttributeSelectionPreferenceTreeModel(details, preference.getRoot()));
		}else{
			PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
			PrivacyPreference mergeAttrSelPreference = merger.mergeAttrSelPreference(details, existingModel.getRootPreference(), preference.getRoot());
			if (mergeAttrSelPreference==null){
				this.logging.debug("Could not merge attribute selection preferences. no changes made");
			}else{
				this.privPrefMgr.storeAttrSelPreference(details, new AttributeSelectionPreferenceTreeModel(details, mergeAttrSelPreference));
			}
		}

	}

	private void  storeAndMergeAttrSelectionPreferenceRequestorSpecific(AttributeSelectionPreferenceDetailsBean details, List<Condition> conditions, CtxIdentifier ctxID){
		AttributeSelectionPreferenceTreeModel existingModel = privPrefMgr.getAttrSelPreference(details);
		AttributeSelectionOutcome outcome = new AttributeSelectionOutcome(ctxID);
		PrivacyPreference preference = new PrivacyPreference(outcome);


		for (Condition condition : conditions){
			PrivacyCondition privacyCondition = new PrivacyCondition(condition);
			PrivacyPreference conditionPreference = new PrivacyPreference(privacyCondition);
			conditionPreference.add(preference);
			preference = conditionPreference;

		}
		if (existingModel==null){
			privPrefMgr.storeAttrSelPreference(details, new AttributeSelectionPreferenceTreeModel(details, preference.getRoot()));
		}else{
			PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
			PrivacyPreference mergeAttrSelPreference = merger.mergeAttrSelPreference(details, existingModel.getRootPreference(), preference.getRoot());
			if (mergeAttrSelPreference==null){
				this.logging.debug("Could not merge attribute selection preferences. no changes made");
			}else{
				this.privPrefMgr.storeAttrSelPreference(details, new AttributeSelectionPreferenceTreeModel(details, mergeAttrSelPreference));
			}
		}

	}
}
