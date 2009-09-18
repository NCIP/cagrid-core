package gov.nih.nci.cagrid.wsenum.common;

import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import javax.xml.namespace.QName;

/** 
 *  WsEnumConstants
 *  An assortment of constants needed by the CaGrid WS-Enumeration infrastructure
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 15, 2006 
 * @version $Id: WsEnumConstants.java,v 1.2 2008-09-05 18:32:11 dervin Exp $ 
 */
public class WsEnumConstants {
	// Namespace URI for WS-Enumeration
	public static final String WS_ENUMERATION_URI = "http://schemas.xmlsoap.org/ws/2004/09/enumeration";

	// package name for generated beans of WS-Enumeration
	public static final String ENUMERATION_PACKAGE_NAME = "org.xmlsoap.schemas.ws._2004._09.enumeration";

	// package name for beans from addressing namespace used by WS-Enumeration
	public static final String ADDRESSING_PACKAGE_NAME = "org.globus.addressing";
	
	// schema names
	public static final String ENUMERATION_WSDL_NAME = "enumeration.wsdl";
	public static final String ENUMERATION_XSD_NAME = "enumeration.xsd";
	public static final String ADDRESSING_XSD_NAME = "addressing.xsd";
	
	// WS-Enumeration WSDL port type
	public static final String PORT_TYPE_NAME = "DataSource";
	
	// type for enumerate response
	public static final String ENUMERATE_RESPONSE_TYPE = "EnumerateResponse";
	
	// QName for enumerate response message
	public static final QName ENUMERATE_RESPONSE_MESSAGE_QNAME = new QName(WS_ENUMERATION_URI, "EnumerateResponseMessage");
    
    // caGrid enumeration service context
    public static final String CAGRID_ENUMERATION_SERVICE_WSDL = "CaGridEnumeration.wsdl";
    public static final String CAGRID_ENUMERATION_SERVICE_NAME = "CaGridEnumeration";
    public static final String CAGRID_ENUMERATION_SERVICE_PACKAGE = "gov.nih.nci.cagrid.enumeration";
    public static final String CAGRID_ENUMERATION_SERVICE_NAMESPACE = 
        "http://" + CAGRID_ENUMERATION_SERVICE_PACKAGE + 
        "/" + CAGRID_ENUMERATION_SERVICE_NAME;
    
    // enumeration response container
    public static final String ENUMERATION_RESPONSE_XSD = "EnumerationResponseContainer.xsd";
    public static final String ENUMERATION_RESPONSE_NAMESPACE = "http://gov.nih.nci.cagrid.enumeration/EnumerationResponseContainer";
    public static final QName ENUMERATION_RESPONSE_QNAME = new QName(ENUMERATION_RESPONSE_NAMESPACE, "EnumerationResponseContainer");
    public static final String ENUMERATION_RESPONSE_PACKAGE = "gov.nih.nci.cagrid.enumeration.stubs.response";
    
    // service property for setting default IterImplType
    public static final String ENUMERATION_SERVICE_PROPERTY_PREFIX = "caGridWsEnumeration_";
    public static final String ITER_IMPL_TYPE_PROPERTY = ENUMERATION_SERVICE_PROPERTY_PREFIX + "iterImplType";
    public static final String DEFAULT_ITER_IMPL_TYPE = IterImplType.CAGRID_CONCURRENT_COMPLETE.name();
    
    private WsEnumConstants() {
        
    }
}
