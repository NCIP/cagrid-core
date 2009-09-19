package org.cagrid.data.style.test.cacore31;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;

/** 
 *  SDK31StyleCreationStory
 *  Tests creating a caGrid Data Service using the SDK31 service style
 * 
 * @author David Ervin
 * 
 * @created Jul 18, 2007 2:35:15 PM
 * @version $Id: SDK31StyleCreationStory.java,v 1.4 2008-11-05 21:02:55 dervin Exp $ 
 */
public class SDK31StyleCreationStory extends Story {
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    
    private DataTestCaseInfo tci = null;

    public SDK31StyleCreationStory() {
        setName("Data Service Creation with caCORE 3_1 Style");
    }


    public String getDescription() {
        return "A test for creating a caGrid data service using the caCORE 3.1 service style";
    }
    
    
    public String getName() {
        return "Data Service Creation with caCORE 3_1 Style";
    }
    
    
    private String getIntroduceBaseDir() {
        String dir = System.getProperty(INTRODUCE_DIR_PROPERTY);
        if (dir == null) {
            fail("Introduce base dir environment variable " + INTRODUCE_DIR_PROPERTY + " is required");
        }
        return dir;
    }
    
    
    public boolean storySetUp() {
        this.tci = new DataTestCaseInfo() {
            public String getServiceDirName() {
                return getName();
            }

            
            public String getName() {
                return "TestCaCORE31StyleService";
            }

            
            public String getNamespace() {
                return "http://" + getPackageName() + "/" + getName();
            }
            

            public String getPackageName() {
                return "gov.nih.nci.cagrid.data.style.test.cacore31";
            }
        };

        return true;
    }
    

    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new DeleteOldServiceStep(tci));
        steps.add(new CreateSDK31StyleServiceStep(
            tci, getIntroduceBaseDir()));
        return steps;
    }
    
    
    protected void storyTearDown() throws Throwable {
        Step deleteStep = new DeleteOldServiceStep(tci);
        deleteStep.runStep();
    }
}
