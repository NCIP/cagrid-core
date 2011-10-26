package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.security.constants.Constants;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.client.AuthenticationClient;

public class LockoutStep extends Step {
    
    private String serviceUrl = null;
    private BasicAuthentication badPasswdCred = null;
    private BasicAuthentication goodPasswdCred = null;
    
    public LockoutStep(String serviceUrl, BasicAuthentication badPasswdCred, BasicAuthentication goodPasswdCred) {
        this.serviceUrl = serviceUrl;
        this.badPasswdCred = badPasswdCred;
        this.goodPasswdCred = goodPasswdCred;
    }
    

    @Override
    public void runStep() throws Throwable {
        AuthenticationClient client = new AuthenticationClient(serviceUrl);
        // login once with the good credential
        try {
            client.authenticate(goodPasswdCred);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to log in with good credential: " + ex.getMessage());
        }
        // lock the account out
        int maxAttempts = getMaxAttempts();
        System.out.println("Max attempts is " + maxAttempts);
        for (int i = 0; i < maxAttempts; i++) {
            try {
                client.authenticate(badPasswdCred);
            } catch (Exception ex) {
                // expected?
            }
        }
        // should be locked out
        try {
            client.authenticate(goodPasswdCred);
            fail("Account should have been locked out");
        } catch (Exception ex) {
            String message = FaultHelper.getMessage(ex);
            boolean locked = message.contains("is locked out");
            if (!locked) {
                ex.printStackTrace();
                fail("Exception wasn't for being locked out: " + ex.getMessage());
            }
        }
    }
    
    
    private int getMaxAttempts() {
        int attempts = -1;
        try {
            attempts = Integer.parseInt(Constants.ALLOWED_ATTEMPTS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error parsing max allowed attempts: " + ex.getMessage());
        }
        return attempts;
    }
}
