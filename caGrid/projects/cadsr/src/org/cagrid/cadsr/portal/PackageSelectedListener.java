package org.cagrid.cadsr.portal;

import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;


public interface PackageSelectedListener {
	public void handlePackageSelection(UMLPackageMetadata pkg);
}
