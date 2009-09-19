package org.cagrid.data.styles.cacore41.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.styles.cacore41.test.steps.SDK41StyleCreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  Sdk41ServiceStyleCreationTest
 *  Tests ability to create a SDK 4.1 style data service
 * 
 * @author David Ervin
 * 
 * @created Jan 29, 2008 9:09:34 AM
 * @version $Id: SDK41ServiceStyleCreationTest.java,v 1.2 2009-04-10 15:15:24 dervin Exp $ 
 */
public class SDK41ServiceStyleCreationTest extends Story {
    private DataTestCaseInfo styleTestCaseInfo = null;

    public SDK41ServiceStyleCreationTest() {
        super();
    }


    public String getDescription() {
        return "Tests ability to create a SDK 4.1 style data service";
    }
    
    
    public String getName() {
        return "SDK 4_1 Style Creation Test";
    }
    
    
    public boolean storySetUp() throws Throwable {
        styleTestCaseInfo = SDK41ServiceStyleSystemTestConstants.getTestServiceInfo();
        
        File serviceDir = new File(styleTestCaseInfo.getDir());
        serviceDir.mkdirs();
        
        return serviceDir.exists() && serviceDir.isDirectory();
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new SDK41StyleCreationStep(styleTestCaseInfo, getIntroduceBaseDir()));
        return steps;
    }
    
    
    public void storyTearDown() throws Throwable {
        Utils.deleteDir(new File(styleTestCaseInfo.getDir()));
    }
    

    public String getIntroduceBaseDir() {
        String dir = System.getProperty(SDK41ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        if (dir == null) {
            fail("Introduce base dir system property " + 
                SDK41ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY + " is required");
        }
        return dir;
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDK41ServiceStyleCreationTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
