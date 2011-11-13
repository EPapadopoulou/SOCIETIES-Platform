package org.societies.context.broker.api;

import java.util.List;

public interface IUserCtxBrokerCallback {
	
	/**
	 * 
	 * @param c_id
	 * @param reason
	 */
	public void cancel(ContextIdentifier c_id, String reason);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxAssociationCreated(ContextAssociation ctxEntity);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void ctxAttributeCreated(ContextAttribute ctxAttribute);

	/**
	 * 
	 * @param list
	 */
	public void ctxEntitiesLookedup(List<ContextEntityIdentifier> list);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxEntityCreated(ContextEntity ctxEntity);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxIndividualCtxEntityCreated(IndividualContextEntity ctxEntity);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRemoved(ContextModelObject ctxModelObject);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRetrieved(ContextModelObject ctxModelObject);

	/**
	 * 
	 * @param list
	 */
	public void ctxModelObjectsLookedup(List<ContextIdentifier> list);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectUpdated(ContextModelObject ctxModelObject);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(List <ContextAttribute> futCtx);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(ContextAttribute futCtx);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(ContextHistoryAttribute hoc);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(List<ContextHistoryAttribute> hoc);

	/**
	 * 
	 * @param c_id
	 */
	public void ok(ICtxIdentifier c_id);

	/**
	 * 
	 * @param list
	 */
	public void ok_list(List<ICtxIdentifier> list);

	/**
	 * 
	 * @param list
	 */
	public void ok_values(List<Object> list);

	/**
	 * needs further refinement
	 * 
	 * @param results
	 */
	public void similartyResults(List<Object> results);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(ContextModelObject ctxModelObj);
}
