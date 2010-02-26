package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.cagrid.data.dcql.DCQLQuery;

/**
 * DCQL2SerializationUtil
 * Utility to serialize / deserialize DCQL 2 queries
 * using the custom castor serialization where required
 * 
 * @author David
 */
public class DCQL2SerializationUtil {

    public static final String CLIENT_CONFIG_LOCATION = "/org/cagrid/data/dcql/mapping/client-config.wsdd";


    private DCQL2SerializationUtil() {
        // just static methods
    }
    
    
    public static void serializeDcql2Query(DCQLQuery query, Writer writer) throws Exception {
        InputStream wsddStream = DCQL2SerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        Utils.serializeObject(query, DCQL2Constants.DCQL2_QUERY_QNAME, writer, wsddStream);
        wsddStream.close();
    }
    
    
    public static String serializeDcql2Query(DCQLQuery query) throws Exception {
        StringWriter writer = new StringWriter();
        serializeDcql2Query(query, writer);
        return writer.getBuffer().toString();
    }
    
    
    public static DCQLQuery deserializeDcql2Query(String text) throws Exception {
        return deserializeDcql2Query(new StringReader(text));
    }
    
    
    public static DCQLQuery deserializeDcql2Query(Reader reader) throws Exception {
        InputStream wsddStream = DCQL2SerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        DCQLQuery query = Utils.deserializeObject(reader, DCQLQuery.class, wsddStream);
        return query;
    }
}
