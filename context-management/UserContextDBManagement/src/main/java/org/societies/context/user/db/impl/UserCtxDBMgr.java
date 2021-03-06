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
package org.societies.context.user.db.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.user.db.impl.model.CtxAssociationDAO;
import org.societies.context.user.db.impl.model.CtxAttributeDAO;
import org.societies.context.user.db.impl.model.CtxEntityDAO;
import org.societies.context.user.db.impl.model.CtxModelDAOTranslator;
import org.societies.context.user.db.impl.model.CtxModelObjectDAO;
import org.societies.context.user.db.impl.model.CtxQualityDAO;
import org.societies.context.user.db.impl.model.IndividualCtxEntityDAO;
import org.societies.context.user.db.impl.model.UserCtxModelObjectNumberDAO;
import org.societies.context.user.db.impl.model.hibernate.CtxEntityIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link IUserCtxDBMgr} interface.
 * 
 * @author 
 * @since 0.0.1
 */
@Service("userCtxDBMgr")
public class UserCtxDBMgr implements IUserCtxDBMgr {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserCtxDBMgr.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;
	
	private final String privateId;

	@Autowired(required=true)
	UserCtxDBMgr (ICommManager commMgr) {

		LOG.info("{} instantiated", this.getClass());
		
		this.privateId = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		LOG.debug("privateId={}", this.privateId);
	}

