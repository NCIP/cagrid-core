package org.cagrid.cql.utilities.iterator;


import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axis.utils.ClassUtils;
import org.cagrid.cql2.results.CQLQueryResults;

/** 
 *  CQLQueryResultsIterator
 *  Iterator over CQL 2 Query Results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQL2QueryResultsIterator implements Iterator<Object> {
    
    private CQLQueryResults results;
	private Iterator<Object> resultIterator;
	private boolean xmlOnly;
	private InputStream wsddConfigStream;
	
	/**
	 * Create a new CQL2QueryResultsIterator which will return Object 
	 * results deserialized with the default configured AXIS deserializers
	 * 
	 * @param results
	 * 		The results to iterate over
	 */
	public CQL2QueryResultsIterator(CQLQueryResults results) {
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
	public CQL2QueryResultsIterator(CQLQueryResults results, boolean xmlOnly) {
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
	public CQL2QueryResultsIterator(CQLQueryResults results, InputStream wsdd) {
		this(results, false, wsdd);
	}
	
	
	/**
	 * Internal constructor
	 * @param results
	 * @param xmlOnly
	 * @param wsddFilename
	 */
	private CQL2QueryResultsIterator(CQLQueryResults results, boolean xmlOnly, InputStream wsdd) {
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

	
	private Iterator<Object> getIterator() {
		if (resultIterator == null) {
			if (results.getObjectResult() != null && results.getObjectResult().length != 0) {
				resultIterator = new CQL2ObjectResultIterator(
					results.getObjectResult(), results.getTargetClassname(), 
					xmlOnly, findConfigWsdd());
			} else if (results.getAttributeResult() != null && results.getAttributeResult().length != 0) {
				resultIterator = new CQL2AttributeResultIterator(results.getAttributeResult(), xmlOnly);
			} else if (results.getAggregationResult() != null) {
			    resultIterator = new CQL2AggregationIterator(results.getAggregationResult(), xmlOnly);
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
    
    
    private static class NullIterator implements Iterator<Object> {
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
