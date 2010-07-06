package org.cagrid.cql.utilities.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.util.DTDResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Cql2SerialzationHelper {
    // castor's DTD
    public static final String CASTOR_MAPPING_DTD = "org/exolab/castor/mapping/mapping.dtd";
    public static final String CASTOR_MAPPING_DTD_ENTITY = "-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN";
    
    // the CQL 2 mapping file
    public static final String MAPPING_LOCATION = "/org/cagrid/cql2/mapping/cql2-castor-mapping.xml";
    
    private static Log LOG = LogFactory.getLog(Cql2SerialzationHelper.class);
    
    private static byte[] mappingBytes = null;
    private static EntityResolver dtdResolver = null;
    
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
    
    
    public static EntityResolver getDtdResolver() {
        if (dtdResolver == null) {
            // simple entity resolver to load the castor dtd from the class loader
            dtdResolver = new DTDResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
                    LOG.trace("RESOLVING ENTITY " + publicId + ", " + systemId);
                    if (CASTOR_MAPPING_DTD_ENTITY.equals(publicId)) {
                        InputStream stream = ClassUtils.getResourceAsStream(Cql2Deserializer.class, CASTOR_MAPPING_DTD);
                        return new InputSource(stream);
                    }
                    return super.resolveEntity(publicId, systemId);
                }
            };
        }
        return dtdResolver;
    }
}
