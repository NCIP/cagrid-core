package org.cagrid.data.test.upgrades;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  UnpackOldServiceStep
 *  Unpacks an old caGrid data service for upgrading
 * 
 * @author David Ervin
 * 
 * @created May 29, 2008 12:13:37 PM
 * @version $Id: UnpackOldServiceStep.java,v 1.2 2008-05-29 18:53:17 dervin Exp $ 
 */
public class UnpackOldServiceStep extends Step {
    
    private static Log logger = LogFactory.getLog(UnpackOldServiceStep.class);
    
    private String serviceZipName;
    
    public UnpackOldServiceStep(String serviceZipName) {
        this.serviceZipName = serviceZipName;
    }
    
    
    public void runStep() throws Throwable {
        String zipDir = UpgradeTestProperties.getUpgradeServicesZipDir();
        String unpackDir = UpgradeTestProperties.getUpgradeServicesExtractDir();
        File zip = new File(zipDir, serviceZipName);
        assertTrue("Service zip file " + zip.getAbsolutePath() + " did not exist", zip.exists());
        logger.debug("Unpacking service zip: " + zip.getAbsolutePath() + " to " + unpackDir);
        ZipUtilities.unzip(zip, new File(unpackDir));
    }
}
