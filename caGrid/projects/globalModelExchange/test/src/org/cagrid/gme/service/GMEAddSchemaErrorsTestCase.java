package org.cagrid.gme.service;

import gov.nih.nci.cagrid.common.Utils;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.test.GMETestCaseBase;
import org.cagrid.gme.test.SpringTestApplicationContextConstants;
import org.springframework.test.annotation.ExpectedException;


public class GMEAddSchemaErrorsTestCase extends GMETestCaseBase {

    // these are loaded by Spring
    protected XMLSchema testSchemaDuplicates;
    protected XMLSchema testSchemaMissingInclude;
    protected XMLSchema testSchemaMissingType;
    protected XMLSchema testSchemaNoNamespace;
    protected XMLSchema testSchemaWrongNamespace;
    protected XMLSchema testSchemaNoImports;


    @Override
    protected String[] getConfigLocations() {
        return (String[]) Utils.appendToArray(super.getConfigLocations(),
            SpringTestApplicationContextConstants.ERRORS_LOCATION);
    }


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.testSchemaDuplicates);
        assertNotNull(this.testSchemaMissingInclude);
        assertNotNull(this.testSchemaMissingType);
        assertNotNull(this.testSchemaNoNamespace);
        assertNotNull(this.testSchemaWrongNamespace);
        assertNotNull(this.testSchemaNoImports);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testEmptySubmission() throws Exception {
        this.gme.publishSchemas(new ArrayList<XMLSchema>());
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testNullSubmission() throws Exception {
        this.gme.publishSchemas(null);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaDuplicates() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaDuplicates);
        this.gme.publishSchemas(schemas);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaMissingInclude() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaMissingInclude);
        this.gme.publishSchemas(schemas);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaMissingType() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaMissingType);
        this.gme.publishSchemas(schemas);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaNoNamespace() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaNoNamespace);
        this.gme.publishSchemas(schemas);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaWrongNamespace() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaWrongNamespace);
        this.gme.publishSchemas(schemas);

    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testSchemaNoImports() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaNoImports);
        this.gme.publishSchemas(schemas);

    }

}
