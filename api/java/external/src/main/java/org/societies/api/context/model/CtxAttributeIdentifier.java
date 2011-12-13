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
package org.societies.api.context.model;

import org.societies.api.mock.EntityIdentifier;

/**
 * This class is used to identify context attributes. It provides methods
 * that return information about the identified attribute including:
 * <ul>
 * <li><tt>OperatorId</tt>: A unique identifier of the CSS or CIS where the 
 * entity containing the identified context attribute was first stored.</li>
 * <li><tt>ModelType</tt>: Describes the type of the identified context model
 * object, i.e. {@link CtxModelType#ATTRIBUTE ATTRIBUTE}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * attribute. e.g. "name".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * <p>
 * Compared to context entity or association identifiers, attribute identifiers
 * additionally contain their <tt>Scope</tt>, i.e. the Entity identifier they
 * are associated with. The format of the resulting identifier is as follows:
 * <pre>
 * &lt;Scope&gt;/ATTRIBUTE/&lt;Type&gt;/&lt;ObjectNumber&gt;
 * </pre>
 * <p>
 * Use the {@link #getScope()} method to retrieve the {@link CtxEntityIdentifier}
 * representing the attribute's <tt>Scope</tt>
 * as a <code>CtxEntityIdentifier</code> object.
 * 
 * @see CtxEntityIdentifier
 * @see CtxIdentifier
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class CtxAttributeIdentifier extends CtxIdentifier {
	
	private static final long serialVersionUID = -282171829285239788L;
	
	/** The scope of this context attribute identifier. */
	private final CtxEntityIdentifier scope;

	/**
	 * Creates a context attribute identifier by specifying the containing
	 * entity, the attribute type and the unique numeric model object identifier
	 * 
	 * @param scope
	 *            the {@link CtxEntityIdentifier} of the context entity containing
	 *            the identified attribute
	 * @param type
	 *            the attribute type, e.g. "name"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	public CtxAttributeIdentifier(CtxEntityIdentifier scope, String type, Long objectNumber) {
		super(scope.getOperatorId(), type, objectNumber);
		this.scope = scope;
	}
	
	/**
	 * Returns the {@link CtxEntityIdentifier} of the context entity containing
	 * the identified attribute.
	 * 
	 * @return the {@link CtxEntityIdentifier} of the context entity containing
	 *         the identified attribute.
	 */
	public CtxEntityIdentifier getScope() {
		return this.scope;
	}

	/**
	 *  Returns the type of the identified context model object, i.e. CtxModelType.ATTRIBUTE
	 *   
	 *  @return CtxModelType#ATTRIBUTE
	 */
	@Override
	public CtxModelType getModelType() {
		return CtxModelType.ATTRIBUTE;
	}
}