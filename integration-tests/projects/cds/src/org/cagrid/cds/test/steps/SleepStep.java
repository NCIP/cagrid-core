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
/*
 * Created on Aug 2, 2006
 */
package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;


/**
 * This step causes the test to sleep for a given amount of time before
 * continuing.
 * 
 * @author Patrick McConnell
 */
public class SleepStep extends Step {
    private long millisec;


    public SleepStep() {
        this(3000);
    }


    public SleepStep(long millisec) {
        super();

        this.millisec = millisec;
    }


    @Override
    public void runStep() throws Throwable {
        Thread.sleep(this.millisec);
    }
}
