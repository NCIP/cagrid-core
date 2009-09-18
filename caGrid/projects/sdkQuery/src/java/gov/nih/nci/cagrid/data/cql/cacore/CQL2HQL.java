package gov.nih.nci.cagrid.data.cql.cacore;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/** 
 *  CQL2HQL
 *  Translates a CQL query to Hibernate v3 HQL
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jul 19, 2006 
 * @version $Id$ 
 */
public class CQL2HQL {
	public static final String TARGET_ALIAS = "xxTargetAliasxx";
	
	private static Map predicateValues;

	/**
	 * Translates a CQL query into an HQL string.  This translation process assumes the
	 * CQL Query has passed validation.  Processing of invalid CQL may or may not procede
	 * with undefined results.
	 * 
	 * @param query
	 * 		The CQL Query to translate into HQL
	 * @param eliminateSubclasses
	 * 		A flag indicating that the query should be formulated to avoid
	 * 		returning subclass instances of the targeted class.
	 * @param caseInsensitive
	 * 		A flag indicating that the query should be made case insensitive
	 * 		by converting all values to lowercase.
	 * @return
	 * 		An HQL query
	 * @throws QueryProcessingException
	 */
	public static String translate(CQLQuery query, boolean eliminateSubclasses, boolean caseInsensitive) 
		throws QueryProcessingException {
		StringBuilder hql = new StringBuilder();
		if (query.getQueryModifier() != null) {
			if (eliminateSubclasses) {
				throw new QueryProcessingException("HQL cannot use the class property when processing projection queries.");
			}
			processModifiedQuery(hql, query.getQueryModifier(), query.getTarget(), caseInsensitive);
		} else {
			processTarget(hql, query.getTarget(), eliminateSubclasses, caseInsensitive);
		}
		return hql.toString();
	}
	
	
	/**
	 * Processes a query with Query Modifications applied to it
	 * 
	 * @param hql
	 * 		The HQL built thus far
	 * @param mods
	 * 		The modifications to the query
	 * @param target
	 * 		The target object
	 * @param caseInsensitive
	 * 		True if the query should be case insensitive
	 * @throws QueryProcessingException
	 */
	private static void processModifiedQuery(StringBuilder hql, QueryModifier mods, Object target, boolean caseInsensitive)
		throws QueryProcessingException {
		if (mods.isCountOnly()) {
			processCountingQuery(hql, mods, target, caseInsensitive);
		} else {
			processAttributeQuery(hql, mods, target, caseInsensitive);
		}
	}
	
	
	/**
	 * Processes a query which returns a count
	 * 
	 * @param hql
	 * 		The HQL query fragment
	 * @param mods
	 * 		The modifications to apply to the query
	 * @param target
	 * 		The query's target object
	 * @param caseInsensitive
	 * 		True if the query should be made case insensitive
	 * @throws QueryProcessingException
	 */
	private static void processCountingQuery(StringBuilder hql, QueryModifier mods, Object target, boolean caseInsensitive) 
		throws QueryProcessingException {
		hql.append("select count(");
		if (mods.getDistinctAttribute() != null) {
			// counting distinct attributes
			hql.append("distinct ").append(TARGET_ALIAS);
			hql.append(".").append(mods.getDistinctAttribute()).append(") ");
			processTarget(hql, target, false, caseInsensitive);
		} else if (mods.getAttributeNames() != null) {
			// counting objects where any one of the attribs is not null
			hql.append("*) ");
			// process the target object normally
			processTarget(hql, target, false, caseInsensitive);
			// only add a where statement if the target has no child restrictions
			boolean addWhereStatement = target.getAssociation() == null 
				&& target.getAttribute() == null && target.getGroup()== null;
			if (addWhereStatement) {
				hql.append(" where ");
			} else {
				hql.append(" and ");
			}
			// build the attribute not null clause
			StringBuilder attribClause = new StringBuilder();
			attribClause.append("(");
			for (int i = 0; i < mods.getAttributeNames().length; i++) {
				attribClause.append(TARGET_ALIAS).append(".");
				attribClause.append(mods.getAttributeNames(i));
				attribClause.append(" is not null");
				if (i + 1 < mods.getAttributeNames().length) {
					attribClause.append(" or ");
				}
			}
			attribClause.append(")");
			// append the attribute not null clause to the target
			hql.append(attribClause.toString());
		} else {
			// counting unique objects
			// need to use an alias in the count clause
			hql.append(TARGET_ALIAS).append(") ");
			processTarget(hql, target, false, caseInsensitive);
		}
	}
	
	
	/**
	 * Processes a query which returns attributes (distinct or otherwise)
	 * 
	 * @param hql
	 * 		The existing HQL fragment
	 * @param mods
	 * 		The modifications to apply to the query
	 * @param target
	 * 		The target object of the query
	 * @throws QueryProcessingException
	 */
	private static void processAttributeQuery(StringBuilder hql, QueryModifier mods, Object target, boolean caseInsensitive)
		throws QueryProcessingException {
		if (mods.getDistinctAttribute() != null) {
			// counting distinct attributes
			hql.append("select distinct ").append(TARGET_ALIAS).append(".").append(mods.getDistinctAttribute());
		} else {
			String[] names = mods.getAttributeNames();
			if (names != null) {
				hql.append("select ");
				for (int i = 0; i < names.length; i++) {
					hql.append(TARGET_ALIAS).append(".").append(names[i]);
					if (i + 1 < names.length) {
						hql.append(", ");
					}
				}
			}
		}
		hql.append(" ");
		processTarget(hql, target, false, caseInsensitive);
	}
	
	
	private static void processTarget(StringBuilder hql, Object target, boolean eliminateSubclasses, boolean caseInsensitive) 
		throws QueryProcessingException {
		String objName = target.getName();
		hql.append("From ").append(objName);
		hql.append(" as ").append(TARGET_ALIAS);
		if (eliminateSubclasses) {			
			hql.append(" where ").append(TARGET_ALIAS).append(".class = ").append(objName);
		}
		if (target.getAttribute() != null) {
			if (eliminateSubclasses) {
				hql.append(" and ");
			} else {
				hql.append(" where ");
			}
			processAttribute(hql, target.getName(), target.getAttribute(), true, caseInsensitive);
		}
		if (target.getAssociation() != null) {
			if (eliminateSubclasses) {
				hql.append(" and ");
			} else {
				hql.append(" where ");
			}
			processAssociation(hql, objName, target.getAssociation(), true, caseInsensitive);
		}
		if (target.getGroup() != null) {
			if (eliminateSubclasses) {
				hql.append(" and ");
			} else {
				hql.append(" where ");
			}
			processGroup(hql, objName, target.getGroup(), true, caseInsensitive);
		}
	}
	
	
	/**
	 * Processes an Object of a CQL Query.
	 * 
	 * @param hql
	 * 		The existing HQL query fragment
	 * @param obj
	 * 		The object to process into HQL
	 * @param caseInsensitive
	 * 		True if the query should be made case insensitive
	 * @throws QueryProcessingException
	 */
	private static void processObject(StringBuilder hql, Object obj, boolean caseInsensitive) throws QueryProcessingException {
		String objName = obj.getName();
		hql.append("select id From ").append(objName);
		if (obj.getAttribute() != null) {
			hql.append(" where ");
			processAttribute(hql, obj.getName(), obj.getAttribute(), false, caseInsensitive);
		}
		if (obj.getAssociation() != null) {
			hql.append(" where ");
			processAssociation(hql, objName, obj.getAssociation(), false, caseInsensitive);
		}
		if (obj.getGroup() != null) {
			hql.append(" where ");
			processGroup(hql, objName, obj.getGroup(), false, caseInsensitive);
		}
	}
	
	
	/**
	 * Proceses an Attribute of a CQL Query.
	 * 
	 * @param hql
	 * 		The existing HQL query fragment
	 * @param objClassName
	 * 		The class name of the object to which this attribute belongs
	 * @param attrib
	 * 		The attribute to process into HQL
	 * @param useAlias
	 * 		If true, the target alias will be used
	 * @param caseInsensitive
	 * 		If true, attribute values will be made lowercase
	 * @throws QueryProcessingException
	 */
	private static void processAttribute(StringBuilder hql, String objClassName,
		Attribute attrib, boolean useAlias, boolean caseInsensitive) throws QueryProcessingException {
		// get the field for the attribute
		Field attribField = null;
		try {
			Class objClass = Class.forName(objClassName);
			attribField = ClassAccessUtilities.getNamedField(objClass, attrib.getName());
		} catch (ClassNotFoundException ex) {
			throw new QueryProcessingException("Object class " + objClassName + "was not found", ex);
		}
		// data type flags
		int typeFlag = AttributeTypeDetector.determineType(attribField);
		if (typeFlag == AttributeTypeDetector.UNKNOWN_TYPE) {
			throw new QueryProcessingException("Unable to determine type of attribute " + attrib.getName() + " of class " + objClassName);
		}
		
		boolean useLowercase = caseInsensitive 
			&& typeFlag != AttributeTypeDetector.BOOLEAN_TYPE 
			&& typeFlag != AttributeTypeDetector.DATE_TYPE; 
		
		String fullAttribName = attrib.getName();
		if (useAlias) {
			fullAttribName = TARGET_ALIAS + "." + attrib.getName();
		}
		
		Predicate predicate = attrib.getPredicate();
		if (predicate == null) {
		    // default is EQUAL_TO, but Axis doesn't fill that in
		    predicate = Predicate.EQUAL_TO;
		}
		boolean nullCheck = predicate.equals(Predicate.IS_NULL) || predicate.equals(Predicate.IS_NOT_NULL);
				
		if (typeFlag == AttributeTypeDetector.DATE_TYPE && !nullCheck) {
			String dateQueryString = createDateQuery(fullAttribName, attrib.getValue().toString(), predicate);
			hql.append(dateQueryString);
		} else {
			String predicateString = convertPredicate(predicate);
			if (useLowercase) {
				hql.append("lower(");
			}
			hql.append(fullAttribName);
			if (useLowercase) {
				hql.append(")");
			}	

			hql.append(" ").append(predicateString).append(" ");
			// unary predicates
			if (!nullCheck) {
				// binary predicates
				if (caseInsensitive && typeFlag != AttributeTypeDetector.BOOLEAN_TYPE) {
					hql.append("lower(");
				}
				if (typeFlag != AttributeTypeDetector.BOOLEAN_TYPE) {
					hql.append("'");
				}
				hql.append(attrib.getValue());
				if (typeFlag != AttributeTypeDetector.BOOLEAN_TYPE) {
					hql.append("'");
				}
				if (caseInsensitive && typeFlag != AttributeTypeDetector.BOOLEAN_TYPE) {
					hql.append(")");
				}
			}
		}
	}
	
	
	/**
	 * Processes an Association of a CQL Query.
	 * 
	 * @param hql
	 * 		The existing HQL query fragment
	 * @param parentAlias
	 * 		The alias of the parent object
	 * @param parentName
	 * 		The class name of the parent object
	 * @param assoc
	 * 		The association to process into HQL
	 * @param caseInsensitive
	 * 		True if the query should be made case insensitive
	 * @throws QueryProcessingException
	 */
	private static void processAssociation(StringBuilder hql, String parentName, 
		Association assoc, boolean useAlias, boolean caseInsensitive) throws QueryProcessingException {
		// get the role name of the association
		String roleName = ClassAccessUtilities.getRoleName(parentName, assoc);
		if (roleName == null) {
			// still null?? no association to the object!
			throw new QueryProcessingException("Association from type " + parentName + 
				" to type " + assoc.getName() + " does not exist.  Use only direct associations");
		}
		// make an HQL subquery for the object
		if (useAlias) {
			hql.append(TARGET_ALIAS).append(".");
		}
		hql.append(roleName).append(".id in (");
		processObject(hql, assoc, caseInsensitive);
		hql.append(")");
	}
	
	
	/**
	 * Processes a Group of a CQL Query.
	 * 
	 * @param hql
	 * 		The existing HQL query fragment
	 * @param parentName
	 * 		The type name of the parent object
	 * @param group
	 * 		The group to process into HQL
	 * @param useAlias
	 * 		True if a target alias should be used
	 * @param caseInsensitive
	 * 		True if the query should be made case insensitive
	 * @throws QueryProcessingException
	 */
	private static void processGroup(StringBuilder hql, String parentName, Group group, 
		boolean useAlias, boolean caseInsensitive) throws QueryProcessingException {
		String logic = convertLogicalOperator(group.getLogicRelation());
		
		// flag indicating a logic clause is needed before adding further query parts
		boolean logicClauseNeeded = false;
		
		// attributes
		if (group.getAttribute() != null) {
			for (int i = 0; i < group.getAttribute().length; i++) {
				logicClauseNeeded = true;
				processAttribute(hql, parentName, group.getAttribute(i), useAlias, caseInsensitive);
				if (i + 1 < group.getAttribute().length) {
					hql.append(" ").append(logic).append(" ");
				}
			}
		}
		
		// associations
		if (group.getAssociation() != null) {
			if (logicClauseNeeded) {
				hql.append(" ").append(logic).append(" ");
			}
			for (int i = 0; i < group.getAssociation().length; i++) {
				logicClauseNeeded = true;
				processAssociation(hql, parentName, group.getAssociation(i), useAlias, caseInsensitive);
				if (i + 1 < group.getAssociation().length) {
					hql.append(" ").append(logic).append(" ");
				}
			}
		}
		
		// subgroups
		if (group.getGroup() != null) {
			if (logicClauseNeeded) {
				hql.append(" ").append(logic).append(" ");
			}
			for (int i = 0; i < group.getGroup().length; i++) {
				hql.append("( ");
				processGroup(hql, parentName, group.getGroup(i), useAlias, caseInsensitive);
				hql.append(" )");
				if (i + 1 < group.getGroup().length) {
					hql.append(" ").append(logic).append(" ");
				}
			}
		}
	}
	
	
	/**
	 * Converts a predicate to its HQL string equivalent.
	 * 
	 * @param p
	 * @return
	 * 		The CQL predicate as HQL 
	 */
	private static String convertPredicate(Predicate p) {
		if (predicateValues == null) {
			predicateValues = new HashMap();
			predicateValues.put(Predicate.IS_NULL, "is null");
			predicateValues.put(Predicate.IS_NOT_NULL, "is not null");
			predicateValues.put(Predicate.EQUAL_TO, "=");
			predicateValues.put(Predicate.GREATER_THAN, ">");
			predicateValues.put(Predicate.GREATER_THAN_EQUAL_TO, ">=");
			predicateValues.put(Predicate.LESS_THAN, "<");
			predicateValues.put(Predicate.LESS_THAN_EQUAL_TO, "<=");
			predicateValues.put(Predicate.LIKE, "LIKE");
			predicateValues.put(Predicate.NOT_EQUAL_TO, "!=");
		}
		return (String) predicateValues.get(p);
	}
	
	
	/**
	 * Converts a logical operator to its HQL string equiavalent.
	 * 
	 * @param op
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
	
	
	private static String createDateQuery(String fullAttributeName, String dateString, Predicate predicate) throws QueryProcessingException {
		Date dateValue = null;
		try {
			dateValue = java.text.DateFormat.getDateTimeInstance().parse(dateString);
		} catch (Exception ex) {
			throw new QueryProcessingException("Error parsing date " + dateString + ": " + ex.getMessage(), ex);
		}
		if (predicate.equals(Predicate.LIKE)) {
			throw new QueryProcessingException("Predicate " + Predicate.LIKE.toString() + " is not valid for Date attribute types!");
		}
		String highPredicate = null;
		String lowPredicate = null;
		if (predicate.equals(Predicate.GREATER_THAN)) {
			highPredicate = convertPredicate(Predicate.GREATER_THAN_EQUAL_TO);
			lowPredicate = convertPredicate(predicate);
		} else if (predicate.equals(Predicate.LESS_THAN)) {
			highPredicate = convertPredicate(Predicate.LESS_THAN_EQUAL_TO);
			lowPredicate = convertPredicate(predicate);
		} else {
			highPredicate = convertPredicate(predicate);
			lowPredicate = highPredicate;
		}
		StringBuilder dateQuery = new StringBuilder();
		// parse the date value into a Java Date object
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateValue);
		dateQuery.append("(");
		// break down each part of the date and query for it with the prefix
		dateQuery.append("year(").append(fullAttributeName).append(") ").append(highPredicate)
			.append(" '").append(cal.get(Calendar.YEAR)).append("' AND ");
		dateQuery.append("month(").append(fullAttributeName).append(") ").append(highPredicate)
			.append(" '").append(cal.get(Calendar.MONTH)).append("' AND ");
		dateQuery.append("day(").append(fullAttributeName).append(") ").append(lowPredicate)
			.append(" '").append(cal.get(Calendar.DAY_OF_MONTH)).append("'");
		dateQuery.append(")");
		return dateQuery.toString();
	}
}
