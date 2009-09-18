package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLCountResult;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;

/** 
 *  CQLResultsCreationUtil
 *  Utility for creating CQL Query Results objects
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 16, 2006 
 * @version $Id$ 
 */
public class CQLResultsCreationUtil {
	
	/**
	 * Creates a CQL Query Results object containing object results
	 * 
	 * @param objects
	 * 		The objects to serialize and place in the object results
	 * @param targetName
	 * 		The name of the targeted class which produced these results
	 * @param classToQname
	 * 		A Mapping from class name to QName
	 * @return
	 * 		A CQLQueryResults instance with its object results populated
	 * 
	 * @throws ResultsCreationException
	 */
	public static CQLQueryResults createObjectResults(List objects, String targetName, Mappings classToQname) throws ResultsCreationException {
		CQLQueryResults results = new CQLQueryResults();
		results.setTargetClassname(targetName);
		QName targetQName = getQname(targetName, classToQname);
		CQLObjectResult[] objectResults = new CQLObjectResult[objects.size()];
		for (int i = 0; i < objects.size(); i++) {
			MessageElement elem = new MessageElement(targetQName, objects.get(i));
			objectResults[i] = new CQLObjectResult(new MessageElement[] {elem});
		}
		results.setObjectResult(objectResults);
		return results;
	}
	
	
	/**
	 * Creates a CQL Query Results instance containing attribute results
	 * 
	 * @param attribArrays
	 * 		A List of String[], which are the values of the attributes.
	 * 		These values must correspond both in number and in order of the
	 * 		attribute names
	 * @param targetClassname
	 * 		The name of the class targeted
	 * @param attribNames
	 * 		The names of the attributes queried for
	 * @return
	 * 		A CQLQueryResults instance with its attribute results populated
	 */
	public static CQLQueryResults createAttributeResults(List attribArrays, String targetClassname, String[] attribNames) {
		CQLQueryResults results = new CQLQueryResults();
		results.setTargetClassname(targetClassname);
		CQLAttributeResult[] attribResults = new CQLAttributeResult[attribArrays.size()];
		for (int i = 0; i < attribArrays.size(); i++) {
			TargetAttribute[] attribs = new TargetAttribute[attribNames.length];
			Object valueArray = attribArrays.get(i);
			String[] attribValues = new String[attribNames.length];
			if (valueArray == null) {
				Arrays.fill(attribValues, null);
			} else if (valueArray.getClass().isArray()) {
				for (int j = 0; j < Array.getLength(valueArray); j++) {
					Object singleValue = Array.get(valueArray, j); 
					attribValues[j] = singleValue == null ? null : singleValue.toString();
				}
			} else {
				attribValues = new String[] {valueArray == null ? null : valueArray.toString()};
			}
			
			for (int j = 0; j < attribNames.length; j++) {
				attribs[j] = new TargetAttribute(attribNames[j], attribValues[j]);
			}
			attribResults[i] = new CQLAttributeResult(attribs);
		}
		results.setAttributeResult(attribResults);
		return results;
	}
	
	
	/**
	 * Creates a CQL Query Results object containing a single count result
	 * 
	 * @param count
	 * 		The total count of all items
	 * @param targetClassname
	 * 		The classname of the query target
	 * @return
	 * 		A CQLQueryResults instance with count results populated
	 */
	public static CQLQueryResults createCountResults(long count, String targetClassname) {
		CQLQueryResults results = new CQLQueryResults();
		results.setTargetClassname(targetClassname);
		CQLCountResult countResult = new CQLCountResult();
		countResult.setCount(count);
		results.setCountResult(countResult);
		return results;
	}
	
	
	private static QName getQname(String className, Mappings classMappings) {
		for (int i = 0; classMappings.getMapping() != null && i < classMappings.getMapping().length; i++) {
			if (classMappings.getMapping(i).getClassName().equals(className)) {
				return QName.valueOf(classMappings.getMapping(i).getQname());
			}
		}
		return null;
	}
}
