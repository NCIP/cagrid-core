/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.data.sdkquery44.encoding;

import gov.nih.nci.cagrid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


public class SDK44EncodingUtils {
    // castor's DTD
	public static final String CASTOR_MAPPING_DTD = "org/exolab/castor/mapping/mapping.dtd";//"mapping.dtd";
	public static final String CASTOR_MAPPING_DTD_ENTITY = "-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN";
    
    // properties expected to be defined in global section of wsdd
    public static final String CASTOR_MARSHALLER_PROPERTY = "castorMarshallerMapping";
    public static final String CASTOR_UNMARSHALLER_PROPERTY = "castorUnmarshallerMapping";

    // default filenames / locations of [un]marshalling mapping documents
	public static final String DEFAULT_MARSHALLER_MAPPING = "/xml-mapping.xml";
    public static final String DEFAULT_UNMARSHALLER_MAPPING = "/unmarshaller-xml-mapping.xml";
    
	protected static Log LOG = LogFactory.getLog(SDK44EncodingUtils.class.getName());
    
	// maps resource location to the contents of the resource
	protected static Map<String, String> resourceMap = 
        Collections.synchronizedMap(new HashMap<String, String>());
    
    protected static EntityResolver dtdResolver = null;
    
    public static Mapping getMarshallerMapping(MessageContext context) {
        return getMapping(context, CASTOR_MARSHALLER_PROPERTY);
    }
    
    
    public static Mapping getUnmarshallerMapping(MessageContext context) {
        return getMapping(context, CASTOR_UNMARSHALLER_PROPERTY);
    }


	protected static Mapping getMapping(MessageContext context, String mappingProperty) {
		long startTime = System.currentTimeMillis();

        // determine the mapping location, starting with a default based on the property
		String mappingLocation = mappingProperty.equals(CASTOR_MARSHALLER_PROPERTY) 
            ? DEFAULT_MARSHALLER_MAPPING : DEFAULT_UNMARSHALLER_MAPPING;
		if (context != null) {
			String prop = (String) context.getProperty(mappingProperty);
			if (prop != null && !prop.trim().equals("")) {
                // the property exists in the message context, use the property value
				mappingLocation = prop;
				LOG.debug("Loading castor mapping from message context property[" + mappingProperty + "]");
			} else {
				try {
                    // attempt to find the property in the wsdd global configuration
					prop = (String) context.getAxisEngine().getConfig().getGlobalOptions().get(mappingProperty);
				} catch (Exception e) {
					LOG.warn("Error reading global configuration:" + e.getMessage(), e);
				}
				if (prop != null && !prop.trim().equals("")) {
					mappingLocation = prop;
					LOG.debug("Loading castor mapping from globalConfiguration property[" + mappingProperty + "]");
				} else {
				    // walk through the WSDD config and find the property in service config options
				    EngineConfiguration config = context.getAxisEngine().getConfig();
		            if (config instanceof WSDDEngineConfiguration) {
		                WSDDDeployment wsdd = ((WSDDEngineConfiguration) config).getDeployment();
		                WSDDService[] services = wsdd.getServices();
		                for (WSDDService service : services) {
		                    prop = service.getParameter(mappingProperty);
		                    if (prop != null && !prop.trim().equals("")) {
		                        // found it!
		                        mappingLocation = prop;
		                        LOG.debug("Loading castor mapping from service " + service.getQName() 
		                            + " property [" + prop + "]");
		                        break;
		                    }
		                }
		            }
                    if (!(prop != null && !prop.trim().equals(""))) {
                        LOG.warn("Unable to locate castor mapping property[" + mappingProperty
                            + "], using default mapping location:" + mappingLocation);
                    }
                }
			}
		} else {
			LOG.debug("Unable to determine message context, using default mapping location:" + mappingLocation);
		}

        // locate the bytes of the mapping file
        String mappingDocument = null;
        try {
            mappingDocument = loadResource(mappingLocation);
        } catch (IOException ex) {
            LOG.error("Error loading mapping file [" + mappingLocation + "] : " + ex.getMessage(), ex);
        }
        
        Mapping mapping = loadMappingFromString(mappingLocation, mappingDocument, getDtdResolver());
        
		long duration = System.currentTimeMillis() - startTime;
		LOG.debug("Time to load mapping file:" + duration + " ms.");

		return mapping;
	}
    
    
    protected static Mapping loadMappingFromString(String mappingLocation,
            String mappingContents, EntityResolver resolver) {
        InputSource mappIS = new org.xml.sax.InputSource(new StringReader(mappingContents));
        Mapping mapping = new Mapping();
        mapping.setEntityResolver(resolver);
        try {
            mapping.loadMapping(mappIS);
        } catch (Exception ex) {
            LOG.error("Error loading castor mapping (" 
                + mappingLocation + "): " + ex.getMessage(), ex);
        }
        return mapping;
    }
    
    
    protected static String loadResource(String resourceName) throws IOException {
        String resource = null;
        synchronized (resourceMap) {
            if (resourceMap.containsKey(resourceName)) {
                resource = resourceMap.get(resourceName);
            } else {
                InputStream stream = ClassUtils.getResourceAsStream(
                    SDK44EncodingUtils.class, resourceName);
                if (stream != null) {
                    StringBuffer buffer = Utils.inputStreamToStringBuffer(stream);
                    resource = buffer.toString();
                    resourceMap.put(resourceName, resource);
                    stream.close();
                }
            }
        }
        return resource;
    }
    
    
    protected static EntityResolver getDtdResolver() {
        if (dtdResolver == null) {
            // simple entity resolver to load the castor dtd from the class loader
            dtdResolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws IOException {
                    if (publicId.equals(CASTOR_MAPPING_DTD_ENTITY)) {
                        String dtd = loadResource(CASTOR_MAPPING_DTD);
                        return new InputSource(new StringReader(dtd));
                    }
                    return null;
                }
            };
        }
        return dtdResolver;
    }
}
