package org.cagrid.data.style.test.cacore32;

import java.io.File;

public abstract class Sdk32TestConstants {
    
    public static String RESOURCES_DIR;
    public static String QUERIES_DIR;
    
    static {
        RESOURCES_DIR = System.getProperty("test.resources.dir") + File.separator + "sdk3.2.1";
        QUERIES_DIR = System.getProperty("test.queries.dir");
    }
    
    public static final String SDK_PACKAGE_ZIP = RESOURCES_DIR + File.separator + "sdk_321_package.zip";
    public static final String EXTENSION_DATA_DOCUMENT = RESOURCES_DIR + File.separator + "extensionData.xml";
    public static final String RESOURCES_ETC_DIR = RESOURCES_DIR + File.separator + "etc";
    public static final String CLASS_TO_QNAME_FILENAME = RESOURCES_ETC_DIR + File.separator + "classToQname.xml";
    public static final String DOMAIN_MODEL_FILENAME = RESOURCES_ETC_DIR + File.separator + "fixed_cabioExampleDomainModel.xml";
    
    public static final String STYLE_NAME = "caCORE SDK v 3.2(.1)";
}
