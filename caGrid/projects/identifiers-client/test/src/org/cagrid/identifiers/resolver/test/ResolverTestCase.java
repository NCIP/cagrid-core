package org.cagrid.identifiers.resolver.test;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.resolver.Resolver;
import org.cagrid.identifiers.retriever.impl.RetrieverService;

public class ResolverTestCase extends TestCase {

	private static Log log = LogFactory.getLog(ResolverTestCase.class);
	
	private static URI identifier;
	
	static {
		try {
			identifier = new URI("https://localhost:8444/namingauthority/NamingAuthorityService/7e4d90b8-3401-4cc6-a7d3-2675eaa23f37");
		} catch(Exception e){
			identifier = null;
		}
	}
	
	public void testGridResolution() {
		try {
			System.out.println("========== testGridResolution =============");
			IdentifierData ivs = new Resolver().resolveGrid(identifier);
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
			IdentifierData ivs = new Resolver().resolveHttp(identifier);
			System.out.println(ivs.toString());
			System.out.println("========== testHttpResolution SUCCESS =============");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
//	public void testCQLRetriever() {
//		try {
//			System.out.println("========== testCQLRetriever =============");
//			IdentifierValues ivs = ResolverUtil.resolveHttp(identifier);
//			RetrieverService rs = new RetrieverService();
//			gov.nih.nci.cagrid.cqlresultset.CQLQueryResults results = 
//				(gov.nih.nci.cagrid.cqlresultset.CQLQueryResults)
//					rs.retrieve("CQLRetriever", ivs);
//			System.out.println("Object result count["+results.getObjectResult().length+"]");
//			System.out.println("========== testCQLRetriever SUCCESS =============");
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ResolverTestCase.class);
	}
}

