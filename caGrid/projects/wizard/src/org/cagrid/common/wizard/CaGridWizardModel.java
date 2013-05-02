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
package org.cagrid.common.wizard;

import javax.swing.JComponent;

import org.pietschy.wizard.OverviewProvider;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;

public class CaGridWizardModel extends MultiPathModel implements OverviewProvider {
    JComponent overviewComponent = null;
	public CaGridWizardModel(Path arg0, JComponent overviewComponent) {
		super(arg0);
		this.overviewComponent = overviewComponent;
	}

	public JComponent getOverviewComponent() {
		return overviewComponent;
	}
	

}
