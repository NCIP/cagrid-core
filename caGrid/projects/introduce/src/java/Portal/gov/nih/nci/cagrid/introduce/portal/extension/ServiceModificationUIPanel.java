package gov.nih.nci.cagrid.introduce.portal.extension;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import javax.swing.Icon;
import javax.swing.JPanel;


public abstract class ServiceModificationUIPanel extends JPanel {
	private ServiceExtensionDescriptionType description;
	private ServiceInformation serviceInfo;


	public ServiceModificationUIPanel(ServiceExtensionDescriptionType desc, ServiceInformation info) {
		this.description = desc;
		this.serviceInfo = info;
	}


	public ServiceExtensionDescriptionType getExtensionDescription() {
		return this.description;
	}


	protected void setExtensionDescription(ServiceExtensionDescriptionType desc) {
		this.description = desc;
	}


	public ServiceInformation getServiceInfo() {
		return serviceInfo;
	}


	public void setServiceInfo(ServiceInformation info) {
		this.serviceInfo = info;
		this.resetGUI();
	}


	protected abstract void resetGUI();
	
	
	//overide me to put an icon on your tab
	public Icon getIcon() {
	    return null;
	}


	public ExtensionTypeExtensionData getExtensionTypeExtensionData() {
		return ExtensionTools.getExtensionData(getExtensionDescription(), getServiceInfo());
	}
	
}
