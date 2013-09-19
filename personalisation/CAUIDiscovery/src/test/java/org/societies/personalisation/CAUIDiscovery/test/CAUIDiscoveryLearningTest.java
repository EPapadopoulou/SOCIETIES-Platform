package org.societies.personalisation.CAUIDiscovery.test;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;

import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUIDiscovery.impl.ActionDictObject;
import org.societies.personalisation.CAUIDiscovery.impl.ConstructUIModel;
import org.societies.personalisation.CAUIDiscovery.impl.MockHistoryData;
import org.societies.personalisation.CAUIDiscovery.impl.TransProbCalculator;


public class CAUIDiscoveryLearningTest {


	private static Long nextValue = 0L;

	private CtxEntity operator = null;
	CAUIDiscovery discover = null;
	PredictorTest predictor = null;
	
	Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

	IAction action1 = null;
	
	CAUIDiscoveryLearningTest(){
		discover = new  CAUIDiscovery();
		operator = createOperator();
		predictor =  new PredictorTest();
		predictor.setTaskModelManager();
	}

	private void  startTesting(){

		System.out.println("1. create history");
		createContextHistoryAttributesSet();
		printHistory(this.mapHocData);


		System.out.println("2. Convert History Data");
		List<MockHistoryData> mockData = discover.convertHistoryData(this.mapHocData);

		System.out.println("Converted Data: "+mockData);

		
		
		System.out.println("3. Perform learning");
		HashMap<Integer,LinkedHashMap<List<String>,ActionDictObject>> dictionary = discover.generateTransitionsDictionaryAll(mockData);

		
		
		
/*		
		for(int i=1; i<= dictionary.size(); i++) {
			System.out.println("Print dictionary, step: "+i+" size:"+dictionary.get(i).size());
			printDictionary(dictionary.get(i));
		}
	*/	
		
		
		HashMap<String,List<String>> ctxActionsMap =  discover.assignContextToAction(dictionary.get(1));
		System.out.println("4. assignContextToAction");
		//System.out.println(ctxActionsMap);


		System.out.println("5. Calculate trans probabilites");
		// add context calculation in calcTrans2Prob
		System.out.println("5. step 2 ");

		try {
			TransProbCalculator transProb = new TransProbCalculator();
			LinkedHashMap<List<String>, HashMap<String, Double>> trans2ProbDictionary = transProb.calcTrans2Prob(dictionary.get(2));
			printTransProbDictionary(trans2ProbDictionary);
			System.out.println("5. step 3 ");
			LinkedHashMap<List<String>,HashMap<String,Double>> trans3ProbDictionary = transProb.calcTrans3Prob(dictionary.get(3));
			printTransProbDictionary(trans3ProbDictionary);

			System.out.println("6. Generate UserIntentModelData");
			ConstructUIModel cmodel = new ConstructUIModel(discover.getCauiTaskManager(),null);
			System.out.println("6. discover.getSriMap()" +discover.getSriMap());
			UserIntentModelData modelData = cmodel.constructNewModel(trans2ProbDictionary,ctxActionsMap,discover.getSriMap());

			System.out.println("*********** model created *******"+ modelData.getActionModel());
			for( IUserIntentAction userAction  : modelData.getActionModel().keySet()){
					System.out.println(userAction);
					//System.out.println(userAction.getActionContext());
			}
		
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		
		
		// model discovered
	
	
		// start predictions
		startPredictions();
	
	}
	
	private void startPredictions(){
		
			//List<IUserIntentAction> predictedAction  = predictor.getPrediction(null, action1).get();
			
			//System.out.println("predictedAction "+predictedAction);
	}

	
	private CtxAttributeValueType findAttributeValueType(Serializable value) {
		if (value == null)
			return CtxAttributeValueType.EMPTY;
		else if (value instanceof String)
			return CtxAttributeValueType.STRING;
		else if (value instanceof Integer)
			return CtxAttributeValueType.INTEGER;
		else if (value instanceof Double)
			return CtxAttributeValueType.DOUBLE;
		else if (value instanceof byte[])
			return CtxAttributeValueType.BINARY;
		else
			throw new IllegalArgumentException(value + ": Invalid value type");
	}


	public void printTransProbDictionary (LinkedHashMap<List<String>,HashMap<String,Double>> transProbDictionary){
		System.out.println("printing transition probabilites ");
		//System.out.println ("**** total number of entries:" + transProbDictionary.size());
		for(List<String> action : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(action);
			System.out.println("Action:"+action+ "| target: "+transTargets);
		}
	}

	public void createContextHistoryAttributesSet(){

		//create actions
		//IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("xc.societies.local/NEC_Cobrowse_Service1"));
			serviceId2.setIdentifier(new URI("xc.societies.local/NEC_Cobrowse_Service2"));
			serviceId1.setServiceInstanceIdentifier("cobrowse-webapp");
			serviceId2.setServiceInstanceIdentifier("cobrowse-webapp");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId2, "testService", "volume", "low");
		IAction action3 = new Action(serviceId1, "testService", "volume", "mute");
		IAction actionX = new Action(serviceId1, "testService", "XXXX", "XXXX");
		IAction action4 = new Action(serviceId2, "testService", "colour", "blue");
		IAction action5 = new Action(serviceId2, "testService", "colour", "green");
		IAction actionY = new Action(serviceId1, "testService", "YYYY", "YYYY");
		//System.out.println ("action service ID "+actionY.getServiceID().getServiceInstanceIdentifier());
		for (int i=0; i<4; i++){

			monitorAction(action1,"home","Monday",10);
			monitorAction(action1,"country","Monday",10);
			monitorAction(action2,"office","Tuesday",15);
			monitorAction(action3,"park","Wednesday",2);
			monitorAction(actionX,"park","Wednesday",2);
			monitorAction(actionY,"park","Wednesday",2);
			monitorAction(action4,"park","Wednesday",2);
			monitorAction(action5,"park","Wednesday",2);
			monitorAction(actionY,"park","Wednesday",2);
			monitorAction(actionX,"park","Wednesday",2);
			monitorAction(action1,"home","Tuesday",10);
			monitorAction(action2,"office","Monday",15);
			monitorAction(action3,"zoo","Monday",2);
			monitorAction(actionY,"park","Monday",2);
			monitorAction(actionY,"park","Monday",2);
			monitorAction(action1,"home","Monday",10);
			monitorAction(action2,"office","Monday",15);
			monitorAction(action3,"park","Monday",2);
			monitorAction(actionX,"park","Monday",2);
			monitorAction(action4,"park","Monday",2);
			monitorAction(action5,"park","Monday",2);
		}
	}

