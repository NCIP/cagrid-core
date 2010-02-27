package org.cagrid.tests.data.styles.cacore42.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import org.cagrid.tests.data.styles.cacore42.steps.BuildExampleProjectStep;
import org.cagrid.tests.data.styles.cacore42.steps.ConfigureExampleProjectStep;

/**
 * Story that configures and builds 
 * the caCORE SDK example project
 * 
 * @author David
 */
public class CreateExampleProjectStory extends Story {
    
    private File tempApplicationDir = null;
    private boolean enableCsm = false;

    public CreateExampleProjectStory(File tempApplicationDir, boolean enableCsm) {
        super();
        this.tempApplicationDir = tempApplicationDir;
        this.enableCsm = enableCsm;
    }


    public String getDescription() {
        return "Configures and builds the caCORE SDK 4_2 example project";
    }
    
    
    public String getName() {
        return "caCORE SDK 4_2 Example Project Creation Story";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new ConfigureExampleProjectStep(tempApplicationDir, enableCsm));
        steps.add(new BuildExampleProjectStep());
        return steps;
    }
}
