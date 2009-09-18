package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchemaNamespace;


public class CheckCacheSchemasStep extends Step {

    private final EndpointReferenceType gmeEPR;
    private final XMLSchemaNamespace targetNamespace;
    private final File directory;


    public CheckCacheSchemasStep(EndpointReferenceType gmeEPR, XMLSchemaNamespace targetNamespace, File directory) {
        this.gmeEPR = gmeEPR;
        this.targetNamespace = targetNamespace;
        this.directory = directory;
    }


    @Override
    public void runStep() throws RemoteException, MalformedURIException, IOException {
        assertNotNull("A non-null EPR must be passed in.", this.gmeEPR);
        assertNotNull("A non-null XMLSchemaNamespace must be passed in.", this.targetNamespace);
        assertNotNull("A non-null directory must be passed in.", this.directory);
        assertTrue("Must not be an existing file.", !this.directory.isFile());

        this.directory.mkdirs();

        // come up with a unique name
        File dir = File.createTempFile("CachedSchemas_", "", this.directory);
        // delete it
        dir.delete();
        // make it into a directory
        dir.mkdirs();

        GlobalModelExchangeClient gme = new GlobalModelExchangeClient(this.gmeEPR);
        Map<XMLSchemaNamespace, File> cachedSchemas = gme.cacheSchemas(this.targetNamespace, dir);
        File rootSchemaFile = cachedSchemas.get(this.targetNamespace);
        assertNotNull("The main schema requested was not found.", rootSchemaFile);

        try {
            SchemaValidator validator = new SchemaValidator("resources/schemas/XMLSchema.xsd");
            validator.validate(rootSchemaFile);
        } catch (SchemaValidationException e) {
            e.printStackTrace();
            fail("Failed to validate schema (" + rootSchemaFile + ")" + e.getMessage());
        }

    }
}
