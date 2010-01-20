package org.cagrid.gaards.ui.common;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cagrid.gaards.credentials.CredentialEntryFactory;
import org.cagrid.gaards.credentials.EncodingUtil;
import org.cagrid.gaards.credentials.X509CredentialDescriptor;
import org.cagrid.gaards.credentials.X509CredentialEntry;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class CredentialManager {
	private static Logger log = Logger.getLogger(CredentialManager.class);
	
    private static final long serialVersionUID = 1L;

    private static CredentialManager instance;

    private Map<String, X509CredentialEntry> credentials;

    private File credentialDir;

    private long lastId = 0;

    private Map<String, File> credentialFiles;


    private CredentialManager() {
        this.credentials = new HashMap<String, X509CredentialEntry>();
        this.credentialFiles = new HashMap<String, File>();
        loadCredentials();
    }


    public synchronized void loadCredentials() {
        this.credentials.clear();
        this.credentialFiles.clear();
        String dir = Utils.getCaGridUserHome() + File.separator + File.separator + "credentials";
        credentialDir = new File(dir);
        credentialDir.mkdirs();
        FileFilter ff = new CredentialFilter();
        File list[] = credentialDir.listFiles(ff);
        for (int i = 0; i < list.length; i++) {
            try {
                long fileId = getFileId(list[i]);
                if (fileId > lastId) {
                    lastId = fileId;
                }

                X509CredentialDescriptor des = EncodingUtil.deserialize(list[i]);
                X509CredentialEntry entry = CredentialEntryFactory.getEntry(des);
                GlobusCredential cred = entry.getCredential();
                if (cred.getTimeLeft() == 0) {
                    list[i].delete();
                } else {
                    credentials.put(entry.getIdentity(), entry);
                    credentialFiles.put(entry.getIdentity(), list[i]);
                }

            } catch (Exception e) {
                list[i].delete();
                log.error(e, e);
            }
        }
    }


    private long getFileId(File f) throws Exception {
        String name = f.getName();
        int index = name.indexOf(".credential");
        String sid = name.substring(0, index);
        return Long.valueOf(sid).longValue();
    }


    public class CredentialFilter implements FileFilter {

        public boolean accept(File pathname) {
            String name = pathname.getName();
            if (name.endsWith(".credential")) {
                int index = name.indexOf(".credential");
                String sid = name.substring(0, index);
                try {
                    Long.valueOf(sid).longValue();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }

    }


    public static CredentialManager getInstance() {
        if (instance == null) {
            instance = new CredentialManager();
        }
        return instance;
    }


    public synchronized X509CredentialEntry setDefaultCredential(X509CredentialEntry credential) throws Exception {
        List<X509CredentialEntry> creds = getCredentials();
        boolean found = false;
        for (int i = 0; i < creds.size(); i++) {
            X509CredentialEntry entry = creds.get(i);
            if (entry.equals(credential)) {
                credential = entry;
                entry.setDefault(true);
                found = true;
            } else if (entry.isDefault()) {
                entry.setDefault(false);
                if (!credentialFiles.containsKey(entry.getIdentity())) {
                    credentials.remove(entry.getIdentity());
                }
            }

        }
        if (!found) {
            credential.setDefault(true);
            credentials.put(credential.getIdentity(), credential);
        }
        return credential;
    }


    public synchronized void addCredential(X509CredentialEntry credential) throws Exception {
        credentials.put(credential.getIdentity(), credential);
        lastId = lastId + 1;
        File f = new File(credentialDir.getAbsolutePath() + File.separator + lastId + ".credential");
        EncodingUtil.serialize(f, credential.getDescriptor());
        credentialFiles.put(credential.getIdentity(), f);
    }


    public synchronized void deleteCredential(X509CredentialEntry credential) {
        credentials.remove(credential.getIdentity());
        File f = (File) credentialFiles.get(credential.getIdentity());
        if (f != null) {
        	f.delete();
        } else {
        	log.error("Credential file does not exist.");
        }
        credentialFiles.remove(credential.getIdentity());
    }


    public synchronized List<X509CredentialEntry> getCredentials() {
        // loadCredentials();
        List<X509CredentialEntry> l = new ArrayList<X509CredentialEntry>();
        Iterator<X509CredentialEntry> itr = this.credentials.values().iterator();
        while (itr.hasNext()) {
            X509CredentialEntry credential = itr.next();
            GlobusCredential cred = credential.getCredential();
            if (cred.getTimeLeft() == 0) {
                deleteCredential(credential);
            } else {
                l.add(credential);
            }
        }
        return l;
    }

}
