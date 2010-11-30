package gov.nih.nci.cagrid.common.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SecurityConstants {
    
    // the maximum proxy path length
    private static final int MAX_PATH_LENGTH = 255;
    
    // use the java provided crypto libraries
    public static final String CRYPTO_PROVIDER = "SunRsaSign";
    
    // the default signing algorithm
    public static final String DEFAULT_SIGNING_ALGORITHM = "SHA256withRSA";
    
    // convert the signing algorithm OID to a string we can use
    // these names are the ones used by the java provided libraries
    // and don't match what was in the BouncyCastle libs
    private static Map<String, String> OID_TO_NAME;
    static {
        OID_TO_NAME = new HashMap<String, String>();

        OID_TO_NAME.put("1.2.840.10040.4.3", "DSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.2", "MD2withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.3", "MD4withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.4", "MD5withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.5", "SHA1withRSA");
        // added OIDs for a variety of algorithms
        // OIDs from http://www.oid-info.com/index.htm
        OID_TO_NAME.put("1.2.840.113549.1.1.11", "SHA256withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.12", "SHA384withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.13", "SHA512withRSA");
    }

    // the set of "valid" RSA signing algorithms we know about
    private static Set<String> RSA_ALGORITHMS;
    static {
        RSA_ALGORITHMS = new HashSet<String>();
        RSA_ALGORITHMS.add("MD2withRSA");
        RSA_ALGORITHMS.add("MD2withRSA");
        RSA_ALGORITHMS.add("MD5withRSA");
        RSA_ALGORITHMS.add("SHA1withRSA");
        RSA_ALGORITHMS.add("SHA256withRSA");
        RSA_ALGORITHMS.add("SHA384withRSA");
        RSA_ALGORITHMS.add("SHA512withRSA");
    }
    
    
    public static final int getMaxProxyPathLength() {
        return MAX_PATH_LENGTH;
    }
    
    
    public static final String getDefaultCryptoProvider() {
        return CRYPTO_PROVIDER;
    }
    
    
    public static final String getAlgorithmNameForOid(String oid) {
        return OID_TO_NAME.get(oid);
    }
    
    
    public static final boolean isKnownRsaAlgorithm(String algName) {
        return RSA_ALGORITHMS.contains(algName);
    }
    
    
    public static final Set<String> getKnownRsaAlgorithms() {
        return Collections.unmodifiableSet(RSA_ALGORITHMS);
    }
    

    private SecurityConstants() {
        // no instantiation, just constants
    }
}
