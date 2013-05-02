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
package org.cagrid.gaards.ui.common;

import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ConfigurationSynchronizer;

public class GAARDSConfigurationSynchronizer implements ConfigurationSynchronizer {

    public void syncronize() throws Exception {
        ServicesManager.getInstance().syncWithUpdatedConfiguration();
    }

}
