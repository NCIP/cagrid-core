package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.encoding.SDKDeserializerFactory;
import gov.nih.nci.cagrid.encoding.SDKSerializerFactory;

public interface Sdk31Constants {

    // sdk serializer constants
    public static final String SDK_SERIALIZER = SDKSerializerFactory.class.getName();
    public static final String SDK_DESERIALIZER = SDKDeserializerFactory.class.getName();

    // castor mapping constants
    public static final String CACORE_CASTOR_MAPPING_FILE = "xml-mapping.xml";
    public static final String CASTOR_MAPPING_WSDD_PARAMETER = "castorMapping";

    // Local SDK api related
    public static final String LOCAL_SDK_CONF_JAR_POSTFIX = "_LocalSdkConfiguration";
}
