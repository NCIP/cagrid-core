package org.cagrid.gme.service;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.stubs.types.InvalidSchemaSubmissionFault;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.cagrid.gme.test.GMETestCaseBase;
import org.springframework.test.annotation.ExpectedException;


/**
 * Probes the GME grid service interface for issues dealing with the conversion
 * to/from empty arrays and dealing with null. Correct function of the
 * conversion dealing with real data is tested by the system tests against a
 * deployed service.
 */

public class GMEServiceInterfaceErrorsTestCase extends GMETestCaseBase {

    protected GlobalModelExchangeImpl serviceImpl = null;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        this.serviceImpl = new GlobalModelExchangeImpl(this.gme);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testEmptySubmission() throws Exception {
        this.serviceImpl.publishXMLSchemas(new XMLSchema[0]);
    }


    @ExpectedException(InvalidSchemaSubmissionFault.class)
    public void testNullSubmission() throws Exception {
        this.serviceImpl.publishXMLSchemas(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullDelete() throws Exception {
        this.serviceImpl.deleteXMLSchemas(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testEmptyDelete() throws Exception {
        this.serviceImpl.deleteXMLSchemas(new XMLSchemaNamespace[0]);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullImported() throws Exception {
        this.serviceImpl.getImportedXMLSchemaNamespaces(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullImporting() throws Exception {
        this.serviceImpl.getImportingXMLSchemaNamespaces(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidImported() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace("http://invalid");
        this.serviceImpl.getImportedXMLSchemaNamespaces(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidImporting() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace("http://invalid");
        this.serviceImpl.getImportingXMLSchemaNamespaces(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testEmptyImported() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace();
        this.serviceImpl.getImportedXMLSchemaNamespaces(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testEmptyImporting() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace();
        this.serviceImpl.getImportingXMLSchemaNamespaces(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testEmptyGet() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace();
        this.serviceImpl.getXMLSchema(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullGet() throws Exception {
        this.serviceImpl.getXMLSchema(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidGet() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace("http://invalid");
        this.serviceImpl.getXMLSchema(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testInvalidBundle() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace("http://invalid");
        this.serviceImpl.getXMLSchemaAndDependencies(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testEmptyBundle() throws Exception {
        XMLSchemaNamespace ns = new XMLSchemaNamespace();
        this.serviceImpl.getXMLSchemaAndDependencies(ns);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testNullBundle() throws Exception {
        this.serviceImpl.getXMLSchemaAndDependencies(null);
    }


    public void testGetNamespaces() throws Exception {
        XMLSchemaNamespace[] schemaNamespaces = this.serviceImpl.getXMLSchemaNamespaces();
        assertEquals(0, schemaNamespaces.length);
    }

}
