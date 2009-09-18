package org.cagrid.gme.service;

import gov.nih.nci.cagrid.common.Utils;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.test.GMETestCaseBase;
import org.cagrid.gme.test.SpringTestApplicationContextConstants;
import org.springframework.test.annotation.ExpectedException;


public class GMEAddSchemaRedefinesTestCase extends GMETestCaseBase {

    // these are loaded by Spring
    protected XMLSchema testSchemaRedefine;
    protected XMLSchema testSchemaRedefined;
    protected XMLSchema testSchemaRedefineNoNamespace;
    protected XMLSchema testInvalidSchemaRedefineWrongNamespace;
    protected XMLSchema testSchemaRedefineWrongNamespaceRedefinedOnly;


    @Override
    protected String[] getConfigLocations() {
        return (String[]) Utils.appendToArray(super.getConfigLocations(),
            SpringTestApplicationContextConstants.REDEFINES_LOCATION);
    }


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.testSchemaRedefine);
        assertNotNull(this.testSchemaRedefined);
        assertNotNull(this.testSchemaRedefineNoNamespace);
        assertNotNull(this.testInvalidSchemaRedefineWrongNamespace);
        assertNotNull(this.testSchemaRedefineWrongNamespaceRedefinedOnly);
    }


    public void testSchemaRedefine() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaRedefine);
        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);

    }


    public void testSchemaRedefined() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaRedefined);
        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }


    public void testSchemaRedefineNoNamespace() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaRedefineNoNamespace);
        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }


    public void testSchemaRedefineWrongNamespaceRedefinedOnly() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaRedefineWrongNamespaceRedefinedOnly);
        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }


    @ExpectedException(value = InvalidSchemaSubmissionFault.class)
    public void testInvalidSchemaRedefineWrongNamespace() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testInvalidSchemaRedefineWrongNamespace);
        this.gme.publishSchemas(schemas);
    }

}
