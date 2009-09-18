package org.cagrid.gme.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;


/**
 * Tests the correct functionality of the utility methods of the XMLSchemaBundle
 * Class
 */
public class XMLSchemaBundleTestCase extends TestCase {

    private XMLSchemaNamespace nonExistantNamespace;

    private XMLSchemaNamespace s1_ns;
    private URI s1_uri;
    private XMLSchemaDocument s1_d1;
    private XMLSchemaDocument s1_d2;
    private XMLSchema s1;

    private XMLSchemaNamespace s2_ns;
    private URI s2_uri;
    private XMLSchemaDocument s2_d1;
    private XMLSchema s2;

    private XMLSchemaNamespace s3_ns;
    private URI s3_uri;
    private XMLSchema s3;
    private XMLSchemaDocument s3_d1;


    public void testEmptyBundle() {
        XMLSchemaBundle bundle = new XMLSchemaBundle();
        validateEmptyBundle(bundle);

        bundle.setImportInformation(null);
        bundle.setXMLSchemas(null);
        validateEmptyBundle(bundle);
    }


    public void testInvalidLookups() {
        XMLSchemaBundle bundle = new XMLSchemaBundle();

        Set<XMLSchema> xmlSchemaCollection = new HashSet<XMLSchema>();
        xmlSchemaCollection.add(this.s1);
        bundle.setXMLSchemas(xmlSchemaCollection);

        assertNull(bundle.getImportedXMLSchemasForTargetNamespace(this.nonExistantNamespace));
        assertNull(bundle.getXMLSchemaForTargetNamespace(this.nonExistantNamespace));
    }


    public void testValidLookupsNoImports() {
        XMLSchemaBundle bundle = new XMLSchemaBundle();

        Set<XMLSchema> xmlSchemaCollection = new HashSet<XMLSchema>();
        xmlSchemaCollection.add(this.s1);
        bundle.setXMLSchemas(xmlSchemaCollection);

        assertNull(bundle.getImportedXMLSchemasForTargetNamespace(this.s1_ns));
        assertNull(bundle.getImportInformationForTargetNamespace(this.s1_ns));
        assertNotNull(bundle.getXMLSchemaForTargetNamespace(this.s1_ns));
        assertEquals(this.s1, bundle.getXMLSchemaForTargetNamespace(this.s1_ns));

        Set<XMLSchemaNamespace> expected = new HashSet<XMLSchemaNamespace>();
        expected.add(this.s1_ns);
        assertEquals(expected, bundle.getXMLSchemaTargetNamespaces());
    }


    public void testValidLookupsWithImports() {
        XMLSchemaBundle bundle = new XMLSchemaBundle();

        // s1,s2
        Set<XMLSchema> xmlSchemaCollection = new HashSet<XMLSchema>();
        xmlSchemaCollection.add(this.s1);
        xmlSchemaCollection.add(this.s2);
        bundle.setXMLSchemas(xmlSchemaCollection);

        // s1 imports s2
        Set<XMLSchemaImportInformation> iiSet = new HashSet<XMLSchemaImportInformation>();
        XMLSchemaImportInformation ii = new XMLSchemaImportInformation();
        ii.setTargetNamespace(this.s1_ns);
        Set<XMLSchemaNamespace> imports = new HashSet<XMLSchemaNamespace>();
        imports.add(this.s2_ns);
        ii.setImports(imports);
        iiSet.add(ii);
        bundle.setImportInformation(iiSet);

        // should be able to retrieve s1's imports
        assertNotNull(bundle.getImportInformationForTargetNamespace(this.s1_ns));
        assertEquals(ii, bundle.getImportInformationForTargetNamespace(this.s1_ns));
        // should be able to see s2 has no imports
        assertNull(bundle.getImportInformationForTargetNamespace(this.s2_ns));

        // should be able to get back the s2 schema as an imported schema from
        // s1
        assertNotNull(bundle.getImportedXMLSchemasForTargetNamespace(this.s1_ns));
        Set<XMLSchema> resultSchemas = new HashSet<XMLSchema>();
        resultSchemas.add(this.s2);
        assertEquals(resultSchemas, bundle.getImportedXMLSchemasForTargetNamespace(this.s1_ns));
        // should be able to see s2 has no imports
        assertNull(bundle.getImportedXMLSchemasForTargetNamespace(this.s2_ns));

        // should be able to get back s1 by namespace
        assertNotNull(bundle.getXMLSchemaForTargetNamespace(this.s1_ns));
        assertEquals(this.s1, bundle.getXMLSchemaForTargetNamespace(this.s1_ns));
        // should be able to get back s2 by namespace
        assertNotNull(bundle.getXMLSchemaForTargetNamespace(this.s2_ns));
        assertEquals(this.s2, bundle.getXMLSchemaForTargetNamespace(this.s2_ns));

        // should be able to list the namespaces in the bundle as s1 and s2
        Set<XMLSchemaNamespace> expected = new HashSet<XMLSchemaNamespace>();
        expected.add(this.s1_ns);
        expected.add(this.s2_ns);
        assertEquals(expected, bundle.getXMLSchemaTargetNamespaces());
    }


