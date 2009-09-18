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
