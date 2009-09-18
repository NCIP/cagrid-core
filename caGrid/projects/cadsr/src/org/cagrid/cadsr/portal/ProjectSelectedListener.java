package org.cagrid.cadsr.portal;

import gov.nih.nci.cadsr.umlproject.domain.Project;


public interface ProjectSelectedListener {
	public void handleProjectSelection(Project project);
}
