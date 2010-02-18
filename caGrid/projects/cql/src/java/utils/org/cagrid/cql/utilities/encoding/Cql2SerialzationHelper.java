package org.cagrid.cql.utilities.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cql2SerialzationHelper {

    public static final String MAPPING_LOCATION = "/org/cagrid/cql2/mapping/cql2-castor-mapping.xml";
    
    private static Log LOG = LogFactory.getLog(Cql2SerialzationHelper.class);
    
    private static byte[] mappingBytes = null;
    
    static synchronized ByteArrayInputStream getMappingStream() throws IOException {
        if (mappingBytes == null) {
            LOG.debug("Reading CQL 2 castor mapping from " + MAPPING_LOCATION);
            InputStream rawStream = Cql2SerialzationHelper.class.getResourceAsStream(MAPPING_LOCATION);
            if (rawStream == null) {
                LOG.error("Mapping not found!");
            }
            ByteArrayOutputStream byteHolder = new ByteArrayOutputStream();
            byte[] temp = new byte[8192];
            int read = -1;
            while ((read = rawStream.read(temp)) != -1) {
                byteHolder.write(temp, 0, read);
            }
            byteHolder.flush();
            byteHolder.close();
            rawStream.close();
            mappingBytes = byteHolder.toByteArray();
        }
        return new ByteArrayInputStream(mappingBytes);
    }
}
