package gov.nih.nci.cagrid.tests.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://tests.cagrid.nci.nih.gov/IntroduceEcho/IntroduceEcho";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "IntroduceEchoKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "IntroduceEchoResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
