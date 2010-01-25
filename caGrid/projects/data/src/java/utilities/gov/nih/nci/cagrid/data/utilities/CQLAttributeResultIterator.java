package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

/** 
 *  CQLAttributeResultIterator
 *  Iterator over attribute results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQLAttributeResultIterator implements Iterator {
    
    public static final QName CQL_ATTRIBUTE_RESULT_QNAME = 
        new QName(CqlSchemaConstants.CQL_RESULT_SET_URI, "CQLAttributeResult");
    
	private CQLAttributeResult[] results;
    private boolean xmlOnly;
	private int currentIndex;
	
	CQLAttributeResultIterator(CQLAttributeResult[] results, boolean xmlOnly) {
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
	 * @return TypeAttribute[] unless xmlOnly == true, 
	 * then a serialized CQLAttributeResult
	 */
	public Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
		currentIndex++;
		CQLAttributeResult result = results[currentIndex];
        if (xmlOnly) {
            StringWriter writer = new StringWriter();
            try {
                Utils.serializeObject(result, CQL_ATTRIBUTE_RESULT_QNAME, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error serializing attribute results: " + ex.getMessage(), ex);
            }
            return writer.getBuffer().toString();
        }
        return result.getAttribute();
	}
}
