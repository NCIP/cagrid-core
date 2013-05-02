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
package gov.nih.nci.cagrid.introduce.portal.discoverytools;

import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;

import javax.swing.JPanel;


public abstract class NamespaceTypeToolsComponent extends JPanel {
	private DiscoveryExtensionDescriptionType descriptor;


	public NamespaceTypeToolsComponent(DiscoveryExtensionDescriptionType descriptor) {
		this.descriptor = descriptor;
	}


	public DiscoveryExtensionDescriptionType getDescriptor() {
		return descriptor;
	}

}
