/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.identifiers.resolver.test;

import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.resolver.Resolver;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.impl.DefaultRetrieverFactory;
import org.cagrid.identifiers.retriever.impl.RetrieverImpl;
import org.cagrid.identifiers.retriever.impl.RetrieverService;

public class RetrieverTestCase extends TestCase {

	private static Log log = LogFactory.getLog(RetrieverTestCase.class);
	private static DefaultRetrieverFactory factory;
	
	static {
		/* 
		 * Create a factory with some retrievers 
		 */
		
		Map<String, Retriever> retrievers = new HashMap<String, Retriever>();
		retrievers.put("r1", new TestRetriever("r1", new String[]{"CODE1"}));
		retrievers.put("r2", new TestRetriever("r2", new String[]{"CODE1", "CODE2"}));
		retrievers.put("r3", new TestRetriever("r3", new String[]{}));
		retrievers.put("r4", new TestRetriever("r4", null));
		retrievers.put("r5", null);
		
		factory = new DefaultRetrieverFactory(retrievers);
	}

	public void testDefaultRetrieverFactory() {
		IdentifierData data = new IdentifierData();
		data.put("CODE1", new KeyData());
		data.put("CODE2", new KeyData());
		
		TestRetriever r = (TestRetriever)factory.getRetriever(data);
		if (!r.getName().equals("r2")) {
			fail("Unexpected retriever object was chosen");
		}
		
		
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(RetrieverTestCase.class);
	}
}

class TestRetriever extends RetrieverImpl {
	
	private String name;
	
	TestRetriever(String name, String[] requiredKeys) {
		super.setRequiredKeys(requiredKeys);
		this.name = name;
	}
	
	public String getName(){ return name; }
	
	public Object retrieve(IdentifierData ivs) throws Exception {
		return "Hello World!";
	}
}

