package org.cagrid.tests.data.styles.cacore42.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import org.cagrid.tests.data.styles.cacore42.steps.BuildExampleProjectStep;
import org.cagrid.tests.data.styles.cacore42.steps.ConfigureExampleProjectStep;
import org.cagrid.tests.data.styles.cacore42.steps.NukeIvyCacheStep;
import org.cagrid.tests.data.styles.cacore42.steps.SdkDatabaseStep;
import org.cagrid.tests.data.styles.cacore42.steps.SdkDatabaseStep.DatabaseOperation;

/**
 * Story that configures and builds 
 * the caCORE SDK example project
 * 
 * @author David
 */
public class CreateExampleProjectStory extends Story {
    
    private File tempApplicationDir = null;

    public CreateExampleProjectStory(File tempApplicationDir) {
        super();
        this.tempApplicationDir = tempApplicationDir;
    }


    public String getDescription() {
        return "Configures and builds the caCORE SDK 4_2 example project";
    }
    
    
    public String getName() {
        return "caCORE SDK 4_2 Example Project Creation Story";
    }
    
    
    public boolean storySetUp() throws Throwable {
        boolean ok = false;
        try {
            new SdkDatabaseStep(DatabaseOperation.DESTROY).runStep();
            ok = true;
        } catch (Throwable th) {
            ok = false;
            throw th;
        }
        return ok;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new NukeIvyCacheStep());
        steps.add(new SdkDatabaseStep(DatabaseOperation.CREATE));
        steps.add(new ConfigureExampleProjectStep(tempApplicationDir));
        steps.add(new BuildExampleProjectStep());
        steps.add(new SdkDatabaseStep(DatabaseOperation.INSTALL));
        return steps;
    }
}
