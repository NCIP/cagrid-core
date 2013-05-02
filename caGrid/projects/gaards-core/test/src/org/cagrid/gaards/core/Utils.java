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
package org.cagrid.gaards.core;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.cagrid.gaards.saml.encoding.TestSAMLEncoding;


public class Utils {
    public static final String CLIENT_WSDD = "/client-config.wsdd";


    public static String serialize(Object o) throws Exception {
        StringWriter writer = new StringWriter();
        gov.nih.nci.cagrid.common.Utils.serializeObject(o, new QName("A", "A"), writer, TestSAMLEncoding.class
            .getResourceAsStream(CLIENT_WSDD));
        return writer.toString();
    }


    public static Object deserialize(String s, Class c) throws Exception {
        return gov.nih.nci.cagrid.common.Utils.deserializeObject(new StringReader(s), c, TestSAMLEncoding.class
            .getResourceAsStream(CLIENT_WSDD));
    }

}