    private void validateEmptyBundle(XMLSchemaBundle bundle) {
        assertNull(bundle.getImportedXMLSchemasForTargetNamespace(this.nonExistantNamespace));
        assertNull(bundle.getImportInformationForTargetNamespace(this.nonExistantNamespace));
        assertNull(bundle.getXMLSchemaForTargetNamespace(this.nonExistantNamespace));

        assertNotNull(bundle.getImportInformation());
        assertEquals(0, bundle.getImportInformation().size());

        assertNotNull(bundle.getXMLSchemas());
        assertEquals(0, bundle.getXMLSchemas().size());

        assertNotNull(bundle.getXMLSchemaTargetNamespaces());
        assertEquals(0, bundle.getXMLSchemaTargetNamespaces().size());
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(XMLSchemaBundleTestCase.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            this.nonExistantNamespace = new XMLSchemaNamespace("http://noschema");

            // SCHEMA 1 (root = s1_d1, docs=(s1_d2))
            this.s1_uri = new URI("http://s1");
            this.s1_ns = new XMLSchemaNamespace(this.s1_uri);

            this.s1_d1 = new XMLSchemaDocument();
            this.s1_d1.setSchemaText("<xml>This is the s1 schema, document s1_d1 text</xml>");
            this.s1_d1.setSystemID("s1_d1");

            this.s1_d2 = new XMLSchemaDocument();
            this.s1_d2.setSchemaText("<xml>This is the s1 schema, document s1_d2 text</xml>");
            this.s1_d2.setSystemID("s1_d2");

            this.s1 = new XMLSchema();
            this.s1.setRootDocument(this.s1_d1);
            this.s1.setTargetNamespace(this.s1_uri);
            Set<XMLSchemaDocument> s1_docs = new HashSet<XMLSchemaDocument>();
            this.s1.setAdditionalSchemaDocuments(s1_docs);

            // SCHEMA 2 (root = s2_d1, docs=())
            this.s2_uri = new URI("http://s2");
            this.s2_ns = new XMLSchemaNamespace(this.s2_uri);

            this.s2_d1 = new XMLSchemaDocument();
            this.s2_d1.setSchemaText("<xml>This is the s2 schema, document s2_d1 text</xml>");
            this.s2_d1.setSystemID("s2_d1");

            this.s2 = new XMLSchema();
            this.s2.setRootDocument(this.s2_d1);

            this.s2.setTargetNamespace(this.s2_uri);

            // SCHEMA 3 (root = s3_d1, docs=())
            this.s3_uri = new URI("http://s3");
            this.s3_ns = new XMLSchemaNamespace(this.s3_uri);

            this.s3_d1 = new XMLSchemaDocument();
            this.s3_d1.setSchemaText("<xml>This is the s3 schema, document s3_d1 text</xml>");
            this.s3_d1.setSystemID("s3_d1");

            this.s3 = new XMLSchema();
            this.s3.setRootDocument(this.s3_d1);

            this.s3.setTargetNamespace(this.s3_uri);

        } catch (URISyntaxException e) {
            fail("Unable to contruct namespaces");
        }
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
