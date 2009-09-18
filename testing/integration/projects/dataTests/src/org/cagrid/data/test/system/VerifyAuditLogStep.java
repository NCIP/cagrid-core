package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  VerifyAuditLogStep
 *  Step to verify both creation of and proper output 
 *  to the data service audit log 
 * 
 * @author David Ervin
 * 
 * @created May 25, 2007 2:21:26 PM
 * @version $Id: VerifyAuditLogStep.java,v 1.1 2008-05-16 19:25:25 dervin Exp $ 
 */
public class VerifyAuditLogStep extends Step {
    
    private String logFilename = null;
    
    public VerifyAuditLogStep(String logFilename) {
        this.logFilename = logFilename;
    }
    

    public void runStep() throws Throwable {
        System.out.println("Running step: " + getClass().getName());
        
        File auditorLogFile = new File(logFilename);
        
        assertTrue("Auditor log file " + logFilename + " did not exist", auditorLogFile.exists());
        assertTrue("Auditor log file " + logFilename + " cannot be read", auditorLogFile.canRead());
        
        // TODO: verify stuff got logged
    }
}
