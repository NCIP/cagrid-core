package gov.nih.nci.cagrid.data.cql2;

import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.CQLResult;

/**
 * CQL2 Query Processor base class
 * 
 * @author David
 */
public abstract class CQL2QueryProcessor {
    private Properties params;
    private InputStream wsddStream;
    private Mappings classMappings;

    public CQL2QueryProcessor() {
        super();
    }
    
    
    /**
     * Configure the query processor with the properties it requires as specified
     * in the Properties instance provided by getRequiredParameters(), and values
     * populated by the user's custom entries, if any.
     * 
     * Subclasses which need to do one-time initialization after configuration has completed
     * may override the <code>initialize()</code> method, which will be invoked
     * at the completion of <code>configure()</code>
     * 
     * @param parameters
     *      The parameters as configured by the user.  The set of keys must contain all
     *      of the keys contained in the Properties object returned 
     *      by <code>getRequiredParamters()</code>.  The values in the parameters will
     *      be either the user defined value or the default value from 
     *      <code>getRequiredParameters()</code>.
     * @param wsdd
     *      The input stream which contains the wsdd configuration for the data service.
     *      This stream may be important to locating type mappings for serializing and
     *      deserializing beans.
     * @param classToQnameMappings
     *      The mapping from classname to QName for serialization purposes.
     * @throws InitializationException
     */
    public void configure(Properties parameters, InputStream wsdd, Mappings classToQnameMappings) throws InitializationException {
        verifyProvidedParameters(parameters);
        this.params = parameters;
        this.wsddStream = wsdd;
        this.classMappings = classToQnameMappings;
        initialize();
    }
    
    
    /**
     * Verifies parameters provided from the configure method 
     * contain all required properties
     * 
     * @param parameters
     * @throws InitializationException
     */
    private void verifyProvidedParameters(Properties parameters) throws InitializationException {
        Set<String> required = new HashSet<String>();
        // add all the required parameters to a set
        Enumeration<Object> requiredKeys = getRequiredParameters().keys();
        while (requiredKeys.hasMoreElements()) {
            required.add((String) requiredKeys.nextElement());
        }
        // remove all the parameters provided
        Enumeration<Object> providedKeys = parameters.keys();
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
    }
    
    
    /**
     * Get a Properties object of parameters the query processor will require 
     * on initialization.  
     * 
     * Subclasses can override this method to return a map describing parameters
     * their implementation needs.
     * 
     * The keys are the names of parameters the query processor 
     * requires, the values are the defaults for those properties.  The default value
     * of a property may be an empty string if it is an optional parameter.
     * The keys MUST be valid java variable names.  They MUST NOT contain spaces 
     * or punctuation.  They may begin with an upper case character.
     * 
     * @return
     *      The required properties for the query processor with their default values
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
    public Set<String> getParametersFromEtc() {
        return new HashSet<String>();
    }
    
    
    /**
     * Get a collection of the supported CQL 2 Extension types
     * for the specified extension point
     * 
     * @param point
     * @return
     */
    public Collection<QName> getSupportedExtensions(Cql2ExtensionPoint point) {
        return new HashSet<QName>();
    }
    
    
    /**
     * @return
     *      The parameters as configured by the user at runtime.
     *      The set of keys must contain all of the keys contained in 
     *      the Properties object returned by <code>getRequiredParameters()</code>.  
     *      The values in the parameters will be either the user defined 
     *      value or the default value from <code>getRequiredParameters()</code>.
     */
    protected Properties getConfiguredParameters() {
        return this.params;
    }
    
    
    /**
     * @return
     *      The input stream which contains the wsdd configuration for the data service.
     *      This stream may be important to locating type mappings for serializing and
     *      deserializing beans.
     */
    protected InputStream getConfiguredWsddStream() {
        return this.wsddStream;
    }
    
    
    /**
     * Gets the class to QName mapping for the query processor.
     * This is derived from information in the service properties supplied
     * through JNDI at service runtime, or it may be overridden for testing.
     * 
     * @return
     * @throws Exception
     */
    protected Mappings getClassToQnameMappings() throws Exception {
        return this.classMappings;
    }
    
    
    /**
     * Perform any post-configuration initialization the query processor requires
     * When this method is called from the configure() method, the getConfiguredParameters()
     * and getConfiguredWsddStream() methods will return properly populated objects
     * 
     * @throws InitializationException
     */
    protected void initialize() throws InitializationException {
        // left empty for subclass implementation
    }
    
    
    /**
     * The primary query processing method.
     * 
     * @param query
     * @return
     * @throws QueryProcessingException
     *      Thrown when an error occurs while handling the query
     * @throws MalformedQueryException
     *      Thrown when the query is found to be defective
     */
    public abstract CQLQueryResults processQuery(CQLQuery query) throws QueryProcessingException, MalformedQueryException;
    
    
    /**
     * Returns an iterator over the CQL results.
     * Subclasses may optionally override this method to provide
     * a custom lazy implementation of an Iterator to the result set.
     * 
     * @param query
     * @return
     * @throws QueryProcessingException
     * @throws MalformedQueryException
     */
    public Iterator<CQLResult> processQueryAndIterate(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        CQLQueryResults results = processQuery(query);
        return new ResultsIterator(results);
    }
    
    
    /**
     * A simple iterator implementation over a CQL Query Results instance
     * which returns individual CQLResult instances on next()
     * 
     * @author David
     */
    private static class ResultsIterator implements Iterator<CQLResult> {
        
        private CQLResult[] results = null;
        private int index = -1;
        
        public ResultsIterator(CQLQueryResults queryResults) {
            if (queryResults.getAggregationResult() != null) {
                results = new CQLResult[] {queryResults.getAggregationResult()};
            } else if (queryResults.getAttributeResult() != null && queryResults.getAttributeResult().length != 0) {
                results = queryResults.getAttributeResult();
            } else if (queryResults.getObjectResult() != null && queryResults.getObjectResult().length != 0) {
                results = queryResults.getObjectResult();
            } else if (queryResults.getExtendedResult() != null) {
                results = new CQLResult[] {queryResults.getExtendedResult()};
            } else {
                results = new CQLResult[0];
            }
        }
        

        public boolean hasNext() {
            return index + 1 < results.length;
        }

        
        public CQLResult next() {
            if (hasNext()) {
                index++;
                return results[index];
            } else {
                throw new NoSuchElementException();
            }
        }
        

        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
        }
    }
}
