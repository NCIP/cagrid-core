package org.cagrid.gaards.core;

import java.security.Provider;

public class EracomUtils {

    public static final String PROVIDER_CLASS_SHORT_NAME = "ERACOMProvider";
    public static final String PROVIDER_CLASS_PACKAGE = "au.com.eracom.crypto.provider.slot";
    
    public static final String KEYSTORE_TYPE = "CRYPTOKI";

    public static final Provider getEracomProvider(int slot) 
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> providerClass = Class.forName(PROVIDER_CLASS_PACKAGE + slot + "." + PROVIDER_CLASS_SHORT_NAME);
        Provider provider = (Provider) providerClass.newInstance();
        return provider;
    }
}
