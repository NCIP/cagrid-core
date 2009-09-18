package gov.nih.nci.cagrid.testing.system.haste;

import java.util.*;

import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

/*
 * HASTE - High-level Automated System Test Environment Copyright (C) 2002
 * Atomic Object, LLC. This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA Contact Atomic Object: Atomic
 * Object, LLC. East Building Suite 190 419 Norwood Ave SE Grand Rapids, MI
 * 49506 USA info@atomicobject.com
 */

/**
 * A Story executes an ordered sequence of Steps. Story is the system-test-level
 * analogue of JUnit's TestCase.
 * 
 * @version $Revision: 1.3 $
 */
@RunWith(JUnit38ClassRunner.class)
public abstract class Story extends junit.framework.TestCase {


    /** The sequence of executable test steps */
    Vector mySteps;

    /** The name of the current step */
    String stepName;
    
    /** The success or failure of storySetUp(), called by setUp() */
    boolean storySetupSuccess;
    
    /** The failure from a story step */
    Throwable stepsFailedThrowable;
    
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        try {
            storySetupSuccess = storySetUp();
        } catch (Throwable e) {
        	// ensure the exception gets printed, JUnit won't always do this
        	System.err.println("Caught exception during set up. Reason: " 
	        	+ e.getMessage());
        	e.printStackTrace();
            throw new Exception(e);
        }
    }


    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
        try {
            storyTearDown();
        } catch (Throwable e) {
        	// ensure the exception gets printed, JUnit won't always do this
        	System.err.println("Caught exception during tear down. Reason: " 
        		+ e.getMessage());
        	e.printStackTrace();
            throw new Exception(e);
        }
    }


    /** Construct a new Story by sequencing the executable tests */
    public Story() {
        super();
    }


    /**
     * Sets the name of the current step.
     */
    public void setStepName(String name) {
        stepName = name;
    }


    public String getStepName() {
        return stepName;
    }


    /**
     * Subclasses implement this method to compose a List of <code>Steps</code>
     * to be executed in order when this Story is run.
     * <p>
     * Example:
     * </p>
     * 
     * <pre>
     * List mySteps = new ArrayList();
     * mySteps.add(new Step1());
     * mySteps.add(new Step2());
     * mySteps.add(new Step3());
     * return mySteps;
     * </pre>
     * 
     * <p>
     * Note that a Story contains <i>only</i> <code>Steps</code>, never
     * other <code>Stories</code>. To create a composite <code>Story</code>,
     * use <code>StoryBook</code>.
     * </p>
     */
    protected abstract Vector steps();


    /**
     * This method is for the user to give a short description of this story.
     * This allows for the creation of another tool to track which stories are
     * implemented
     */
    public abstract String getDescription();


    /**
     * Runs the bare test sequence.
     * 
     * @exception Throwable
     *                if any exception is thrown
     */
    @Test
    public void runBare() throws Throwable {
        System.out.println("\n===============================================");
        System.out.println("STORY: " + getClass().getName());
        
        // If setUp fails, exception will be thrown out of the test
        System.out.println("STORY: " + getClass().getName() + " setting up");
        setUp();

        if (storySetupSuccess) {
            try {
                mySteps = steps();
                runTest();
            } catch (Throwable t) {
                System.err.println("Caught exception while running tests: " + t.getMessage());
                System.err.println("Stopping testing and tearing down");
                t.printStackTrace();
                // mark this story as failed
                stepsFailedThrowable = t;
            } finally {
                System.out.println("STORY: " + getClass().getName() + " COMPLETE. Tearing down");
                tearDown();
                if (stepsFailedThrowable != null) {
                    System.out.println("Failing due to exception during STORY: " + getClass().getName() + 
                        " due to reason: " + stepsFailedThrowable.getMessage());
                    fail(stepsFailedThrowable.getMessage());
                }
            }
        } else {
            // TODO: does this need to call tearDown() as well?
            fail(getClass().getName() + ".storySetUp() failed, not running its steps");
        }        
    }


    /**
     * Override runTest() to use steps instead of reflection-targeted methods.
     */
    protected void runTest() throws Throwable {
        if (mySteps == null || mySteps.size() == 0) {
            fail("No Steps were sequenced for this Story");
        }
        for (int i = 0; i < mySteps.size(); i++) {
            Step currentStep = (Step) mySteps.elementAt(i);
            setStepName(currentStep.getName());
            System.out.println("\n> " + currentStep.getName() + " (" + getClass().getName() + ")\n");
            currentStep.runStep();
        }
    }
    

    // used to make sure that if we are going to use a junit testsuite to test
    // this
    // that the test suite will not error out looking for a single test......
    public void testDummy() throws Throwable {
    }
    

    /**
     * Override this method to perform any setup operations required for the steps
     * @return
     *      True if setup succedes, false if it does not.
     * @throws Throwable
     */
    protected boolean storySetUp() throws Throwable {
        return true;
    }
    

    /**
     * Override this method to perform any cleanup needed after running the steps
     * @throws Throwable
     */
    protected void storyTearDown() throws Throwable {   
    }
}
