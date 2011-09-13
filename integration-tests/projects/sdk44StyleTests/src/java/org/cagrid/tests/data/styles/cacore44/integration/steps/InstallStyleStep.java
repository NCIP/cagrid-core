package org.cagrid.tests.data.styles.cacore44.integration.steps;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.tests.data.styles.cacore44.integration.SDK44ServiceStyleSystemTestConstants;


public class InstallStyleStep extends Step {

    public void runStep() throws Throwable {
        String introduce = System.getProperty(SDK44ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        String zipLocation = System.getProperty(SDK44ServiceStyleSystemTestConstants.STYLE_ZIP_PROPERTY);
        if (introduce == null) {
            fail("System property " + SDK44ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY 
                + " must be set to the Introduce directory");
        }
        if (zipLocation == null) {
            fail("System property " + SDK44ServiceStyleSystemTestConstants.STYLE_ZIP_PROPERTY 
                + " must be set to the style package location");
        }
        File introduceDir = new File(introduce);
        File zip = new File(zipLocation);
        ZipUtilities.unzip(zip, introduceDir);
    }
}
