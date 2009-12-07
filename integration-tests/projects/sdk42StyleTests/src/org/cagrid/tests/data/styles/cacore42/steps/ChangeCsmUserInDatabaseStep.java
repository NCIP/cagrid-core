package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;

public class ChangeCsmUserInDatabaseStep extends AbstractDatabaseStep {
    
    private static Log LOG = LogFactory.getLog(ChangeCsmUserInDatabaseStep.class);
    
    public static final String USER_PROXY_FILE = "user.proxy";
    public static final String SDK_USER_ID = "/O=caBIG/OU=caGrid/OU=Training/OU=Dorian/CN=SDKUser1";
    
    private SecureContainer serviceContainer = null;

    public ChangeCsmUserInDatabaseStep(SecureContainer container) {
        super();
        this.serviceContainer = container;
    }


    public void runStep() throws Throwable {
        String userId = getTestUserId();
        StringBuffer update = new StringBuffer();
        update.append("Update CSM_USER set LOGIN_NAME = '");
        update.append(userId).append("' ");
        update.append("where LOGIN_NAME = '");
        update.append(SDK_USER_ID).append("';");
        LOG.debug("Executing SQL: " + update.toString());
        getDatabase().update(update.toString());
    }
    
    
    private String getTestUserId() {
        String id = null;
        try {
            File proxyFile = new File(serviceContainer.getCertificatesDirectory(), USER_PROXY_FILE);
            GlobusCredential cred = new GlobusCredential(proxyFile.getAbsolutePath());
            id = cred.getIdentity();
            LOG.debug("Test user identity determined to be " + id);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining test user proxy: " + ex.getMessage());
        }
        return id;
    }
}
