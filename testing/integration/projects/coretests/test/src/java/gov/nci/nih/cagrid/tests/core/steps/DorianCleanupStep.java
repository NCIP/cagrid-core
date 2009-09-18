/*
 * Created on Jun 13, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

/**
 * This step drops the dorian table from the configured database.
 * 
 * @author Patrick McConnell
 */
public class DorianCleanupStep extends AbstractDbCleanupStep {
    public DorianCleanupStep() {
        super();
    }


    public DorianCleanupStep(String dbUrl, String user, String password) {
        super(dbUrl, user, password);
    }


    @Override
    public void runStep() throws Throwable {
        super.dropDatabases(new String[]{"DORIAN",});
    }


    public static void main(String[] args) throws Throwable {
        new DorianCleanupStep().runStep();
    }
}
