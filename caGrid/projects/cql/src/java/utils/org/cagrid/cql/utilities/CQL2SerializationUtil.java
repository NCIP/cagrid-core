package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLTargetObject;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

public class CQL2SerializationUtil {

    public static String serializeCql2Query(CQLQuery query) throws Exception {
        Mapping mapping = new Mapping();
        InputStream mappingStream = CQL2SerializationUtil.class.getResourceAsStream("/org/cagrid/cql2/mapping/cql2-castor-mapping.xml");
        mapping.loadMapping(new InputSource(mappingStream));
        
        StringWriter writer = new StringWriter();
        
        Marshaller m = new Marshaller(writer);
        m.setSuppressNamespaces(true);
        m.setSuppressXSIType(true);
        m.setMapping(mapping);
        m.marshal(query);
        
        return writer.getBuffer().toString();
    }
    
    
    public static CQLQuery deserializeCql2Query(String text) throws Exception {
        Mapping mapping = new Mapping();
        InputStream mappingStream = CQL2SerializationUtil.class.getResourceAsStream("/org/cagrid/cql2/mapping/cql2-castor-mapping.xml");
        mapping.loadMapping(new InputSource(mappingStream));
        
        Unmarshaller u = new Unmarshaller();
        u.setMapping(mapping);
        Object val = u.unmarshal(new StringReader(text));
        return (CQLQuery) val;
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
