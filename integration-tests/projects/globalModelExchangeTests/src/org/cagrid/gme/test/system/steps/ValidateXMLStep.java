package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;


public class ValidateXMLStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final File xmlFile;
    private final boolean shouldXMLValidate;
    private final boolean shouldXSDExist;


    public ValidateXMLStep(EndpointReferenceType gmeEPR, File xmlFile, boolean shouldXMLValidate, boolean shouldXSDExist) {
        this.gmeEPR = gmeEPR;
        this.xmlFile = xmlFile;
        this.shouldXMLValidate = shouldXMLValidate;
        this.shouldXSDExist = shouldXSDExist;

    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null xmlFile must be passed in.", this.xmlFile);

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);

        try {
            gme.validateXMLFile(xmlFile);
            if (!shouldXSDExist) {
                fail("The Schema for file (" + this.xmlFile + ") and should not have existed but did.");

            }
            if (!shouldXMLValidate) {
                fail("Schema validation did not fail for file (" + this.xmlFile + ") and should have.");
            }

        } catch (NoSuchNamespaceExistsFault e) {
            if (shouldXSDExist) {
                fail("The Schema for file (" + this.xmlFile + ") and should have existed but did not.");
            }
        } catch (SchemaValidationException e) {
            if (shouldXMLValidate) {
                e.printStackTrace();
                fail("Schema validation failed for file (" + this.xmlFile + ") and should not have:" + e.getMessage());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Test configuration error:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Test configuration error:" + e.getMessage());
        }

    }
}
