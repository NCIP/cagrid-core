/*
 * Created on Aug 2, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

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
