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
package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;

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
        wsddStream.close();
        return query;
    }
    
    
    public static void serializeDcql2QueryResults(DCQLQueryResultsCollection results, Writer writer) throws Exception {
        InputStream wsddStream = DCQL2SerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        Utils.serializeObject(results, DCQL2Constants.DCQL2_RESULTS_QNAME, writer, wsddStream);
        wsddStream.close();
    }
    
    
    public static DCQLQueryResultsCollection deserializeDcql2QueryResults(Reader reader) throws Exception {
        InputStream wsddStream = DCQL2SerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        DCQLQueryResultsCollection results = Utils.deserializeObject(reader, DCQLQueryResultsCollection.class, wsddStream);
        wsddStream.close();
        return results;
    }
    
    
    public static DCQLQuery cloneQueryBean(DCQLQuery query) throws Exception {
        StringWriter writer = new StringWriter();
        serializeDcql2Query(query, writer);
        return deserializeDcql2Query(new StringReader(writer.getBuffer().toString()));
    }
}
