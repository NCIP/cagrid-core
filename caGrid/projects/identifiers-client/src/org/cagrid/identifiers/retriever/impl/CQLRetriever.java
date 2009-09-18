package org.cagrid.identifiers.retriever.impl;

import java.io.StringBufferInputStream;

import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.utils.XMLUtils;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;
import org.cagrid.identifiers.retriever.Retriever;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class CQLRetriever extends Retriever {

	public Object retrieve( IdentifierValuesImpl ivs ) throws Exception {
		
		validateTypes( ivs );
		
		String[] eprStrs = ivs.getValues("EPR");
		String[] cqlStrs = ivs.getValues("CQL");
		
		if (eprStrs == null || eprStrs.length == 0 || cqlStrs == null || cqlStrs.length == 0) {
			System.out.println("No data available to perform CQL resolution");
			return null;
		}
		
		//
		// Deserialize EPR
		//
		System.out.println("Going to deserialize EPR={" + eprStrs[0] + "}");
		
		StringBufferInputStream fis = new StringBufferInputStream( eprStrs[0] );
		EndpointReferenceType endpoint = (EndpointReferenceType) 
				ObjectDeserializer.deserialize(new InputSource(fis),
					         EndpointReferenceType.class);

		//
		// Deserialize query
		//
		System.out.println("Going to deserialize CQL={" + cqlStrs[0] + "}");
		
		gov.nih.nci.cagrid.cqlquery.CQLQuery query = (gov.nih.nci.cagrid.cqlquery.CQLQuery) 
				gov.nih.nci.cagrid.common.Utils.deserializeObject(
					new java.io.StringReader(cqlStrs[0]), gov.nih.nci.cagrid.cqlquery.CQLQuery.class);
		
		String endpointUrl = endpoint.getAddress().toString();
		String portName = endpoint.getPortType().getLocalPart();

		
		return query( query, endpointUrl, portName );
	}

	public static gov.nih.nci.cagrid.cqlresultset.CQLQueryResults 
		query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery, String url, String portName) throws Exception {

		gov.nih.nci.cagrid.data.stubs.QueryRequest params = new gov.nih.nci.cagrid.data.stubs.QueryRequest();
		gov.nih.nci.cagrid.data.stubs.QueryRequestCqlQuery cqlQueryContainer = new gov.nih.nci.cagrid.data.stubs.QueryRequestCqlQuery();
		cqlQueryContainer.setCQLQuery(cqlQuery);
		params.setCqlQuery(cqlQueryContainer);

		org.apache.axis.client.Service service = new org.apache.axis.client.Service();
		
		org.apache.axis.description.OperationDesc oper = 
			new org.apache.axis.description.OperationDesc();
        oper.setName("query");
       
        oper.addParameter(
        		new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", "QueryRequest"), 
        		new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", ">QueryRequest"), 
        		gov.nih.nci.cagrid.data.stubs.QueryRequest.class, 
        		org.apache.axis.description.ParameterDesc.IN, false, false);
      
        oper.setReturnType(new QName("http://gov.nih.nci.cagrid.data/DataService", ">QueryResponse"));
        oper.setReturnClass(gov.nih.nci.cagrid.data.stubs.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", "QueryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
      
		org.apache.axis.client.Call _call = (org.apache.axis.client.Call) service.createCall();
		_call.setTargetEndpointAddress(url);
        _call.setOperation(oper);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://data.cagrid.nci.nih.gov/DataService/QueryRequest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "query"));
		Object ret = _call.invoke( new Object[]{ params } );
		
		gov.nih.nci.cagrid.data.stubs.QueryResponse resp = null;

		if (ret instanceof Element) {
			XMLUtils.ElementToStream((Element) ret, System.out);
		} else {
			resp = (gov.nih.nci.cagrid.data.stubs.QueryResponse)ret;
		}

		return (resp != null ? resp.getCQLQueryResultCollection() : null);
	}
}
