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
package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.iso21090.tests.integration.SDK43ServiceStyleSystemTestConstants;

public class InstallStyleStep extends Step {

    public void runStep() throws Throwable {
        String introduce = System.getProperty(SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        String zipLocation = System.getProperty(SDK43ServiceStyleSystemTestConstants.STYLE_ZIP_PROPERTY);
        if (introduce == null) {
            fail("System property " + SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY 
                + " must be set to the Introduce directory");
        }
        if (zipLocation == null) {
            fail("System property " + SDK43ServiceStyleSystemTestConstants.STYLE_ZIP_PROPERTY 
                + " must be set to the style package location");
        }
        File introduceDir = new File(introduce);
        File zip = new File(zipLocation);
        ZipUtilities.unzip(zip, introduceDir);
    }
}
