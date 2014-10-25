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
package org.societies.api.privacytrust.privacy.model.privacypolicy.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

/**
 * @author Eliza
 */
public class PrivacyConditionsConstantValues {

    private static final String[] hours = new String[]{"12", "24", "36", "48", "60", "72", "84", "96"};
    private static final String[] minutes = new String[]{"30", "60", "90", "120", "180", "240", "300", "360", "480", "600"};
    private static final String[] seconds = new String[]{"10", "20", "30", "40", "50", "60", "90", "120", "150", "180", "210", "240"};
    private static final String[] timeValues = new String[]{"30 minutes", "1 hour", "2 hours", "6 hours", "12 hours", "1 day", "2 days", "3 days", "4 days", "a week", "until account is deactivated"};
    private static final List<String> timeValuesAsList = Arrays.asList(timeValues);
    
    private static final String[] boolValues = new String[]{"yes", "no"};
    private static final String[] shareValues = new String[]{"No sharing", "affiliated services", "3rd parties", "anyone"};
    private static final List<String> shareValuesAsList = Arrays.asList(shareValues);
    
    public static String getBetterDataRetention(String value1, String value2) throws PrivacyException{

		if ((!timeValuesAsList.contains(value1) || !timeValuesAsList.contains(value2))){
			throw new PrivacyException("Ranges: "+value1+" or "+value2+" are not acceptable values for a data retention condition ");
		}
		
		int indexOf_Value1 = timeValuesAsList.indexOf(value1);
		int indexOf_Value2 = timeValuesAsList.indexOf(value2);
		
		if (indexOf_Value1>=indexOf_Value2){
			return value2;
		}else{
			return value1;
		}
		
    }
    
    public static String getBetterSharedValue(String value1, String value2) throws PrivacyException {
		if ((!shareValuesAsList.contains(value1) || !shareValuesAsList.contains(value2))){
			throw new PrivacyException("Ranges: "+value1+" or "+value2+" are not acceptable values for a \"shared with\" condition ");
		}
		
		
		int indexOf_Value1 = shareValuesAsList.indexOf(value1);
		int indexOf_Value2 = shareValuesAsList.indexOf(value2);
		
		if (indexOf_Value1>=indexOf_Value2){
			return value2;
		}else{
			return value1;
		}
    }
    public static String getBetterConditionValue(ConditionConstants constant){
    	switch (constant){
    	case  DATA_RETENTION:
    		return timeValues[0];
    	case MAY_BE_INFERRED:
    		return boolValues[1];
    	case SHARE_WITH_3RD_PARTIES:
    		return shareValues[0];
    	default: return boolValues[0];
    	}
    }
    
    public static String getBetterConditionValue(ConditionConstants constant, String value1, String value2) throws PrivacyException{
    	switch (constant){
    	case  DATA_RETENTION:

				return getBetterDataRetention(value1, value2);

    	case MAY_BE_INFERRED:
    		if (value1.equalsIgnoreCase(value2)){
    			return value1;
    		}
    		return boolValues[1];
    	case SHARE_WITH_3RD_PARTIES:

				return getBetterSharedValue(value1, value2);

    	default: 
    		if (value1.equalsIgnoreCase(value2)){
    			return value1;
    		}
    		return boolValues[0];
    	}
    }
    
    public static String[] getValues(ConditionConstants condition) {
        switch (condition) {
        	case DATA_RETENTION:
        		return timeValues;
            case MAY_BE_INFERRED:
                return boolValues;
            case RIGHT_TO_ACCESS_HELD_DATA:
                return boolValues;
            case RIGHT_TO_CORRECT_INCORRECT_DATA:
                return boolValues;
            case RIGHT_TO_OPTOUT:
                return boolValues;
            case SHARE_WITH_3RD_PARTIES:
                return shareValues;
            case STORE_IN_SECURE_STORAGE:
                return boolValues;
        }

        return new String[]{};
    }
    
    
    public static List<String> getValuesAsList(ConditionConstants condition){
    	return Arrays.asList(getValues(condition));
    }
}
