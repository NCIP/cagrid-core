package org.cagrid.cadsr.portal;

import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;


public interface ClassSelectedListener {
	public void handleClassSelection(UMLClassMetadata clazz);
}
