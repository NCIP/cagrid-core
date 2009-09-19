package org.cagrid.identifiers.resolver.test;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.AttributedQName;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ServiceNameType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.impl.IdentifierValuesImpl;
import org.cagrid.identifiers.resolver.ResolverUtil;
import org.cagrid.identifiers.retriever.impl.RetrieverService;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class ResolverTestCase extends TestCase {

	private static Log log = LogFactory.getLog(ResolverTestCase.class);
	
	private String identifier = "http://140.254.126.79:8080/osumc/70ef4ac1-a39a-4652-a46b-a2c6b0194728";
	//private String identifier = "https://140.254.126.79:8443/osumc/ssl/70ef4ac1-a39a-4652-a46b-a2c6b0194728";

	public void testGridResolution() {
		try {
			System.out.println("========== testGridResolution =============");
			IdentifierValuesImpl ivs = ResolverUtil.resolveGrid(identifier);
			System.out.println(ivs.toString());
			System.out.println("========== testGridResolution SUCCESS =============");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testHttpResolution() {
		try {
			System.out.println("========== testHttpResolution =============");
			IdentifierValuesImpl ivs = ResolverUtil.resolveHttp(identifier);
			System.out.println(ivs.toString());
			System.out.println("========== testHttpResolution SUCCESS =============");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testCQLRetriever() {
		try {
			System.out.println("========== testCQLRetriever =============");
			IdentifierValuesImpl ivs = ResolverUtil.resolveHttp(identifier);
			RetrieverService rs = new RetrieverService();
			gov.nih.nci.cagrid.cqlresultset.CQLQueryResults results = 
				(gov.nih.nci.cagrid.cqlresultset.CQLQueryResults)
					rs.retrieve("CQLRetriever", ivs);
			System.out.println("Object result count["+results.getObjectResult().length+"]");
			System.out.println("========== testCQLRetriever SUCCESS =============");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ResolverTestCase.class);
	}
}

