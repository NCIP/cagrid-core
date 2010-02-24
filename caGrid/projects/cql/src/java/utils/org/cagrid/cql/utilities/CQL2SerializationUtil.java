package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLTargetObject;

/**
 * CQL2SerializationUtil
 * Utility to serialize / deserialize CQL 2 queries
 * using the custom castor serialization
 * 
 * @author David
 */
public class CQL2SerializationUtil {
    
    private CQL2SerializationUtil() {
        // just static methods
    }
    
    
    public static void serializeCql2Query(CQLQuery query, Writer writer) throws Exception {
        InputStream wsddStream = CQL2SerializationUtil.class.getResourceAsStream("/org/cagrid/cql2/mapping/client-config.wsdd");
        Utils.serializeObject(query, CQLConstants.CQL2_QUERY_QNAME, writer, wsddStream);
        wsddStream.close();
    }
    

    public static String serializeCql2Query(CQLQuery query) throws Exception {
        StringWriter writer = new StringWriter();
        serializeCql2Query(query, writer);
        return writer.getBuffer().toString();
    }
    
    
    public static CQLQuery deserializeCql2Query(String text) throws Exception {
        return deserializeCql2Query(new StringReader(text));
    }
    
    
    public static CQLQuery deserializeCql2Query(Reader reader) throws Exception {
        InputStream wsddStream = CQL2SerializationUtil.class.getResourceAsStream("/org/cagrid/cql2/mapping/client-config.wsdd");
        CQLQuery query = Utils.deserializeObject(reader, CQLQuery.class, wsddStream);
        return query;
    }
    
    
    public static void main(String[] args) {
        try {
            CQLQuery query = new CQLQuery();
            CQLTargetObject target = new CQLTargetObject();
            target.setClassName("foo.bar");
            target.set_instanceof("zor");
            CQLAttribute attribute = new CQLAttribute();
            attribute.setName("word");
            attribute.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
            AttributeValue value = new AttributeValue();
            value.setStringValue("hello");
            attribute.setAttributeValue(value);
            target.setCQLAttribute(attribute);
            query.setCQLTargetObject(target);

            System.out.println("Serializing");
            String serialized = serializeCql2Query(query);
            serialized = XMLUtilities.formatXML(serialized);
            System.out.println(serialized);
            
            System.out.println("Deserializing");
            CQLQuery query2 = deserializeCql2Query(serialized);
            System.out.println("got it? " + query2 != null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
