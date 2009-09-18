package org.cagrid.gme.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


/**
 * Replaces the "real" db properties with the "testing" db properties (or a
 * specified set).
 * 
 * @author oster
 */
public class SetDatabasePropertiesStep extends Step {

    private final File gmeServiceDir;
    protected File replacementProperties;


    public SetDatabasePropertiesStep(File gmeServiceDir) {
        this(gmeServiceDir, new File(gmeServiceDir, "test/resources/gme.test.properties"));
    }


    public SetDatabasePropertiesStep(File gmeServiceDir, File replacementProperties) {
        this.gmeServiceDir = gmeServiceDir;
        this.replacementProperties = replacementProperties;
    }


    @Override
    public void runStep() throws Throwable {
        File targetDBPropertiesFile = new File(this.gmeServiceDir, "etc/gme.properties");

        assertNotNull(targetDBPropertiesFile);
        assertTrue("Couldn't write the target properties file (" + targetDBPropertiesFile.getCanonicalPath() + ")",
            targetDBPropertiesFile.canWrite());

        assertNotNull(this.replacementProperties);
        assertTrue("Couldn't read the replacement properties file (" + this.replacementProperties.getCanonicalPath()
            + ")", this.replacementProperties.canRead());

        Utils.copyFile(this.replacementProperties, targetDBPropertiesFile);
    }
}
