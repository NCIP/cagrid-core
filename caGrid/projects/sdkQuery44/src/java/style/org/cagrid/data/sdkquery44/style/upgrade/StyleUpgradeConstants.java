package org.cagrid.data.sdkquery44.style.upgrade;

public interface StyleUpgradeConstants {

    public static final String LATEST_VERSION = "1.5";
    
    public static final String LATEST_JAR_SUFFIX = "-1.5.jar";
    
    public static final String OLD_SERIALIZER_FACTORY_NAME = 
        "org.cagrid.iso21090.sdkquery44.encoding.SDK44SerializerFactory";
    public static final String OLD_DESERIALIZER_FACTORY_NAME = 
        "org.cagrid.iso21090.sdkquery44.encoding.SDK44DeserializerFactory";
    
    public static final String OLD_QUERY_PROCESSOR_CLASS_NAME = "org.cagrid.iso21090.sdkquery44.processor.cql2.SDK44CQL2QueryProcessor";
}
