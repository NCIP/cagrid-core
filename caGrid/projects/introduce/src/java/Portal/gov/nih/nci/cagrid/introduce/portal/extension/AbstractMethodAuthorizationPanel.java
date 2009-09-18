package gov.nih.nci.cagrid.introduce.portal.extension;

import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import javax.swing.JPanel;

public abstract class AbstractMethodAuthorizationPanel extends JPanel {

	private AuthorizationExtensionDescriptionType authDesc;

	private ServiceInformation serviceInfo;

	private ServiceType service;

	private MethodType method;
	
	private boolean isUsed;

	public AbstractMethodAuthorizationPanel(
			AuthorizationExtensionDescriptionType authDesc,
			ServiceInformation serviceInfo, ServiceType service,
			MethodType method) {
		this.authDesc = authDesc;
		this.serviceInfo = serviceInfo;
		this.service = service;
		this.method = method;
	}
	


	public AuthorizationExtensionDescriptionType getAuthorizationExtensionDescriptionType() {
		return authDesc;
	}

	public ServiceInformation getServiceInformation() {
		return serviceInfo;
	}

	public ServiceType getService() {
		return service;
	}

	public MethodType getMethod() {
		return method;
	}

	public abstract ExtensionType getAuthorizationExtensionData() throws Exception;
	
}
