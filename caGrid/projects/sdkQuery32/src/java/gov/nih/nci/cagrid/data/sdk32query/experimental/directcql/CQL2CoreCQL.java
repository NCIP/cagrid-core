package gov.nih.nci.cagrid.data.sdk32query.experimental.directcql;

import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.system.query.cql.CQLAssociation;
import gov.nih.nci.system.query.cql.CQLAttribute;
import gov.nih.nci.system.query.cql.CQLGroup;
import gov.nih.nci.system.query.cql.CQLObject;
import gov.nih.nci.system.query.cql.CQLPredicate;
import gov.nih.nci.system.query.cql.CQLQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** 
 *  CQL2CoreCQL
 *  Utility to convert caGrid's authoritative CQL (schema based, registered, widely used, documented)
 *  to the caCORE version of CQL.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Jan 22, 2007 
 * @version $Id: CQL2CoreCQL.java,v 1.1 2007-03-08 20:21:41 dervin Exp $ 
 */
public class CQL2CoreCQL {
	
	private static Map predicateMap;

	/**
	 * Converts caGrid CQL to caCORE CQL.  Since the caCORE CQL is based on an outdated and unreleased
	 * version of the caGrid CQL schema, query modifiers are not directly supported and will cause
	 * a QueryProcessingException to be thrown.  QueryModifiers may be implemented with post-processing
	 * of the query result list.
	 * 
	 * @param query
	 * 		The query to convert
	 * @return
	 * 		The converted query
	 * @throws QueryProcessingException
	 */
	public static CQLQuery convert(gov.nih.nci.cagrid.cqlquery.CQLQuery query) throws QueryProcessingException {
		CQLQuery convertedQuery = new CQLQuery();
		convertedQuery.setTarget(convertObject(query.getTarget()));
		
		// modifiers would be really nice!
		if (query.getQueryModifier() != null) {
			throw new QueryProcessingException("caCORE's CQL does not support query modifiers");
		}
		return convertedQuery;
	}
	
	
	private static CQLObject convertObject(gov.nih.nci.cagrid.cqlquery.Object object) {
		CQLObject convertedObject = new CQLObject();
		convertedObject.setName(object.getName());
		if (object.getAssociation() != null) {
			convertedObject.setAssociation(convertAssociation(object.getAssociation()));
		} else if (object.getAttribute() != null) {
			convertedObject.setAttribute(convertAttribute(object.getAttribute()));
		} else if (object.getGroup() != null) {
			convertedObject.setGroup(convertGroup(object.getGroup()));
		}
		return convertedObject;
	}
	
	
	private static CQLAttribute convertAttribute(gov.nih.nci.cagrid.cqlquery.Attribute attribute) {
		CQLAttribute convertedAttribute = new CQLAttribute();
		convertedAttribute.setName(attribute.getName());
		convertedAttribute.setPredicate(convertPredicate(attribute.getPredicate() != null ? attribute.getPredicate() : Predicate.EQUAL_TO));
		convertedAttribute.setValue(attribute.getValue().toString());
		return convertedAttribute;
	}
	
	
	/**
	 * Converts caGrid CQL predicates to caCORE CQL predicates.  This implementation will instantiate
	 * a static hash map on the first call in to it, which allows subsequent calls to be performed
	 * in constant time (at most log(9) operations to find a value) instead of linear time. 
	 * @param predicate
	 * @return
	 * 		The converted CQL predicate
	 */
	private static CQLPredicate convertPredicate(gov.nih.nci.cagrid.cqlquery.Predicate predicate) {
		if (predicateMap == null) {
			predicateMap = new HashMap();
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._EQUAL_TO, CQLPredicate.EQUAL_TO);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._GREATER_THAN, CQLPredicate.GREATER_THAN);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._GREATER_THAN_EQUAL_TO, CQLPredicate.GREATER_THAN_EQUAL_TO);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._IS_NOT_NULL, CQLPredicate.IS_NOT_NULL);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._IS_NULL, CQLPredicate.IS_NULL);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._LESS_THAN, CQLPredicate.LESS_THAN);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate.LESS_THAN_EQUAL_TO, CQLPredicate.LESS_THAN_EQUAL_TO);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._LIKE, CQLPredicate.LIKE);
			predicateMap.put(gov.nih.nci.cagrid.cqlquery.Predicate._NOT_EQUAL_TO, CQLPredicate.NOT_EQUAL_TO);
		}
		return (CQLPredicate) predicateMap.get(predicate.getValue());
	}
	
	
	private static CQLAssociation convertAssociation(gov.nih.nci.cagrid.cqlquery.Association association) {
		CQLAssociation convertedAssociation = new CQLAssociation();
		convertedAssociation.setName(association.getName());
		convertedAssociation.setSourceRoleName(association.getRoleName());
		// target role name?
		
		if (association.getAssociation() != null) {
			convertedAssociation.setAssociation(convertAssociation(association.getAssociation()));
		} else if (association.getAttribute() != null) {
			convertedAssociation.setAttribute(convertAttribute(association.getAttribute()));
		} else if (association.getGroup() != null) {
			convertedAssociation.setGroup(convertGroup(association.getGroup()));
		}
		return convertedAssociation;
	}
	
	
	private static CQLGroup convertGroup(gov.nih.nci.cagrid.cqlquery.Group group) {
		CQLGroup convertedGroup = new CQLGroup();
		if (group.getAssociation() != null) {
			CQLAssociation[] convertedAssociations = new CQLAssociation[group.getAssociation().length];
			for (int i = 0; i < group.getAssociation().length; i++) {
				convertedAssociations[i] = convertAssociation(group.getAssociation(i));
			}
			convertedGroup.setAssociationCollection(Arrays.asList(convertedAssociations));
		}
		if (group.getAttribute() != null) {
			CQLAttribute[] convertedAttributes = new CQLAttribute[group.getAttribute().length];
			for (int i = 0; i < group.getAttribute().length; i++) {
				convertedAttributes[i] = convertAttribute(group.getAttribute(i));
			}
			convertedGroup.setAttributeCollection(Arrays.asList(convertedAttributes));
		}
		if (group.getGroup() != null) {
			CQLGroup[] convertedGroups = new CQLGroup[group.getGroup().length];
			for (int i = 0; i < group.getGroup().length; i++) {
				convertedGroups[i] = convertGroup(group.getGroup(i));
			}
			convertedGroup.setGroupCollection(Arrays.asList(convertedGroups));
		}
		return convertedGroup;
	}
}
