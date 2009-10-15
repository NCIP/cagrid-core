package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;


public class SdkProjectExpectedFiles {

    private static final String[] LOCAL_CLIENT_CONF = new String[] {"application-config-client.xml",
            "application-config-security.xml", "application-config.xml", "log4j.dtd"};
    private static final String[] LOCAL_CLIENT_LIB = new String[] {
        "acegi-security-1.0.4.jar", "antlr-2.7.6.jar", "asm-1.5.3.jar", "axis-1.4.jar",
        "c3p0-0.9.0.jar", "caGrid-CQL-cql.1.0-1.3.jar", "caGrid-data-common-1.3.jar",
        "caGrid-data-stubs-1.3.jar", "caGrid-sdkQuery4-beans-1.3.jar", "caGrid-sdkQuery4-processor-1.3.jar",
        "castor-1.0.2.jar", "cglib-2.1_3.jar", "cog-axis-noversion.jar", "cog-jglobus-1.2.jar",
        "commons-codec-1.3.jar", "commons-collections-3.2.jar", "commons-discovery-0.2.jar", "commons-lang-1.0.1.jar",
        "commons-logging-1.1.jar", "csmapi-4.2.jar", "dom4j-1.4.jar", "ehcache-1.2.4.jar", "ejb3-persistence-1.0.1.jar",
        "hibernate-3.2.0.ga.jar", "hibernate-annotations-3.2.0.jar", "hibernate-validator-3.0.0.GA.jar", "jaxen-1.1.1.jar",
        "jaxrpc-1.1.jar", "jdom-1.1.jar", "jta-1.0.1B.jar", "log4j-1.2.13.jar", "mysql-connector-java-5.1.5.jar",
        "ojdbc14-10.2.0.3.0.jar", "sdk-client-framework-4.2.jar", "sdk-grid-jaas-client-4.2.jar", "sdk-security-4.2.jar",
        "sdk-security-client-4.2.jar", "sdk-system-core-4.2.jar", "servlet-2.3.1.jar", "spring-2.0.2.jar", "xercesImpl-2.7.1.jar"
    };
    private static final String[] REMOTE_CLIENT_CONF = new String[] {"application-config-client-info.xml",
            "application-config-client.xml", "log4j.dtd", "log4j.xml", "mapping.dtd"};
    private static final String[] REMOTE_CLIENT_LIB = new String[] {
        "acegi-security-1.0.4.jar", "antlr-2.7.6.jar", "asm-1.5.3.jar", "axis-1.2RC2.jar", 
        "caGrid-CQL-cql.1.0-1.3.jar", "castor-1.0.2.jar", "cglib-2.1_3.jar", "cog-jglobus-1.2.jar",
        "commons-codec-1.3.jar", "commons-logging-1.1.jar", "dom4j-1.4.jar", "ejb3-persistence-1.0.1.jar",
        "hibernate-3.2.0.ga.jar", "hibernate-annotations-3.2.0.jar", "hibernate-validator-3.0.0.GA.jar", 
        "jaxen-1.1.1.jar", "jaxrpc-1.1.jar", "jdom-1.1.jar", "log4j-1.2.13.jar", "sdk-client-framework-4.2.jar",
        "sdk-grid-remoting-4.2.jar", "sdk-security-client-4.2.jar", "spring-2.0.2.jar", "xercesImpl-2.7.1.jar"
    };


    public static String[] getExpectedLocalClientLibFiles(String applicationName) {
        String[] applicationJarNames = new String[]{applicationName + "-beans.jar", applicationName + "-orm.jar",
                applicationName + "-schema.jar"};
        String[] expected = LOCAL_CLIENT_LIB;
        for (String name : applicationJarNames) {
            expected = (String[]) Utils.appendToArray(expected, name);
        }
        return expected;
    }


    public static String[] getExpectedLocalClientConfFiles() {
        return LOCAL_CLIENT_CONF;
    }


    public static String[] getExpectedRemoteClientConfFiles() {
        return REMOTE_CLIENT_CONF;
    }
    
    
    public static String[] getExpectedRemoteClientLibFiles(String applicationName) {
        String[] applicationJarNames = new String[]{applicationName + "-beans.jar", applicationName + "-schema.jar"};
        String[] expected = REMOTE_CLIENT_LIB;
        for (String name : applicationJarNames) {
            expected = (String[]) Utils.appendToArray(expected, name);
        }
        return expected;
    }
    
    
    private SdkProjectExpectedFiles() {
        // don't instantiate me
    }
}
