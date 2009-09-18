package org.cagrid.gme.test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaImportInformation;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.service.GME;
import org.cagrid.gme.stubs.types.NoSuchNamespaceExistsFault;


public abstract class GMETestCaseBase extends GMEIntegrationTestCaseBase {
    protected GME gme;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.gme);
    }


    protected void assertPublishedContents(XMLSchema schema) throws NoSuchNamespaceExistsFault {
        List<XMLSchema> list = new ArrayList<XMLSchema>(1);
        list.add(schema);
        assertPublishedContents(list);
    }


    protected void assertPublishedContents(List<XMLSchema> schemas) throws NoSuchNamespaceExistsFault {
        Collection<URI> namespaces = this.gme.getNamespaces();
        assertEquals(schemas.size(), namespaces.size());

        for (XMLSchema schema : schemas) {
            namespaces.contains(schema.getTargetNamespace());
            assertEquals(schema, this.gme.getSchema(schema.getTargetNamespace()));
        }
    }


    protected void assertSchemaImportsSchema(XMLSchema importer, XMLSchema imported) throws NoSuchNamespaceExistsFault {
        Collection<URI> importedNamespaces = this.gme.getImportedNamespaces(importer.getTargetNamespace());
        assertTrue(importedNamespaces.contains(imported.getTargetNamespace()));

        Collection<URI> importingNamespaces = this.gme.getImportingNamespaces(imported.getTargetNamespace());
        assertTrue(importingNamespaces.contains(importer.getTargetNamespace()));

        XMLSchemaBundle schemaBundle = this.gme.getSchemBundle(importer.getTargetNamespace());
        XMLSchemaImportInformation ii = schemaBundle.getImportInformationForTargetNamespace(new XMLSchemaNamespace(
            importer.getTargetNamespace()));
        assertTrue(ii.getImports().contains(new XMLSchemaNamespace(imported.getTargetNamespace())));
    }


    protected void assertNotPublished(XMLSchema schema) throws NoSuchNamespaceExistsFault {
        List<XMLSchema> list = new ArrayList<XMLSchema>(1);
        list.add(schema);
        assertNotPublished(list);
    }


    protected void assertNotPublished(List<XMLSchema> schemas) throws NoSuchNamespaceExistsFault {
        for (XMLSchema schema : schemas) {
            // Make sure the namespace isn't in the list
            assertFalse(this.gme.getNamespaces().contains(schema.getTargetNamespace()));

            // Make sure an error is raised when it's asked to be returned
            try {
                this.gme.getSchema(schema.getTargetNamespace());
                fail("The deleted schema should no longer exist, but was returned.");
            } catch (NoSuchNamespaceExistsFault f) {
                // expected
            }
        }
    }


    protected void assertNoImports(XMLSchema schema) throws NoSuchNamespaceExistsFault {
        assertEquals(0, this.gme.getImportedNamespaces(schema.getTargetNamespace()).size());
    }


    protected void assertNotImported(XMLSchema schema) throws NoSuchNamespaceExistsFault {
        assertEquals(0, this.gme.getImportingNamespaces(schema.getTargetNamespace()).size());
    }
}
