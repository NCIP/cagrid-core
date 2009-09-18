package gov.nih.nci.cagrid.data.sdk32query.experimental.hql313;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.sdk32query.BooleanAttributeCheckCache;
import gov.nih.nci.cagrid.data.sdk32query.ClassAccessUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** 
 *  CQL2HQL
 *  Converter utility to turn CQL into HQL compatible with
 *  Hibernate 3.1.3 for use with caCORE SDK 3.2
 * 
 * @author David Ervin
 * 
 * @created Mar 2, 2007 10:26:47 AM
 * @version $Id: CQL2HQL.java,v 1.3 2008-02-05 21:33:09 dervin Exp $ 
 */
public class CQL2HQL {
	
	private static Map predicateValues = null;
	
	/**
	 * Converts CQL to HQL suitable for use with Hibernate v3.1.3 
	 * and the caCORE SDK version 3.2
	 * 
	 * @param query
	 * 		The query to convert
	 * @param avoidSubclasses
	 * 		A flag to indicate the target has subclasses, which we should not return
	 * @param caseInsensitive
	 * 		A flag to indicate the query should be performed without regard to case
	 * @return
	 * 		The result of converting CQL to HQL
	 * @throws QueryProcessingException
	 */
	public static String convertToHql(CQLQuery query, 
		boolean avoidSubclasses, boolean caseInsensitive) throws QueryProcessingException {
		// create a string builder to build up the HQL
		StringBuilder hql = new StringBuilder();
		processTarget(query.getTarget(), hql, avoidSubclasses, caseInsensitive);
		if (query.getQueryModifier() != null) {
			handleQueryModifier(query.getQueryModifier(), hql);
		}
		return hql.toString();
	}
	
	
	/**
	 * Applies query modifiers to the HQL query
	 * 
	 * @param mods
	 * 		The modifiers to apply
	 * @param hql
	 * 		The HQL to apply the modifications to
	 */
	private static void handleQueryModifier(QueryModifier mods, StringBuilder hql) {
		StringBuilder prepend = new StringBuilder();
		if (mods.isCountOnly()) {
			prepend.append("select count(");
			if (mods.getDistinctAttribute() != null) {
				prepend.append("distinct ").append(mods.getDistinctAttribute());
			} else {
				prepend.append('*');
			}
			prepend.append(')');
		} else {
			prepend.append("select ");
			if (mods.getDistinctAttribute() != null) {
				prepend.append("distinct ").append(mods.getDistinctAttribute());
			} else {
				for (int i = 0; i < mods.getAttributeNames().length; i++) {
					prepend.append(mods.getAttributeNames(i));
					if (i + 1 < mods.getAttributeNames().length) {
						prepend.append(", ");
					}
				}
			}
		}
		
		prepend.append(' ');
		
		hql.insert(0, prepend.toString());
	}
	
	
	/**
	 * Processes the target object of a CQL query
	 * 
	 * @param target
	 * 		The target of a CQL query
	 * @param hql
	 * 		The hql string builder to append to
	 * @param avoidSubclasses
	 * 		A flag to indicate the target has subclasses, which we should not return
	 * @param caseInsensitive
	 * 		A flag to indicate the query should be performed without regard to case
	 * @throws QueryProcessingException
	 */
	private static void processTarget(Object target, StringBuilder hql,
		boolean avoidSubclasses, boolean caseInsensitive) throws QueryProcessingException {
		
		List associationTrace = new LinkedList();
		
		hql.append("From ").append(target.getName()).append(' ');
		
		if (target.getAssociation() != null) {
			hql.append("where ");
			processAssociation(target.getAssociation(), hql, associationTrace, target.getName(), caseInsensitive);
		}
		if (target.getAttribute() != null) {
			hql.append("where ");
			processAttribute(target.getAttribute(), hql, associationTrace, target.getName(), caseInsensitive);
		}
		if (target.getGroup() != null) {
			hql.append("where ");
			processGroup(target.getGroup(), hql, associationTrace, target.getName(), caseInsensitive);
		}
		
		if (avoidSubclasses) {
			boolean mustAddWhereClause = 
				target.getAssociation() == null
				&& target.getAttribute() == null
				&& target.getGroup() == null;
			if (mustAddWhereClause) {
				hql.append(" where ");
			} else {
				hql.append(" and ");
			}
			hql.append("class = ").append(target.getName());
		}
	}
	
	
	/**
	 * Processes a CQL query attribute into HQL
	 * 
	 * @param attribute
	 * 		The CQL attribute
	 * @param hql
	 * 		The HQL statement fragment
	 * @param associationTrace
	 * 		The trace of associations
	 * @param objectClassName
	 * 		The class name of the object to which this association belongs
	 * @param caseInsensitive
	 * 		A flag indicating that queries should be performed without regard to case
	 * @throws QueryProcessingException
	 */
	private static void processAttribute(Attribute attribute, StringBuilder hql, 
		List associationTrace, String objectClassName, boolean caseInsensitive) throws QueryProcessingException {
	    Predicate predicate = attribute.getPredicate();
	    if (predicate == null) {
	        predicate = Predicate.EQUAL_TO;
	    }
		boolean isBoolAttribute = BooleanAttributeCheckCache.isFieldBoolean(objectClassName, attribute.getName());
		boolean unaryPredicate = predicate.equals(Predicate.IS_NOT_NULL)
			|| predicate.equals(Predicate.IS_NULL);
		
		String trace = associationTrace.size() != 0 ? buildAssociationTrace(associationTrace) : null;
		
		if (caseInsensitive && !isBoolAttribute) {
			hql.append("lower(");
		}
		if (trace != null) {
			hql.append(trace).append('.');
		}
		hql.append(attribute.getName());
		if (caseInsensitive && !isBoolAttribute) {
			hql.append(')');
		}
		hql.append(' ');
		String predicateAsString = convertPredicate(predicate);
		if (!unaryPredicate) {
			hql.append(predicateAsString).append(' ');
			if (caseInsensitive && !isBoolAttribute) {
				hql.append("lower(");
			}
			if (!isBoolAttribute) {
				hql.append('\'');
			}
			hql.append(attribute.getValue());
			if (!isBoolAttribute) {
				hql.append('\'');
			}
			if (caseInsensitive && !isBoolAttribute) {
				hql.append(')');
			}
		} else {
			hql.append(predicateAsString);
		}
	}
	
	
	/**
	 * Processes CQL associations into HQL
	 * 
	 * @param association
	 * 		The CQL association
	 * @param hql
	 * 		The HQL fragment which will be edited
	 * @param associationTrace
	 * 		The trace of associations
	 * @param originClassName
	 * 		The class name of the type to which this association belongs
	 * @param caseInsensitive
	 * 		A flag indicating that queries should be performed without regard to case
	 * @throws QueryProcessingException
	 */
	private static void processAssociation(Association association, StringBuilder hql, List associationTrace,
		String originClassName, boolean caseInsensitive) throws QueryProcessingException {
		String roleName = ClassAccessUtilities.getRoleName(originClassName, association);
		if (roleName == null) {
			// still null?? no association to the object!
			throw new QueryProcessingException("Association from type " + originClassName + 
				" to type " + association.getName() + " does not exist.  Use only direct associations");
		}
		// add the role name to the association trace
		associationTrace.add(roleName);
		
		if (association.getAssociation() != null) {
			processAssociation(association.getAssociation(), hql, associationTrace, association.getName(), caseInsensitive);
		}
		if (association.getAttribute() != null) {
			processAttribute(association.getAttribute(), hql, associationTrace, association.getName(), caseInsensitive);
		}
		if (association.getGroup() != null) {
			processGroup(association.getGroup(), hql, associationTrace, association.getName(), caseInsensitive);
		}
		
		// remove this association from the trace
		associationTrace.remove(associationTrace.size() - 1);
	}
	
	
	/**
	 * Processes a CQL group into HQL
	 * 
	 * @param group
	 * 		The CQL Group
	 * @param hql
	 * 		The HQL fragment which will be edited
	 * @param associationTrace
	 * 		The trace of associations
	 * @param originClass
	 * 		The class to which this group belongs
	 * @param caseInsensitive
	 * 		A flag indicating queries should be performed without regard to case
	 * @throws QueryProcessingException
	 */
	private static void processGroup(Group group, StringBuilder hql, List associationTrace, 
		String originClass, boolean caseInsensitive) throws QueryProcessingException {
		String logic = convertLogicalOperator(group.getLogicRelation());
		boolean mustAddLogic = false;
		
		// open the group
		hql.append('(');
		
		if (group.getAssociation() != null) {
			for (int i = 0; i < group.getAssociation().length; i++) {
				mustAddLogic = true;
				processAssociation(group.getAssociation(i), hql, associationTrace, originClass, caseInsensitive);
				if (i + 1 < group.getAssociation().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getAttribute() != null) {
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getAttribute().length; i++) {
				mustAddLogic = true;
				processAttribute(group.getAttribute(i), hql, associationTrace, originClass, caseInsensitive);
				if (i + 1 < group.getAttribute().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getGroup() != null) {
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getGroup().length; i++) {
				processGroup(group.getGroup(i), hql, associationTrace, originClass, caseInsensitive);
				if (i + 1 < group.getGroup().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		
		// close the group
		hql.append(')');
	}
	
	
	/**
	 * Converts a predicate to its HQL string equivalent.
	 * 
	 * @param p
	 * 		The CQL predicate to convert
	 * @return
	 * 		The CQL predicate as HQL 
	 */
	private static String convertPredicate(Predicate p) {
		if (predicateValues == null) {
			predicateValues = new HashMap();
			predicateValues.put(Predicate.EQUAL_TO, "=");
			predicateValues.put(Predicate.GREATER_THAN, ">");
			predicateValues.put(Predicate.GREATER_THAN_EQUAL_TO, ">=");
			predicateValues.put(Predicate.LESS_THAN, "<");
			predicateValues.put(Predicate.LESS_THAN_EQUAL_TO, "<=");
			predicateValues.put(Predicate.LIKE, "LIKE");
			predicateValues.put(Predicate.NOT_EQUAL_TO, "!=");
			predicateValues.put(Predicate.IS_NOT_NULL, "is not null");
			predicateValues.put(Predicate.IS_NULL, "is null");
		}
		return (String) predicateValues.get(p);
	}
	
	
	/**
	 * Converts a logical operator to its HQL string equiavalent.
	 * 
	 * @param op
	 * 		The logical operator to convert
	 * @return
	 * 		The CQL logical operator as HQL
	 */
	private static String convertLogicalOperator(LogicalOperator op) throws QueryProcessingException {
		if (op.getValue().equals(LogicalOperator._AND)) {
			return "AND";
		} else if (op.getValue().equals(LogicalOperator._OR)) {
			return "OR";
		}
		throw new QueryProcessingException("Logical operator '" + op.getValue() + "' is not recognized.");
	}
	
	
	/**
	 * Builds a trace of association names from a list of those names
	 * 
	 * @param associationTrace
	 * @return
	 * 		An HQL fragment
	 */
	private static String buildAssociationTrace(List associationTrace) {
		// build up what the trace to this association looks like
		StringBuilder trace = new StringBuilder();
		Iterator traceIter = associationTrace.iterator();
		while (traceIter.hasNext()) {
			trace.append(traceIter.next());
			if (traceIter.hasNext()) {
				trace.append('.');
			}
		}
		return trace.toString();
	}
}
