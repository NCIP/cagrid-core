package gov.nih.nci.cagrid.data.sdk32query.experimental.directcql;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.sdk32query.ClassAccessUtilities;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** 
 *  DirectCQLQueryProcessor
 *  An <i><b>experiemental</i></b> CQL implementation to query the application service with CQL
 *  instead of conversion to HQL.
 *  This implementation should <i><b>NOT</i></b> be used in a production environment as it is considered
 *  experiemntal at this point!
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Jan 22, 2007 
 * @version $Id: DirectCQLQueryProcessor.java,v 1.1 2007-03-08 20:21:41 dervin Exp $ 
 */
public class DirectCQLQueryProcessor extends CQLQueryProcessor {
	
	public static final String APPLICATION_SERVICE_URL = "appserviceUrl";

	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		ApplicationService service = ApplicationService.getRemoteInstance(
			getConfiguredParameters().getProperty(APPLICATION_SERVICE_URL));
		
		// caCORE doesn't support query modifiers, so I'm removing them for post processing
		QueryModifier queryMods = cqlQuery.getQueryModifier();
		cqlQuery.setQueryModifier(null);
		
		gov.nih.nci.system.query.cql.CQLQuery convertedQuery = null;
		try {
			// convert REAL cql to appservice's fake CQL classes
			convertedQuery = CQL2CoreCQL.convert(cqlQuery);
		} catch (Exception ex) {
			throw new QueryProcessingException("Error converting query format: " + ex.getMessage(), ex);
		}
		
		List resultList = null;
		try {
			// run the query
			resultList = service.query(convertedQuery, convertedQuery.getTarget().getName());
		} catch (Exception ex) {
			throw new QueryProcessingException("Error executing query: " + ex.getMessage(), ex);
		}
		
		CQLQueryResults results = null;
		
		// if query modifiers are present, apply them
		if (queryMods != null) {
			results = applyQueryModifiers(resultList, queryMods, cqlQuery.getTarget().getName());
		} else {
			// result list to object result set
			try {
				results = CQLResultsCreationUtil.createObjectResults(resultList,
					convertedQuery.getTarget().getName(), getClassToQnameMappings());
			} catch (Exception ex) {
				throw new QueryProcessingException("Error creating results: " + ex.getMessage(), ex);
			}			
		}
					
