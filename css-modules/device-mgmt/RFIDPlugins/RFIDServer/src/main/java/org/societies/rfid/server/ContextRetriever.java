/**
 * 
 */
package org.societies.rfid.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.css.devicemgmt.rfid.RfidReader;
import org.societies.api.css.devicemgmt.rfid.RfidWakeupUnit;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * @author Eliza
 *
 */
public class ContextRetriever {

	private static final String TAG_TO_IDENTITY = "tagToIdentity";
	private static final String TAG_TO_PASSWORD = "tagToPassword";
	private static final String TAG_TO_SYMLOC = "tagToSymloc";
	private static final String TAG_TO_TIME = "tagToTime";
	private static final String RFID_WAKEUP_UNITS = "rfidWakeupUnits";
	private static final String RFID_SERVER_ENTITY = "RFID_SERVER_ENTITY";
	private Hashtable<String, String> tagToPassword;
	private Hashtable<String, String> tagToIdentity;
	private Hashtable<String, String> tagToSymloc;
	private Hashtable<String, String> tagToTime;
	private ArrayList<RfidReader> rfidReaders;
	private ArrayList<RfidWakeupUnit> rfidWakeupUnits;



	private ICtxBroker ctxBroker;
	private IIdentity serverIdentity;

	public ContextRetriever(ICtxBroker ctxBroker, IIdentity serverIdentity){
		this.ctxBroker = ctxBroker;
		this.serverIdentity = serverIdentity;
		this.tagToPassword = new Hashtable<String, String>();
		this.tagToIdentity = new Hashtable<String, String>();
		this.tagToSymloc = new Hashtable<String, String>();
		this.tagToTime = new Hashtable<String, String>();
		this.rfidReaders = new ArrayList<RfidReader>();
		this.rfidWakeupUnits = new ArrayList<RfidWakeupUnit>();
		try {
			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				CtxEntity ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
				Set<CtxAttribute> tagToPassAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
				if (tagToPassAttributes.size()>0){
					this.tagToPassword = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToPassAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}
				Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
				if (tagToIdAttributes.size()>0){
					this.tagToIdentity = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToIdAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}
				Set<CtxAttribute> tagToSymlocAttributes = ctxEntity.getAttributes(TAG_TO_SYMLOC);
				if (tagToSymlocAttributes.size()>0){
					this.tagToSymloc = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToSymlocAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}
				Set<CtxAttribute> tagToTimeAttributes = ctxEntity.getAttributes(TAG_TO_TIME);
				if (tagToTimeAttributes.size()>0){
					this.tagToTime = (Hashtable<String, String>) SerialisationHelper.deserialise(tagToTimeAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}
				Set<CtxAttribute> rfidWakeupUnitAttributes = ctxEntity.getAttributes(RFID_WAKEUP_UNITS);
				if (rfidWakeupUnitAttributes.size()>0){
					this.rfidWakeupUnits = (ArrayList<RfidWakeupUnit>) SerialisationHelper.deserialise(rfidWakeupUnitAttributes.iterator().next().getBinaryValue(), this.getClass().getClassLoader());
				}

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Hashtable<String, String> getTagToIdentity() {
		return tagToIdentity;
	}

	public void setTagToIdentity(Hashtable<String, String> tagToIdentity) {
		this.tagToIdentity = tagToIdentity;
	}

	public Hashtable<String, String> getTagToPassword() {
		return tagToPassword;
	}

	public void setTagToPassword(Hashtable<String, String> tagToPassword) {
		this.tagToPassword = tagToPassword;
	}

	public Hashtable<String, String> getTagToSymloc() {
		return tagToSymloc;
	}

	public void setTagToSymloc(Hashtable<String, String> tagToSymloc) {
		this.tagToSymloc = tagToSymloc;
	}

	public Hashtable<String, String> getTagToTime() {
		return tagToTime;
	}

	public void setTagToTime(Hashtable<String, String> tagToTime) {
		this.tagToTime = tagToTime;
	}
	public ArrayList<RfidReader> getRfidReaders() {
		return rfidReaders;
	}

	public void setRfidReaders(ArrayList<RfidReader> rfidReaders) {
		this.rfidReaders = rfidReaders;
	}

	public ArrayList<RfidWakeupUnit> getRfidWakeupUnits() {
		return rfidWakeupUnits;
	}

	public void setRfidWakeupUnits(ArrayList<RfidWakeupUnit> rfidWakeupUnits) {
		this.rfidWakeupUnits = rfidWakeupUnits;
	}

	public void updateRFIDWakeupContext() {
		try {
			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			CtxEntity ctxEntity;
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
			}else{
				ctxEntity = this.ctxBroker.createEntity(serverIdentity, RFID_SERVER_ENTITY).get();
			}		

			Set<CtxAttribute> rfidWakeupUnitAttributes = ctxEntity.getAttributes(RFID_WAKEUP_UNITS);
			if (rfidWakeupUnitAttributes.size()==0){
				CtxAttribute rfidWakeupUnitAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), RFID_WAKEUP_UNITS).get();
				this.ctxBroker.updateAttribute(rfidWakeupUnitAttribute.getId(), SerialisationHelper.serialise(rfidWakeupUnits));


			}else{
				CtxAttribute rfidWakeupUnitAttribute = rfidWakeupUnitAttributes.iterator().next();
				this.ctxBroker.updateAttribute(rfidWakeupUnitAttribute.getId(), SerialisationHelper.serialise(rfidWakeupUnits));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateRFIDTagContext(){
		try {

			List<CtxIdentifier> list = ctxBroker.lookup(serverIdentity, CtxModelType.ENTITY, RFID_SERVER_ENTITY).get();
			CtxEntity ctxEntity;
			if (list.size()>0){
				CtxIdentifier ctxEntityId = list.get(0);
				ctxEntity = (CtxEntity) ctxBroker.retrieve(ctxEntityId).get();
			}else{
				ctxEntity = this.ctxBroker.createEntity(serverIdentity, RFID_SERVER_ENTITY).get();
			}			


			Set<CtxAttribute> tagToPasswordAttributes = ctxEntity.getAttributes(TAG_TO_PASSWORD);
			if (tagToPasswordAttributes.size()==0){
				CtxAttribute tagToPasswordAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), TAG_TO_PASSWORD).get();
				this.ctxBroker.updateAttribute(tagToPasswordAttribute.getId(), SerialisationHelper.serialise(tagToPassword)).get();

			}else{
				CtxAttribute tagToPasswordAttribute = tagToPasswordAttributes.iterator().next();
				this.ctxBroker.updateAttribute(tagToPasswordAttribute.getId(), SerialisationHelper.serialise(tagToPassword)).get();


			}



			Set<CtxAttribute> tagToIdAttributes = ctxEntity.getAttributes(TAG_TO_IDENTITY);
			if (tagToIdAttributes.size()==0){
				CtxAttribute tagToIdentityAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), TAG_TO_IDENTITY).get();
				this.ctxBroker.updateAttribute(tagToIdentityAttribute.getId(), SerialisationHelper.serialise(tagToIdentity));


			}else{
				CtxAttribute tagToIdentityAttribute = tagToIdAttributes.iterator().next();
				this.ctxBroker.updateAttribute(tagToIdentityAttribute.getId(), SerialisationHelper.serialise(tagToIdentity));
			}

			Set<CtxAttribute> tagToSymlocAttributes = ctxEntity.getAttributes(TAG_TO_SYMLOC);
			if (tagToSymlocAttributes.size()==0){
				CtxAttribute tagToSymlocAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), TAG_TO_SYMLOC).get();
				this.ctxBroker.updateAttribute(tagToSymlocAttribute.getId(), SerialisationHelper.serialise(tagToSymloc));


			}else{
				CtxAttribute tagToSymlocAttribute = tagToSymlocAttributes.iterator().next();
				this.ctxBroker.updateAttribute(tagToSymlocAttribute.getId(), SerialisationHelper.serialise(tagToSymloc));
			}

			Set<CtxAttribute> tagToTimeAttributes = ctxEntity.getAttributes(TAG_TO_TIME);
			if (tagToTimeAttributes.size()==0){
				CtxAttribute tagToTimeAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), TAG_TO_TIME).get();
				this.ctxBroker.updateAttribute(tagToTimeAttribute.getId(), SerialisationHelper.serialise(tagToSymloc));


			}else{
				CtxAttribute tagToTimeAttribute = tagToTimeAttributes.iterator().next();
				this.ctxBroker.updateAttribute(tagToTimeAttribute.getId(), SerialisationHelper.serialise(tagToTime));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
