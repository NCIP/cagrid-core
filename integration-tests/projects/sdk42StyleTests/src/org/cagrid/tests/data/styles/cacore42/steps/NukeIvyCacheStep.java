package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NukeIvyCacheStep extends Step {
    
    private static Log LOG = LogFactory.getLog(NukeIvyCacheStep.class);
    
    public NukeIvyCacheStep() {
        super();
    }
    

    public void runStep() throws Throwable {
        File homeDir = new File(System.getProperty("user.home"));
        if (homeDir.exists()) {
            File bdaCache = new File(homeDir, ".ivy2-bda-utils");
            if (bdaCache.exists()) {
                LOG.debug("Deleting BDA utils ivy cache at " + bdaCache.getAbsolutePath());
                Utils.deleteDir(bdaCache);
            } else {
                LOG.debug("BDA utils ivy cache " + bdaCache.getAbsolutePath() + " not found");
            }
            File sdkCache = new File(homeDir, ".ivy2-sdk");
            if (sdkCache.exists()) {
                LOG.debug("Deleting sdk ivy cache at " + sdkCache.getAbsolutePath());
                Utils.deleteDir(bdaCache);
            } else {
                LOG.debug("SDK ivy cache " + sdkCache.getAbsolutePath() + " not found");
            }
        } else {
            LOG.warn("User home dir " + homeDir.getAbsolutePath() + " not found");
        }
    }
}
