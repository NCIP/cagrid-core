package org.cagrid.tests.data.styles.cacore44.integration.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.tests.data.styles.cacore44.integration.steps.InvokeLocalIntegratedCqlStep;
import org.cagrid.tests.data.styles.cacore44.integration.steps.InvokeLocalTranslatedCqlStep;

public class TranslateAndExecuteCqlStory extends Story {
    
    public TranslateAndExecuteCqlStory() {
        super();
    }
    

    public String getDescription() {
        return "Translates CQL to HQL and runs it against the caCORE SDK local API";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new InvokeLocalTranslatedCqlStep());
        steps.add(new InvokeLocalIntegratedCqlStep());
        return steps;
    }
}
