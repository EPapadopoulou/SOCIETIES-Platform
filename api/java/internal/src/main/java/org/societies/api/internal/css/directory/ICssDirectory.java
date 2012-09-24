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

package org.societies.api.internal.css.directory;

import java.util.Collection;

/**
 * @author Perumal Kuppuudaiyar
 * @author Babak.Farshchian@sintef.no
 */
public interface ICssDirectory {

	/**
	 * Description: This method provide interface to add new CSS object to CSS
	 * directory
	 * 
	 * @param css object to be added to directory
	 */
	void addCss(Object css);

	/**
	 * Description: This method allows to delete specific CSS entry from CSS
	 * Directory
	 * 
	 * @param css object to be deleted from directory
	 */
	void deleteCss(Object css);

	/**
	 * Description : This method can be used to update the changes in the CSS
	 * which is already exists in the CSS directory
	 * 
	 * @param css to be updated or replaced
	 * @param update new css object to be placed in the directory
	 */
	void updateCss(Object css, Object update);

	/**
	 * Description : Queries list of CSS available in the CSS directory
	 * @return collection of CSS objects from CSS directory
	 */
	Collection<Object> findForAllCss();

	/**
	 * Description : Queries list of CSS object with CIS group filter 
	 * @param cisgroup for which list of CSS will retrieved from directory  
	 * @return collection of CSS object
	 */
	Collection<Object> findForAllCss(Object cisgroup);

	/**
	 * Descripption : Search for specific CSS in the directory
	 * @param searchinfo searching object or criteria
	 * @return a CSS object if available or null
	 */
	Object findCss(Object searchinfo);
	
	/**
	 * Descripption : Search for specigified CSS in the directory
	 * @param cisIdList searching object or criteria
	 * @return collection of CSS advert object
	 */
	Collection<Object> searchById(Collection<Object> cisIdList);
	

}
