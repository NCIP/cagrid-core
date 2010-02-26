package org.cagrid.dcql.utilities.encoding;

import java.net.URL;

public class Dcql2SerialzationHelper {

    public static final String MAPPING_LOCATION = "/org/cagrid/data/dcql/mapping/dcql2-castor-mapping.xml";
    
    static synchronized URL getMapping() {
        return Dcql2SerialzationHelper.class.getResource(MAPPING_LOCATION);
    }
}
