package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import java.rmi.RemoteException;


import namingauthority.IdentifierValues;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.resolver.Resolver;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;

public class IdentifiersClientGridResolutionStep extends Step {

    private IdentifiersTestInfo testInfo;
   
    public IdentifiersClientGridResolutionStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }
    
    @Override
    public void runStep() throws RemoteException, MalformedURIException {
    	
    	assertNotNull("Null identifier list", testInfo.getIdentifiers());
    	assertNotNull("Null identifier values list", testInfo.getIdentifierValues());
    	
    	// Just pick one
    	URI identifier = testInfo.getIdentifiers().get(0);
    	IdentifierValues values = testInfo.getIdentifierValues().get(0);
    	
    	assertNotNull("Null identifier", identifier);
    	assertNotNull("Null values", values);
    	
    	System.out.println("Going to HTTP resolve [" + identifier.toString() + "]");
    	org.cagrid.identifiers.namingauthority.domain.IdentifierValues resolvedValues = null;
    	try {
			resolvedValues = new Resolver().resolveGrid(new java.net.URI(identifier.toString()));
			System.out.println(resolvedValues.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Grid Resolution failed: " + e.getMessage());
		} 
    	
		org.cagrid.identifiers.namingauthority.domain.IdentifierValues
			insertedValues = gov.nih.nci.cagrid.identifiers.common.MappingUtil.map( values );
					
    	assertEquals( insertedValues, resolvedValues );
    }
}
