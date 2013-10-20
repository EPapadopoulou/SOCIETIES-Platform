/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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

package org.societies.orchestration.cpa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.api.internal.orchestration.IDataCollectorSubscriber;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * Driver code for the CPA process of analysing CIS activity, 
 * and trigger suggestions for new CISes if applicable.
 * 
 * @author Bjørn Magnus Mathisen, based on the work by Fraser Blackmun
 * @version 0
 * 
 */

public class CPA implements IDataCollectorSubscriber, Runnable
{
    protected static Logger LOG = LoggerFactory.getLogger(CPA.class);
	private CPACreationPatterns cpaCreationPatterns;
    private ICisDataCollector collector;
	private Date lastTemporaryCheck;
    private String cisId;
    private List<IActivity> newActivities;
    private List<CtxChangeEvent> newContext;

    public CPA(ICisDataCollector collector, String cisId){
        this.collector = collector; this.cisId = cisId;
        init();
    }

	private void process() {
        List<IActivity> tmpNewActivities = null;
        synchronized (newActivities){
            tmpNewActivities = CPA.deepCopyActivities(newActivities);
            newActivities.clear();
        }
        cpaCreationPatterns.analyze(tmpNewActivities);

	}
    public static List<IActivity> deepCopyActivities(List<IActivity> inp){
        List<IActivity> ret = new ArrayList<IActivity>();
        IActivity tmpAct = null;
        for(IActivity act : inp){
            tmpAct = new Activity(act);
            ret.add(tmpAct);
        }
        return ret;
    }
    @Override
    public void receiveNewData(List<?> newData) {
        if(newData.get(0) instanceof IActivity){
           List<IActivity> tmpActList = (List<IActivity>)newData;
           this.newActivities.addAll(tmpActList);
        }else if(newData.get(0) instanceof CtxChangeEvent){
           List<CtxChangeEvent> tmpCtxList = (List<CtxChangeEvent>)newData;
           this.newContext.addAll(tmpCtxList);
        }


    }
    public void receiveNewData(Object newData){
        if(newData instanceof  IActivity)
            this.newActivities.add((IActivity)newData);
    }
    public ICisDataCollector getCollector() {
        return collector;
    }

    public void setCollector(ICisDataCollector collector) {
        this.collector = collector;
    }
    public List<IActivity> safeCast(List<?> inp){
        List<IActivity> ret = new ArrayList<IActivity>();
        try{
            List<IActivity> castTry = (List<IActivity>) inp;
            ret.addAll(castTry);
        } catch (ClassCastException e){

        }
        return ret;
    }
    @Override
    public void run() {
        if(this.collector != null)
            newActivities.addAll(safeCast(this.collector.subscribe(this.cisId,this)));
        while (true) {
            try {
                Date date = new Date();
                if (date.getTime() >= (lastTemporaryCheck.getTime() + (1000 * 10))) {
                    process();
                    lastTemporaryCheck.setTime(date.getTime());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

//    @Override
//    public void receiveResult(Activityfeed activityFeedObject) {
//        String lastTimeStr = Long.toString(lastTime);
//        String nowStr = Long.toString(System.currentTimeMillis());
//        = new ArrayList<IActivity>();
//        System.out.println("icis.getActivityFeed(): "+cises.get(0).getActivityFeed());
//        for(ICisOwned icis : cises){
//            IActivityFeedCallback c = new ActivityFeedCallback();
//            icis.getActivityFeed().getActivities(lastTimeStr+" "+nowStr,null)
//            actDiff.addAll(); //getting the diff.
//        }
//    }


    
    public CPACreationPatterns getCPACreationPatterns() {
    	return cpaCreationPatterns;
    }
    
    public void setCPACreationPatterns(CPACreationPatterns cpaCreationPatterns) {
    	this.cpaCreationPatterns = cpaCreationPatterns;
    }
    public void init(){
        this.newActivities = new ArrayList<IActivity>();
        this.lastTemporaryCheck = new Date(0);
        this.cpaCreationPatterns = new CPACreationPatterns();
        this.cpaCreationPatterns.init();
        LOG.info("new CPA started for CIS "+this.cisId);
    }
/*    public List<String> getTrends(int n){
        //ArrayList<String> ret = new ArrayList<String>();
        //this.cpaCreationPatterns.getGraph().getTrends();
        //return ret;
        return cpaCreationPatterns.getGraph().topTrends(n);
    }*/
    
}