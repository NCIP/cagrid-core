package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2Walker;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2WalkerDomainModelValidationHandler;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql2.CQLQuery;

public class Cql2DomainModelValidatorTestCase extends TestCase {
    
    public static final String TEST_DOCS_LOCATION = "test/resources";
    public static final String CQL_EXAMPLES_LOCATION = TEST_DOCS_LOCATION + "/testQueries";
    public static final String DOMAIN_MODEL_NAME = TEST_DOCS_LOCATION + "/isoExample_domainModel.xml";
    
    public static Set<String> EXPECTED_ERROR_DOCS = null;
    static {
        EXPECTED_ERROR_DOCS = new HashSet<String>();
        EXPECTED_ERROR_DOCS.add("singleAttributeFromCash.xml");
        EXPECTED_ERROR_DOCS.add("distinctAttributeFromCash.xml");
    }
    
    private DomainModel model = null;
    private CQL1toCQL2Converter converter = null;
    private Cql2Walker validator = null;
    
    public Cql2DomainModelValidatorTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        validator = new Cql2Walker();
        try {
            FileReader reader = new FileReader(DOMAIN_MODEL_NAME);
            model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            validator.addListener(new Cql2WalkerDomainModelValidationHandler(model));
            converter = new CQL1toCQL2Converter(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up validator: " + ex.getMessage());
        }
    }
    
    
    public void testExampleQueries() {
        File docsDir = new File(CQL_EXAMPLES_LOCATION);
        File[] queryDocs = docsDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() 
                    && pathname.getName().toLowerCase().endsWith(".xml");   
                }
            });
        Comparator<File> fileSorter = new Comparator<File>() {
            public int compare(File f1, File f2) {
                return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
            }
        };
        Arrays.sort(queryDocs, fileSorter);
        for (File queryDocument : queryDocs) {
            try {
                System.out.println("Testing " + queryDocument.getName());
                String oldText = Utils.fileToStringBuffer(queryDocument).toString();
                System.out.println("CQL 1 Query:\n" + oldText);
                gov.nih.nci.cagrid.cqlquery.CQLQuery oldCql = Utils.deserializeObject(new StringReader(oldText), gov.nih.nci.cagrid.cqlquery.CQLQuery.class);
                CQLQuery query = converter.convertToCql2Query(oldCql);
                String newText = CQL2SerializationUtil.serializeCql2Query(query);
                System.out.println("CQL 2 Query:\n" + newText);
                validator.walkCql(query);
                if (EXPECTED_ERROR_DOCS.contains(queryDocument.getName())) {
                    fail("Expected " + queryDocument.getName() + " to fail!");
                }
            } catch (Exception ex) {
                if (EXPECTED_ERROR_DOCS.contains(queryDocument.getName())) {
                    System.out.println("Yep, that was expected");
                } else {
                    ex.printStackTrace();
                    fail("Error validating CQL 2 query (" + queryDocument.getName() + "): " + ex.getMessage());
                }
            }
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(Cql2DomainModelValidatorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