	/*
	 * Used for JUnit testing only
	 */
	public UserCtxDBMgr() {

		LOG.info("{} instantiated - fooId", this.getClass());

		// TODO !!!!!! Identity should be instantiated properly
		this.privateId = null;
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#createAssociation(java.lang.String)
	 */
	@Override
	public CtxAssociation createAssociation(String type) throws CtxException {

		if (type == null) {
			throw new NullPointerException("type can't be null");
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxAssociationIdentifier id = new CtxAssociationIdentifier(
				this.privateId,	type, modelObjectNumber);
		final CtxAssociationDAO associationDAO = new CtxAssociationDAO(id);

		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(associationDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new UserCtxDBMgrException("Could not create association of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		if (!associationDAO.getEventTopics().isEmpty()) {
			final CtxChangeEvent event = new CtxChangeEvent(associationDAO.getId());
			final String[] eventTopics = associationDAO.getEventTopics().toArray(new String[0]);
			final CtxEventScope eventScope = CtxEventScope.BROADCAST;
			if (LOG.isDebugEnabled())
				LOG.debug("Sending context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "'");
			if (this.ctxEventMgr != null)
				this.ctxEventMgr.post(event, eventTopics, eventScope);
			else
				LOG.error("Could not send context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "': "
						+ "ICtxEventMgr service is not available");
		}
		
		return (CtxAssociation) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public CtxAttribute createAttribute(final CtxEntityIdentifier scope,
			final String type) throws CtxException {

		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}
		
		final CtxEntityDAO entityDAO;
		try {
			entityDAO = this.retrieve(CtxEntityDAO.class, scope);
		} catch (Exception e) {
			throw new UserCtxDBMgrException("Could not create attribute of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		}
		if (entityDAO == null) {	
			throw new UserCtxDBMgrException("Could not create attribute of type '"
					+ type + "': Scope not found: " + scope);
		}

		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxAttributeIdentifier id =	
				new CtxAttributeIdentifier(scope, type, modelObjectNumber);
		final CtxAttributeDAO attributeDAO = new CtxAttributeDAO(id);
		final CtxQualityDAO qualityDAO = new CtxQualityDAO(id);
		attributeDAO.setQuality(qualityDAO);
		entityDAO.addAttribute(attributeDAO);
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(attributeDAO);
			tx.commit();				
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new UserCtxDBMgrException("Could not create attribute of type '"
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		if (!attributeDAO.getEventTopics().isEmpty()) {
			final CtxChangeEvent event = new CtxChangeEvent(attributeDAO.getId());
			final String[] eventTopics = attributeDAO.getEventTopics().toArray(new String[0]);
			final CtxEventScope eventScope = CtxEventScope.BROADCAST;
			if (LOG.isDebugEnabled())
				LOG.debug("Sending context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "'");
			if (this.ctxEventMgr != null)
				this.ctxEventMgr.post(event, eventTopics, eventScope);
			else
				LOG.error("Could not send context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "': "
						+ "ICtxEventMgr service is not available");
		}

		return (CtxAttribute) this.retrieve(id);
	}

	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#createEntity(java.lang.String)
	 */
	@Override
	public CtxEntity createEntity(String type) throws CtxException {

		if (type == null) {
			throw new NullPointerException("type can't be null");
		}
		
		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxEntityIdentifier id = 
				new CtxEntityIdentifier(this.privateId, type, modelObjectNumber);		
		final CtxEntityDAO entityDAO = new CtxEntityDAO(id);
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(entityDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new UserCtxDBMgrException("Could not create entity of type '" 
					+ "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		if (!entityDAO.getEventTopics().isEmpty()) {
			final CtxChangeEvent event = new CtxChangeEvent(entityDAO.getId());
			final String[] eventTopics = entityDAO.getEventTopics().toArray(new String[0]);
			final CtxEventScope eventScope = CtxEventScope.BROADCAST;
			if (LOG.isDebugEnabled())
				LOG.debug("Sending context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "'");
			if (this.ctxEventMgr != null)
				this.ctxEventMgr.post(event, eventTopics, eventScope);
			else
				LOG.error("Could not send context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "': "
						+ "ICtxEventMgr service is not available");
		}
		
		return (CtxEntity) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#createIndividualEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public IndividualCtxEntity createIndividualEntity(final String ownerId,
			final String type) throws CtxException {

		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}
		if (type == null) {
			throw new NullPointerException("type can't be null");
		}
			
		final Long modelObjectNumber = this.generateNextObjectNumber();
		final CtxEntityIdentifier id = 
				new CtxEntityIdentifier(ownerId, type, modelObjectNumber);
		final IndividualCtxEntityDAO entityDAO = new IndividualCtxEntityDAO(id);

		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(entityDAO);
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new UserCtxDBMgrException("Could not create individual entity of type '" 
					+ type + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		// create IS_MEMBER_OF association for new IndividualCtxEntity
		final CtxAssociation isMemberOfAssoc = this.createAssociation(CtxAssociationTypes.IS_MEMBER_OF);
		isMemberOfAssoc.setParentEntity(id);
		this.update(isMemberOfAssoc);
		
		// create IS_ADMIN_OF association for new IndividualCtxEntity
		final CtxAssociation isAdminOfAssoc = this.createAssociation(CtxAssociationTypes.IS_ADMIN_OF);
		isAdminOfAssoc.setParentEntity(id);
		this.update(isAdminOfAssoc);
		
		if (!entityDAO.getEventTopics().isEmpty()) {
			final CtxChangeEvent event = new CtxChangeEvent(entityDAO.getId());
			final String[] eventTopics = entityDAO.getEventTopics().toArray(new String[0]);
			final CtxEventScope eventScope = CtxEventScope.BROADCAST;
			if (LOG.isDebugEnabled())
				LOG.debug("Sending context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "'");
			if (this.ctxEventMgr != null)
				this.ctxEventMgr.post(event, eventTopics, eventScope);
			else
				LOG.error("Could not send context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "': "
						+ "ICtxEventMgr service is not available");
		}
		
		return (IndividualCtxEntity) this.retrieve(id);
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#retrieveIndividualEntity(java.lang.String)
	 */
	@Override
	public IndividualCtxEntity retrieveIndividualEntity(final String ownerId)
			throws CtxException {

		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}
			
		final IndividualCtxEntity entity;
		
		final Session session = this.sessionFactory.openSession();
		try {
			final Query query = session.getNamedQuery("getIndividualCtxEntityIdByOwnerId");
			query.setParameter("ownerId", ownerId, Hibernate.STRING);
			final CtxEntityIdentifier entityId = (CtxEntityIdentifier) query.uniqueResult();
			if (entityId != null) {
				entity = (IndividualCtxEntity) this.retrieve(entityId);
			} else { 
				entity = null;
			}
        } catch (Exception e) {
        	throw new UserCtxDBMgrException("Could not retrieve individual entity for CSS '"
        			+ ownerId + "': "	+ e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
			
		return entity;
	}

	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#lookup(java.lang.String, java.util.Set)
	 */
	@Override
	public Set<CtxIdentifier> lookup(final String ownerId, final Set<String> types)
			throws CtxException {

		if (ownerId == null) { 
			throw new NullPointerException("ownerId can't be null");
		}
		if (types == null) { 
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}
		
		LOG.debug("lookup: ownerId={}, types={}", ownerId, types);
		
		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();
		
		result.addAll(this.lookup(ownerId, CtxModelType.ENTITY, types));
		result.addAll(this.lookup(ownerId, CtxModelType.ATTRIBUTE, types));
		result.addAll(this.lookup(ownerId, CtxModelType.ASSOCIATION, types));

		LOG.debug("lookup: result={}", result);
		return result;
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#lookup(java.lang.String, org.societies.api.context.model.CtxModelType, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<CtxIdentifier> lookup(final String ownerId, 
			final CtxModelType modelType, final Set<String> types)
					throws CtxException {

		if (ownerId == null) {
			throw new NullPointerException("ownerId can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (types == null) {
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}

		LOG.debug("lookup: ownerId={}, modelType={}, types={}", 
				new Object[] { ownerId, modelType, types });

		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();

		final Session session = sessionFactory.openSession();
		try {
			final Query query;
			switch (modelType) {
			case ENTITY:
				// lookup for entities
				query = session.getNamedQuery("getCtxEntityIdsByOwnerIdAndType");
				break;
			case ATTRIBUTE:
				// lookup for attributes
				query = session.getNamedQuery("getCtxAttributeIdsByOwnerIdAndType");
				break;
			case ASSOCIATION:
				// lookup for associations
				query = session.getNamedQuery("getCtxAssociationIdsByOwnerIdAndType");
				break;
			default:
				throw new IllegalArgumentException("Unsupported context model type: " + modelType);
			}

			for (final String type : types) {
				query.setParameter("ownerId", ownerId, Hibernate.STRING);
				query.setParameter("type", type, Hibernate.STRING);
				result.addAll(query.list());
			}
		} catch (Exception e) {
			throw new UserCtxDBMgrException(e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		LOG.debug("lookup: result={}", result);
		return result;
	}

	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#lookup(CtxEntityIdentifier, CtxModelType, Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<CtxIdentifier> lookup(final CtxEntityIdentifier scope,
			final CtxModelType modelType, final Set<String> types) throws CtxException {

		if (scope == null) {
			throw new NullPointerException("scope can't be null");
		}
		if (modelType == null) {
			throw new NullPointerException("modelType can't be null");
		}
		if (types == null) { 
			throw new NullPointerException("types can't be null");
		}
		if (types.isEmpty()) { 
			throw new IllegalArgumentException("types can't be empty");
		}
		
		LOG.debug("lookup: scope={}, modelType={}, types={}", 
				new Object[] { scope, modelType, types });
		
		final Set<CtxIdentifier> result = new LinkedHashSet<CtxIdentifier>();

		final Session session = sessionFactory.openSession();
		try {
			for (final String type : types) {
				switch (modelType) {
				case ATTRIBUTE:
					// lookup for attributes
					final Query query = session.getNamedQuery("getCtxAttributeIdsByScopeAndType");
					query.setParameter("scope", scope.toString(), Hibernate.STRING);
					query.setParameter("type", type, Hibernate.STRING);
					result.addAll(query.list());
					break;
					
				case ASSOCIATION:
					// lookup for associations
					final Query childQuery = session.getNamedQuery("getCtxAssociationIdsByChildEntityIdAndType");
					childQuery.setParameter("childEntId", scope, Hibernate.custom(CtxEntityIdentifierType.class));
					childQuery.setParameter("type", type, Hibernate.STRING);
					result.addAll(childQuery.list());

					final Query parentQuery = session.getNamedQuery("getCtxAssociationsByParentEntityIdAndType");
					parentQuery.setParameter("parentEntId", scope, Hibernate.custom(CtxEntityIdentifierType.class));
					parentQuery.setParameter("type", type, Hibernate.STRING);
					result.addAll(parentQuery.list());
					break;
					
				default:
					throw new IllegalArgumentException("Unsupported context model type: " + modelType);
				}
			}
		} catch (Exception e) {
			throw new UserCtxDBMgrException(e.getLocalizedMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		LOG.debug("lookup: result={}", result);
		return result;
	}

	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#lookupEntities(java.lang.String, java.lang.String, java.io.Serializable, java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CtxEntityIdentifier> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

		if (entityType == null)
			throw new NullPointerException("entityType can't be null");
		if (attribType == null)
			throw new NullPointerException("attribType can't be null");

		final List<CtxEntityIdentifier> foundList = new ArrayList<CtxEntityIdentifier>();

		final Session session = sessionFactory.openSession();
		final Query query;
		try {

			if (minAttribValue instanceof String && maxAttribValue instanceof String) {

				query = session.getNamedQuery("getCtxEntityIdsByAttrStringValue");
				query.setParameter("entType", entityType, Hibernate.STRING);
				query.setParameter("attrType", attribType, Hibernate.STRING);
				query.setParameter("minAttribValue", (String) minAttribValue, Hibernate.STRING);
				query.setParameter("maxAttribValue", (String) maxAttribValue, Hibernate.STRING);

			} else if (minAttribValue instanceof Integer && maxAttribValue instanceof Integer) {

				query = session.getNamedQuery("getCtxEntityIdsByAttrIntegerValue");
				query.setParameter("entType", entityType, Hibernate.STRING);
				query.setParameter("attrType", attribType, Hibernate.STRING);
				query.setParameter("minAttribValue", (Integer) minAttribValue, Hibernate.INTEGER);
				query.setParameter("maxAttribValue", (Integer) maxAttribValue, Hibernate.INTEGER);

			} else if (minAttribValue instanceof Double && maxAttribValue instanceof Double) {

				query = session.getNamedQuery("getCtxEntityIdsByAttrDoubleValue");
				query.setParameter("entType", entityType, Hibernate.STRING);
				query.setParameter("attrType", attribType, Hibernate.STRING);
				query.setParameter("minAttribValue", (Double) minAttribValue, Hibernate.DOUBLE);
				query.setParameter("maxAttribValue", (Double) maxAttribValue, Hibernate.DOUBLE);

			} else if (minAttribValue instanceof byte[]) {

				query = session.getNamedQuery("getCtxEntityIdsByAttrBinaryValue"); 
				query.setParameter("entType", entityType, Hibernate.STRING);
				query.setParameter("attrType", attribType, Hibernate.STRING);
				query.setParameter("minAttribValue", (byte[]) minAttribValue, Hibernate.BINARY);
				
			} else {
				throw new UserCtxDBMgrException("Unsupported attribute value types: "
						+ "minAttribValue=" + minAttribValue.getClass().getName()
						+ ", maxAttribValue=" + maxAttribValue.getClass().getName());
			}

			foundList.addAll(query.list());
		} catch (Exception e) {
			throw new UserCtxDBMgrException("Could not lookup context entities of type '" 
        			+ entityType + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}

		return foundList;
	}

	@Override
	public CtxModelObject remove(CtxIdentifier id) throws CtxException {
		
		if (id == null)
			throw new NullPointerException("id can't be null");
		
		final CtxModelObject result = this.retrieve(id);
		if (result == null)
			return null;
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		CtxModelObjectDAO dao = CtxModelDAOTranslator.getInstance().fromCtxModelObject(result);
		try{
			tx = session.beginTransaction();
			session.delete(dao);
			session.flush();
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw new UserCtxDBMgrException("Could not remove entity '" + id 
					+ "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
		
		if (!dao.getEventTopics().isEmpty()) {
			final CtxChangeEvent event = new CtxChangeEvent(dao.getId());
			final String[] eventTopics = dao.getEventTopics().toArray(new String[0]);
			final CtxEventScope eventScope = CtxEventScope.BROADCAST;
			if (LOG.isDebugEnabled())
				LOG.debug("Sending context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "'");
			if (this.ctxEventMgr != null)
				this.ctxEventMgr.post(event, eventTopics, eventScope);
			else
				LOG.error("Could not send context change event " + event
						+ " to topics '" + Arrays.toString(eventTopics) 
						+ "' with scope '" + eventScope + "': "
						+ "ICtxEventMgr service is not available");
		}
		
		return result;
	}

	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#retrieve(org.societies.api.context.model.CtxIdentifier)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CtxModelObject retrieve(CtxIdentifier id) throws CtxException {

		final CtxModelObject result;
		final CtxModelObjectDAO dao;

        try {
        	switch (id.getModelType()) {
        	
        	case ENTITY:            	
            	dao = this.retrieve(CtxEntityDAO.class, id);
            	if (dao == null)
            		break;
            	final Session session = this.sessionFactory.openSession();
            	Query query;
            	// CtxAssociations where this entity is member of
            	final Set<CtxAssociationIdentifier> associationIds = 
    					new HashSet<CtxAssociationIdentifier>();
            	try {
            		if (dao instanceof IndividualCtxEntityDAO) {

            			final Set<CtxEntityIdentifier> communityIds = 
            					new HashSet<CtxEntityIdentifier>();
            			// Retrieve CtxAssociations where this entity is parent
            			query = session.getNamedQuery("getCtxAssociationsByParentEntityId");
            			query.setParameter("parentEntId", ((CtxEntityDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			final List<CtxAssociationDAO> associations = query.list();
            			for (final CtxAssociationDAO association : associations) {
            				associationIds.add(association.getId());
            				if (CtxAssociationTypes.IS_MEMBER_OF.equals(association.getId().getType()))
            					communityIds.addAll(association.getChildEntities());
            			}
            			((IndividualCtxEntityDAO) dao).setCommunities(communityIds);
            			
            			// Retrieve CtxAssociationIds where this entity is child
            			query = session.getNamedQuery("getCtxAssociationIdsByChildEntityId");
            			query.setParameter("childEntId", ((CtxEntityDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			associationIds.addAll(query.list());
            			
            		} else if (dao instanceof CtxEntityDAO) {

            			// Retrieve CtxAssociationIds where this entity is parent
            			query = session.getNamedQuery("getCtxAssociationIdsByParentEntityId");
            			query.setParameter("parentEntId", ((CtxEntityDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			associationIds.addAll(query.list());
            			// Retrieve CtxAssociationIds where this entity is child
            			query = session.getNamedQuery("getCtxAssociationIdsByChildEntityId");
            			query.setParameter("childEntId", ((CtxEntityDAO) dao).getId(), 
            					Hibernate.custom(CtxEntityIdentifierType.class));
            			associationIds.addAll(query.list());
            		}
            		
            		((CtxEntityDAO) dao).setAssociations(associationIds);
            		
            	} finally {
            		if (session != null)
            			session.close();
            	}
            	break;
            	
        	case ATTRIBUTE:	
        		dao = this.retrieve(CtxAttributeDAO.class, id);
        		break;
        		
        	case ASSOCIATION:
        		dao = this.retrieve(CtxAssociationDAO.class, id);
        		break;
        	
        	default:
        		throw new UserCtxDBMgrException("Could not retrieve '"
    					+ id + "': Unsupported CtxModelType: " + id.getModelType());
            }
        	
        	if (dao == null)
        		return null;
        	
        	result = CtxModelDAOTranslator.getInstance().fromCtxModelObjectDAO(dao);
         
        } catch (Exception e) {
			throw new UserCtxDBMgrException("Could not retrieve '"
					+ id + "': " + e.getLocalizedMessage(), e);
		}

		return result;
	}
	
	/*
	 * @see org.societies.context.api.user.db.IUserCtxDBMgr#update(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public CtxModelObject update(CtxModelObject modelObject) throws CtxException {

		if (modelObject == null) 
			throw new NullPointerException("modelObject can't be null");
		
		final Session session = sessionFactory.openSession();
		Transaction tx = null;
		CtxModelObjectDAO modelObjectDAO = CtxModelDAOTranslator.getInstance().fromCtxModelObject(modelObject);
		try {	
			tx = session.beginTransaction();
			modelObjectDAO = (CtxModelObjectDAO) session.merge(modelObjectDAO);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw new UserCtxDBMgrException("Could not update '"
					+ modelObject + "': " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
		
		final CtxChangeEvent event = new CtxChangeEvent(modelObjectDAO.getId());
		modelObjectDAO.getEventTopics().add(CtxChangeEventTopic.UPDATED);
		final String[] eventTopics = modelObjectDAO.getEventTopics().toArray(new String[0]);
		final CtxEventScope eventScope = CtxEventScope.BROADCAST;
		if (LOG.isDebugEnabled())
			LOG.debug("Sending context change event " + event
					+ " to topics '" + Arrays.toString(eventTopics) 
					+ "' with scope '" + eventScope + "'");
		if (this.ctxEventMgr != null)
			this.ctxEventMgr.post(event, eventTopics, eventScope);
		else
			LOG.error("Could not send context change event " + event
					+ " to topics '" + Arrays.toString(eventTopics) 
					+ "' with scope '" + eventScope + "': "
					+ "ICtxEventMgr service is not available");
		      
		return this.retrieve(modelObject.getId());
	}
	
	@SuppressWarnings("unchecked")
	private <T extends CtxModelObjectDAO> T retrieve(
			final Class<T> modelObjectClass,
			final CtxIdentifier ctxId) throws Exception {
		
		T result = null;
		
		final Session session = sessionFactory.openSession();
		try {
			final Criteria criteria = session.createCriteria(modelObjectClass)
					.add(Restrictions.eq("ctxId", ctxId));
			result = (T) criteria.uniqueResult();
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private <T extends CtxModelObjectDAO> Set<T> retrieve(
			final Class<T> modelObjectClass,
			final String type, final Date startDate,
			final Date endDate) throws Exception {
		
		final Set<T> result = new HashSet<T>();
		
		final Session session = sessionFactory.openSession();
		try {
			final Criteria criteria = session.createCriteria(modelObjectClass);
		
			if (type != null)
				criteria.add(Restrictions.eq("type", type));
		
			if (startDate != null) 
				criteria.add(Restrictions.ge("lastModified", startDate));
		
			if (endDate != null)
				criteria.add(Restrictions.le("lastModified", endDate));
	
			result.addAll(criteria.list());
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}
	
	private Long generateNextObjectNumber() throws UserCtxDBMgrException {
		
		final UserCtxModelObjectNumberDAO objectNumberDAO =
				new UserCtxModelObjectNumberDAO();
		
		final Session session = this.sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(objectNumberDAO);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw new UserCtxDBMgrException(
					"Could not generate next context model object number");
		} finally {
			if (session != null)
				session.close();
		}
		
		return objectNumberDAO.getNextValue();
	}

}