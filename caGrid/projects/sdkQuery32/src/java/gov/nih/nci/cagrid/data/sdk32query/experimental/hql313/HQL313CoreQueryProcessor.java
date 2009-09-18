package gov.nih.nci.cagrid.data.sdk32query.experimental.hql313;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.sdk32query.SubclassCheckCache;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/** 
 *  HQL313CoreQueryProcessor
 *  CQL query processor working against the caCORE SDK v3.2,
 *  using HQL for Hibernate v3.1.3
 * 
 * @author David Ervin
 * 
 * @created Mar 2, 2007 3:22:07 PM
 * @version $Id: HQL313CoreQueryProcessor.java,v 1.3 2007-04-23 19:44:54 dervin Exp $ 
 */
public class HQL313CoreQueryProcessor extends CQLQueryProcessor {
	public static final String APPSERVICE_URL = "applicationServiceUrl";
	public static final String CASE_INSENSITIVE_QUERIES = "caseInsensitive";
	public static final String DEFAULT_CASE_INSENSITIVE = String.valueOf(false);
    public static final String USE_LOCAL_API = "useLocalApi";
    public static final String USE_LOCAL_API_DEFAULT = String.valueOf(false);

	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		// get configuration parameters
		boolean caseInsensitive = Boolean.valueOf(
			getConfiguredParameters().getProperty(CASE_INSENSITIVE_QUERIES)).booleanValue();
		
		// init the application service
		ApplicationService service = getAppserviceInstance();
		
		// see if the target has subclasses
		boolean hasSubclasses = SubclassCheckCache.hasClassProperty(cqlQuery.getTarget().getName(), service);
		
		// create the HQL query
		String hqlQuery = CQL2HQL.convertToHql(cqlQuery, hasSubclasses, caseInsensitive);
		
		List rawResults = null;
		try {
			// run the query
			rawResults = service.query(new HQLCriteria(hqlQuery), cqlQuery.getTarget().getName());
		} catch (ApplicationException ex) {
			throw new QueryProcessingException("Error querying application service: " + ex.getMessage(), ex);
		}
		
		// convert the results as apporpriate
		CQLQueryResults results = null;
		if (cqlQuery.getQueryModifier() == null) {
			// object results
			try {
				results = CQLResultsCreationUtil.createObjectResults(
					rawResults, cqlQuery.getTarget().getName(), getClassToQnameMappings());
			} catch (Exception ex) {
				throw new QueryProcessingException("Error creating CQL Query Results: " + ex.getMessage(), ex);
			}
		} else {
			if (cqlQuery.getQueryModifier().isCountOnly()) {
				long value = Long.valueOf(rawResults.get(0).toString()).longValue();
				results = CQLResultsCreationUtil.createCountResults(value, cqlQuery.getTarget().getName());
			} else {
				if (cqlQuery.getQueryModifier().getDistinctAttribute() != null) {
					List attribArrays = new ArrayList();
					Iterator rawIter = rawResults.iterator();
					while (rawIter.hasNext()) {
						Object item = rawIter.next();
						String[] singleArray = new String[] {item != null ? item.toString() : null};
						attribArrays.add(singleArray);
					}
					results = CQLResultsCreationUtil.createAttributeResults(
						attribArrays, cqlQuery.getTarget().getName(), cqlQuery.getQueryModifier().getAttributeNames());
				} else {
					results = CQLResultsCreationUtil.createAttributeResults(
						rawResults, cqlQuery.getTarget().getName(), cqlQuery.getQueryModifier().getAttributeNames());
				}
			}
		}
		
		return results;
	}
	
	
	private Mappings getClassToQnameMappings() throws Exception {
		// get the mapping file name
		String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
		Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
		return mappings;
	}
    
    
    private ApplicationService getAppserviceInstance() {
        String useLocalValue = getConfiguredParameters().getProperty(USE_LOCAL_API);
        boolean useLocal = Boolean.valueOf(useLocalValue).booleanValue();
        if (useLocal) {
            return ApplicationServiceProvider.getLocalInstance();
        }
        String url = getConfiguredParameters().getProperty(APPSERVICE_URL);
        return ApplicationService.getRemoteInstance(url);
    }
	
	
	public Properties getRequiredParameters() {
		Properties props = new Properties();
		props.setProperty(APPSERVICE_URL, "");
		props.setProperty(CASE_INSENSITIVE_QUERIES, DEFAULT_CASE_INSENSITIVE);
        props.setProperty(USE_LOCAL_API, USE_LOCAL_API_DEFAULT);
		return props;
	}

}
