package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.identifiers.client.IdentifiersNAServiceClient;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.InvalidIdentifierValuesFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthorityConfigurationFault;
import gov.nih.nci.cagrid.identifiers.stubs.types.NamingAuthoritySecurityFault;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import java.rmi.RemoteException;
import java.util.ArrayList;

import namingauthority.IdentifierValues;
import namingauthority.KeyData;
import namingauthority.KeyValues;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.test.system.IdentifiersTestInfo;


public class ChangePublicIdentifierCreationStep extends Step {

    private IdentifiersTestInfo testInfo;
    private String publicCreation;
   
    public ChangePublicIdentifierCreationStep(IdentifiersTestInfo info, String newPublicCreation) {
        this.testInfo = info;
        this.publicCreation = newPublicCreation;
    }
    
    @Override
    public void runStep() throws Exception {
    	
    	EndpointReferenceType epr = testInfo.getGridSvcEPR();
        IdentifiersNAServiceClient client = new IdentifiersNAServiceClient( epr, testInfo.getSysAdminUser() );
        client.setAnonymousPrefered(false);

    	KeyValues[] newKeyValues = new KeyValues[1];
    	newKeyValues[0] = new KeyValues();
    	newKeyValues[0].setKey(Keys.PUBLIC_CREATION);
    	newKeyValues[0].setKeyData(new KeyData(null, new String[]{this.publicCreation}));

   		client.replaceKeys(testInfo.getSystemIdentifier(), new IdentifierValues(newKeyValues));
    }
}
