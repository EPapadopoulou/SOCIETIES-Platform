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
package org.societies.api.cis.management;


/**
 * @author Babak.Farshchian@sintef.no
 *
 */
public interface ICisManager {
	/**
	 * Create a new CIS for the CSS represented by cssId. Password is needed and is the
	 * same as the CSS password.
	 * After this method is called a CIS is created with mode set to mode.
	 * 
	 * The CSS who creates the CIS will be the owner. Ownership can be changed
	 * later.
	 * 
	 * TODO: define what values mode can have and what each means.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId and cssPassword are to recognise the user
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * @param mode membership type, e.g 1= read-only.
	 * TODO define mode better.
	 * @return link to the {@link ICisEditor} representing the new CIS, or 
	 * null if the CIS was not created.
	 */
	ICisEditor createCis(String cssId, String cssPassword, String cisName, String cisType, int mode);
	/**
	 * Delete a specific CIS represented by cisId. The cisId is available in the
	 * method of {@link ICisEditor} representing the CIS to be deleted. This method
	 * will delete only one CIS with the ID passed as cisId.
	 * 
	 * TODO: Need to give a more meaningful return.
	 * 
	 * @param cssId and cssPassword of the owner of the CIS.
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise.
	 */
	Boolean deleteCis(String cssId, String cssPassword, String cisId);
	/**
	 * Get a CIS Record with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID of the CIS to get.
	 * @return the CISRecord with the ID cisID, or null if no such CIS exists.
	 */
	ICisRecord getCis(String cssId, String cisId);
	
	/**
	 * Return an array of all the CISs that match the query. 
	 * 
	 * TODO: need to refine this to something better. I am not sure how the query will be created.
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	ICisRecord[] getCisList(ICisRecord query);
	
	Boolean requestNewCisOwner(String currentOwnerCssId, String currentOwnerCssPassword,
		String newOwnerCssId, String cisId);

}
