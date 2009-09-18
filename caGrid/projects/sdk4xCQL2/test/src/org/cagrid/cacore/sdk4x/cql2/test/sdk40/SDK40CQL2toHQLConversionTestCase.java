package org.cagrid.cacore.sdk4x.cql2.test.sdk40;

import org.cagrid.cacore.sdk4x.cql2.test.AbstractCQL2ToHQLConversionTestCase;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SDK40CQL2toHQLConversionTestCase extends AbstractCQL2ToHQLConversionTestCase {
    
    public static final String DOMAIN_MODEL_LOCATION = "test/docs/models/sdk40example_DomainModel.xml";
    
    public SDK40CQL2toHQLConversionTestCase(String name) {
        super(name);
    }
    
    
    public String getDomainModelFilename() {
        return DOMAIN_MODEL_LOCATION;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK40CQL2toHQLConversionTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
