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
package org.societies.domainauthority.rest.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.domainauthority.DaRestException;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.security.digsig.DigsigException;
import org.societies.domainauthority.rest.control.XmlDocumentAccess;
import org.societies.domainauthority.rest.util.FileName;
import org.societies.domainauthority.rest.util.Files;
import org.societies.domainauthority.rest.util.UrlParamName;

/**
 * Class for hosting and merging xml documents that are being signed over time by multiple parties.
 * 
 * @author Mitja Vardjan
 */
@Path(UrlPath.PATH_XML_DOCUMENTS)
public class XmlDocument extends HttpServlet {

	private static final long serialVersionUID = 4625772782444356957L;

	private static Logger LOG = LoggerFactory.getLogger(XmlDocument.class);

	public XmlDocument() {
		LOG.info("Constructor");
	}

	/**
	 * Method processing HTTP GET requests, producing "application/xml" MIME media type.
	 * HTTP response: the requested file, e.g., service client in form of jar file.
	 * Error 401 if file name or signature not valid.
	 * Error 500 on server error.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		if (request.getPathInfo() == null) {
			LOG.warn("HTTP GET: request.getPathInfo() is null");
			return;
		}
		String path = request.getPathInfo().replaceFirst("/", "");
//		String path = request.getParameter(UrlPath.URL_PARAM_FILE);
		String signature = request.getParameter(UrlPath.URL_PARAM_SIGNATURE);
		
		LOG.info("HTTP GET: path = {}, signature = " + signature, path);

		byte[] file;

		if (!XmlDocumentAccess.isAuthorized(path, signature)) {
			LOG.warn("Invalid filename or key");
			// Return HTTP status code 401 - Unauthorized
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		file = XmlDocumentAccess.getDocumentDao().get(path).getXmlDoc();

		LOG.info("Serving {}", path);
		
		response.setContentLength(file.length);
		response.setContentType("application/xml");
		try {
			ServletOutputStream stream = response.getOutputStream();
			stream.write(file);
			stream.flush();
		} catch (IOException e) {
			LOG.warn("Could not write response", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	/**
	 * Method processing HTTP PUT requests.
	 */
	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) {
		
		String path = request.getPathInfo().replaceFirst("/", "");
		String cert = request.getParameter(UrlPath.URL_PARAM_PUB_KEY);
		String endpoint = request.getParameter(UrlPath.URL_PARAM_NOTIFICATION_ENDPOINT);
		String signature = request.getParameter(UrlPath.URL_PARAM_SIGNATURE);

		LOG.info("HTTP PUT from {}; path = {}, endpoint = " + endpoint + ", pubKey = " +
				cert + ", signature = " + signature, request.getRemoteHost(), path);

		int status;
		InputStream is;
		try {
			is = getInputStream(request);
		} catch (DaRestException e) {
			LOG.warn("HTTP PUT, ", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (path != null && cert != null && endpoint != null) {
			status = putNewDocument(path, cert, endpoint, is);
		} else if (path != null && signature != null) {
			status = mergeDocument(path, signature, is);
		} else {
			status = HttpServletResponse.SC_BAD_REQUEST;
			return;
		}
		response.setStatus(status);
	}
	
	private InputStream getInputStream(HttpServletRequest request) throws DaRestException {
		
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		List<FileItem> items;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			throw new DaRestException(e);
		}

		// Process the uploaded items
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// Process FormField;
			} else {
				// Process Uploaded File
				try {
					return item.getInputStream();
				} catch (IOException e) {
					throw new DaRestException(e);
				}
			}
		}
		throw new DaRestException("No payload found in HTTP request");
	}
	
	private int putNewDocument(String path, String cert, String endpoint, InputStream is) {

		cert = UrlParamName.url2Base64(cert);
		LOG.debug("HTTP PUT: cert fixed to {}", cert);

		path = FileName.removeUnsupportedChars(path);
		LOG.debug("HTTP PUT: path fixed to {}", path);
		
		if (XmlDocumentAccess.getDocumentDao().get(path) != null) {
			LOG.warn("HTTP PUT: document {} already exists", path);
			return HttpServletResponse.SC_CONFLICT;
		}
		try {
			byte[] xml = IOUtils.toByteArray(is);
			XmlDocumentAccess.addDocument(path, cert, xml, endpoint);
			return HttpServletResponse.SC_OK;
		} catch (IOException e) {
			LOG.warn("Could not write document {}", path, e);
			// Return HTTP status code 500 - Internal Server Error
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} catch (DigsigException e) {
			LOG.warn("Could not store public key", e);
			// Return HTTP status code 500 - Internal Server Error
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}
	
	private int mergeDocument(String path, String signature, InputStream is) {
		
		byte[] xml;
		try {
			xml = IOUtils.toByteArray(is);
		} catch (IOException e) {
			LOG.warn("mergeDocument: ", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		
		boolean success;
		try {
			success = XmlDocumentAccess.mergeDocument(path, xml, signature);
		} catch (DigsigException e) {
			LOG.warn("mergeDocument: ", e);
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
		if (success) {
			return HttpServletResponse.SC_OK;
		} else {
			return HttpServletResponse.SC_UNAUTHORIZED;
		}
	}
}