		return results;
	}
	
	
	public Properties getRequiredParameters() {
		Properties params = new Properties();
		params.setProperty(APPLICATION_SERVICE_URL, "");
		return params;
	}
	
	
	private Mappings getClassToQnameMappings() throws Exception {
		// get the mapping file name
		String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
		Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
		return mappings;
	}
	
	
	private CQLQueryResults applyQueryModifiers(List resultList, QueryModifier mods, String targetName) throws QueryProcessingException {
		CQLQueryResults results = null;
		if (mods.getDistinctAttribute() != null) {
			Set distincts = processDistinctAttribute(resultList, mods.getDistinctAttribute());
			if (mods.isCountOnly()) {
				return CQLResultsCreationUtil.createCountResults(distincts.size(), targetName);
			}
			// create arrays of single object length for attribute results
			List arrays = new ArrayList(distincts.size());
			Iterator distinctIter = distincts.iterator();
			while (distinctIter.hasNext()) {
				String[] simpleArray = new String[] {(String) distinctIter.next()};
				arrays.add(simpleArray);
			}
			// create and return attribute results
			return CQLResultsCreationUtil.createAttributeResults(arrays, targetName, 
				new String[] {mods.getDistinctAttribute()});
		} else if (mods.isCountOnly()) {
			// no need to process further
			return CQLResultsCreationUtil.createCountResults(resultList.size(), targetName);
		} else if (mods.getAttributeNames() != null) {
			List attribArrays = processMultipleAttributes(resultList, mods.getAttributeNames());
			return CQLResultsCreationUtil.createAttributeResults(attribArrays, targetName, mods.getAttributeNames());
		}
		
		return results;
	}
	
	
	private Set processDistinctAttribute(List resultList, String attribName) throws QueryProcessingException {
		Object[] emptyArray = new Object[] {};
		Set distincts = new HashSet();
		Iterator resultIter = resultList.iterator();
		boolean useGetter = true;
		Method getter = null;
		Field field = null;
		while (resultIter.hasNext()) {
			Object obj = resultIter.next();
			String value = null;
			// decide if the method or the field should be used
			if (useGetter && getter == null) {
				getter = ClassAccessUtilities.getNamedGetterMethod(obj.getClass(), attribName);
				useGetter = getter != null;
			}
			if (!useGetter && field == null) {
				field = ClassAccessUtilities.getNamedField(obj.getClass(), attribName);
			}
			
			if (useGetter) {
				System.out.println("Using getter method");
				try {
					Object rawValue = getter.invoke(obj, emptyArray); 
					value = rawValue != null ? rawValue.toString() : null;
					distincts.add(value);
				} catch (InvocationTargetException ex) {
					throw new QueryProcessingException("Error invoking getter for attribute " 
						+ obj.getClass().getName() + "." + attribName, ex);
				} catch (IllegalAccessException ex) {
					throw new QueryProcessingException("Error accessing getter for attribute "
						+ obj.getClass().getName() + "." + attribName, ex);
				}
			} else if (!useGetter && field != null) {
				System.out.println("Using field");
				try {
					Object rawValue = field.get(obj);
					value = rawValue != null ? rawValue.toString() : null;
					distincts.add(value);
				} catch (IllegalAccessException ex) {
					throw new QueryProcessingException("Error accessing field for attribute " 
						+ obj.getClass().getName() + "." + attribName);
				}
			} else {
				throw new QueryProcessingException("No getter or field found for attribute " 
					+ obj.getClass().getName() + "." + attribName);
			}
		}
		return distincts;
	}
	
	
	private List processMultipleAttributes(List resultList, String[] attribNames) throws QueryProcessingException {
		Object[] emptyArray = new Object[] {};
		List attribArrays = new ArrayList();		
		Map getters = new HashMap();
		Map fields = new HashMap();
		Iterator resultIter = resultList.iterator();
		while (resultIter.hasNext()) {
			Object obj = resultIter.next();
			String[] values = new String[attribNames.length];
			
			if (getters.size() == 0 && fields.size() == 0) {
				// accessors not yet found, populate them
				for (int i = 0; i < attribNames.length; i++) {
					Method getter = ClassAccessUtilities.getNamedGetterMethod(obj.getClass(), attribNames[i]);
					if (getter != null) {
						System.out.println("Getter found for " + attribNames[i]);
						getters.put(attribNames[i], getter);
					}
					Field field = ClassAccessUtilities.getNamedField(obj.getClass(), attribNames[i]);
					if (field != null) {
						System.out.println("Field found for " + attribNames[i]);
						fields.put(attribNames[i], field);
					}
				}
			}
			
			// walk through attribute names
			for (int i = 0; i < attribNames.length; i++) {
				// check for a getter method and field
				Method getter = (Method) getters.get(attribNames[i]);
				Field field = (Field) fields.get(attribNames[i]);
				if (getter != null) {
					System.out.println("Using getter for " + attribNames[i]);
					// use the getter
					try {
						Object rawValue = getter.invoke(obj, emptyArray);
						values[i] = rawValue != null ? rawValue.toString() : null;
					} catch (InvocationTargetException ex) {
						throw new QueryProcessingException("Error invoking getter for attribute " 
							+ obj.getClass().getName() + "." + attribNames[i], ex);
					} catch (IllegalAccessException ex) {
						throw new QueryProcessingException("Error accessing getter for attribute "
							+ obj.getClass().getName() + "." + attribNames[i], ex);
					}
				} else if (field != null) {
					System.out.println("Using field for " + attribNames[i]);
					try {
						Object rawValue = field.get(obj);
						values[i] = rawValue != null ? rawValue.toString() : null;
					} catch (IllegalAccessException ex) {
						throw new QueryProcessingException("Error accessing field for attribute " 
							+ obj.getClass().getName() + "." + attribNames[i]);
					}
				} else {
					// no accessor found
					throw new QueryProcessingException("No getter or field found for attribute " 
						+ obj.getClass().getName() + "." + attribNames[i]);
				}
			}
			
			// add the value array to the list of arrays (confused yet?)
			attribArrays.add(values);
		}
		return attribArrays;
	}
}
