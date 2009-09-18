package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;

import java.io.File;
import java.util.Properties;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SpecificServiceInformation extends ServiceInformation {

	private ServiceType service;
	
	public SpecificServiceInformation(ServiceInformation info, ServiceType specificService) {
		super(info.getServiceDescriptor(), info.getIntroduceServiceProperties(), info.getBaseDirectory());
		this.service = specificService;
	}
	
	public SpecificServiceInformation(ServiceDescription service, Properties properties, File baseDirectory, ServiceType specificService) {
		super(service, properties, baseDirectory);
		this.service = specificService;
	}

	public ServiceType getService() {
		return service;
	}

	public void setService(ServiceType service) {
		this.service = service;
	}

}