	private void monitorAction(IAction action, String location, String dayOfWeek, Integer hourOfDay){

		CtxHistoryAttribute mockPrimaryHocActionAttrX = createMockHocActionAttr(action);
		List<CtxHistoryAttribute> escortingCtxDataX = new ArrayList<CtxHistoryAttribute>();
		CtxHistoryAttribute attrLocationX = createMockHocAttr(CtxAttributeTypes.LOCATION_SYMBOLIC,location);
		
		CtxHistoryAttribute attrDowX = createMockHocAttr(CtxAttributeTypes.DAY_OF_WEEK,dayOfWeek);
		
		CtxHistoryAttribute attrHodX = createMockHocAttr(CtxAttributeTypes.HOUR_OF_DAY,hourOfDay);
		
		escortingCtxDataX.add(attrLocationX);
		escortingCtxDataX.add(attrDowX);
		escortingCtxDataX.add(attrHodX);
		this.mapHocData.put(mockPrimaryHocActionAttrX, escortingCtxDataX);
	}


	private CtxHistoryAttribute createMockHocAttr(String ctxAttrType, Serializable ctxAttrValue){

		CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),ctxAttrType.toString(),getNextValue());
		CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);

		if(ctxAttrValue instanceof String){
			ctxAttr.setStringValue(ctxAttrValue.toString());
		} else if (ctxAttrValue instanceof Double){
			ctxAttr.setDoubleValue((Double)ctxAttrValue);
		}else if (ctxAttrValue instanceof Integer){
			ctxAttr.setIntegerValue((Integer)ctxAttrValue);
		}
		CtxHistoryAttribute ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		return ctxHocAttr;
	}

	private CtxHistoryAttribute createMockHocActionAttr(IAction action){
		CtxHistoryAttribute ctxHocAttr = null;
		try {
			CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),CtxAttributeTypes.LAST_ACTION,getNextValue());
			CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);
			ctxAttr.setBinaryValue(SerialisationHelper.serialise(action));
			ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxHocAttr;
	}

	private void printHistory(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){
		int i = 0;
		for(CtxHistoryAttribute ctxHocAttr :mapHocData.keySet()){

			try {
				IAction action = (IAction)SerialisationHelper.deserialise(ctxHocAttr.getBinaryValue(), this.getClass().getClassLoader());
				List<CtxHistoryAttribute> escortingAttrList = mapHocData.get(ctxHocAttr);
				System.out.println(i+" primary Attr: {"+action.getparameterName() +" "+action.getvalue()+"} escorting: {" +escortingAttrList.get(0).getStringValue()+" "+escortingAttrList.get(1).getStringValue()+" "+escortingAttrList.get(2).getIntegerValue()+"}");
				i++;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private CtxEntity createOperator(){
		CtxEntityIdentifier ctxEntId = new CtxEntityIdentifier("operatorID","person",getNextValue());
		CtxEntity ctxEntity = new CtxEntity(ctxEntId);
		setOperatorEntity(ctxEntity);
		return ctxEntity;
	}

	private void setOperatorEntity(CtxEntity entity){
		operator = entity;
	}


	public static Long getNextValue() {
		return nextValue++;
	}


	public static void main(String[] args) {
		CAUIDiscoveryLearningTest cdt = new CAUIDiscoveryLearningTest();
		cdt.startTesting();
	}


	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		System.out.println ("**** printing dictionary contents *****");
		System.out.println ("**** total number of entries:" + dictionary.size());

		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();

			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.toString());
		}
	}

	// dead code

	/*
	public List<MockHistoryData> createMockHistorySet(){
		List<MockHistoryData>  data = new ArrayList<MockHistoryData>();
		//MockHistoryData(String action, String parameterName, Map<String,String> context){
		Map<String,String> context = new HashMap<String,String>();
		context.put("temperature","hot");
		context.put("SymLoc","free");
		Date date = new Date();
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		data.add(new MockHistoryData("volume","mute",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramA","valueA",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramB","valueB",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramC","valueC",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramX","valueX",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramY","valueY",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramA","valueA",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramB","valueB",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramC","valueC",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramO","valueO",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramP","valueP",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramA","valueA",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramB","valueB",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramC","valueC",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramL","valueL",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramA","valueA",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramB","valueB",context,date,serviceId1.getServiceInstanceIdentifier()));
		data.add(new MockHistoryData("paramC","valueC",context,date,serviceId1.getServiceInstanceIdentifier()));

		List<MockHistoryData> newSet = new ArrayList<MockHistoryData>();

		for(int i=0; i<1; i++){
			newSet.addAll(data);
		}
		return newSet;
	}
	 */



}