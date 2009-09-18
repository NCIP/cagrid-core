package org.cagrid.cacore.sdk4x.cql2.test.sdk411;

import org.cagrid.cacore.sdk4x.cql2.test.AbstractCQL2ToHQLConversionTestCase;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SDK411CQL2toHQLConversionTestCase extends AbstractCQL2ToHQLConversionTestCase {
    
    public static final String DOMAIN_MODEL_LOCATION = "test/docs/models/sdk411example_DomainModel.xml";
    
    public SDK411CQL2toHQLConversionTestCase(String name) {
        super(name);
    }
    
    
    public String getDomainModelFilename() {
        return DOMAIN_MODEL_LOCATION;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK411CQL2toHQLConversionTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
