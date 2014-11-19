package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache.PreferenceCache;

public class AttrSelPreferenceManager {

	private final static Logger logging = LoggerFactory.getLogger(AttrSelPreferenceManager.class);
	private PrivacyPreferenceManager privPrefMgr;
	private final IIdentity userIdentity;

	public AttrSelPreferenceManager(PrivacyPreferenceManager privPrefMgr){
		this.privPrefMgr = privPrefMgr;
		this.userIdentity = privPrefMgr.getIdm().getThisNetworkNode();
	}

	public List<AttributeSelectionPreferenceDetailsBean> getAttrSelPreferenceDetails() {
		return privPrefMgr.getPrefCache().getAttrSelPreferenceDetails();
	}

	public boolean deleteAttSelPreference(AttributeSelectionPreferenceDetailsBean details) {
		return privPrefMgr.getPrefCache().removeAttSelPreference(details);
	}


	public Hashtable<Resource, CtxIdentifier> evaluateAttributeSelectionPreferences(Agreement agreement){
		logging.debug("evaluateAttributeSelectionPreferences");

		Hashtable<Resource, CtxIdentifier> results = new Hashtable<Resource, CtxIdentifier>();

		List<ResponseItem> responseItems = agreement.getRequestedItems();
		for (ResponseItem respItem : responseItems){
			logging.debug("processing {}", ResourceUtils.toString(respItem.getRequestItem().getResource()));
			AttributeSelectionPreferenceDetailsBean details = new AttributeSelectionPreferenceDetailsBean();
			details.setRequestor(agreement.getRequestor());
			details.setDataType(respItem.getRequestItem().getResource().getDataType());
			AttributeSelectionPreferenceTreeModel attrSelPreference = getAttrSelPreference(details);

			if (attrSelPreference!=null){
				PreferenceEvaluator ppE = new PreferenceEvaluator(privPrefMgr, agreement.getRequestor(), userIdentity);
				CtxIdentifier ctxID = ppE.evaluateAttrPreference(attrSelPreference, respItem.getRequestItem().getConditions());
				logging.debug("evaluate: {} result: {}", attrSelPreference, ctxID);
				if (ctxID!=null){
					results.put(respItem.getRequestItem().getResource(), ctxID);
				}
			}else{
				RequestorBean req = new RequestorBean();
				req.setRequestorId(agreement.getRequestor().getRequestorId());
				details.setRequestor(req);
				attrSelPreference = getAttrSelPreference(details);

				if (attrSelPreference!=null){
					PreferenceEvaluator ppE = new PreferenceEvaluator(privPrefMgr, agreement.getRequestor(), userIdentity);
					CtxIdentifier ctxID = ppE.evaluateAttrPreference(attrSelPreference, respItem.getRequestItem().getConditions());
					logging.debug("evaluate: {} result: {}", attrSelPreference, ctxID);
					if (ctxID!=null){
						results.put(respItem.getRequestItem().getResource(), ctxID);
					}
				}else{
					details.setRequestor(null);
					attrSelPreference = getAttrSelPreference(details);

					if (attrSelPreference!=null){
						PreferenceEvaluator ppE = new PreferenceEvaluator(privPrefMgr, agreement.getRequestor(), userIdentity);
						CtxIdentifier ctxID = ppE.evaluateAttrPreference(attrSelPreference, respItem.getRequestItem().getConditions());
						logging.debug("evaluate: {} result: {}", attrSelPreference, ctxID);
						if (ctxID!=null){
							results.put(respItem.getRequestItem().getResource(), ctxID);
						}
					}
				}
			}
		}
		logging.debug("evaluateAttributeSelectionPreferences result: {}", results);

		return results;
	}


	public boolean storeAttrSelPreference(AttributeSelectionPreferenceDetailsBean details, AttributeSelectionPreferenceTreeModel model) {
		return privPrefMgr.getPrefCache().addAttrSelPreference(details, model);
	}

	public AttributeSelectionPreferenceTreeModel getAttrSelPreference(
			AttributeSelectionPreferenceDetailsBean details) {

		AttributeSelectionPreferenceTreeModel attrSelPreference = privPrefMgr.getPrefCache().getAttrSelPreference(details);
		logging.debug("retrieve {}, result: {}", details, attrSelPreference);
		return attrSelPreference;
	}

	public boolean deleteAttSelPreferences() {
		return privPrefMgr.getPrefCache().removeAttSelPreferences();
	}



}
