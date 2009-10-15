package org.cagrid.tests.data.styles.cacore42.steps;

import java.io.File;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public abstract class BaseExampleProjectStep extends Step {
    

    public static final String PROPERTY_SDK_BASE = "sdk.unpack.dir";
    
    public static final String EXAMPLE_PROJECT_DIR = "sdk-toolkit" + File.separator + "example-project";

    public BaseExampleProjectStep() {
        super();
    }
    
    
    protected File getSdkDir() {
        String dirName = System.getProperty(PROPERTY_SDK_BASE);
        assertNotNull("System property " + PROPERTY_SDK_BASE + " not defined or empty", dirName);
        return new File(dirName);
    }
    
    
    protected File getExampleProjectDir() {
        return new File(getSdkDir(), EXAMPLE_PROJECT_DIR);
    }
    
    
    protected File getCodegenPropertiesFile() {
        return new File(getExampleProjectDir(), "build" + File.separator + "codegen.properties");
    }
    
    
    protected File getInstallPropertiesFile() {
        return new File(getExampleProjectDir(), "build" + File.separator + "install.properties");
    }
}
