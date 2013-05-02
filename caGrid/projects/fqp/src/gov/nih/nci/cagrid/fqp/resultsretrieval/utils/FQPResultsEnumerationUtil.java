/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.fqp.resultsretrieval.utils;

import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.wsenum.utils.EnumConfigDiscoveryUtil;
import gov.nih.nci.cagrid.wsenum.utils.EnumIteratorFactory;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql.utilities.CQLConstants;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.CQLResult;
import org.cagrid.cql2.results.ExtendedCQLResult;
import org.globus.ws.enumeration.EnumIterator;

/**
 * FQPResultsEnumerationUtil
 * Sets up a WS-Enumeration retrieval of CQL 2 query results.
 * 
 * @author ervin
 */
public class FQPResultsEnumerationUtil {
    
    private static Log LOG = LogFactory.getLog(FQPResultsEnumerationUtil.class);
    
    private FQPResultsEnumerationUtil() {
        // prevent instantiation
    }
    

    public static EnumerationResponseContainer setUpEnumeration(CQLQueryResults cqlResults)
        throws FederatedQueryProcessingException {
        
        // placeholder for CQL Result type QName
        QName resultTypeQname = determineCqlResultQname(cqlResults);
        LOG.debug("Determined CQL query results to be of type " + resultTypeQname.toString());
        
        LOG.debug("Wrapping CQL Results with an Iterator interface");
        Iterator<? extends CQLResult> resultsIter = iterateResults(cqlResults);
                
        // create an EnumIterator instance
        LOG.debug("Creating EnumIterator instance");
        EnumIterator enumIter = null;
        try {
            // requires client-config.wsdd here for CQL 2 serialization
            InputStream configStream = 
                CQL2SerializationUtil.class.getResourceAsStream(
                    CQL2SerializationUtil.CLIENT_CONFIG_LOCATION);
            IterImplType iterImplementation = EnumConfigDiscoveryUtil.getConfiguredIterImplType();
            enumIter = EnumIteratorFactory.createIterator(
                iterImplementation, resultsIter, resultTypeQname, configStream);
            configStream.close();
        } catch (Exception ex) {
            throw new FederatedQueryProcessingException("Error creating EnumIterator instance: " + ex.getMessage(), ex);
        }
        
        // create the response... this will properly initialize the Enumeration resource
        LOG.debug("Preparing enumeration resource and response");
        EnumerationResponseContainer responseContainer = null;
        try {
            responseContainer = EnumerateResponseFactory.createEnumerationResponse(enumIter);
        } catch (Exception ex) {
            throw new FederatedQueryProcessingException("Error creating Enumeration resource: " + ex.getMessage(), ex);
        }
        
        return responseContainer;
    }
    
    
    private static QName determineCqlResultQname(CQLQueryResults results) {
        // default to object results
        QName name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(CQLObjectResult.class);
        if (results.getAggregationResult() != null) {
            name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(CQLAggregateResult.class);
        } else if (results.getAttributeResult() != null && results.getAttributeResult().length != 0) {
            name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(CQLAttributeResult.class);
        } else if (results.getExtendedResult() != null) {
            name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(ExtendedCQLResult.class);
        }
        return name;
    }
    
    
    private static Iterator<? extends CQLResult> iterateResults(CQLQueryResults results) {
        CQLResult[] items = null;
        if (results.getObjectResult() != null && results.getObjectResult().length != 0) {
            items = results.getObjectResult();
        } else if (results.getAttributeResult() != null && results.getAttributeResult().length != 0) {
            items = results.getAttributeResult();
        } else if (results.getAggregationResult() != null) {
            items = new CQLAggregateResult[] {results.getAggregationResult()};
        } else if (results.getExtendedResult() != null) {
            items = new ExtendedCQLResult[] {results.getExtendedResult()};
        } else {
            items = new CQLResult[0];
        }
        
        final CQLResult[] finalItems = items;
        
        Iterator<? extends CQLResult> iter = new Iterator<CQLResult>() {
            
            private int index = -1;

            public boolean hasNext() {
                return index + 1 < finalItems.length;
            }
            

            public CQLResult next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                index++;
                return finalItems[index];
            }
            

            public void remove() {
                throw new UnsupportedOperationException("remove() is not supported");
            }
        };
        return iter;
    }
}
