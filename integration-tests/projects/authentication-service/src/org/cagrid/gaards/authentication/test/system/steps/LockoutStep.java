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
package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.security.constants.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.client.AuthenticationClient;

public class LockoutStep extends Step {
    
    private String serviceUrl = null;
    private String serviceContainerDir = null;
    private BasicAuthentication badPasswdCred = null;
    private BasicAuthentication goodPasswdCred = null;
    
    public LockoutStep(String serviceUrl, String serviceContainerDir,
        BasicAuthentication badPasswdCred, BasicAuthentication goodPasswdCred) {
        this.serviceUrl = serviceUrl;
        this.serviceContainerDir = serviceContainerDir;
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
        // whitelist the account and verify it is no longer locked
        addIdToWhitelist(goodPasswdCred.getUserId());
        sleep(2000);
        // should be unlocked now
        try {
            client.authenticate(goodPasswdCred);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to log in with whitelisted credential");
        }
    }
    
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            // ?
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
    
    
    private void addIdToWhitelist(String userId) {
        try {
            File whitelist = getWhitelistFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(whitelist));
            writer.write(userId);
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error writing to whitelist file: " + ex.getMessage());
        }
    }
    
    
    private void removeIdFromWhitelist(String userId) {
        try {
            File whitelist = getWhitelistFile();
            StringBuffer buff = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(whitelist));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!userId.equals(line)) {
                    buff.append(line).append("\n");
                }
            }
            reader.close();
            whitelist.delete();
            Utils.stringBufferToFile(buff, whitelist);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error writing to whitelist file: " + ex.getMessage());
        }
    }
    
    
    private File getWhitelistFile() {
        File whitelist = new File(serviceContainerDir, "webapps/wsrf/WEB-INF/etc/cagrid_AuthenticationService/lockout-whitelist.txt");
        return whitelist;
    }
}
