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
package org.societies.domainauthority.rest.model;

import java.security.cert.X509Certificate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.societies.api.security.digsig.DigsigException;
import org.societies.domainauthority.rest.control.ServiceClientJarAccess;

/**
 * Description of an XML document that is signed by various parties over time
 *
 * @author Mitja Vardjan
 *
 */
@Entity
@Table(name = "org_societies_security_darest_docs")
public class Document {
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;

	@Column(name="path")
	private String path;

	@Lob
	@Column(name="ownerCertSerialized")
	private byte[] ownerCertSerialized;

	@Lob
	@Column(name="xmlDoc")
	private byte[] xmlDoc;
	
	@Column(name="notificationEndpoint")
	private String notificationEndpoint;
	
	@Column(name="numSigners")
	private int numSigners;
	
	@Column(name="minNumSigners")
	private int minNumSigners;
	
	/**
	 * Default constructor for Hibernate only
	 */
	public Document() {
	}
	
	public Document(String path, X509Certificate ownerCert, byte[] xmlDoc, String notificationEndpoint,
			int minNumSigners) throws DigsigException {
		this.path = path;
		this.ownerCertSerialized = ServiceClientJarAccess.getSigMgr().cert2ba(ownerCert);
		this.xmlDoc = xmlDoc;
		this.notificationEndpoint = notificationEndpoint;
		this.minNumSigners = minNumSigners;
		this.numSigners = 0;
	}

	/**
	 * @return Relative path to the file
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path Relative path to the file
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ownerCertSerialized
	 */
	public byte[] getOwnerCertSerialized() {
		return ownerCertSerialized;
	}

	/**
	 * @param ownerCertSerialized the ownerCertSerialized to set
	 * @throws DigsigException 
	 */
	public void setOwnerCertSerialized(byte[] ownerKeySerialized) {
		this.ownerCertSerialized = ownerKeySerialized;
	}

	/**
	 * @return the xmlDoc
	 */
	public byte[] getXmlDoc() {
		return xmlDoc;
	}

	/**
	 * @param xmlDoc the xmlDoc to set
	 */
	public void setXmlDoc(byte[] xmlDoc) {
		this.xmlDoc = xmlDoc;
	}

	/**
	 * @return the notificationEndpoint
	 */
	public String getNotificationEndpoint() {
		return notificationEndpoint;
	}

	/**
	 * @param notificationEndpoint the notificationEndpoint to set
	 */
	public void setNotificationEndpoint(String notificationEndpoint) {
		this.notificationEndpoint = notificationEndpoint;
	}
	
	/**
	 * @return the numSigners
	 */
	public int getNumSigners() {
		return numSigners;
	}

	/**
	 * @param numSigners the numSigners to set
	 */
	public void setNumSigners(int numSigners) {
		this.numSigners = numSigners;
	}

	/**
	 * @return the minNumSigners
	 */
	public int getMinNumSigners() {
		return minNumSigners;
	}

	/**
	 * @param minNumSigners the minNumSigners to set
	 */
	public void setMinNumSigners(int minNumSigners) {
		this.minNumSigners = minNumSigners;
	}

	@Override
	public String toString() {
		return "ID: " + id +
				", path: " + path +
				", signed by " + numSigners + " (min " + minNumSigners + ")" + " subjects.";
	}
}
