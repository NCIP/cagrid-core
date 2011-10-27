package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.security.authentication.BetterLockoutManager;
import gov.nih.nci.security.authentication.LockoutManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;


/**
 * WhitelistUpdater Tool to listen for changes to the whitelist file and update
 * the lockout manager accordingly
 * 
 * @author ervin
 */
public class WhitelistUpdater {
    
    private static Log LOG = LogFactory.getLog(WhitelistUpdater.class);

    private File monitoredDirectory = null;
    private String monitoredFilename = null;
    private Set<String> previousWhitelist = null;
    
    
    public static void monitorWhitelist(String whitelistFile) throws Exception {
        WhitelistUpdater updater = new WhitelistUpdater(whitelistFile);
        updater.startMonitoring();
    }

    private WhitelistUpdater(String whitelistFilename) {
        File file = new File(whitelistFilename);
        this.monitoredDirectory = file.getParentFile();
        this.monitoredFilename = file.getName();
        LOG.debug("Monitoring for changes to " + monitoredFilename + " in directory " + monitoredDirectory);
        this.previousWhitelist = new HashSet<String>();
    }


    public void startMonitoring() throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject listendir = fsManager.resolveFile(monitoredDirectory.getAbsolutePath());
        FileListener listener = new FileListener() {
            
            @Override
            public void fileDeleted(FileChangeEvent event) throws Exception {
                // verify the filename is the one we're interested in
                String filename = event.getFile().getName().getBaseName();
                if (monitoredFilename.equals(filename)) {
                    LOG.info("Whitelist file " + monitoredFilename + " was deleted.  Purging whitelist");
                    BetterLockoutManager lockoutManager = LockoutManager.getInstance().getDelegatedLockoutManager();
                    synchronized (previousWhitelist) {
                        // delist all previously known whitelisted users
                        for (String whitelistedUser : previousWhitelist) {
                            lockoutManager.unWhitelistUser(whitelistedUser);
                        }
                        previousWhitelist.clear();
                    }
                } else {
                    LOG.debug("Noticed the file " + filename + " was deleted; ignoring since it's not " + monitoredFilename);
                }
            }
            
            
            @Override
            public void fileCreated(FileChangeEvent event) throws Exception {
                // verify the filename is the one we're interested in
                String filename = event.getFile().getName().getBaseName();
                if (monitoredFilename.equals(filename)) {
                    LOG.info("Whitelist file " + monitoredFilename + " was created.  Loading whitelist");
                    BetterLockoutManager lockoutManager = LockoutManager.getInstance().getDelegatedLockoutManager();
                    synchronized (previousWhitelist) {
                        try {
                            File whitelistFile = new File(event.getFile().getName().getPathDecoded());
                            BufferedReader reader = new BufferedReader(new FileReader(whitelistFile));
                            String userId = null;
                            while ((userId = reader.readLine()) != null) {
                                userId = userId.trim();
                                LOG.debug("Found " + userId + " in the whitelist");
                                lockoutManager.whitelistUser(userId);
                                previousWhitelist.add(userId);
                            }
                        } catch (IOException ex) {
                            LOG.error("Error reading whitelist: " + ex.getMessage(), ex);
                            LOG.error("Suggest deleting the whitelist file to purge, and recreating the list");
                        }
                    }
                } else {
                    LOG.debug("Noticed the file " + filename + " was created; ignoring since it's not " + monitoredFilename);
                }
            }
            
            
            @Override
            public void fileChanged(FileChangeEvent event) throws Exception {
             // verify the filename is the one we're interested in
                String filename = event.getFile().getName().getBaseName();
                if (monitoredFilename.equals(filename)) {
                    LOG.info("Whitelist file " + monitoredFilename + " was changed.  Loading whitelist");
                    BetterLockoutManager lockoutManager = LockoutManager.getInstance().getDelegatedLockoutManager();
                    synchronized (previousWhitelist) {
                        try {
                            File whitelistFile = new File(event.getFile().getName().getPathDecoded());
                            BufferedReader reader = new BufferedReader(new FileReader(whitelistFile));
                            Set<String> loadedWhitelist = new HashSet<String>();
                            String userId = null;
                            while ((userId = reader.readLine()) != null) {
                                userId = userId.trim();
                                LOG.debug("Found " + userId + " in the whitelist");
                                lockoutManager.whitelistUser(userId);
                                loadedWhitelist.add(userId);
                            }
                            reader.close();
                            // remove any entries from the previous whitelist that are NOT in the loaded one
                            previousWhitelist.removeAll(loadedWhitelist);
                            for (String removedId : previousWhitelist) {
                                LOG.debug("User " + removedId + " was not in the new list; removing");
                                lockoutManager.unWhitelistUser(removedId);
                            }
                            previousWhitelist.clear();
                            previousWhitelist.addAll(loadedWhitelist);
                        } catch (IOException ex) {
                            LOG.error("Error reading whitelist: " + ex.getMessage(), ex);
                            LOG.error("Suggest deleting the whitelist file to purge, and recreating the list");
                        }
                    }
                } else {
                    LOG.debug("Noticed the file " + filename + " was changed; ignoring since it's not " + monitoredFilename);
                }
            }
        };
        DefaultFileMonitor fm = new DefaultFileMonitor(listener);
        fm.setRecursive(true);
        fm.addFile(listendir);
        fm.start();
        // if the file already exists, fire off a fake event to the listener to force-load the list
        FileObject whitelist = fsManager.resolveFile(monitoredDirectory, monitoredFilename);
        if (whitelist.exists()) {
            listener.fileCreated(new FileChangeEvent(whitelist));
        }
    }
    
    
    public static void main(String[] args) {
        try {
            WhitelistUpdater.monitorWhitelist("etc/lockout-whitelist.txt");
            String waitForIt = new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.println(waitForIt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
