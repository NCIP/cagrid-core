package org.cagrid.data.sdkquery44.test;

import gov.nih.nci.cacoresdk.domain.other.datatype.CdDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.DsetAdDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.TelUrlDataType;
import gov.nih.nci.iso21090.NullFlavor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.sdkquery44.translator.ConstantValueResolver;
import org.cagrid.data.sdkquery44.translator.IsoDatatypesConstantValueResolver;

public class IsoDatatypesConstantResolverTestCase extends TestCase {
    
    private ConstantValueResolver resolver = null;
    
    public IsoDatatypesConstantResolverTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        resolver = new IsoDatatypesConstantValueResolver();
    }
    
    
    public void testCdStringConstant() {
        findConstant(CdDataType.class, "CODESYSTEM", "value3", "codeSystem");
    }
    
    
    public void testTelUrlNullFlavorConstant() {
        findConstant(TelUrlDataType.class, NullFlavor.NA, "value1", "nullFlavor");
    }
    
    
    public void testDsetAdStringConstant() {
        findConstant(DsetAdDataType.class, "CODESYSTEM", "value1", "item", "part_0", "codeSystem");
    }
    
    
    public void testCdNoConstantFound() {
        findConstant(CdDataType.class, null, "value4", "codeSystem");
    }
    
    
    public void testDsetAdNoConstantFound() {
        findConstant(DsetAdDataType.class, null, "value1", "item", "part_1", "codeSystem");
    }
    
    
    private void findConstant(Class<?> topLevel, Object expected, String... path) {
        List<String> pathList = new ArrayList<String>();
        for (String p : path) {
            pathList.add(p);
        }
        Object found = resolver.getConstantValue(topLevel.getName(), pathList);
        if (found == null && expected == null) {
            // expected
        } else {
            assertEquals("Unexpected constant value found", expected, found);
        }
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(IsoDatatypesConstantResolverTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
