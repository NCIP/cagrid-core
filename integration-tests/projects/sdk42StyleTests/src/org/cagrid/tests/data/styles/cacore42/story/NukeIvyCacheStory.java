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
