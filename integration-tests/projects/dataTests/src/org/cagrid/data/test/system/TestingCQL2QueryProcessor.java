package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.cql2.Cql2ExtensionPoint;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.extensionsupport.SupportedExtensions;
import org.cagrid.cql2.results.CQLQueryResults;

/**
 * TestingCQL2QueryProcessor
 * CQL 2 query processor for really simple tests
 * 
 * @author David
 */
public class TestingCQL2QueryProcessor extends CQL2QueryProcessor {
    
    public static final String TEST_EXTENSION_NAMESPACE = "http://org.cagrid.cql2.testing/TestCql2Extension";

    public TestingCQL2QueryProcessor() {
        super();
    }


    public CQLQueryResults processQuery(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Collection<QName> getSupportedExtensions(Cql2ExtensionPoint point) {
        QName[] names = null;
        switch (point) {
            case ATTRIBUTE:
                names = getTestingSupportedExtensionsBean().getAttributeExtension();
                break;
            case MODIFIER:
                names = getTestingSupportedExtensionsBean().getModifierExtension();
                break;
            case OBJECT:
                names = getTestingSupportedExtensionsBean().getObjectExtension();
                break;
            case RESULT:
                names = getTestingSupportedExtensionsBean().getResultExtension();
                break;
        }
        return Arrays.asList(names);
    }
    
    
    public static SupportedExtensions getTestingSupportedExtensionsBean() {
        SupportedExtensions support = new SupportedExtensions();
        support.setAttributeExtension(new QName[] {
            new QName(TEST_EXTENSION_NAMESPACE, "Attrib1"),
            new QName(TEST_EXTENSION_NAMESPACE, "Attrib2")
        });
        support.setModifierExtension(new QName[0]);
        support.setObjectExtension(new QName[] {
            new QName(TEST_EXTENSION_NAMESPACE, "Object1"),
            new QName(TEST_EXTENSION_NAMESPACE, "Object2"),
            new QName(TEST_EXTENSION_NAMESPACE, "Object3"),
            new QName(TEST_EXTENSION_NAMESPACE, "Object4")
        });
        support.setResultExtension(new QName[] {
            new QName(TEST_EXTENSION_NAMESPACE, "Results1")
        });
        return support;
    }
}
