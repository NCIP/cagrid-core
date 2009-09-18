package org.cagrid.gme.service.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaDocument;
import org.cagrid.gme.service.domain.XMLSchemaInformation;
import org.cagrid.gme.test.GMEIntegrationTestCaseBase;


public class XMLSchemaInformationDaoTestCase extends GMEIntegrationTestCaseBase {

    protected XMLSchemaInformationDao xmlSchemaInformationDao;


    public void testWithoutDependencies() throws URISyntaxException {

        XMLSchemaInformation schemaInfo = new XMLSchemaInformation();
        XMLSchema schema = new XMLSchema();
        schemaInfo.setSchema(schema);

        URI namespace = new URI("http://foo");
        schema.setTargetNamespace(namespace);

        XMLSchemaDocument doc = new XMLSchemaDocument();
        doc.setSystemID("foo");
        doc.setSchemaText("my schema");

        XMLSchemaDocument doc2 = new XMLSchemaDocument();
        doc2.setSystemID("foo2");
        doc2.setSchemaText("my schema");

        schema.setRootDocument(doc);
        Set<XMLSchemaDocument> docSet = new HashSet<XMLSchemaDocument>();
        docSet.add(doc2);
        schema.setAdditionalSchemaDocuments(docSet);

        this.xmlSchemaInformationDao.save(schemaInfo);

        Collection<URI> allNamespaces = this.xmlSchemaInformationDao.getAllNamespaces();
        assertEquals(1, allNamespaces.size());
        assertTrue(allNamespaces.contains(namespace));

        XMLSchema schemaForURI = this.xmlSchemaInformationDao.getXMLSchemaByTargetNamespace(namespace);
        assertEquals(schema, schemaForURI);

        Collection<XMLSchemaInformation> dependingSchemas = this.xmlSchemaInformationDao
            .getDependingXMLSchemaInformation(namespace);
        assertEquals(0, dependingSchemas.size());
    }
}
