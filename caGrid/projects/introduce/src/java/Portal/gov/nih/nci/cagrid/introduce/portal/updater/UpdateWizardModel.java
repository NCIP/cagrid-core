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
package gov.nih.nci.cagrid.introduce.portal.updater;

import javax.swing.JComponent;

import org.pietschy.wizard.OverviewProvider;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;

public class UpdateWizardModel extends MultiPathModel implements OverviewProvider {

	public UpdateWizardModel(Path arg0) {
		super(arg0);
	}

	public JComponent getOverviewComponent() {
		return new UpdateOverviewPanel();
	}

}
