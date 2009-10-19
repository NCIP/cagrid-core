package org.cagrid.data.sdkquery41.processor;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.DataServiceConstants;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.filter.Filter;

public class CQLAttributeDefaultPredicateUtil {
    
    private static Log LOG = LogFactory.getLog(CQLAttributeDefaultPredicateUtil.class);

    private CQLAttributeDefaultPredicateUtil() {
        
    }
    
    
    public static CQLQuery checkDefaultPredicates(CQLQuery original) throws Exception {
        LOG.debug("Checking query for Attributes with no predicate defined");
        StringWriter originalWriter = new StringWriter();
        Utils.serializeObject(original, DataServiceConstants.CQL_QUERY_QNAME, originalWriter);
        Element root = XMLUtilities.stringToDocument(originalWriter.getBuffer().toString()).getRootElement();
        Filter attributeNoPredicateFilter = new Filter() {
            public boolean matches(Object o) {
                if (o instanceof Element) {
                    Element e = (Element) o;
                    if (e.getName().equals("Attribute") && e.getAttribute("predicate") == null) {
                        return true;
                    }
                }
                return false;
            }
        };
        List<?> attributesWithNoPredicate = root.getContent(attributeNoPredicateFilter);
        Iterator<?> attribIter = attributesWithNoPredicate.iterator();
        while (attribIter.hasNext()) {
            LOG.debug("Adding default predicate to an attribute");
            Element elem = (Element) attribIter.next();
            elem.setAttribute("predicate", "EQUAL_TO");
        }
        String xml = XMLUtilities.elementToString(root);
        CQLQuery edited = Utils.deserializeObject(new StringReader(xml), CQLQuery.class);
        return edited;
    }
}
