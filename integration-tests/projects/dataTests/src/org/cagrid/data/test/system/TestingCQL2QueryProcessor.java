package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.cql2.Cql2ExtensionPoint;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.cql.utilities.CQL2ResultsCreationUtil;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.extensionsupport.SupportedExtensions;
import org.cagrid.cql2.results.CQLQueryResults;
import org.projectmobius.bookstore.Book;
import org.projectmobius.bookstore.BookStore;

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
        List<?> results = getResultsList(query);
        String targetName = query.getCQLTargetObject().getClassName();
        CQLQueryResults queryResults = CQL2ResultsCreationUtil.createObjectResults(results, targetName, getQname(targetName));
        return queryResults;
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
    

    private List<?> getResultsList(CQLQuery query) throws QueryProcessingException {
        List<?> results = new LinkedList<Object>();
        String targetName = query.getCQLTargetObject().getClassName();
        if (targetName.equals(Book.class.getName())) {
            results = TestQueryResultsGenerator.getResultBooks();
        } else if (targetName.equals(BookStore.class.getName())) {
            results = TestQueryResultsGenerator.getResultBookStore();
        } else {
            throw new QueryProcessingException("Target " + targetName + " is not valid!");
        }
        return results;
    }
    
    
    private QName getQname(String targetClassname) {
        Mappings map = TestQueryResultsGenerator.getClassToQnameMappings();
        for (ClassToQname c2q : map.getMapping()) {
            if (c2q.getClassName().equals(targetClassname)) {
                return QName.valueOf(c2q.getQname());
            }
        }
        return null;
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
