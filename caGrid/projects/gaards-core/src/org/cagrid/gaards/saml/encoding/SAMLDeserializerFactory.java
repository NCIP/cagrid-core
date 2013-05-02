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
package org.cagrid.gaards.saml.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SAMLDeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SAMLDeserializerFactory.class.getName());


	public SAMLDeserializerFactory(Class javaType, QName xmlType) {
		super(SAMLDeserializer.class, xmlType, javaType);
		LOG.debug("Initializing SAMLDeserializer for class:" + javaType + " and QName:" + xmlType);
	}
}
