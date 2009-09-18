package gov.nih.nci.cagrid.validator;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

/** 
 *  ServiceValidationStory
 *  Story for service validation
 * 
 * @author David Ervin
 * 
 * @created Aug 28, 2007 10:33:18 AM
 * @version $Id: ServiceValidationStory.java,v 1.2 2008-03-25 20:04:01 dervin Exp $ 
 */
public class ServiceValidationStory extends Story {
    
    private String name;
    private String description;
    private Vector<Step> setUpSteps;
    private Vector<Step> tests;
    private Vector<Step> tearDownSteps;

    public ServiceValidationStory(String name, String desc, 
        final Vector<Step> setUp, final Vector<Step> tests, final Vector<Step> tearDown) {
        super();
        this.name = name;
        this.description = desc;        
        this.setUpSteps = setUp;
        this.tests = tests;
        this.tearDownSteps = tearDown;
    }
    
    
    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }
    
    
    protected boolean storySetUp() throws Throwable {
        if (setUpSteps != null) {
            for (Step step : setUpSteps) {
                step.runStep();
            }
        }
        return true;
    }
    
    
    public Vector steps() {
        return tests; 
    }
    
    
    protected void storyTearDown() throws Throwable {
        super.storyTearDown();
        if (tearDownSteps != null) {
            for (Step step : tearDownSteps) {
                step.runStep();
            }
        }
    }
}
