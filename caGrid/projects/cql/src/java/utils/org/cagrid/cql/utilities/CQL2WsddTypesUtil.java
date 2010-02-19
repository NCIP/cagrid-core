package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
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
    public static final String WSDD_END_TAG = "</deployment>";

    private CQL2WsddTypesUtil() {
        
    }
    
    
    public static void addCql2TypesMappingToWsdd(File wsddFile) throws IOException {
        LOG.debug("Adding CQL 2 types mapping to wsdd " + wsddFile.getAbsolutePath());
        StringBuffer wsdd = Utils.fileToStringBuffer(wsddFile);
        removeTypesMapping(wsdd);
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
    
    
    private static void removeTypesMapping(StringBuffer wsdd) {
        int start = wsdd.indexOf(START_MAPPING_COMMENT);
        if (start != -1) {
            int end = wsdd.indexOf(END_MAPPING_COMMENT, start);
            // remove the whole CQL 2 types mapping block
            LOG.debug("Old types mapping found in wsdd; removing");
            wsdd.delete(start, end + END_MAPPING_COMMENT.length());
        }
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
