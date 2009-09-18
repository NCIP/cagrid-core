/*
 * Portions of this file Copyright 1999-2005 University of Chicago Portions of
 * this file Copyright 1999-2005 The University of Southern California. This
 * file or a portion of this file is licensed under the terms of the Globus
 * Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html. If you redistribute this
 * file, with or without modifications, you must include this notice in the
 * file.
 */

package gov.nih.nci.cagrid.metadata;

import gov.nih.nci.cagrid.metadata.exceptions.InternalRuntimeException;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.QueryInvalidException;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.axis.gsi.GSIConstants;
import org.globus.axis.util.Util;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse;
import org.oasis.wsrf.properties.GetMultipleResourceProperties_Element;
import org.oasis.wsrf.properties.GetMultipleResourceProperties_PortType;
import org.oasis.wsrf.properties.GetResourceProperty;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.properties.InvalidQueryExpressionFaultType;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFaultType;
import org.oasis.wsrf.properties.QueryEvaluationErrorFaultType;
import org.oasis.wsrf.properties.QueryExpressionType;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.QueryResourceProperties_Element;
import org.oasis.wsrf.properties.QueryResourceProperties_PortType;
import org.oasis.wsrf.properties.UnknownQueryExpressionDialectFaultType;
import org.oasis.wsrf.properties.WSResourcePropertiesServiceAddressingLocator;
import org.w3c.dom.Element;

public class ResourcePropertyHelper {

	static {
		Util.registerTransport();
	}

	public static MessageElement[] queryResourceProperties(
			EndpointReferenceType endpoint, String queryExpression)
			throws RemoteResourcePropertyRetrievalException,
			QueryInvalidException {
		return queryResourceProperties(endpoint, queryExpression, null);
	}

