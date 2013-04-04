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
package org.societies.api.internal.privacytrust.privacy.model.dataobfuscation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.ObfuscationLevelType;


/**
 * Describe obfuscation algorithm of a data type:
 * - obfuscable or not
 * - how (continuous, discrete)
 * - result persistable
 * And provide example of obfuscation for the GUI
 * @author Olivier Maridat (Trialog)
 */
public abstract class ObfuscatorInfo {
	protected boolean obfuscable;
	protected boolean persistable;
	protected ObfuscationLevelType obfuscationLevelType;
	protected int nbOfObfuscationLevelStep;
	protected String obfuscableDataType;
	protected Map<Double, String> obfuscationExamples;

	/**
	 * To know if this obfuscation can be done on this node
	 * @return True if the obfuscation can be done on this node. Otherwise, it is useless to try to do anything with this obfuscator
	 */
	public boolean isObfuscable() {
		return obfuscable;
	}

	/**
	 * To know if the obfuscated data can be persisted or not
	 * @return the wrapped data to obfuscate
	 */
	public boolean isPersistable() {
		return persistable;
	}

	/**
	 * Type of the obfuscation continous or discrete
	 * If discrete, the number of steps is useful
	 * @see getNbOfObfuscationLevelStep()
	 * @return the type of the obfuscation
	 */
	public ObfuscationLevelType getObfuscationLevelType() {
		return obfuscationLevelType;
	}

	/**
	 * Number of classes for a discrete obfuscation level
	 * @return the number of steps available
	 */
	public int getNbOfObfuscationLevelStep() {
		return nbOfObfuscationLevelStep;
	}

	/**
	 * SOCIETIES type of the obfuscable data
	 * @return the type of the obfuscable data
	 */
	public String getObfuscableDataType() {
		return obfuscableDataType;
	}

	/**
	 * Return a friendly example of obfuscation at a specific obfuscation level
	 * @param obfuscationLevel The example provided will demonstrate an obfuscation near this obfuscation level
	 * @return Friendly example of obfuscation. Null if no examples are provided.
	 */
	public String getObfuscationExample(double obfuscationLevel) {
		// - No example: null
		if (null == obfuscationExamples || obfuscationExamples.size() <= 0) {
			return null;
		}
		// - Examples: search the good slot
		double previous = 0;
		String lastExample = null;
		for(Entry<Double, String> entry : obfuscationExamples.entrySet()) {
			double current = entry.getKey().doubleValue();
			lastExample = entry.getValue();
			// Current slot
			if (obfuscationLevel >= previous && obfuscationLevel < current) {
				return lastExample;
			}
			previous = current;

		}
		// Last slot or not in the slots: return the last one
		return lastExample;
	}

	/**
	 * Retrieve all friendly obfuscation examples
	 * @return All friendly obfuscation examples. Null if no examples are provided.
	 */
	public List<String> getAllObfuscationExamples() {
		if (null == obfuscationExamples || obfuscationExamples.size() <= 0) {
			return null;
		}
		return new ArrayList<String>(obfuscationExamples.values());
	}
}
