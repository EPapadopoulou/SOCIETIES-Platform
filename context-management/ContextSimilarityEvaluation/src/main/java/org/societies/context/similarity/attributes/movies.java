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
package org.societies.context.similarity.attributes;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.context.similarity.utilities.GetContextData;

/**
 * Describe your class here...
 *
 * @author John
 *
 */
public class movies {

	private CtxAttributeTypes cat;
	private GetContextData gcd;
	private static final Logger LOG = LoggerFactory.getLogger(occupation.class);

	public movies(){
		gcd = new GetContextData();
	}
	
	public HashMap<String, Double> evaluate(String[] allOwners){
		HashMap<String, Double> results = new HashMap<String, Double>();
		HashMap<String, Integer> resultcount = new HashMap<String, Integer>();
		Integer totalCount = allOwners.length;
 
		for (String css : allOwners){
			// get context value
			// TODO  sort this out later
			//CtxAttribute contextResult = gcd.getContext(css, cat.MOVIES);
			//String contextValue = contextResult.getStringValue();			
			String[] splitString = css.split(",");
			//
			for (String i : splitString){
				LOG.info("EBOYLANTESTSTRINGMOV: " + i);
				if (resultcount.containsKey(i)){
					resultcount.put(i, resultcount.get(i) + 1);
				} else {
					resultcount.put(i, 1);
				}
				
			}
			//
		}
		LOG.info("EBOYLANLOGFOOTPrint: " + resultcount.toString());
		//analyse results
		for (String k : resultcount.keySet()){
			
			float percent=(float)resultcount.get(k)/totalCount*100;
			results.put(k, (double)percent);
			LOG.info("EBOYLANMOVIEPERCENT: " + percent);
		}
		LOG.info("EBOYLANMOVIERESULT: " + results);
		
		return results;
		//
	}
}
