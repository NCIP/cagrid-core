/*
 * Created on Jun 5, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.compare.BeanComparator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This is a unit test that validates the serialization and deserialization of
 * the ServiceMetadata class.
 * 
 * @testType unit
 * @author Patrick McConnell
 */
public class ServiceMetadataSerializationTest extends TestCase {
    public ServiceMetadataSerializationTest(String name) {
        super(name);
    }


    /**
     * Deserializes a ServiceMetadata XML document twice and makes sure that the
     * results are the same.
     */
    public void testSerialization() throws Exception {
        File metadataFile = new File(System.getProperty("ServiceMetadataSerializationTest.file", "test"
            + File.separator + "resources" + File.separator + "ServiceMetadataSerializationTest" + File.separator
            + "serviceMetadata.xml"));

        ServiceMetadata m1 = (ServiceMetadata) Utils
            .deserializeDocument(metadataFile.toString(), ServiceMetadata.class);
        ServiceMetadata m2 = (ServiceMetadata) Utils
            .deserializeDocument(metadataFile.toString(), ServiceMetadata.class);

        new BeanComparator(this).assertEquals(m1, m2);
    }


    public static void main(String[] args) throws Exception {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ServiceMetadataSerializationTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
