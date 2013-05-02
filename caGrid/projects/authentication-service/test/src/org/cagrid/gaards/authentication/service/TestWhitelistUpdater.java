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
package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.security.authentication.LockoutManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

public class TestWhitelistUpdater extends TestCase {
    
    public static long LOCKOUT_DURATION = 10000;
    public static int MAX_ATTEMPTS = 4;
    public static long LOCKOUT_MEMORY = 500;
    
    private File whitelistFile = null;
    private Random rand = null;
    
    public TestWhitelistUpdater() {
        LockoutManager.initialize(String.valueOf(LOCKOUT_DURATION), String.valueOf(LOCKOUT_MEMORY), String.valueOf(MAX_ATTEMPTS));
    }
    
    
    public void setUp() {
        try {
            File tmpDir = new File("./tmp");
            tmpDir.mkdir();
            this.whitelistFile = File.createTempFile("TempWhitelist", ".txt", tmpDir);
            this.rand = new Random();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error creating the temporary whitelist file: " + ex.getMessage());
        }
        try {
            // running the whitelist updater in a thread because it needs to recieve asynchronous updates
            // when the file system changes
            Runnable runme = new Runnable() {
                public void run() {
                    try {
                        WhitelistUpdater.monitorWhitelist(whitelistFile.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(runme).start();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up the whitelist monitor: " + ex.getMessage());
        }
    }
    
    
    public void tearDown() {
        if (this.whitelistFile.exists()) {
            this.whitelistFile.delete();
        }
    }
    
    
    private String getFakeUserName() {
        return "Test_User_" + rand.nextLong();
    }
    
    
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void addIdToWhitelist(String userId) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.whitelistFile));
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
            StringBuffer buff = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(this.whitelistFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!userId.equals(line)) {
                    buff.append(line).append("\n");
                }
            }
            reader.close();
            this.whitelistFile.delete();
            Utils.stringBufferToFile(buff, this.whitelistFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error writing to whitelist file: " + ex.getMessage());
        }
    }
    
    
    public void testAccountWhitelisted() {
        String userId = getFakeUserName();
        addIdToWhitelist(userId);
        sleep(LOCKOUT_DURATION / 2);
        // give that a moment to be picked up
        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            LockoutManager.getInstance().setFailedAttempt(userId);
        }
        assertFalse("User was locked out, even though user was whitelisted", LockoutManager.getInstance().isUserLockedOut(userId));
    }
    
    
    public void testLockoutThenWhitelist() {
        String userId = getFakeUserName();
        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            LockoutManager.getInstance().setFailedAttempt(userId);
        }
        assertTrue("User was not locked out", LockoutManager.getInstance().isUserLockedOut(userId));
        addIdToWhitelist(userId);
        // give that a moment to be seen by the whitelist updater
        sleep(LOCKOUT_DURATION / 2);
        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            LockoutManager.getInstance().setFailedAttempt(userId);
        }
        assertFalse("User was locked out, even though user was whitelisted", LockoutManager.getInstance().isUserLockedOut(userId));
    }
    
    
    public void testRemoveFromWhitelist() {
        String userId = getFakeUserName();
        addIdToWhitelist(userId);
        sleep(LOCKOUT_DURATION / 2);
        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            LockoutManager.getInstance().setFailedAttempt(userId);
        }
        assertFalse("User was locked out, even though user was whitelisted", LockoutManager.getInstance().isUserLockedOut(userId));
        removeIdFromWhitelist(userId);
        sleep(LOCKOUT_DURATION / 2);
        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            LockoutManager.getInstance().setFailedAttempt(userId);
        }
        assertTrue("User was not locked out", LockoutManager.getInstance().isUserLockedOut(userId));
    }
}
