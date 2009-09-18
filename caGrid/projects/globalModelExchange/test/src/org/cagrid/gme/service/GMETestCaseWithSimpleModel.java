package org.cagrid.gme.service;

import gov.nih.nci.cagrid.common.Utils;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.cagrid.gme.test.GMETestCaseBase;
import org.cagrid.gme.test.SpringTestApplicationContextConstants;


public abstract class GMETestCaseWithSimpleModel extends GMETestCaseBase {

    // these are loaded by Spring
    protected XMLSchema testSchemaSimpleA;
    protected XMLSchema testSchemaSimpleB;
    protected XMLSchema testSchemaSimpleC;
    protected XMLSchema testSchemaSimpleD;
    protected XMLSchema testSchemaSimpleE;
    protected XMLSchema testSchemaSimpleF;


    @Override
    protected String[] getConfigLocations() {
        return (String[]) Utils.appendToArray(super.getConfigLocations(),
            SpringTestApplicationContextConstants.SIMPLE_LOCATION);
    }


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.testSchemaSimpleA);
        assertNotNull(this.testSchemaSimpleB);
        assertNotNull(this.testSchemaSimpleC);
        assertNotNull(this.testSchemaSimpleD);
        assertNotNull(this.testSchemaSimpleE);
        assertNotNull(this.testSchemaSimpleF);
    }


    protected void publishAllSchemas() throws InvalidSchemaSubmissionFault, NoSuchNamespaceExistsFault {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaSimpleA);
        schemas.add(this.testSchemaSimpleB);
        schemas.add(this.testSchemaSimpleC);
        schemas.add(this.testSchemaSimpleD);
        schemas.add(this.testSchemaSimpleE);
        schemas.add(this.testSchemaSimpleF);
        this.gme.publishSchemas(schemas);

        assertPublishedContents(schemas);
    }

}
