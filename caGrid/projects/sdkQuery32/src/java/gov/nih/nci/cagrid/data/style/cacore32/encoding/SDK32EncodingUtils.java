package gov.nih.nci.cagrid.data.style.cacore32.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.apache.axis.MessageContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


public class SDK32EncodingUtils {
    public static final String CASTOR_MAPPING_DTD = "mapping.dtd";
    public static final String CASTOR_MAPPING_DTD_ENTITY = "-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN";
    public static final String DEFAULT_MARSHALLER_MAPPING = "/xml-mapping.xml";
    public static final String DEFAULT_UNMARSHALLER_MAPPING = "/unmarshaller-xml-mapping.xml";
    public static final String CASTOR_MARSHALLER_PROPERTY = "castorMarshallerMapping";
    public static final String CASTOR_UNMARSHALLER_PROPERTY = "castorUnmarshallerMapping";

    protected static Log LOG = LogFactory.getLog(SDK32EncodingUtils.class.getName());

    // maps <mapping location> to bytes of the Mapping file
    // using Hashtable for synchronization correctness
    protected static Map<String, byte[]> mappingCacheMap = new Hashtable();


    public static Mapping getMarshallerMapping(MessageContext context) {
        return getMapping(context, CASTOR_MARSHALLER_PROPERTY);
    }


    public static Mapping getUnmarshallerMapping(MessageContext context) {
        return getMapping(context, CASTOR_UNMARSHALLER_PROPERTY);
    }


    private static Mapping getMapping(MessageContext context, String mappingProperty) {
        long startTime = System.currentTimeMillis();

        EntityResolver resolver = new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                if (publicId.equals(CASTOR_MAPPING_DTD_ENTITY)) {
                    InputStream in = ClassUtils.getResourceAsStream(SDK32EncodingUtils.class, CASTOR_MAPPING_DTD);
                    return new InputSource(in);
                }
                return null;
            }
        };

        // determine the mapping location, starting with a default based on the
        // property
        String mappingLocation = mappingProperty.equals(CASTOR_MARSHALLER_PROPERTY)
            ? DEFAULT_MARSHALLER_MAPPING
            : DEFAULT_UNMARSHALLER_MAPPING;
        if (context != null) {
            String prop = (String) context.getProperty(mappingProperty);
            if (prop != null && !prop.trim().equals("")) {
                // the property exists in the message context, use the property
                // value
                mappingLocation = prop;
                LOG.debug("Loading castor mapping from message context property[" + mappingProperty + "]");
            } else {
                try {
                    // attempt to find the property in the wsdd global
                    // configuration
                    prop = (String) context.getAxisEngine().getConfig().getGlobalOptions().get(mappingProperty);
                } catch (Exception e) {
                    LOG.debug("Error reading global configuration:" + e.getMessage(), e);
                }
                if (prop != null && !prop.trim().equals("")) {
                    mappingLocation = prop;
                    LOG.debug("Loading castor mapping from globalConfiguration property[" + mappingProperty + "]");
                } else {
                    LOG.debug("Unable to locate castor mapping property[" + mappingProperty
                        + "], using default mapping location:" + mappingLocation);
                }
            }
        } else {
            LOG.debug("Unable to determine message context, using default mapping location:" + mappingLocation);
        }

        // locate the bytes of the mapping file
        byte[] mappingBytes = null;
        if (mappingCacheMap.containsKey(mappingLocation)) {
            LOG.debug("Loading Mapping from cache for location:" + mappingLocation);
            mappingBytes = mappingCacheMap.get(mappingLocation);
        } else {
            LOG.debug("Unable to loading Mapping from cache for location:" + mappingLocation);
            LOG.debug("Attempting to load mapping from mapping location:" + mappingLocation);
            InputStream mappingStream = ClassUtils.getResourceAsStream(SDK32EncodingUtils.class, mappingLocation);
            if (mappingStream == null) {
                LOG.error("Mapping file [" + mappingLocation + "] was null!");
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int len = -1;
                try {
                    while ((len = mappingStream.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    mappingBytes = bos.toByteArray();
                    mappingCacheMap.put(mappingLocation, mappingBytes);
                    mappingStream.close();
                } catch (IOException ex) {
                    LOG.error("Error loading mapping file [" + mappingLocation + "] : " + ex.getMessage(), ex);
                }
            }
        }

        ByteArrayInputStream mappingStream = new ByteArrayInputStream(mappingBytes);
        Mapping mapping = loadMappingFromStream(mappingLocation, mappingStream, resolver);

        long duration = System.currentTimeMillis() - startTime;
        LOG.debug("Time to load mapping file:" + duration + " ms.");

        return mapping;
    }


    private static Mapping loadMappingFromStream(String mappingLocation, InputStream mappingStream,
        EntityResolver resolver) {
        InputSource mappIS = new org.xml.sax.InputSource(mappingStream);
        Mapping mapping = new Mapping();
        mapping.setEntityResolver(resolver);
        try {
            mapping.loadMapping(mappIS);
        } catch (Exception e) {
            LOG.error("Unable to load mapping file:" + mappingLocation, e);
            mapping = null;
        }
        return mapping;
    }
}
