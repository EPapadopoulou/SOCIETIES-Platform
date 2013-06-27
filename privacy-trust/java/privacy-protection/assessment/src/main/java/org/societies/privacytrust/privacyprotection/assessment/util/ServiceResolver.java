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
package org.societies.privacytrust.privacyprotection.assessment.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.service.ServiceUnavailableException;

/**
 * Mapper for Java classes, OSGi bundles, and SOCIETIES 3rd party services.
 *
 * @author mitjav
 *
 */
public class ServiceResolver implements BundleContextAware {

	private static Logger LOG = LoggerFactory.getLogger(ServiceResolver.class);

	private BundleContext bundleContext;
	private IServiceDiscovery serviceDiscovery;
	
	private Map<String, List<BundleInfo>> class2bundleMap = new HashMap<String, List<BundleInfo>>();

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void init() {
		LOG.debug("init()");
	}
	
	/**
	 * Setter to be called only automatically by Spring dependency injection.
	 * 
	 * @param serviceDiscovery the serviceDiscovery to set
	 */
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		LOG.debug("setServiceDiscovery()");
		this.serviceDiscovery = serviceDiscovery;
	}

//	public String getServiceIdFromBundle(String bundleSymbolicName) {
//		
//	}
//	
//	public List<String> getServiceIdFromClass(String className) {
//		
//	}

	public List<String> getBundleSymbolicName(String className) {
		
		LOG.debug("getBundleSymbolicName({})", className);

		List<BundleInfo> previousInfo = class2bundleMap.get(className);
		if (previousInfo != null) {
//			return previousInfo.;
		}
		
		Enumeration<URL> entries;
		URL entry;
		String cn;
		int index;
		List<String> result = new ArrayList<String>();

		if (bundleContext == null) {
			LOG.warn("bundleContext is null. This is OK only if it happened during JUnit test");
			return result;
		}
		if (className == null) {
			return result;
		}
		Bundle[] bundles = bundleContext.getBundles();
		//LOG.debug("Number of all bundles: {}", bundles.length);
		
		for (Bundle bundle : bundles) {

			//LOG.debug("Getting entries for bundle ID {}: {}", bundle.getBundleId(), bundle.getSymbolicName());

			entries = bundle.findEntries("/", "*.class", true);
			while (entries != null && entries.hasMoreElements()) {
				entry = entries.nextElement();
				//LOG.debug("Found entry: {}", entry);
				cn = entry.toString().replaceFirst("bundleentry://", "");
				index = cn.indexOf("/");
				if (index > 0) {
					if (cn.length() > (index + 1)) {
						++index;
					}
					cn = cn.substring(index);
					cn = cn.replaceAll("/", ".");
					index = cn.lastIndexOf(".class");
					if (index > 0) {
						cn = cn.substring(0, index);
					}
				}
				//LOG.debug("Entry class name: {}", cn);
				if (className.equals(cn)) {
					LOG.debug("Found matching class name in {}", bundle.getSymbolicName());
					try {
						ServiceResourceIdentifier serviceId;
						serviceId = getServiceId(bundle);
						result.add(serviceId.getIdentifier().toASCIIString());
					} catch (AssessmentException e) {
						result.add(bundle.getSymbolicName());
					}
				}
			}
		}
		if (result.size() > 1) {
			LOG.warn("Class {} is present in multiple ({}) bundles", className, result.size());
		}
		return result;
	}
	
	private ServiceResourceIdentifier getServiceId(Bundle bundle) throws AssessmentException {
		
		try {
			// The proxy is not null even if the service is not available, this
			// is used mainly to trigger ServiceUnavailableException
			if (serviceDiscovery != null) {
				LOG.debug("getServiceId(): Using service discovery to map bundle {} to service ID",
						bundle.getSymbolicName());
				Service service = ServiceModelUtils.getServiceFromBundle(bundle, serviceDiscovery);
				if (service == null) {
					throw new AssessmentException("Bundle " + bundle.getSymbolicName() +
							" does not seem to be a SOCIETIES 3P service");
				}
				return service.getServiceIdentifier();
			}
			else {
				LOG.warn("getServiceId(): Service discovery proxy is null.");
				throw new AssessmentException("Service discovery proxy is null.");				
			}
		} catch (ServiceUnavailableException e) {
			LOG.debug("getServiceId(): Service discovery not available, cannot map bundle {} to service ID",
					bundle.getSymbolicName());
			throw new AssessmentException("Service discovery not available, cannot map bundle to service ID");
		}
	}
}
