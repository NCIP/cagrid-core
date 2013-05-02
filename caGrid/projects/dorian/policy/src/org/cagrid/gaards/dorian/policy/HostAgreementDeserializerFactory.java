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
package org.cagrid.gaards.dorian.policy;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HostAgreementDeserializerFactory extends BaseDeserializerFactory {

    protected static Log LOG = LogFactory.getLog(HostAgreementDeserializerFactory.class.getName());


    public HostAgreementDeserializerFactory(Class javaType, QName xmlType) {
        super(HostAgreementDeserializer.class, xmlType, javaType);
        LOG.debug("Initializing HostAgreementDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
    }
}
