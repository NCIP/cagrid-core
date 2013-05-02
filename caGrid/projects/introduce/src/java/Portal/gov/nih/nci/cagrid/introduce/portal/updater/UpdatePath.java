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

import gov.nih.nci.cagrid.introduce.portal.updater.steps.CheckForUpdatesStep;
import gov.nih.nci.cagrid.introduce.portal.updater.steps.DownloadsUpdatesStep;
import gov.nih.nci.cagrid.introduce.portal.updater.steps.FinishedStep;

import org.pietschy.wizard.models.SimplePath;

public class UpdatePath extends SimplePath {

	public UpdatePath(boolean autoCheck){
		
		CheckForUpdatesStep selectStep = new CheckForUpdatesStep(autoCheck);
		DownloadsUpdatesStep downloadStep = new DownloadsUpdatesStep(selectStep);
		FinishedStep finishedStep = new FinishedStep();
		this.addStep(selectStep);
		this.addStep(downloadStep);
		this.addStep(finishedStep);
		
	}

}
