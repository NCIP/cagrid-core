package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.cql2.validation.Cql2DomainValidator;
import gov.nih.nci.cagrid.data.cql2.validation.DomainModelCql2DomainValidator;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql2.CQLQuery;

public class Cql2DomainModelValidatorTestCase extends TestCase {
    
    public static final String TEST_DOCS_LOCATION = "ext" + File.separator + "dependencies" + File.separator + "test" + File.separator + "docs";
    public static final String CQL2_EXAMPLES_LOCATION = TEST_DOCS_LOCATION + File.separator + "cql2Examples";
    public static final String SDK40_DOMAIN_MODEL_NAME = TEST_DOCS_LOCATION + File.separator + "models" + File.separator + "sdk40example_DomainModel.xml";
    
    private Cql2DomainValidator validator = null;
    
    public Cql2DomainModelValidatorTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            FileReader reader = new FileReader(SDK40_DOMAIN_MODEL_NAME);
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            validator = new DomainModelCql2DomainValidator(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up validator: " + ex.getMessage());
        }
    }
    
    
    public void testExampleQueries() {
        File docsDir = new File(CQL2_EXAMPLES_LOCATION);
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
                String text = Utils.fileToStringBuffer(queryDocument).toString();
                CQLQuery query = CQL2SerializationUtil.deserializeCql2Query(text);
                validator.validateAgainstDomainModel(query);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error validating CQL 2 query (" + queryDocument.getName() + "): " + ex.getMessage());
            }
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(Cql2DomainModelValidatorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
