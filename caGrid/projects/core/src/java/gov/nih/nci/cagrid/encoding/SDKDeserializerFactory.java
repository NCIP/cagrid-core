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
package gov.nih.nci.cagrid.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDKDeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDKDeserializerFactory.class.getName());


	public SDKDeserializerFactory(Class javaType, QName xmlType) {
		super(SDKDeserializer.class, xmlType, javaType);
		LOG.debug("Initializing SDKDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
