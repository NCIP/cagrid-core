package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CQL2WsddTypesUtil
 * Utility that adds the CQL 2 beans type mappings to a wsdd file
 * 
 * @author David
 */
public class CQL2WsddTypesUtil {
    
    private static Log LOG = LogFactory.getLog(CQL2WsddTypesUtil.class);
    
    public static final String START_MAPPING_COMMENT = "<!-- START CQL 2 TYPE MAPPINGS -->";
    public static final String END_MAPPING_COMMENT = "<!-- END CQL 2 TYPE MAPPINGS -->";
    public static final String INTRODUCE_MAPPING_START = "<!-- START INTRODUCE TYPEMAPPINGS -->";
    public static final String INTRODUCE_MAPPING_END = "<!-- END INTRODUCE TYPEMAPPINGS -->";
    public static final String WSDD_END_TAG = "</deployment>";
    
    
    public static void main(String[] args) {
        try {
            File wsdd = new File("server-config.wsdd");
            addCql2TypesMappingToWsdd(wsdd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

    private CQL2WsddTypesUtil() {
        // no instantiation, just a static util
    }
    
    
    public static void addCql2TypesMappingToWsdd(File wsddFile) throws IOException {
        LOG.debug("Adding CQL 2 types mapping to wsdd " + wsddFile.getAbsolutePath());
        // read the existing WSDD file in
        FileInputStream wsddStream = new FileInputStream(wsddFile);
        StringBuffer wsdd = Utils.inputStreamToStringBuffer(wsddStream);
        wsddStream.close();
        // remove any existing CQL 2 types mapping block
        removeCql2TypesMappingBlock(wsdd);
        // remove any introduce-added types mapping for CQL 2 types
        removeIntroduceManagedCql2TypesMappings(wsdd);
        // find the wsdd end tag, write mappings there
        int insertLocation = wsdd.indexOf(WSDD_END_TAG);
        if (insertLocation == -1) {
            LOG.error("Couldn't find " + WSDD_END_TAG + " in wsdd!");
            throw new IllegalStateException("Malformed wsdd found (" + wsddFile.getAbsolutePath() + ")");
        }
        String typesMappingText = getTypesFromCql2Wsdd();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding types mapping text:\n");
            LOG.debug(typesMappingText);
        }
        wsdd.insert(insertLocation, typesMappingText);
        Utils.stringBufferToFile(wsdd, wsddFile);
    }
    
    
    private static void removeCql2TypesMappingBlock(StringBuffer wsdd) {
        int start = wsdd.indexOf(START_MAPPING_COMMENT);
        if (start != -1) {
            int end = wsdd.indexOf(END_MAPPING_COMMENT, start);
            // remove the whole CQL 2 types mapping block
            LOG.debug("Old types mapping found in wsdd; removing");
            wsdd.delete(start, end + END_MAPPING_COMMENT.length());
        }
    }
    
    
    private static void removeIntroduceManagedCql2TypesMappings(StringBuffer wsdd) {
        // constrain the search space
        int searchStart = wsdd.indexOf(INTRODUCE_MAPPING_START) + INTRODUCE_MAPPING_START.length();
        int searchEnd = wsdd.indexOf(INTRODUCE_MAPPING_END);
        
        // serializer="org.cagrid.cql.utilities.encoding.Cql2SerializerFactory" deserializer="org.cagrid.cql.utilities.encoding.Cql2DeserializerFactory" type="ns1:org.cagrid.cql2.results.CQLQueryResults"
        String serializerAttributes = "serializer=\"org.cagrid.cql.utilities.encoding.Cql2SerializerFactory\" deserializer=\"org.cagrid.cql.utilities.encoding.Cql2DeserializerFactory\"";
        int foundIndex = 0;
        while ((foundIndex = wsdd.indexOf(serializerAttributes, searchStart)) != -1) {
            // verify the type mapping is in the Introduce managed part
            if (foundIndex < searchEnd) {
                // find the open and close brackets for this element and remove it
                int endOfElement = wsdd.indexOf("/>", foundIndex) + 2;
                int startOfElement = previousIndexOf(wsdd, "<", foundIndex);
                wsdd.replace(startOfElement, endOfElement, "");
            } else {
                break;
            }
        }
    }
    
    
    private static int previousIndexOf(StringBuffer buff, String search, int fromIndex) {
        int index = 0;
        int prevIndex = -1;
        while ((index = buff.indexOf(search, index)) != -1) {
            if (index > fromIndex) {
                break;
            }
            prevIndex = index;
            index += search.length();
        }
        return prevIndex;
    }
    
    
    private static String getTypesFromCql2Wsdd() throws IOException {
        LOG.debug("Reading CQL 2 types mapping from packaged wsdd");
        InputStream wsddStream = CQL2WsddTypesUtil.class.getResourceAsStream("/org/cagrid/cql2/mapping/client-config.wsdd");
        StringBuffer wsdd = Utils.inputStreamToStringBuffer(wsddStream);
        wsddStream.close();
        int start = wsdd.indexOf(START_MAPPING_COMMENT);
        int end = wsdd.indexOf(END_MAPPING_COMMENT, start) + END_MAPPING_COMMENT.length();
        return wsdd.substring(start, end);
    }
}
