package org.cagrid.cql.utilities.iterator;

import gov.nih.nci.cagrid.common.Utils;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.cagrid.cql2.results.CQLAttributeResult;

/** 
 *  CQL2AttributeResultIterator
 *  Iterator over attribute results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQL2AttributeResultIterator implements Iterator<Object> {
    
    public static final QName CQL2_ATTRIBUTE_RESULT_QNAME = 
        new QName("http://CQL.caBIG/2/org.cagrid.cql2.results", "CQLAttributeResult");
    
	private CQLAttributeResult[] results;
    private boolean xmlOnly;
	private int currentIndex;
	
	CQL2AttributeResultIterator(CQLAttributeResult[] results, boolean xmlOnly) {
		this.results = results;
        this.xmlOnly = xmlOnly;
		this.currentIndex = -1;
	}
	

	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
	}


	public boolean hasNext() {
		return currentIndex + 1 < results.length;
	}


	/**
	 * @return TargetAttribute[] unless xmlOnly == true, 
	 * then a serialized CQLAttributeResult
	 */
	public Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
        Object value = null;
		currentIndex++;
		CQLAttributeResult result = results[currentIndex];
        if (xmlOnly) {
            StringWriter writer = new StringWriter();
            try {
                Utils.serializeObject(result, CQL2_ATTRIBUTE_RESULT_QNAME, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error serializing attribute results: " + ex.getMessage(), ex);
            }
            value = writer.getBuffer().toString();
        } else {
            value = result.getAttribute();
        }
        return value;
	}
}
