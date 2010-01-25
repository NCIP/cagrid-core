package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLIdentifierResult;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

/** 
 *  CQLIdentifierResultIterator
 *  Iterator over CQL Identifier Results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQLIdentifierResultIterator implements Iterator {
    public static final QName CQL_IDENTIFIER_RESULT_QNAME = 
        new QName(CqlSchemaConstants.CQL_RESULT_SET_URI, "CQLIdentifierResult");
    
	private CQLIdentifierResult[] results;
    private boolean xmlOnly;
	private int currentIndex;
	
	CQLIdentifierResultIterator(CQLIdentifierResult[] results, boolean xmlOnly) {
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


	public Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
		currentIndex++;
		CQLIdentifierResult result = results[currentIndex];
        if (xmlOnly) {
            StringWriter writer = new StringWriter();
            try {
                Utils.serializeObject(result, CQL_IDENTIFIER_RESULT_QNAME, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error serializing identifier result: " + ex.getMessage(), ex);
            }
            return writer.getBuffer().toString();
        }
		return result.getIdentifier();
	}
}