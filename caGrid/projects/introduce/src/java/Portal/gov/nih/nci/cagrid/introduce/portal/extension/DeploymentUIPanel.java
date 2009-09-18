package gov.nih.nci.cagrid.introduce.portal.extension;

import gov.nih.nci.cagrid.introduce.beans.extension.DeploymentExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import javax.swing.JPanel;


public abstract class DeploymentUIPanel extends JPanel {
	private DeploymentExtensionDescriptionType description;
	private ServiceInformation serviceInfo;


	public DeploymentUIPanel(DeploymentExtensionDescriptionType desc, ServiceInformation info) {
		this.description = desc;
		this.serviceInfo = info;
	}


	public DeploymentExtensionDescriptionType getExtensionDescription() {
		return this.description;
	}


	protected void setExtensionDescription(DeploymentExtensionDescriptionType desc) {
		this.description = desc;
	}


	public ServiceInformation getServiceInfo() {
		return serviceInfo;
	}


	public void setServiceInfo(ServiceInformation info) {
		this.serviceInfo = info;
		this.resetGUI();
	}


	public abstract void resetGUI();
	
	public abstract void preDeploy() throws Exception;
}
