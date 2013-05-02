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

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HostAgreementSerializerFactory extends BaseSerializerFactory {

    protected static Log LOG = LogFactory.getLog(HostAgreementSerializerFactory.class.getName());


    public HostAgreementSerializerFactory(Class javaType, QName xmlType) {
        super(HostAgreementSerializer.class, xmlType, javaType);
        LOG.debug("Initializing HostAgreementSerializerFactory for class:" + javaType + " and QName:" + xmlType);
    }
}
