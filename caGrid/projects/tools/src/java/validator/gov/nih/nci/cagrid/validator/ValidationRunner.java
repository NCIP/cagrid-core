package gov.nih.nci.cagrid.validator;

import gov.nih.nci.cagrid.testing.system.haste.Story;
import gov.nih.nci.cagrid.tests.core.beans.validation.Interval;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestResult;
import junit.textui.TestRunner;

import org.apache.axis.types.Time;

/** 
 *  ValidationRunner
 *  Runs a validation package of tests
 * 
 * @author David Ervin
 * 
 * @created Aug 29, 2007 1:17:37 PM
 * @version $Id: ValidationRunner.java,v 1.3 2008-11-12 23:36:16 jpermar Exp $ 
 */
public class ValidationRunner {
    
    public static final String VALIDATION_DESCRIPTION_PROPERTY = "validation.desc";

    private ValidationPackage pack;

    public ValidationRunner(ValidationPackage pack) {
        this.pack = pack;
    }


    public List<TestResult> testNow() {
        List<Story> validationStories = pack.getValidationStories();
        TestRunner runner = new TestRunner();
        //run each story and collect results
        List<TestResult> results = new ArrayList<TestResult>();
        for (Story s : validationStories) {
        	TestResult result = runner.doRun(s);
        	results.add(result);
        }
        System.out.flush();
        return results;
    }


    public void startScheduledTest() {
        // the whole schedule is optional
        if (pack.getValidationSchedule() != null) {
            // a timer task to execute the tests
            TimerTask task = new TimerTask() {
                public void run() {
                    //StoryBook validationStory = pack.getValidationStoryBook();
                	List<Story> validationStories = pack.getValidationStories();
                    TestRunner runner = new TestRunner();
                    // TODO: something else with output here?
                    for (Story s : validationStories) {
                    	runner.doRun(s);
                    }
                    System.out.flush();
                }
            };

            // initialize the timer
            String name = pack.getValidationSchedule().getTaskName();
            Timer timer = new Timer(name, false);

            Time startTime = pack.getValidationSchedule().getStart();
            Date startDateTime = new Date(); // NOW by default
            if (startTime != null) {
                // figure out the start time / date
                Calendar startCal = startTime.getAsCalendar();
                Calendar cal = new GregorianCalendar();
                cal.setTime(startDateTime); // baseline of right now
                cal.set(Calendar.HOUR, startCal.get(Calendar.HOUR));
                cal.set(Calendar.MINUTE, startCal.get(Calendar.MINUTE));
                cal.set(Calendar.AM_PM, startCal.get(Calendar.AM_PM));
                startDateTime = cal.getTime();
            }
            if (pack.getValidationSchedule().getInterval() != null) {
                Interval interval = pack.getValidationSchedule().getInterval();
                long waitMs = 0;
                waitMs += (1000 * interval.getSeconds());
                waitMs += (1000 * 60 * interval.getMinutes());
                waitMs += (1000 * 60 * 60 * interval.getHours());
                timer.scheduleAtFixedRate(task, startDateTime, waitMs);
            } else {
                // schedule the task once for a given time
                timer.schedule(task, startDateTime);
            }
        } else {
            // no schedule, fall back to execute now
            testNow();
        }
    }


    public static void main(String[] args) {
        try {
            String descFilename = null;
            
            if (args.length == 1) {
                descFilename = args[0];
            } else {
                descFilename = System.getProperty(VALIDATION_DESCRIPTION_PROPERTY);
                if (descFilename == null) {
                    throw new IllegalArgumentException("No validation description file could be found");
                }
            }
            System.out.println("Using " + descFilename);
            FileInputStream in = new FileInputStream(descFilename);
            ValidationPackage pack = GridDeploymentValidationLoader.loadValidationPackage(in);
            in.close();
            ValidationRunner runner = new ValidationRunner(pack);
            List<TestResult> results = runner.testNow();
            int count = 0;
            for (TestResult result : results) {
            	count += result.errorCount();
            	count += result.failureCount();
            }
            System.exit(count);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
