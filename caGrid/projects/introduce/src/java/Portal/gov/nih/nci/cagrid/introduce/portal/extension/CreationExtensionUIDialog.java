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
package gov.nih.nci.cagrid.introduce.portal.extension;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.awt.Frame;

import javax.swing.JDialog;

public abstract class CreationExtensionUIDialog extends JDialog {
	private ServiceExtensionDescriptionType description;
	private ServiceInformation serviceInfo;
	
	public CreationExtensionUIDialog(Frame frame, ServiceExtensionDescriptionType desc, ServiceInformation info) {
		super(frame,true);
		this.description = desc;
		this.serviceInfo = info;
	}
	
	
	public ServiceExtensionDescriptionType getExtensionDescription() {
		return description;
	}
	
	
	protected void setExtensionDescription(ServiceExtensionDescriptionType desc) {
		this.description = desc;
	}
	
	
	public ServiceInformation getServiceInfo() {
		return serviceInfo;
	}
	

	protected void setServiceInfo(ServiceInformation serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
	
	
	public ExtensionTypeExtensionData getExtensionTypeExtensionData() {
		return ExtensionTools.getExtensionData(getExtensionDescription(), getServiceInfo());
	}
}
