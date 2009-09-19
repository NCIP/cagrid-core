package org.cagrid.fqp.test.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.common.DCQLConstants;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * UrlReplacer
 * Replaces URL placeholders in a DCQL query
 * 
 * @author ervin
 */
public class UrlReplacer {

    public static DCQLQuery replaceUrls(DCQLQuery query, Map<String, String> replacements) throws Exception {
        StringWriter writer = new StringWriter();
        Utils.serializeObject(query, DCQLConstants.DCQL_QUERY_QNAME, writer);
        StringBuffer dcqlXml = writer.getBuffer();
        for (String placeholder : replacements.keySet()) {
            replaceAll(dcqlXml, placeholder, replacements.get(placeholder));
        }
        DCQLQuery cleaned = (DCQLQuery) Utils.deserializeObject(new StringReader(dcqlXml.toString()), DCQLQuery.class);
        return cleaned;
    }
    
    
    private static void replaceAll(StringBuffer buffer, String placeholder, String replacement) {
        int index = -1;
        while ((index = buffer.indexOf(placeholder)) != -1) {
            buffer.replace(index, index + placeholder.length(), replacement);
        }
    }
}
