package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;

import java.io.File;
import java.util.Properties;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SpecificMethodInformation extends SpecificServiceInformation {

	private MethodType method;
	
	public SpecificMethodInformation(ServiceInformation info, ServiceType specificService, MethodType method) {
		super(info, specificService);
		this.method = method;
		
	}
	
	public SpecificMethodInformation(ServiceDescription service, Properties properties, File baseDirectory, ServiceType specificService, MethodType method) {
		super(service, properties, baseDirectory, specificService);
		this.method = method;
	}

    public MethodType getMethod() {
        return method;
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }


}
