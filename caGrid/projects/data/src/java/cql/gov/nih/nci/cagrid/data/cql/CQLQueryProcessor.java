package gov.nih.nci.cagrid.data.cql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/** 
 *  CQLQueryProcessor
 *  Abstract class the service providers must extend to process 
 *  CQL Queries to a caGrid data service.
 *  A DataService instance will have only one instance of the query
 *  processor, created on the first call to the query method.  At creation
 *  time, the processor will be configured via the initialize method.
 *  All subsequent calls to the query method will simply invoke the 
 *  <code>processQuery</code> method.
 * 
 * @deprecated As of caGrid 1.4, CQL 2 is the preferred query language.  http://cagrid.org/display/dataservices/CQL+2
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 25, 2006 
 * @version $Id$ 
 */
public abstract class CQLQueryProcessor {	
	private Properties params;
	private InputStream wsddStream;
	
	public CQLQueryProcessor() {
	
	}
	
	
	/**
	 * Initialize the query processor with the properties it requires as specified
	 * in the Properties instance provided by getRequiredParameters(), and values
	 * populated by the user's custom entries, if any.
	 * 
	 * @param parameters
	 * 		The parameters as configured by the user.  The set of keys must contain all
	 * 		of the keys contained in the Properties object returned 
	 * 		by <code>getRequiredParamters()</code>.  The values in the parameters will
	 *		be either the user defined value or the default value from 
	 *		<code>getRequiredParameters()</code>.
	 * @param wsdd
	 * 		The input stream which contains the wsdd configuration for the data service.
	 * 		This stream may be important to locating type mappings for serializing and
	 * 		deserializing beans.
	 * @throws InitializationException
	 */
	public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
		Set<String> required = new HashSet<String>();
        // add all the required parameters to a set
        Enumeration requiredKeys = getRequiredParameters().keys();
        while (requiredKeys.hasMoreElements()) {
            required.add((String) requiredKeys.nextElement());
        }
        // remove all the parameters provided
        Enumeration providedKeys = parameters.keys();
        while (providedKeys.hasMoreElements()) {
            required.remove(providedKeys.nextElement().toString());
        }
        // verify the provided parameters cover the required ones
		if (required.size() != 0) {
			// some required parameters NOT specified!
			StringBuffer error = new StringBuffer();
			error.append("Required parameters for query processor ");
			error.append(getClass().getName()).append(" not specified: ");
			Iterator<String> requiredKeyIter = required.iterator();
			while (requiredKeyIter.hasNext()) {
				error.append(requiredKeyIter.next());
				if (requiredKeyIter.hasNext()) {
					error.append(", ");
				}
			}
			throw new InitializationException(error.toString());
		}
		this.params = parameters;
		this.wsddStream = wsdd;
	}
	
	
	/**
	 * @return
	 * 		The parameters as configured by the user at runtime.
     *      The set of keys must contain all of the keys contained in 
     *      the Properties object returned by <code>getRequiredParamters()</code>.  
     *      The values in the parameters will be either the user defined 
     *      value or the default value from <code>getRequiredParameters()</code>.
	 */
	protected Properties getConfiguredParameters() {
		return this.params;
	}
	
	
	/**
	 * @return
	 * 		The input stream which contains the wsdd configuration for the data service.
	 * 		This stream may be important to locating type mappings for serializing and
	 * 		deserializing beans.
	 */
	protected InputStream getConfiguredWsddStream() {
		return this.wsddStream;
	}
	
	
	/**
	 * Processes the CQL Query
     * 
	 * @param cqlQuery
	 * @return The results of processing a CQL query
	 * @throws MalformedQueryException
	 * 		Should be thrown when the query itself does not conform to the
	 * 		CQL standard or attempts to perform queries outside of 
	 * 		the exposed domain model
	 * @throws QueryProcessingException
	 * 		Thrown for all exceptions in query processing not related
	 * 		to the query being malformed
	 */
	public abstract CQLQueryResults processQuery(CQLQuery cqlQuery) 
		throws MalformedQueryException, QueryProcessingException;
	
	
	/**
	 * Get a Properties object of parameters the query processor will require 
	 * on initialization.  
	 * 
	 * Subclasses can override this method to return a map describing paramters
	 * their implementation needs.
	 * 
	 * The keys are the names of parameters the query processor 
	 * requires, the values are the defaults for those properties.  The default value
	 * of a property may be an empty string if it is an optional paramter.
	 * The keys MUST be valid java variable names.  They MUST NOT contain spaces 
	 * or punctuation.  They may begin with an uppercase character.
	 * 
	 * @return
	 * 		The required properties for the query processor with their default values
	 */
	public Properties getRequiredParameters() {
		return new Properties();
	}
    
    
    /**
     * Get a set of property names whose values should be file names
     * prepended with the location of the service's etc directory.
     * Potential uses include locating config files, storing logs, etc.
     * 
     * Subclasses can override this method to return a Set of Strings which
     * are the names of property keys from the getRequiredParameters() method.
     * 
     * @return
     *      The set of property names
     */
    public Set<String> getPropertiesFromEtc() {
        return new HashSet<String>();
    }
    
    
    /**
     * Get the classname of the configuration user interface for 
     * this CQL Query Processor.  This class should exist in the same 
     * JAR as the query processor, as well as any (non-java / caGrid)
     * classes it depends on.  This class <i><b>MUST</b></i>
     * implement the abstract base class
     * <code>gov.nih.nci.cagrid.data.cql.ui.CQLQueryProcessorConfigUI</code>
     *  
     * @return
     *      The class name of the configuration user interface,
     *      or <code>null</code> if no UI is provided
     */
    public String getConfigurationUiClassname() {
        return null;
    }
}
