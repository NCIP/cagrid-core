package org.cagrid.tests.data.styles.cacore42.story;

import java.util.Vector;

import org.cagrid.tests.data.styles.cacore42.steps.NukeIvyCacheStep;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

public class NukeIvyCacheStory extends Story {

    public NukeIvyCacheStory() {
        super();
    }


    public String getDescription() {
        return "Deletes the Ivy cache used by the caCORE SDK build process";
    }
    
    
    public String getName() {
        return "Nuke Ivy Cache Story";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new NukeIvyCacheStep());
        return steps;
    }

}
