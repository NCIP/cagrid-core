/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.iso21090.tests.integration.steps.InvokeLocalIntegratedCqlStep;
import org.cagrid.iso21090.tests.integration.steps.InvokeLocalTranslatedCqlStep;

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
