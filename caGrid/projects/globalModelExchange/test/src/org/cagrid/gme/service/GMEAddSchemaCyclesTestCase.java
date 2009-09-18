package org.cagrid.gme.service;

import gov.nih.nci.cagrid.common.Utils;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.test.GMETestCaseBase;
import org.cagrid.gme.test.SpringTestApplicationContextConstants;
import org.springframework.test.annotation.ExpectedException;


public class GMEAddSchemaCyclesTestCase extends GMETestCaseBase {

    // these are loaded by Spring
    protected XMLSchema testSchemaCycleA;
    protected XMLSchema testSchemaCycleB;


    @Override
    protected String[] getConfigLocations() {
        return (String[]) Utils.appendToArray(super.getConfigLocations(),
            SpringTestApplicationContextConstants.CYCLES_LOCATION);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testCycleAMissingDocumentB() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaCycleA);
        this.gme.publishSchemas(schemas);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testCycleBMissingDocumentA() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaCycleB);
        this.gme.publishSchemas(schemas);
    }


    public void testCyclesAFirst() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaCycleA);
        schemas.add(this.testSchemaCycleB);

        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }


    public void testCyclesBFirst() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaCycleB);
        schemas.add(this.testSchemaCycleA);

        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }
}
