/**
z * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
package org.societies.personalization.socialprofiler.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.societies.api.internal.sns.ISocialData;
import org.societies.personalization.socialprofiler.Variables;
import org.societies.personalization.socialprofiler.datamodel.SocialGroup;
import org.societies.personalization.socialprofiler.datamodel.SocialPerson;
import org.societies.personalization.socialprofiler.datamodel.impl.RelTypes;
import org.societies.personalization.socialprofiler.datamodel.impl.SocialPersonImpl;
import org.societies.personalization.socialprofiler.exception.NeoException;





public class ProfilerEngine implements Variables{

	private static final Logger 			logger 							= Logger.getLogger(ProfilerEngine.class);
	private GraphManager					graph;
	private DatabaseConnection 				databaseConnection;
	private ISocialData						socialData;
	
	private List<?> 			friends 	= new ArrayList<Person>();
	private List<?> 			profiles 	= new ArrayList<Person>();
	private List<?>	 			groups 		= new ArrayList<Group>();
	private List<?> 			activities = new ArrayList<ActivityEntry>();
	
	private boolean 			firstTime   = true;
	
	
	private Hashtable<String, ArrayList<String>>  credentials_sn			= new Hashtable<String, ArrayList<String>> ();
	private Hashtable<String, ArrayList<String>>  credentials_sn_auxiliary	= new Hashtable<String, ArrayList<String>> ();
	
	
	public ProfilerEngine(GraphManager graph, DatabaseConnection databaseConnection, ISocialData socialData){
	
		this.graph 					= graph;
		this.databaseConnection 	= databaseConnection;
		this.socialData				= socialData;
		generateCompleteNetwork();
		
	}
	
	
	public ISocialData getSocialData(){
		return this.socialData;
	}

	public void setSocialData(ISocialData socialData){
		this.socialData = socialData;
		
	}
	
	
	/**
	 * returns the service given as parameter to the constructor
	 * @return ServiceImpl
	 */
	 public GraphManager getService() {
		return graph;
	}

	
	/**
	 * returns the databaseConnection given as parameter to the constructor
	 * @return
	 */
	public final DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}
	
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
	
	
	
	
	/**
	 * 
	 * @param option can be 1:FIRST TIME or 2:UPDATE ONLY if option is 1 
	 * then this function also generates info but no updates are 
	 * done if option 2 , the function generates if necessary but 
	 * also updates
	 */
	public void UpdateNetwork(int option){
	
		logger.info("=============================================================");
		logger.info("====           SOCIAL PROFILER UPDATE                   =====");
		logger.info("=============================================================");
		logger.info("=== UPDATING NETWORK , all new users will be added to network");
		logger.info("=== updating or removing if necessary the existing users     "); 
		logger.info("=============================================================");
		
		socialData.updateSocialData();
		// Update data source
		profiles 	= socialData.getSocialProfiles();
		friends  	= socialData.getSocialPeople();
		groups 	 	= socialData.getSocialGroups();
		activities	= socialData.getSocialActivity();
		

		if (!databaseConnection.connectMysql()){
		   logger.error("Cannot proceed with request due to database connection problems.");
		   return;
	   }
		
	
		
		
		logger.debug("=============================================================");
		logger.debug("=== Traversing NEO GRAPH     "); 
		logger.debug("=============================================================");
		
		// ANALIZZO ESISTENTI
		ArrayList<String> list_usersIds = new ArrayList<String>();  // empty LIST
		
		try {
			list_usersIds = graph.getGraphNodesIds(graph.getAllGraphNodes());
		} 
		catch (NeoException e) {
			logger.error("Cannot get graph nodes IDs: " + e.getMessage());
			return;
		}
		
		// If the Graph Node contains at least one node ....
		if (list_usersIds.size()>0){
			
			for (int i=0;i<list_usersIds.size();i++){
				if (list_usersIds.get(i)!=null){
					generateTree(list_usersIds.get(i), null, option);
				}
			}
		}
		
		
		
		databaseConnection.addInfoForCommunityProfile();

		databaseConnection.closeMysql();
		logger.debug("=============================================================");
		logger.debug("====      SOCIAL PROFILER COMPLETED UPDATE              =====");
		logger.debug("=============================================================");
	}
	
	private void generateCompleteNetwork(){
		logger.debug("GENERATING the whole network including isolated clusters and/or nodes");
		graph.createPerson("ROOT");
		
		// creating base user
		String userId = "myself";
		logger.debug("### adding new cluster using user "+userId); 
		generateTree(userId,null,FIRST_TIME); 
		if (!databaseConnection.connectMysql()){
		   logger.error("Cannot proceed with request due to database connection problems.");
		   return;
	   }
		databaseConnection.addInfoForCommunityProfile();
		databaseConnection.closeMysql();
	}
	
	
	/**
	 * Add a new User
	 * @param p
	 */
	public void linkToRoot(SocialPerson p){
		Transaction tx = graph.getNeoService().beginTx();
		try{
			Node startPersonNode	=  ((SocialPersonImpl) p).getUnderlyingNode();
			Node rootNode			=  ((SocialPersonImpl) graph.getPerson("ROOT")).getUnderlyingNode();
			
			startPersonNode.createRelationshipTo(rootNode, RelTypes.TRAVERSER);
			tx.success();
		}
		finally{
			tx.finish();
		}	
	}
	
	
	
	
	public void generateTree(String current_id, String previous_id, int option) {
		
		
		
		logger.debug("=============================================================");
		logger.debug("==== 					GENERATING TREE 					===");
		logger.debug("=============================================================");
		logger.debug("->> current_id : "+current_id+" previous_id: "+previous_id+" opt:"+option);
		logger.debug("=============================================================");

		
		logger.debug("--- checking if current user "+current_id+" exists on neo network");
		
		SocialPerson currentPerson=graph.getPerson(current_id);
		
		if (currentPerson==null){
			
			logger.info("----the current user "+current_id+" doesn't exist on Neo network");
							    
			// friends List but not used any more!
			//List<SocialPerson> list= new ArrayList(getSocialData().getSocialPeople());   //serviceXml.friendsGetFacebook(client);
			logger.debug("-->creating user "+current_id+" on Neo network");
			SocialPerson startPerson=graph.createPerson(current_id);
			
			databaseConnection.addUserToDatabase(current_id, startPerson.getName());
			
			logger.debug("REMOVING USER "+current_id);
			credentials_sn.remove(current_id);
			
			linkToRoot(startPerson);
			
			logger.debug("---# initialising user"+current_id+" profile percentages");
			
			// TODO - initialize percentages
			//graph.updatePersonPercentages(current_id,"0", "0", "0", "0","0","0");
			
			logger.debug("---*checking previous user id in order to create relationship");
			
			if (previous_id==null){
				logger.debug("previous user id is null -> no relationship will be created");
			}
			else{
				String nameDescription=current_id+previous_id;
				SocialPerson endPerson=graph.getPerson(previous_id);
				logger.debug("---# Trying to create relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
				graph.createDescription(startPerson, endPerson, current_id, previous_id);
			}
			
			
			
			// ADD GROUP for the USER	
			createGroupsAndCategories(current_id, startPerson, (List<Group>)groups);			 	
			
			// TODO: actually is not used
			//     createFanPagesAndCategories(current_id, startPerson, client);    
			
			// Update USER INTERESTs
			initialiseUserInformation(current_id, startPerson);
			
			generateUserInformation(current_id, (List<Person>)profiles);
			
			initialiseUserProfiles(current_id, (List<Person>)profiles);
			
			
			//// SET WINDOW TIME to get the last Activities
			
			
			//current time- 1 week				
			java.util.TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
			java.util.Date today = new java.util.Date();
			java.sql.Timestamp timestamp=new java.sql.Timestamp(today.getTime());
			long current_time = (timestamp.getTime())/1000;
			//1 week=7 x 24 x 60 x 60=604800
			long week_time=604800;
			long end_date=current_time-week_time;
			long end_date1=end_date*1000;
			Date d_end = new Date(end_date1);
			
			///// ANALIZZARE LE ACTIVITIES
			//generateInitialProfileContent(current_id, activities, d_end); 		//till one week before , then update
								
			for(int i=0;i<friends.size();i++){
				
				String friend= ((Person)friends.get(i)).getId();
				if (friend==null){  
					logger.warn("retrieved a null friends");
				}else{
					logger.debug("friend id "+ friend);
				}
				generateTree(friend, current_id, option);
			}
					
		}
		else{
		
			logger.info("---current user "+current_id+" exists on Neo network");
			if (option==FIRST_TIME){
				SocialPerson startPerson=graph.getPerson(current_id);
				
				
				
				if (previous_id==null){
					logger.debug("previous user is null=> nothing to check - end of this sub-branch");
				}else{
					logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
					SocialPerson endPerson=graph.getPerson(previous_id);
					
					boolean exists= false;  //service.existsRelationship(startPerson, endPerson);
					
					if (exists==false){
						logger.debug("#### the relationship doesn't exist");
						logger.debug("####looking through user friends to determine if a relationship is necessary");
						
						boolean necessary=false;
						ArrayList<String> list= null; //serviceXml.friendsGetFacebook(client);
						for(int i=0;i<list.size();i++){
							String friend=list.get(i);
							if (friend.equals(previous_id)){
								necessary=true;
							}
						}
						if (necessary==true){
							logger.debug("a relationship is necessary and will be created");
							String nameDescription=current_id+previous_id;
							logger.debug("---Creating relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
							graph.createDescription(startPerson, endPerson, current_id, previous_id);	
							
						}else{
							logger.debug("NO relationship is necessary - end of check");
						}
					}else{
						logger.debug("####a relationship was found between the 2 nodes- end of check");
					}
				}
			}
			else if((option==UPDATE_EVERYTHING)		||
					(option==UPDATE_ONLY_STREAM)	||
					(option==UPDATE_STREAM_AND_FANPAGES_AND_GROUPS)	||
					(option==UPDATE_STREAM_AND_USER_INFORMATION)){
			
					logger.debug("---->checking if current user "+current_id+" still exists on the CA platform with valid credentials");
					boolean answer=true;//serviceXml.checkIfUserExists(credentials_sn, "facebook", current_id);
					
					if (answer==true){
							logger.debug("--current user "+current_id+" found on platform");
					
					
							
							ArrayList<String> list1=new ArrayList<String>();//serviceXml.friendsGetFacebook(client);
							logger.debug("@@@verifying that user "+current_id+" credentials are valid and that basic permissions function properly@@@");
							
							if (list1.size()==0) {
									logger.debug("the credentials of user "+current_id+" don't function : Reason : possible invalid session keys");
									logger.debug("removing current id "+current_id+" from neo netowrk , index and from credentials database");
									logger.info("REMOVING USER + DELETING FROM NEO"+current_id);
									credentials_sn.remove(current_id);
									graph.deletePerson(current_id);
						
							}
							else{
								
									logger.debug("the credentials of user "+current_id+" function properly");
									credentials_sn.remove(current_id);
									SocialPerson startPerson=graph.getPerson(current_id);
									if (previous_id==null){
										logger.debug("previous user is null=> nothing to check - end of this sub-branch");
									}else{
										logger.debug("####checking if there is a relationship between current "+current_id+" and previous"+previous_id);
										SocialPerson endPerson=graph.getPerson(previous_id);
										boolean exists= false;//service.existsRelationship(startPerson, endPerson);
							
										if (exists==false){
											logger.debug("#### the relationship doesn't exist");
											logger.debug("####looking through user friends to determine if a relationship is necessary");
											boolean necessary=false;
											ArrayList<String> list=null;//serviceXml.friendsGetFacebook(client);
											for(int i=0;i<list.size();i++){
												String friend=list.get(i);
												if (friend.equals(previous_id)){
													necessary=true;
												}
											}
											if (necessary==true){
												logger.debug("a relationship is necessary and will be created");
												String nameDescription=current_id+previous_id;
												logger.debug("---Creating relationship between "+current_id+" and "+previous_id+" with name "+nameDescription);
												graph.createDescription(startPerson, endPerson, current_id,previous_id);	
											}else{
												logger.debug("NO relationship is necessary - end of check");
											}
										}else{
											logger.debug("####a relationship was found between the 2 nodes- end of check");
										}
									}
						
						/**
						switch (option){
							case UPDATE_EVERYTHING :{
								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, client);	// adding additional groups if necessary
								updateUserInformation(current_id, startPerson, client);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_ONLY_STREAM :{
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_FANPAGES_AND_GROUPS :{
								createFanPagesAndCategories(current_id, startPerson, client); //adding additional fan pages if necessary
								createGroupsAndCategories(current_id, startPerson, client);	// adding additional groups if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							case UPDATE_STREAM_AND_USER_INFORMATION :{
								updateUserInformation(current_id, startPerson, client);//modifying general info if necessary
								updateProfileContent(current_id, startPerson, client);//updating profile content information if necessary
								break;
							}
							default :{
								logger.debug("ERROR , nothing will be updated , the update option introduced doesn't exist");
							}
						}
						**/
						
						logger.debug("REMOVING USER "+current_id);
						credentials_sn.remove(current_id);
					}
				}else{ 
					logger.debug("removing current id"+current_id+" from neo netowrk , index ");
					graph.deletePerson(current_id);
					databaseConnection.deleteUserFromDatabase(current_id);
				}
					
					
					
				
			}
		}	
		logger.info("=============================================================");
		logger.info("====           END of UPDATE                   =====");
		logger.info("=============================================================");
		
	}

	
	/**
	 * Create associtation between USER <==> GROUP
	 * @param current_id
	 * @param startPerson
	 * @param groups
	 */
	public void createGroupsAndCategories(String current_id,  SocialPerson startPerson, List<Group> groups){

		logger.debug(" === [ GROUPS ] followed by user "+current_id);	
					
		ArrayList <String> groups_ids=new ArrayList <String> ();
		
		ArrayList <Long> existent_groups_ids	=  graph.getListOfGroups(current_id);
		ArrayList <Long> remaining_groups_ids	=  graph.convertArrayOfStringToLong(groups_ids);
		
		graph.projectArrays(remaining_groups_ids, existent_groups_ids);
				
		for(int j=0;j<remaining_groups_ids.size();j++){
			
			String groupId=remaining_groups_ids.get(j).toString();
			
			if (groupId!=null){
			
				logger.debug("Group[id] => "+ groupId);
				SocialGroup group	=	graph.linkGroup(startPerson, groupId);
				logger.debug("[ADD] Content to [GROUP]:"+groupId);
				
				
				Group currentGroup 	= findGroup(groupId);
				String type			= currentGroup.getTitle();
				String subType		= currentGroup.getDescription();
				
				graph.updateGroup	(groupId, currentGroup.getId().getGroupId() , type, subType,
									null/*group_data.get(5)*/,null/*group_data.get(1)*/,null/*group_data.get(4)*/
					);
				
				if ((!type.equals(""))&&(type!=null)&&(!subType.equals(""))&&(subType!=null)){
//					logger.debug("checking if type "+type+" and subtype "+subType+" of Group "+groupId+" exists" );
//					group.linkGroupCategoryAndSubCategory(group, type, subType, startPerson);
				}
			}
			else logger.warn(" Group [NULL]");
	
		}
	}

	
	
	public void initialiseUserInformation(String current_id, SocialPerson person){
		
		logger.debug("[INIT] GeneralInfo and Interest for user "+current_id);
		
		logger.debug("[INTERESTS]");
		
		graph.linkInterests(person,current_id+"_Interests" );   
		
		graph.updateInterests(current_id+"_Interests","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","nothing_yet","0");
		
		logger.debug("[GENERAL_INFO]");
		
		graph.linkGeneralInfo(person,current_id+"_GeneralInfo"); 
		graph.updateGeneralInfo(current_id+"_GeneralInfo","nothing_yet","nothing_yet","nothing_yet","nothing_yet", "nothing_yet","nothing_yet","nothing_yet","nothing_yet");
	}

	private Group findGroup(String groupId) {
		for (int i=0; i<groups.size(); i++)
			if (groupId.equals(((Group)groups.get(i)).getId().getGroupId())) return (Group)groups.get(i);	
		return null;
	}
	
	
	public void generateUserInformation(String current_id, List<Person> profiles){
		
		logger.debug("===== [MAKE Basic INFO] GeneralInfo and Interests for user "+current_id);
		
		
		ArrayList <Long> userId=new ArrayList<Long> ();
		userId.add(Long.parseLong(current_id));
		
			Person user = profiles.get(0); // to be improved!!!!
			
			// TODO: Transform List of values into strings!
			graph.updateInterests(user.getName()+"_Interests", 
								  "activities", 
								  "interestList", 
							      "music",
							      "movies", 
							      "books", 
							      "quotations", 
							      user.getAboutMe(),
								  user.getUpdated().toString());
				
			
			
			graph.updateGeneralInfo(user.getName()+"_GeneralInfo", 
									user.getName().getGivenName(), 
									user.getName().getFamilyName(),	
									user.getBirthday().toString(), 
									user.getGender().name(), 
									user.getLivingArrangement().toString(), 
									user.getCurrentLocation().getFormatted(), 
									user.getPoliticalViews(), 
									user.getReligion());
			
		
	}
	
	
	
	public void initialiseUserProfiles(String current_id, List<Person> profiles){
		
		logger.info("@@@@ creating and initialising the user profiles @@@@");
		//		logger.debug(" ---- NarcissismManiac---Profile  ");		
		//		graph.linkNarcissismManiac(person,current_id+"_NarcissismManiac" );
		//		graph.updateNarcissismManiac(current_id+"_NarcissismManiac", "0", "0", "0");
		//		logger.debug(" ---- SuperActiveManiac---Profile  ");
		//		graph.linkSuperActiveManiac(person, current_id+"_SuperActiveManiac");
		//		graph.updateSuperActiveManiac(current_id+"_SuperActiveManiac", "0", "0", "0");
		//		logger.debug(" ---- PhotoManiac---Profile  ");
		//		graph.linkPhotoManiac(person, current_id+"_PhotoManiac");
		//		graph.updatePhotoManiac(current_id+"_PhotoManiac", "0", "0", "0");
		//		logger.debug(" ---- SurfManiac---Profile  ");
		//		graph.linkSurfManiac(person, current_id+"_SurfManiac");
		//		graph.updateSurfManiac(current_id+"_SurfManiac", "0", "0", "0");
		//		logger.debug(" ---- QuizManiac---Profile  ");
		//		graph.linkQuizManiac(person, current_id+"_QuizManiac");
		//		graph.updateQuizManiac(current_id+"_QuizManiac", "0", "0","0");
	}
	
}
