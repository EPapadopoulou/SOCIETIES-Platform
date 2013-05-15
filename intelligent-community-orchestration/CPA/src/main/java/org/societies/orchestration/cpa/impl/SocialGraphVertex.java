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

import org.societies.api.cis.orchestration.model.ISocialGraphEdge;
import org.societies.api.cis.orchestration.model.ISocialGraphVertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SocialGraphVertex implements ISocialGraphVertex {
    private static final int maxLastActs = 10;
	private String name;
	private ArrayList<ISocialGraphEdge> edges;
    public List<String> trends;
    public Queue<String> lastActs;
    private boolean trend = false;
	public SocialGraphVertex(){
        init();
	}
	public SocialGraphVertex(String name){
		this.name = name;
        init();
	}
    public void init(){
        edges = new ArrayList<ISocialGraphEdge>();
        trends = new ArrayList<String>();
        lastActs = new LinkedList<String>();
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ISocialGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<ISocialGraphEdge> edges) {
		this.edges = edges;
	}
	public String toString(){
		return name;
	}
    public boolean hasTrend(String trend){
        for(String s: lastActs)
            if(s.equals(trend))
                return true;
        return false;
    }
    public void addAct(String act){
        lastActs.add(act);
        if(lastActs.size()>maxLastActs)
            lastActs.poll();
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
    }
}