	public static MessageElement[] queryResourceProperties(
			EndpointReferenceType endpoint, String queryExpression,
			InputStream wsdd) throws RemoteResourcePropertyRetrievalException,
			QueryInvalidException {

		WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();

		if (wsdd != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(wsdd);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		QueryExpressionType query = new QueryExpressionType();

		try {
			query.setDialect(WSRFConstants.XPATH_1_DIALECT);
		} catch (MalformedURIException e) {
			// this should never happen, and the user can't fix it if it does
			throw new InternalRuntimeException(e);
		}

		query.setValue(queryExpression);

		QueryResourceProperties_PortType port;

		try {
			port = locator.getQueryResourcePropertiesPort(endpoint);
		} catch (ServiceException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}

		setAnonymous((Stub) port);

		QueryResourceProperties_Element request = new QueryResourceProperties_Element();
		request.setQueryExpression(query);

		QueryResourcePropertiesResponse response = null;

		response = issueRPQuery(port, request);

		return response.get_any();

	}

	public static Element getResourceProperties(EndpointReferenceType endpoint)
			throws ResourcePropertyRetrievalException,
			RemoteResourcePropertyRetrievalException, QueryInvalidException {
		return getResourceProperties(endpoint, (InputStream) null);
	}

	public static Element getResourceProperties(EndpointReferenceType endpoint,
			InputStream wsdd) throws ResourcePropertyRetrievalException,
			RemoteResourcePropertyRetrievalException, QueryInvalidException {
		String dialect = WSRFConstants.XPATH_1_DIALECT;
		String queryExpression = "/";

		WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();

		if (wsdd != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(wsdd);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}

		QueryExpressionType query = new QueryExpressionType();

		try {
			query.setDialect(dialect);
		} catch (MalformedURIException e) {
			// this should never happen, and the user can't fix it if it does
			throw new InternalRuntimeException(e);
		}

		query.setValue(queryExpression);

		QueryResourceProperties_PortType port;
		try {
			port = locator.getQueryResourcePropertiesPort(endpoint);
		} catch (ServiceException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}

		setAnonymous((Stub) port);

		QueryResourceProperties_Element request = new QueryResourceProperties_Element();
		request.setQueryExpression(query);

		QueryResourcePropertiesResponse response = issueRPQuery(port, request);

		MessageElement messageElements[] = response.get_any();
		if (messageElements == null) {
			return (null);
		}

		if (messageElements.length > 1) {
			throw new ResourcePropertyRetrievalException(
					"Resource property query returned "
							+ Integer.toString(messageElements.length)
							+ " elements; I only know how to deal with one");
		}
		Element element;
		try {
			element = messageElements[0].getAsDOM();
		} catch (Exception e) {
			throw new ResourcePropertyRetrievalException(
					"Error parsing message element(" + messageElements[0] + ")",
					e);
		}
		return element;

	}

	public static Element getResourceProperty(EndpointReferenceType endpoint,
			QName rpName) throws ResourcePropertyRetrievalException,
			RemoteResourcePropertyRetrievalException,
			InvalidResourcePropertyException {
		return getResourceProperty(endpoint, rpName, null);
	}

	public static Element getResourceProperty(EndpointReferenceType endpoint,
			QName rpName, InputStream wsdd)
			throws ResourcePropertyRetrievalException,
			RemoteResourcePropertyRetrievalException,
			InvalidResourcePropertyException {
		GetResourceProperty port;
		WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();

		if (wsdd != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(wsdd);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		try {
			port = locator.getGetResourcePropertyPort(endpoint);
		} catch (ServiceException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}

		setAnonymous((Stub) port);

		GetResourcePropertyResponse response = null;

		try {
			response = port.getResourceProperty(rpName);
		} catch (InvalidResourcePropertyQNameFaultType e) {
			throw new InvalidResourcePropertyException(e);
		} catch (RemoteException e) {
			throw new RemoteResourcePropertyRetrievalException(
					"Error getting resource property; " + "endpoint was '"
							+ endpoint + "', name was '" + rpName.toString(), e);
		}

		MessageElement[] messageElements = response.get_any();
		if (messageElements == null) {
			return (null);
		}
		if (messageElements.length > 1) {
			throw new ResourcePropertyRetrievalException(
					"Get resource property returned "
							+ Integer.toString(messageElements.length)
							+ " elements; I only know how to deal with one");
		}
		Element element;
		try {
			element = messageElements[0].getAsDOM();
		} catch (Exception e) {
			throw new ResourcePropertyRetrievalException(
					"Error parsing message element(" + messageElements[0] + ")",
					e);
		}
		return element;
	}

	public static Element[] getResourceProperties(
			EndpointReferenceType endpoint, QName[] rpNames)
			throws ResourcePropertyRetrievalException {
		return getResourceProperties(endpoint, rpNames, null);
	}

	public static Element[] getResourceProperties(
			EndpointReferenceType endpoint, QName[] rpNames, InputStream wsdd)
			throws ResourcePropertyRetrievalException {
		WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();
		
		if (wsdd != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(wsdd);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		
		GetMultipleResourceProperties_PortType port;
		try {
			port = locator.getGetMultipleResourcePropertiesPort(endpoint);
		} catch (ServiceException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}

		setAnonymous((Stub) port);

		GetMultipleResourceProperties_Element request = new GetMultipleResourceProperties_Element();
		request.setResourceProperty(rpNames);

		GetMultipleResourcePropertiesResponse response;
		try {
			response = port.getMultipleResourceProperties(request);
		} catch (InvalidResourcePropertyQNameFaultType e) {
			throw new InvalidResourcePropertyException(e);
		} catch (RemoteException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}

		Element result[];
		try {
			result = AnyHelper.toElement(response.get_any());
		} catch (Exception e) {
			throw new ResourcePropertyRetrievalException(
					"Error converting resource properties to elements: "
							+ e.getMessage(), e);
		}
		return result;
	}

	private static void setAnonymous(Stub stub) {
		stub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS,
				Boolean.TRUE);
		stub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
				NoAuthorization.getInstance());
		stub._setProperty(GSIConstants.GSI_AUTHORIZATION,
				org.globus.gsi.gssapi.auth.NoAuthorization.getInstance());
	}

	private static QueryResourcePropertiesResponse issueRPQuery(
			QueryResourceProperties_PortType port,
			QueryResourceProperties_Element request)
			throws QueryInvalidException,
			RemoteResourcePropertyRetrievalException {
		QueryResourcePropertiesResponse response = null;
		try {
			response = port.queryResourceProperties(request);
		} catch (InvalidQueryExpressionFaultType e) {
			throw new QueryInvalidException(e);
		} catch (QueryEvaluationErrorFaultType e) {
			throw new QueryInvalidException(e);
		} catch (UnknownQueryExpressionDialectFaultType e) {
			// shouldn't happen and user can't handle this
			throw new InternalRuntimeException(e);
		} catch (RemoteException e) {
			throw new RemoteResourcePropertyRetrievalException(e);
		}
		return response;
	}
}
