package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.iso21090.tests.integration.steps.BuildExampleProjectStep;
import org.cagrid.iso21090.tests.integration.steps.ConfigureExampleProjectStep;
import org.cagrid.iso21090.tests.integration.steps.NukeIvyCacheStep;

/**
 * Story that configures and builds 
 * the caCORE SDK example with ISO 21090 data types project
 * 
 * @author David
 */
public class CreateExampleProjectStory extends Story {
    
    public CreateExampleProjectStory() {
        super();
    }


    public String getDescription() {
        return "Configures and builds the caCORE SDK 4_3 with ISO 21090 datatypes example project";
    }
    
    
    public String getName() {
        return "caCORE SDK 4_3 Example Project With ISO 21090 Data Types Creation Story";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new NukeIvyCacheStep());
        steps.add(new ConfigureExampleProjectStep());
        steps.add(new BuildExampleProjectStep());
        return steps;
    }
}
