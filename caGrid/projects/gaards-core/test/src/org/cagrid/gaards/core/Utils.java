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
