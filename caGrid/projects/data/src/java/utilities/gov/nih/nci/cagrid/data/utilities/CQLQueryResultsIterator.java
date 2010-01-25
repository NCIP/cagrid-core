package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLCountResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.apache.axis.utils.ClassUtils;

/** 
 *  CQLQueryResultsIterator
 *  Iterator over CQL Query Results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @deprecated As of caGrid 1.4, CQL 2 is the preferred query language.  http://cagrid.org/display/dataservices/CQL+2
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQLQueryResultsIterator implements Iterator {
    public static final QName CQL_COUNT_RESULT_QNAME = 
        new QName(CqlSchemaConstants.CQL_RESULT_SET_URI, "CQLCountResult");
    
    
	private CQLQueryResults results;
	private Iterator resultIterator;
	private boolean xmlOnly;
	private InputStream wsddConfigStream;
	
	/**
	 * Create a new CQLQueryResultsIterator which will return Object 
	 * results deserialized with the default configured AXIS deserializers
	 * 
	 * @param results
	 * 		The results to iterate over
	 */
	public CQLQueryResultsIterator(CQLQueryResults results) {
		this(results, false, null);
	}
	
	
	/**
	 * When returning objects, setting xmlOnly to true will bypass the
	 * AXIS deserializer and return straight XML strings.  When set to
	 * false, the results are deserialized with the default AXIS config
	 * 
	 * @param results
	 * 		The resuls to iterate over
	 * @param xmlOnly
	 * 		A flag to indicate if xml strings or objects should be returned
	 */
	public CQLQueryResultsIterator(CQLQueryResults results, boolean xmlOnly) {
		this(results, xmlOnly, null);
	}
	
	
	/**
	 * When returning objects, the supplied WSDD configuration file is used
	 * to configure the AXIS deserializers
	 * 
	 * @param results
	 * 		The results to iterate over
	 * @param wsdd
	 * 		The filename of a wsdd file to use for configuration
	 */
	public CQLQueryResultsIterator(CQLQueryResults results, InputStream wsdd) {
		this(results, false, wsdd);
	}
	
	
	/**
	 * Internal constructor
	 * @param results
	 * @param xmlOnly
	 * @param wsddFilename
	 */
	private CQLQueryResultsIterator(CQLQueryResults results, boolean xmlOnly, InputStream wsdd) {
		if (results == null) {
			throw new IllegalArgumentException("Results cannot be null");
		}
		this.results = results;
		this.xmlOnly = xmlOnly;
		this.wsddConfigStream = wsdd;
	}
	

	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
	}


	public boolean hasNext() {
		return getIterator().hasNext();
	}


	public Object next() {
		return getIterator().next();
	}

	
	private Iterator getIterator() {
		if (resultIterator == null) {
			if (results.getObjectResult() != null && results.getObjectResult().length != 0) {
				resultIterator = new CQLObjectResultIterator(
					results.getObjectResult(), results.getTargetClassname(), 
					xmlOnly, findConfigWsdd());
			} else if (results.getAttributeResult() != null && results.getAttributeResult().length != 0) {
				resultIterator = new CQLAttributeResultIterator(results.getAttributeResult(), xmlOnly);
			} else if (results.getIdentifierResult() != null && results.getIdentifierResult().length != 0) {
				resultIterator = new CQLIdentifierResultIterator(results.getIdentifierResult(), xmlOnly);
			} else if (results.getCountResult() != null) {
				resultIterator = new CountIterator(results.getCountResult(), xmlOnly);
			} else {
				resultIterator = new NullIterator("No results");
			}
		}
		return resultIterator;
	}
	
	
	private InputStream findConfigWsdd() {
		if (wsddConfigStream == null) {
			// use the axis default client configuration
			wsddConfigStream = ClassUtils.getResourceAsStream(getClass(), "client-config.wsdd");
		}
		return wsddConfigStream;
	}
    
    
    private static class CountIterator implements Iterator {
        private Iterator iter;
        
        public CountIterator(CQLCountResult result, boolean xmlOnly) {
            Object item = null;
            if (xmlOnly) {
                StringWriter writer = new StringWriter();
                try {
                    Utils.serializeObject(result, CQL_COUNT_RESULT_QNAME, writer);
                } catch (Exception ex) {
                    throw new RuntimeException("Error serializing count result: " + ex.getMessage(), ex);
                }
                item = writer.getBuffer().toString();
            } else {
                item = Long.valueOf(result.getCount());
            }
            List<Object> tmp = new ArrayList<Object>(1);
            tmp.add(item);
            iter = tmp.iterator();
        }
        
        
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
        }
        
        
        public boolean hasNext() {
            return iter.hasNext();
        }
        
        
        public Object next() {
            return iter.next();
        }
    }
	
	
	private static class NullIterator implements Iterator {
		private String errorMessage;
		
		public NullIterator(String err) {
			this.errorMessage = err;
		}
		
		
		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
		}
		
		
		public boolean hasNext() {
			return false;
		}
		
		
		public Object next() {
			throw new NoSuchElementException(errorMessage);
		}
	}
}
