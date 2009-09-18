package org.cagrid.gme.xerces;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.XSModel;
import org.cagrid.gme.common.XSDUtil;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.sax.GMEXMLSchemaLoader;
import org.cagrid.gme.service.dao.XMLSchemaInformationDao;
import org.w3c.dom.ls.LSInput;


public class XercesSchemaTestCase extends TestCase {

    public void testNoImports() {
        try {
            List<XMLSchema> schemas = new ArrayList<XMLSchema>();
            URI ns = new URI("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.common");
            schemas.add(XSDUtil.createSchema(ns, new File("test/resources/schema/cagrid/common/common.xsd")));

            XSModel model = loadSchemas(schemas, null);
            assertEquals(2, model.getNamespaceItems().getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    public void testMissingImportFailure() {
        testFailingSchema("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata",
            "test/resources/schema/cagrid/caGridMetadata.xsd");
    }


    public void testMissingType() {
        testFailingSchema("gme://missingtype", "test/resources/schema/invalid/missingtype.xsd");
    }


    public void testMissingInclude() {
        testFailingSchema("gme://missinginclude", "test/resources/schema/invalid/missinginclude.xsd");
    }


    public void testDuplicateElements() {
        testFailingSchema("gme://duplicateelements", "test/resources/schema/invalid/duplicateelements.xsd");
    }


    // public void testIncludes() {
    // try {
    //
    // XSModel model =
    // loadSchemas(GMETestSchemaBundles.getSimpleIncludeBundle(), null);
    //
    // assertEquals(2, model.getNamespaceItems().getLength());
    // } catch (Exception e) {
    // e.printStackTrace();
    // fail(e.getMessage());
    // }
    // }

    public void testImports() {
        try {

            List<XMLSchema> schemas = new ArrayList<XMLSchema>();
            URI ns1 = new URI("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice");
            schemas.add(XSDUtil.createSchema(ns1, new File("test/resources/schema/cagrid/data/data.xsd")));

            URI ns2 = new URI("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.common");
            schemas.add(XSDUtil.createSchema(ns2, new File("test/resources/schema/cagrid/common/common.xsd")));

            XSModel model = loadSchemas(schemas, null);
            // TODO: why is this 4, and not 3? how can we prevent it from
            // processing schemas from imports that we've already processed
            assertEquals(4, model.getNamespaceItems().getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    private void testFailingSchema(String namepace, String location) {
        assertNotNull("Cannot test a null namespace.", namepace);
        assertNotNull("Cannot test a null location.", location);
        try {
            List<XMLSchema> schemas = new ArrayList<XMLSchema>();
            URI ns1 = new URI(namepace);
            schemas.add(XSDUtil.createSchema(ns1, new File(location)));

            try {
                XSModel model = loadSchemas(schemas, null);
                fail("Parser should have thrown exception due to missing import!");
            } catch (XMLParseException e) {
                // expected
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    private static final XSModel loadSchemas(final List<XMLSchema> schemas, XMLSchemaInformationDao dao)
        throws IllegalArgumentException, XMLParseException {
        if (schemas == null) {
            throw new IllegalArgumentException("Schemas must be non null.");
        }

        LSInputList list = new LSInputList() {
            public LSInput item(int index) {
                DOMInputImpl input = new DOMInputImpl();
                input.setSystemId(schemas.get(index).getRootDocument().getSystemID());
                input.setStringData(schemas.get(index).getRootDocument().getSchemaText());
                return input;
            }


            public int getLength() {
                return schemas.size();
            }
        };

        GMEXMLSchemaLoader schemaLoader = new GMEXMLSchemaLoader(schemas, dao);

        XSModel model = schemaLoader.loadInputList(list);
        if (model == null) {
            throw schemaLoader.getErrorHandler().createXMLParseException();
        }

        return model;
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(XercesSchemaTestCase.class);
    }
}