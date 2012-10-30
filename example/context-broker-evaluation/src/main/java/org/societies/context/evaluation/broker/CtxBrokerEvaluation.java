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
package org.societies.context.evaluation.broker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerEvaluation {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerEvaluation.class);

	private static final String XLS_INPUT_FILE = "realityMining.xls";

	/** The Internal Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgrService;


	HashMap<Integer, List<Integer>> data = new HashMap<Integer, List<Integer>>();

	CtxBrokerEvaluation(){

	}

	@Autowired(required=true)
	public CtxBrokerEvaluation(ICtxBroker internalCtxBroker, ICommManager commMgr) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.internalCtxBroker = internalCtxBroker;
		LOG.info("*** CtxBroker service "+this.internalCtxBroker);

		this.commMgrService = commMgr;
		LOG.info("*** commMgrService instantiated "+this.commMgrService);

		try {
			this.readData(XLS_INPUT_FILE);
		} catch (Exception e) {
			LOG.error("Could not read " + XLS_INPUT_FILE 
					+ ": " + e.getLocalizedMessage(), e);
			throw e;
		}

	}

	private void readData(String file) throws Exception {

		//ReadExcel test = new ReadExcel();

		//test.setInputFile("./realityMining.xls");

		this.data = xlsReader(file);

		if (LOG.isInfoEnabled())
			LOG.info("CtxBrokerEvaluation data "+data);	


		// create individual entities
		for( Integer i : data.keySet()){

			if (this.getIndiEntity(i) == null) {
				String identityString = "identity_"+i+"@societies.local";

				IIdentity cssIDx =  this.commMgrService.getIdManager().fromJid(identityString);

				IndividualCtxEntity indiEnt = (IndividualCtxEntity) this.internalCtxBroker.createIndividualEntity(cssIDx, CtxEntityTypes.PERSON).get();
				CtxAttribute attributeEvalID = this.internalCtxBroker.createAttribute(indiEnt.getId(), "evaluationID").get();
				attributeEvalID.setIntegerValue(i);
				this.internalCtxBroker.update(attributeEvalID);
				//System.out.println(indiEnt.getId());

			}
		}
		List<CtxIdentifier> allIndiEntityIDList = this.internalCtxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PERSON).get();
		System.out.println("idList: "+allIndiEntityIDList);
		System.out.println("idList size : "+allIndiEntityIDList.size());

		//individual entities created
		LOG.info("individual entities created ");
		// add friend associations
		IndividualCtxEntity indiEntity = null;

		LOG.info("create associations ");
		for(CtxIdentifier indiEntIdentifier : allIndiEntityIDList){

			indiEntity = (IndividualCtxEntity) this.internalCtxBroker.retrieve(indiEntIdentifier).get();

			CtxAttribute attrID = getEvaluationID(indiEntity);

			if(attrID != null){

				List<Integer> idFriendsList = data.get(attrID.getIntegerValue());

				LOG.info("friends for indiEntity "+indiEntity.getId() +" are :"+idFriendsList);

				Set<CtxAssociationIdentifier> friendsAssocIdSet = indiEntity.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH);
				List<CtxAssociationIdentifier> friendsAssocIdList = new ArrayList<CtxAssociationIdentifier>(friendsAssocIdSet);
				
				CtxAssociation friendsAssoc = null;
				if(friendsAssocIdSet.size() == 0  ){

					friendsAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.IS_FRIENDS_WITH).get();
					LOG.info("friendsAssoc "+friendsAssoc.getId() +" created ");
				} else {
					friendsAssoc =   (CtxAssociation) this.internalCtxBroker.retrieve(friendsAssocIdList.get(0)).get();
				}
					
					
					if(idFriendsList.size() > 0 ){
						friendsAssoc.setParentEntity((CtxEntityIdentifier) indiEntIdentifier);
						for(Integer i : idFriendsList ){
							if(this.getIndiEntity(i) != null){
								IndividualCtxEntity friendIndiEntity = this.getIndiEntity(i);
								friendsAssoc.addChildEntity(friendIndiEntity.getId());
							}
						}
						this.internalCtxBroker.update(friendsAssoc);	
					}
			
				
				// end if assoc


			
			}
		}

	}

	//attribute type "evaluationID" has value "i"
	// for this i retrieve the respective individual entity

	private IndividualCtxEntity getIndiEntity(Integer i){

		IndividualCtxEntity indiEntityResult = null;

		//HashMap<Integer, List<Integer>> data

		try {
			//List<CtxIdentifier> attrEvalIDs = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE,"evaluationID").get();
			List<CtxIdentifier> listIds = this.internalCtxBroker.lookup(CtxModelType.ENTITY,CtxEntityTypes.PERSON).get();

			List<CtxEntityIdentifier> entityCtxId = new ArrayList<CtxEntityIdentifier>();

			for(CtxIdentifier ctxId : listIds){
				entityCtxId.add((CtxEntityIdentifier) ctxId);
			}

			List<CtxEntityIdentifier> entityIdList = internalCtxBroker.lookupEntities(entityCtxId, "evaluationID", i).get();
			if( entityIdList.size() == 1 ) {
				CtxEntityIdentifier indiEntityID = entityIdList.get(0);
				indiEntityResult = (IndividualCtxEntity) this.internalCtxBroker.retrieve(indiEntityID).get();
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

		return indiEntityResult;
	}


	private CtxAttribute getEvaluationID(IndividualCtxEntity indiEntity){

		CtxAttribute attrID = null;

		Set<CtxAttribute> attrsSet = indiEntity.getAttributes("evaluationID");
		List<CtxAttribute> attrListID = new ArrayList<CtxAttribute>(attrsSet);
		if(attrListID.size() > 0  ) attrID = attrListID.get(0);

		return attrID;
	}



	public HashMap<Integer, List<Integer>> xlsReader(String inputFile) throws Exception {

		final HashMap<Integer, List<Integer>> mapOfContextData = new HashMap<Integer, List<Integer>>();

		final InputStream is = this.getClass().getResourceAsStream("/" + inputFile);
		if (is == null)
			throw new FileNotFoundException(inputFile + " (No such file in resources)");
		final Workbook w;
		try {
			w = Workbook.getWorkbook(is);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			Integer key = 0;
			// Loop over column and lines
			// get the labels of the columns

			for (int j = 0; j < sheet.getColumns(); j++) {
				List<Integer> data = new ArrayList<Integer>();
				key=j;

				//for each column store the data
				for (int i = 0; i < sheet.getRows(); i++) { //from the second row and on
					Cell cell = sheet.getCell(j, i);
					//System.out.println("key:"+j);
					//System.out.println("cell column:"+cell.getColumn());
					//System.out.println("cell getContents:"+cell.getContents());
					if (cell.getContents().equals("1") ) {
						data.add(i+1); 
					}

				}
				mapOfContextData.put(key,data);

			}
			if (LOG.isInfoEnabled()) {
				LOG.info("sheet.getColumns():"+sheet.getColumns());
				LOG.info("sheet.getRows():"+sheet.getRows());
			}
		} finally {
			if (is != null)
				is.close();
		}

		return mapOfContextData;
	}
}