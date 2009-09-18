/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;


/**
 * This step creates a temporary globus container by copying GLOBUS_LOCATION to
 * a temp directory.
 * 
 * @author Patrick McConnell
 */
public class GlobusCreateStep extends Step {
    private GlobusHelper globus;


    public GlobusCreateStep(GlobusHelper globus) {
        super();
        this.globus = globus;
    }


    @Override
    public void runStep() throws Throwable {
        this.globus.createTempGlobus();
    }
}