package org.cagrid.gme.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;
import org.cagrid.gme.stubs.types.UnableToDeleteSchemaFault;
import org.springframework.test.annotation.ExpectedException;


public class GMEDeletesSchemaSimpleTestCase extends GMETestCaseWithSimpleModel {

    public void testSingleDelete() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaSimpleF);
        this.gme.publishSchemas(schemas);
        assertPublishedContents(schemas);

        this.gme.deleteSchemas(this.gme.getNamespaces());
        assertPublishedContents(new ArrayList<XMLSchema>());
        assertNotPublished(schemas);
    }


    public void testDeleteWithImport() throws Exception {
        List<XMLSchema> schemas = new ArrayList<XMLSchema>();
        schemas.add(this.testSchemaSimpleB);
        schemas.add(this.testSchemaSimpleC);
        schemas.add(this.testSchemaSimpleF);
        this.gme.publishSchemas(schemas);
        assertPublishedContents(schemas);

        assertSchemaImportsSchema(this.testSchemaSimpleB, this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleF);
        assertNotImported(this.testSchemaSimpleF);

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleB.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);

        List<XMLSchema> expected = new ArrayList<XMLSchema>();
        expected.add(this.testSchemaSimpleC);
        expected.add(this.testSchemaSimpleF);
        assertPublishedContents(expected);
        assertNotPublished(this.testSchemaSimpleB);

        assertNoImports(this.testSchemaSimpleC);
        assertNotImported(this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleF);
        assertNotImported(this.testSchemaSimpleF);
    }


    public void testDeleteA() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleA.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);

        List<XMLSchema> expected = new ArrayList<XMLSchema>();
        expected.add(this.testSchemaSimpleB);
        expected.add(this.testSchemaSimpleC);
        expected.add(this.testSchemaSimpleD);
        expected.add(this.testSchemaSimpleE);
        expected.add(this.testSchemaSimpleF);
        assertPublishedContents(expected);
        assertNotPublished(this.testSchemaSimpleA);

        assertSchemaImportsSchema(this.testSchemaSimpleB, this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleC);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleB);
        assertNotImported(this.testSchemaSimpleD);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleF);
        assertNotImported(this.testSchemaSimpleF);
    }


    public void testDeleteAF() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleA.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleF.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);

        List<XMLSchema> expected = new ArrayList<XMLSchema>();
        expected.add(this.testSchemaSimpleB);
        expected.add(this.testSchemaSimpleC);
        expected.add(this.testSchemaSimpleD);
        expected.add(this.testSchemaSimpleE);
        assertPublishedContents(expected);

        List<XMLSchema> notExpected = new ArrayList<XMLSchema>();
        notExpected.add(this.testSchemaSimpleA);
        notExpected.add(this.testSchemaSimpleF);
        assertNotPublished(notExpected);

        assertSchemaImportsSchema(this.testSchemaSimpleB, this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleC);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleB);
        assertNotImported(this.testSchemaSimpleD);
        assertSchemaImportsSchema(this.testSchemaSimpleD, this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleE);
    }


    public void testDeleteABD() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleA.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleB.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleD.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);

        List<XMLSchema> expected = new ArrayList<XMLSchema>();
        expected.add(this.testSchemaSimpleC);
        expected.add(this.testSchemaSimpleE);
        expected.add(this.testSchemaSimpleF);
        assertPublishedContents(expected);

        List<XMLSchema> notExpected = new ArrayList<XMLSchema>();
        notExpected.add(this.testSchemaSimpleA);
        notExpected.add(this.testSchemaSimpleB);
        notExpected.add(this.testSchemaSimpleD);
        assertNotPublished(notExpected);

        assertNoImports(this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleE);
        assertNoImports(this.testSchemaSimpleF);

    }


    public void testDeleteDE() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleD.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleE.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);

        List<XMLSchema> expected = new ArrayList<XMLSchema>();
        expected.add(this.testSchemaSimpleA);
        expected.add(this.testSchemaSimpleB);
        expected.add(this.testSchemaSimpleC);
        expected.add(this.testSchemaSimpleF);
        assertPublishedContents(expected);

        List<XMLSchema> notExpected = new ArrayList<XMLSchema>();
        notExpected.add(this.testSchemaSimpleD);
        notExpected.add(this.testSchemaSimpleE);
        assertNotPublished(notExpected);

        assertSchemaImportsSchema(this.testSchemaSimpleA, this.testSchemaSimpleB);
        assertNotImported(this.testSchemaSimpleA);
        assertSchemaImportsSchema(this.testSchemaSimpleB, this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleC);
        assertNoImports(this.testSchemaSimpleF);
        assertNotImported(this.testSchemaSimpleF);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testDeleteEmpty() throws Exception {
        List<URI> schemasToDelete = new ArrayList<URI>();
        this.gme.deleteSchemas(schemasToDelete);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testDeleteNullError() throws Exception {
        this.gme.deleteSchemas(null);
    }


    @ExpectedException(NoSuchNamespaceExistsFault.class)
    public void testDeleteBeforePublishError() throws Exception {
        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleF.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);
    }


    @ExpectedException(UnableToDeleteSchemaFault.class)
    public void testDeleteErrorsWithImport() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleB.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);
    }


    @ExpectedException(UnableToDeleteSchemaFault.class)
    public void testDeleteErrorsWithImportAndOthers() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleB.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleD.getTargetNamespace());
        schemasToDelete.add(this.testSchemaSimpleF.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);
    }


    @ExpectedException(UnableToDeleteSchemaFault.class)
    public void testDeleteErrorsWithNestedImport() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleC.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);
    }


    @ExpectedException(UnableToDeleteSchemaFault.class)
    public void testDeleteErrorsWithMultipleImports() throws Exception {
        publishAllSchemas();

        List<URI> schemasToDelete = new ArrayList<URI>();
        schemasToDelete.add(this.testSchemaSimpleE.getTargetNamespace());
        this.gme.deleteSchemas(schemasToDelete);
    }

}
