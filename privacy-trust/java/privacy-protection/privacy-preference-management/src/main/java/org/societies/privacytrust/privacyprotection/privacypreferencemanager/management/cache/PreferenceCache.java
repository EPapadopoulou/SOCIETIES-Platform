/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AttributeSelectionPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.attrSel.AttributeSelectionPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PreferenceRetriever;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PreferenceStorer;

/**
 * @author PUMA
 *
 */
public class PreferenceCache {

	private List<PPNCacheEntry> ppnList = new ArrayList<PPNCacheEntry>();
	List<AccCtrlCacheEntry> accCtrlList = new ArrayList<AccCtrlCacheEntry>();
	List<IDSCacheEntry> idsList = new ArrayList<IDSCacheEntry>();
	List<AttrSelCacheEntry> attrSelList = new ArrayList<AttrSelCacheEntry>();
	List<DObfCacheEntry> dobfList = new ArrayList<DObfCacheEntry>();
	private PreferenceStorer storer;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private PrivacyPreferenceManager privPrefMgr;
	private final IIdentity userIdentity;

	public PreferenceCache(PrivacyPreferenceManager privPrefMgr) {
		this.privPrefMgr = privPrefMgr;
		this.userIdentity = privPrefMgr.getIdm().getThisNetworkNode();

		storer = new PreferenceStorer(this.privPrefMgr);
		try {
			List<CtxIdentifier> list = privPrefMgr.getCtxBroker().lookup(this.userIdentity, CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE).get();
			for (CtxIdentifier ctxEntityID : list){
				CtxEntity ctxEntity = (CtxEntity) privPrefMgr.getCtxBroker().retrieve(ctxEntityID).get();

				this.loadPPNPreferences(ctxEntity.getAttributes(CtxTypes.PPN_PREFERENCE));

				this.loadAccCtrlPreferences(ctxEntity.getAttributes(CtxTypes.ACC_CTRL_PREFERENCE));

				this.loadAttrSelPreferences(ctxEntity.getAttributes(CtxTypes.ATTR_SEL_PREFERENCE));

				this.loadIDSPreferences(ctxEntity.getAttributes(CtxTypes.IDS_PREFERENCE));

				this.loadDObfPreferences(ctxEntity.getAttributes(CtxTypes.DOBF_PREFERENCE));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	private void loadPPNPreferences(Set<CtxAttribute> attributes) {
		for (CtxAttribute ctxAttribute : attributes){
			CtxAttributeIdentifier locationCtxID = ctxAttribute.getId();
			try {
				Object obj = SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), getClass().getClassLoader());
				PPNPrivacyPreferenceTreeModel model = PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModel((PPNPrivacyPreferenceTreeModelBean) obj, privPrefMgr.getIdm());
				PPNCacheEntry entry = new PPNCacheEntry(model.getDetails(), model, locationCtxID);
				this.ppnList.add(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	

	private void loadAccCtrlPreferences(Set<CtxAttribute> attributes) {
		for (CtxAttribute ctxAttribute : attributes){
			CtxAttributeIdentifier locationCtxID = ctxAttribute.getId();
			try {
				Object obj = SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), getClass().getClassLoader());
				AccessControlPreferenceTreeModel model = PrivacyPreferenceUtils.toAccCtrlPreferenceTreeModel((AccessControlPreferenceTreeModelBean) obj, privPrefMgr.getIdm());
				AccCtrlCacheEntry entry = new AccCtrlCacheEntry(model.getDetails(), model, locationCtxID);
				this.accCtrlList.add(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	private void loadAttrSelPreferences(Set<CtxAttribute> attributes) {

		for (CtxAttribute ctxAttribute : attributes){
			CtxAttributeIdentifier locationCtxID = ctxAttribute.getId();
			try {
				Object obj = SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), getClass().getClassLoader());
				AttributeSelectionPreferenceTreeModel model = PrivacyPreferenceUtils.toAttSelPreferenceTreeModel((AttributeSelectionPreferenceTreeModelBean) obj, privPrefMgr.getIdm());
				AttrSelCacheEntry entry = new AttrSelCacheEntry(model.getDetails(), model, locationCtxID);
				this.attrSelList.add(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void loadIDSPreferences(Set<CtxAttribute> attributes) {
		for (CtxAttribute ctxAttribute : attributes){
			CtxAttributeIdentifier locationCtxID = ctxAttribute.getId();
			try {
				Object obj = SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), getClass().getClassLoader());
				IDSPrivacyPreferenceTreeModel model = PrivacyPreferenceUtils.toIDSPrivacyPreferenceTreeModel((IDSPrivacyPreferenceTreeModelBean) obj, privPrefMgr.getIdm());
				IDSCacheEntry entry = new IDSCacheEntry(model.getDetails(), model, locationCtxID);
				this.idsList.add(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	private void loadDObfPreferences(Set<CtxAttribute> attributes) {
		for (CtxAttribute ctxAttribute : attributes){
			CtxAttributeIdentifier locationCtxID = ctxAttribute.getId();
			try {
				Object obj = SerialisationHelper.deserialise(ctxAttribute.getBinaryValue(), getClass().getClassLoader());
				DObfPreferenceTreeModel model = PrivacyPreferenceUtils.toDObfPreferenceTreeModel((DObfPrivacyPreferenceTreeModelBean) obj, privPrefMgr.getIdm());
				DObfCacheEntry entry = new DObfCacheEntry(model.getDetails(), model, locationCtxID);
				this.dobfList.add(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	public boolean addPPNPreference(PPNPreferenceDetailsBean details, PPNPrivacyPreferenceTreeModel model){
		if (details==null){
			throw new NullPointerException("Trying to add ppn preference with null details");	
		}
		if (model == null){
			throw new NullPointerException("Trying to add null ppn preference - details:"+details.toString());
		}
		PPNPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModelBean(model);

		synchronized (ppnList) {
			for (PPNCacheEntry entry : ppnList){
				if (entry.equalsDetails(details)){
					entry.setModel(model);
					return storer.storeExisting(entry.getLocationCtxID(), bean);
				}
			}
			//entry not found, adding new entry and storing new attribute in DB.

			try {
				CtxAttributeIdentifier locationCtxID = storer.storeNewPreference(bean, CtxTypes.PPN_PREFERENCE);
				if (locationCtxID == null){
					throw new PrivacyException("Error storing new ppn preference in DB");
				}
				PPNCacheEntry entry = new PPNCacheEntry(details, model, locationCtxID);
				this.ppnList.add(entry);
				return true;

			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean addIDSPreference(IDSPreferenceDetailsBean details, IDSPrivacyPreferenceTreeModel model){
		if (details==null){
			throw new NullPointerException("Trying to add IDS preference with null details");	
		}
		if (model == null){
			throw new NullPointerException("Trying to add null IDS preference - details:"+details.toString());
		}	

		IDSPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toIDSPreferenceTreeModelBean(model);

		synchronized (idsList) {


			for (IDSCacheEntry entry : this.idsList){
				if (entry.equalsDetails(details)){
					entry.setModel(model);
					return storer.storeExisting(entry.getLocationCtxID(), bean);
				}
			}
			//entry not found, adding new entry and storing new attribute in DB.

			try {
				CtxAttributeIdentifier locationCtxID = storer.storeNewPreference(bean, CtxTypes.IDS_PREFERENCE);
				if (locationCtxID == null){
					throw new PrivacyException("Error storing new ids preference in DB");
				}
				IDSCacheEntry entry = new IDSCacheEntry(details, model, locationCtxID);
				this.idsList.add(entry);
				return true;
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean addDObfPreference(DObfPreferenceDetailsBean details, DObfPreferenceTreeModel model){
		if (details==null){
			throw new NullPointerException("Trying to add dobf preference with null details");	
		}
		if (model == null){
			throw new NullPointerException("Trying to add null dobf preference - details:"+details.toString());
		}	

		DObfPrivacyPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toDObfPrivacyPreferenceTreeModelBean(model);

		synchronized(dobfList){
			for (DObfCacheEntry entry : this.dobfList){
				if (entry.equalsDetails(details)){
					entry.setModel(model);
					return storer.storeExisting(entry.getLocationCtxID(), bean);
				}
			}
			//entry not found, adding new entry and storing new attribute in DB.

			try {
				CtxAttributeIdentifier locationCtxID = storer.storeNewPreference(bean, CtxTypes.DOBF_PREFERENCE);
				if (locationCtxID == null){
					throw new PrivacyException("Error storing new dobf preference in DB");
				}

				DObfCacheEntry entry = new DObfCacheEntry(details, model, locationCtxID);
				this.dobfList.add(entry);
				return true;
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean addAccCtrlPreference(AccessControlPreferenceDetailsBean details, AccessControlPreferenceTreeModel model){
		if (details==null){
			throw new NullPointerException("Trying to add accCtrl preference with null details");	
		}
		if (model == null){
			throw new NullPointerException("Trying to add null accCtrl preference - details:"+details.toString());
		}	

		AccessControlPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toAccessControlPreferenceTreeModelBean(model);

		synchronized (accCtrlList) {
			for (AccCtrlCacheEntry entry : this.accCtrlList){
				if (entry.equalsDetails(details)){
					entry.setModel(model);
					return storer.storeExisting(entry.getLocationCtxID(), bean);
				}
			}
			//entry not found, adding new entry and storing new attribute in DB.

			try {
				CtxAttributeIdentifier locationCtxID = storer.storeNewPreference(bean, CtxTypes.ACC_CTRL_PREFERENCE);
				if (locationCtxID==null){
					throw new PrivacyException("Error storing new accCtrl preference in DB");
				}

				AccCtrlCacheEntry entry = new AccCtrlCacheEntry(details, model, locationCtxID);
				this.accCtrlList.add(entry);
				return true;
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}	
		}
	}

	public boolean addAttrSelPreference(AttributeSelectionPreferenceDetailsBean details, AttributeSelectionPreferenceTreeModel model){
		if (details==null){
			throw new NullPointerException("Trying to add attrSel preference with null details");	
		}
		if (model == null){
			throw new NullPointerException("Trying to add null attrSel preference - details:"+details.toString());
		}	
		AttributeSelectionPreferenceTreeModelBean bean = PrivacyPreferenceUtils.toAttributeSelectionPreferenceTreeModelBean(model);

		synchronized (attrSelList) {
			for (AttrSelCacheEntry entry : this.attrSelList){
				if (entry.equalsDetails(details)){
					entry.setModel(model);
					return storer.storeExisting(entry.getLocationCtxID(), bean);
				}
			}
			//entry not found, adding new entry and storing new attribute in DB.

			try {
				CtxAttributeIdentifier locationCtxID = storer.storeNewPreference(bean, CtxTypes.ATTR_SEL_PREFERENCE);
				if (locationCtxID == null){
					throw new PrivacyException("Error storing new attrSel preference in DB.");
				}
				AttrSelCacheEntry entry = new AttrSelCacheEntry(details, model, locationCtxID);
				this.attrSelList.add(entry);
				return true;
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}	
		}
	}

	public PPNPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details){
		synchronized (ppnList) {
			for (PPNCacheEntry entry : this.ppnList){
				this.logging.debug("Comparing "+PrivacyPreferenceUtils.toString(details)+" with "+PrivacyPreferenceUtils.toString(entry.getDetails()));
				if (entry.equalsDetails(details)){
					return entry.getModel();
				}
			}
			this.logging.debug("Could not find PPN preference with details: "+PrivacyPreferenceUtils.toString(details));
			return null;
		}
	}

	public IDSPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetailsBean details){
		synchronized (idsList) {
			for (IDSCacheEntry entry : this.idsList){
				this.logging.debug("Comparing "+PrivacyPreferenceUtils.toString(details)+" with "+PrivacyPreferenceUtils.toString(entry.getDetails()));
				if (entry.equalsDetails(details)){
					return entry.getModel();
				}
			}
			this.logging.debug("Could not find IDS preference with details: "+PrivacyPreferenceUtils.toString(details));
			return null;
		}
	}

	public DObfPreferenceTreeModel getDObfPreference(DObfPreferenceDetailsBean details){
		synchronized (dobfList) {
			for (DObfCacheEntry entry : this.dobfList){
				this.logging.debug("Comparing "+PrivacyPreferenceUtils.toString(details)+" with "+PrivacyPreferenceUtils.toString(entry.getDetails()));
				if (entry.equalsDetails(details)){
					return entry.getModel();
				}
			}
			this.logging.debug("Could not find DOBF preference with details: "+PrivacyPreferenceUtils.toString(details));
			return null;	
		}
	}

	public AccessControlPreferenceTreeModel getAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		synchronized (accCtrlList) {
			for (AccCtrlCacheEntry entry : this.accCtrlList){
				this.logging.debug("Comparing "+PrivacyPreferenceUtils.toString(details)+" with "+PrivacyPreferenceUtils.toString(entry.getDetails()));			
				if (entry.equalsDetails(details)){
					return entry.getModel();
				}
			}
			this.logging.debug("Could not find ACCCTRL preference with details: "+PrivacyPreferenceUtils.toString(details));
			return null;	
		}
	}

	public AttributeSelectionPreferenceTreeModel getAttrSelPreference(AttributeSelectionPreferenceDetailsBean details){
		synchronized (attrSelList) {
			for (AttrSelCacheEntry entry : this.attrSelList){
				this.logging.debug("Comparing "+PrivacyPreferenceUtils.toString(details)+" with "+PrivacyPreferenceUtils.toString(entry.getDetails()));
				if (entry.equalsDetails(details)){
					return entry.getModel();
				}
			}
			this.logging.debug("Could not find ATTRSEL preference with details: "+PrivacyPreferenceUtils.toString(details));
			return null;	
		}
	}

	public boolean removePPNPreference(PPNPreferenceDetailsBean details){
		if (details==null){
			throw new NullPointerException("Trying to remove ppn preference with null details");	
		}
		synchronized (ppnList) {
			for (PPNCacheEntry entry : this.ppnList){
				if (entry.equalsDetails(details)){
					CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
					try {
						CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
						if (ctxModelObject == null){
							return false;
						}
						return ppnList.remove(entry);


					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;	
		}
	}

	public boolean removeIDSPreference(IDSPreferenceDetailsBean details){
		if (details==null){
			throw new NullPointerException("Trying to remove ids preference with null details");	
		}
		synchronized (idsList) {
			for (IDSCacheEntry entry : this.idsList){
				if (entry.equalsDetails(details)){
					CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
					try {
						CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
						if (ctxModelObject == null){
							return false;
						}

						return idsList.remove(entry);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;	
		}
	}

	public boolean removeDObfPreference(DObfPreferenceDetailsBean details){
		if (details==null){
			throw new NullPointerException("Trying to remove dobf preference with null details");	
		}
		synchronized (dobfList) {
			for (DObfCacheEntry entry : this.dobfList){
				if (entry.equalsDetails(details)){
					CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
					try {
						CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
						if (ctxModelObject==null){
							return false;
						}
						return dobfList.remove(entry);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;	
		}
	}

	public boolean removeAccCtrlPreference(AccessControlPreferenceDetailsBean details){
		if (details==null){
			throw new NullPointerException("Trying to remove accCtrl preference with null details");	
		}
		synchronized (accCtrlList) {
			for (AccCtrlCacheEntry entry : this.accCtrlList){
				if (entry.equalsDetails(details)){
					CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
					try {
						CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
						if (ctxModelObject==null){
							return false;
						}
						return accCtrlList.remove(entry);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;	
		}
	}

	public boolean removeAttSelPreference(AttributeSelectionPreferenceDetailsBean details){
		if (details==null){
			throw new NullPointerException("Trying to remove attrSel preference with null details");	
		}
		synchronized (attrSelList) {
			for (AttrSelCacheEntry entry : this.attrSelList){
				if (entry.equalsDetails(details)){
					CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
					try {
						CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
						if (ctxModelObject==null){
							return false;
						}
						return this.attrSelList.remove(entry);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CtxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return false;	
		}
	}

	public List<PPNPreferenceDetailsBean> getPPNPreferenceDetails(){
		List<PPNPreferenceDetailsBean> list = new ArrayList<PPNPreferenceDetailsBean>();
		synchronized (ppnList) {
			for (PPNCacheEntry entry : ppnList){
				list.add(entry.getDetails());
			}
			return list;	
		}
	}

	public List<IDSPreferenceDetailsBean> getIDSPreferenceDetails(){

		List<IDSPreferenceDetailsBean> list = new ArrayList<IDSPreferenceDetailsBean>();
		synchronized (idsList) {
			for (IDSCacheEntry entry : idsList){
				list.add(entry.getDetails());
			}
			return list;	
		}
	}

	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails(){

		List<DObfPreferenceDetailsBean> list = new ArrayList<DObfPreferenceDetailsBean>();

		synchronized (dobfList) {
			for (DObfCacheEntry entry : dobfList){
				list.add(entry.getDetails());
			}
			return list;	
		}
	}

	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails(){

		List<AccessControlPreferenceDetailsBean> list = new ArrayList<AccessControlPreferenceDetailsBean>();
		synchronized (accCtrlList) {
			for (AccCtrlCacheEntry entry : this.accCtrlList){
				list.add(entry.getDetails());
			}
			return list;	
		}
	}

	public List<AttributeSelectionPreferenceDetailsBean> getAttrSelPreferenceDetails(){

		List<AttributeSelectionPreferenceDetailsBean> list = new ArrayList<AttributeSelectionPreferenceDetailsBean>();
		synchronized (attrSelList) {
			for (AttrSelCacheEntry entry : this.attrSelList){
				list.add(entry.getDetails());
			}
			return list;	
		}
	}

	public boolean removePPNPreferences(){
		boolean removed = true;

		synchronized (ppnList) {
			PPNCacheEntry[] entriesArray = new PPNCacheEntry[ppnList.size()];
			ppnList.toArray(entriesArray);
			for (PPNCacheEntry entry : entriesArray){

				CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
				try {
					CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
					if (ctxModelObject == null){
						removed = false;
					}else{
						ppnList.remove(entry);
					}

				} catch (InterruptedException e) {
					removed = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				}
			}

			return removed;	
		}

	}

	public boolean removeIDSPreferences(){
		boolean removed = true;
		synchronized (idsList) {
			IDSCacheEntry[] entriesArray = new IDSCacheEntry[idsList.size()];
			idsList.toArray(entriesArray);
			for (IDSCacheEntry entry : entriesArray){

				CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
				try {
					CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
					if (ctxModelObject == null){
						removed = false;
					}else{
						idsList.remove(entry);
					}

				} catch (InterruptedException e) {
					removed = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				}
			}

			return removed;	
		}
	}

	public boolean removeDObfPreferences(){
		boolean removed = true;
		synchronized (dobfList) {
			DObfCacheEntry[] entriesArray = new DObfCacheEntry[dobfList.size()];
			dobfList.toArray(entriesArray);
			for (DObfCacheEntry entry : entriesArray){

				CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
				try {
					CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
					if (ctxModelObject == null){
						removed = false;
					}else{
						dobfList.remove(entry);
					}

				} catch (InterruptedException e) {
					removed = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				}
			}

			return removed;	
		}
	}

	public boolean removeAccCtrlPreferences(){
		boolean removed = true;
		synchronized (accCtrlList) {
			AccCtrlCacheEntry[] entriesArray = new AccCtrlCacheEntry[accCtrlList.size()];
			accCtrlList.toArray(entriesArray);
			for (AccCtrlCacheEntry entry : entriesArray){

				CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
				try {
					CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
					if (ctxModelObject == null){
						removed = false;
					}else{
						accCtrlList.remove(entry);
					}

				} catch (InterruptedException e) {
					removed = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				}
			}

			return removed;	
		}
	}

	public boolean removeAttSelPreferences(){
		boolean removed = true;
		synchronized (attrSelList) {
			AttrSelCacheEntry[] entriesArray = new AttrSelCacheEntry[attrSelList.size()];
			attrSelList.toArray(entriesArray);
			for (AttrSelCacheEntry entry : entriesArray){

				CtxAttributeIdentifier locationCtxID = entry.getLocationCtxID();
				try {
					CtxModelObject ctxModelObject = privPrefMgr.getCtxBroker().remove(locationCtxID).get();
					if (ctxModelObject == null){
						removed = false;
					}else{
						attrSelList.remove(entry);
					}

				} catch (InterruptedException e) {
					removed = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					removed = false;
				}
			}

			return removed;	
		}
	}
}
