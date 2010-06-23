package org.cagrid.iso21090.sdkquery.translator.cql2;

import gov.nih.nci.iso21090.DSet;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLObject;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.iso21090.sdkquery.translator.ConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.CqlDataBucket;
import org.cagrid.iso21090.sdkquery.translator.DatatypeFlavor;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.cagrid.iso21090.sdkquery.translator.QueryTranslationException;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationException;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;


/** 
 *  CQL2ParameterizedHQL
 *  Converter utility to turn CQL into HQL using positional parameters 
 *  compatible with Hibernate 3.2.0ga
 *  
 *  Supports ISO 21090 data types in the caCORE SDK 4.3
 * 
 * @author David Ervin
 * 
 * @created Mar 2, 2007 10:26:47 AM
 * @version $Id: CQL2ParameterizedHQL.java,v 1.14 2009-04-24 14:53:56 dervin Exp $ 
 */
public class CQL2ToParameterizedHQL {
    public static final String TARGET_ALIAS = "__TargetAlias__";
    
    private static Log LOG = LogFactory.getLog(CQL2ToParameterizedHQL.class);
	
	// maps a CQL 2 predicate to its HQL string representation
    private static Map<java.lang.Object, String> predicateValues = null;
    static {
        predicateValues = new HashMap<java.lang.Object, String>();
        predicateValues.put(BinaryPredicate.EQUAL_TO, "=");
        predicateValues.put(BinaryPredicate.GREATER_THAN, ">");
        predicateValues.put(BinaryPredicate.GREATER_THAN_EQUAL_TO, ">=");
        predicateValues.put(BinaryPredicate.LESS_THAN, "<");
        predicateValues.put(BinaryPredicate.LESS_THAN_EQUAL_TO, "<=");
        predicateValues.put(BinaryPredicate.LIKE, "like");
        predicateValues.put(BinaryPredicate.NOT_EQUAL_TO, "!=");
        predicateValues.put(UnaryPredicate.IS_NOT_NULL, "is not null");
        predicateValues.put(UnaryPredicate.IS_NULL, "is null");
    }
    
    private TypesInformationResolver typesInformationResolver = null;
    private ConstantValueResolver constantValueResolver = null;
    private boolean caseInsensitive;
    
    
    public CQL2ToParameterizedHQL(TypesInformationResolver typesInfoResolver, 
        ConstantValueResolver constantValueResolver, boolean caseInsensitive) {
        this.typesInformationResolver = typesInfoResolver;
        this.constantValueResolver = constantValueResolver;
        this.caseInsensitive = caseInsensitive;
    }
    
	
	/**
	 * Converts CQL to parameterized HQL suitable for use with 
     * Hibernate v3.2.0ga and caCORE SDK 4.3 ISO data types
	 * 
	 * @param query
	 * 		The query to convert
	 * @return
	 * 		A parameterized HQL Query representing the CQL query
	 * @throws QueryTranslationException
	 */
	public ParameterizedHqlQuery convertToHql(CQLQuery query) throws QueryTranslationException {
	    LOG.debug("Converting caGrid Query Language to Hibernate Query Language");
		// create a string builder to build up the HQL
		StringBuilder rawHql = new StringBuilder();
        
        // create the list in which parameters will be placed
        List<java.lang.Object> parameters = new LinkedList<java.lang.Object>();
        
        // determine if the target has subclasses
        boolean hasSubclasses = false;
        try {
            hasSubclasses = typesInformationResolver.classHasSubclasses(query.getCQLTargetObject().getClassName());
        } catch (TypesInformationException ex) {
            throw new QueryTranslationException(ex.getMessage(), ex);
        }
        LOG.debug(query.getCQLTargetObject().getClassName() 
            + (hasSubclasses ? " has subclasses" : " has no subclasse"));
        
        // begin processing at the target level
		processTarget(query.getCQLTargetObject(), rawHql, parameters, hasSubclasses);
        
        // apply query modifiers
		if (query.getCQLQueryModifier() != null) {
			handleQueryModifier(query.getCQLQueryModifier(), rawHql);
		} else {
		    // select only unique objects
            rawHql.insert(0, "Select distinct (" + TARGET_ALIAS + ") ");      
        }
        
        // build the final query object
        ParameterizedHqlQuery hqlQuery = new ParameterizedHqlQuery(rawHql.toString(), parameters);
		return hqlQuery;
	}
	
	
	/**
	 * Applies query modifiers to the HQL query
	 * 
	 * @param mods
	 * 		The modifiers to apply
	 * @param hql
	 * 		The HQL to apply the modifications to
	 */
	private void handleQueryModifier(CQLQueryModifier mods, StringBuilder hql) {
		StringBuilder prepend = new StringBuilder();
		if (mods.getCountOnly() != null && mods.getCountOnly().booleanValue()) {
			prepend.append("select count(");
			if (mods.getDistinctAttribute() != null) {
				prepend.append("distinct ").append(mods.getDistinctAttribute());
			} else {
				prepend.append("distinct " + TARGET_ALIAS);
			}
			prepend.append(')');
		} else {
		    // select distinct tuples
			prepend.append("select distinct ");
			if (mods.getDistinctAttribute() != null) {
				prepend.append(mods.getDistinctAttribute());
			} else {
				for (int i = 0; i < mods.getNamedAttribute().length; i++) {
					prepend.append(mods.getNamedAttribute(i).getAttributeName());
					if (i + 1 < mods.getNamedAttribute().length) {
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
     * @param parameters
     *      The list of positional parameter values
	 * @param avoidSubclasses
	 * 		A flag to indicate the target has subclasses, which we should not return
	 * @throws QueryTranslationException
	 */
	private void processTarget(CQLObject target, StringBuilder hql, 
	    List<java.lang.Object> parameters, boolean avoidSubclasses) throws QueryTranslationException {
		LOG.debug("Processing target " + target.getClassName());
        
        // the stack of associations processed at the current depth of the query
		Stack<CQLAssociatedObject> associationStack = new Stack<CQLAssociatedObject>();
		List<CqlDataBucket> typesProcessingList = new ArrayList<CqlDataBucket>();
        
        // start the query
		hql.append("From ").append(target.getClassName()).append(" as ").append(TARGET_ALIAS).append(' ');
		// keep track of where we are in processing
		addTypeProcessingInformation(typesProcessingList, target.getClassName(), TARGET_ALIAS);
		
		if (target.getCQLAttribute() != null) {
            hql.append("where ");
            processAttribute(target.getCQLAttribute(), hql, parameters, target, TARGET_ALIAS, associationStack, typesProcessingList);
        }
		if (target.getCQLAssociatedObject() != null) {
			hql.append("where ");
			processAssociation(target.getCQLAssociatedObject(), hql, parameters, associationStack, 
			    typesProcessingList, target, TARGET_ALIAS);
		}
		if (target.getCQLGroup() != null) {
			hql.append("where ");
			processGroup(target.getCQLGroup(), hql, parameters, associationStack, typesProcessingList, target, TARGET_ALIAS);
		}
		
		if (avoidSubclasses) {
		    LOG.debug("Target class has subclasses, appending .class in where clause");
			boolean mustAddWhereClause = 
				target.getCQLAssociatedObject() == null
				&& target.getCQLAttribute() == null
				&& target.getCQLGroup() == null;
			if (mustAddWhereClause) {
				hql.append(" where ");
			} else {
				hql.append(" and ");
			}
			hql.append(TARGET_ALIAS).append(".class = ?");
			java.lang.Object classDiscriminatorInstance = null;
			try {
			    classDiscriminatorInstance = typesInformationResolver.getClassDiscriminatorValue(target.getClassName());
			    LOG.debug("Class discriminator determined to be " + String.valueOf(classDiscriminatorInstance));
			} catch (TypesInformationException ex) {
			    String message = "Error determining class discriminator for " + target.getClassName() + ": " + ex.getMessage();
			    LOG.error(message, ex);
			    throw new QueryTranslationException(message, ex);
			}
            parameters.add(classDiscriminatorInstance);
		}
	}
	
	
	/**
	 * Processes a CQL query attribute into HQL
	 * 
	 * @param attribute
	 * 		The CQL attribute
	 * @param hql
	 * 		The HQL statement fragment
     * @param parameters
     *      The positional parameters list
	 * @param associationTrace
	 * 		The trace of associations
	 * @param objectClassName
	 * 		The class name of the object to which this association belongs
	 * @throws QueryTranslationException
	 */
	private void processAttribute(CQLAttribute attribute, StringBuilder hql, 
        List<java.lang.Object> parameters, CQLObject queryObject, String queryObjectAlias, 
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList)
	    throws QueryTranslationException {
        LOG.debug("Processing attribute " + queryObject.getClassName() + "." + attribute.getName());
        
        // determine what the flavor of this attribute is
		DatatypeFlavor flavor = typesProcessingList.get(typesProcessingList.size() - 1).datatypeFlavor;
		LOG.debug("Datatype flavor is " + flavor.name());
		// DSET<Ii>, (and TEL and CD) ends up as "COMPLEX_WITH_SIMPLE_CONTENT" because it's modeled as an
		// association to DSET, then to Ii, which is that type.  Appears to work OK.
		// FIXME: DSET<Ad> doesn't work because I can't get the information about the part names inside the AD
		// out of the Hibernate configuration object API.  Interestingly, AD by itself is fine.
		switch (flavor) {
		    case STANDARD:
		    case ENUMERATION:
		        processStandardAttribute(attribute, hql, parameters, queryObject, queryObjectAlias);
		        break;
		    case COMPLEX_WITH_SIMPLE_CONTENT:
		        processComplexAttributeWithSimpleOrMixedContent(
		            attribute, hql, parameters, associationStack, typesProcessingList);
		        break;
		    case COMPLEX_WITH_MIXED_CONTENT:
		        processComplexAttributeWithSimpleOrMixedContent(
                    attribute, hql, parameters, associationStack, typesProcessingList);
		        break;
		    case COMPLEX_WITH_COLLECTION_OF_COMPLEX:
		        if (currentlyWrappedByDset(typesProcessingList)) {
		            processDsetOfComplexDatatypeWithCollectionOfComplexAttributesWithSimpleContent(
		                attribute, hql, parameters, associationStack, typesProcessingList);
		        } else {
		            processComplexAttributeWithCollectionOfComplexAttributesWithSimpleContent(
		                attribute, hql, parameters, associationStack, typesProcessingList);
		        }
		        break;
		    case COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT:
		        processComplexAttributeWithSimpleOrMixedContent(
                    attribute, hql, parameters, associationStack, typesProcessingList);
		        break;
		    case COLLECTION_OF_COMPLEX_WITH_COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT:
		        // gah
		        break;       
		}
	}
	
	
	private boolean currentlyWrappedByDset(List<CqlDataBucket> typesProcessingList) {
	    boolean wrappedByDset = false;
	    for (int i = typesProcessingList.size() - 1; i >= 0 && !wrappedByDset; i--) {
	        String name = typesProcessingList.get(i).clazz;
	        wrappedByDset = DSet.class.getName().equals(name);
	    }
	    return wrappedByDset;
	}
	
	
	/**
	 * Processes CQL associations into HQL
	 * 
	 * @param association
	 * 		The CQL association
	 * @param hql
	 * 		The HQL fragment which will be edited
     * @param parameters
     *      The positional HQL query parameters
	 * @param associationTrace
	 * 		The trace of associations
	 * @param sourceClassName
	 * 		The class name of the type to which this association belongs
	 * @throws QueryTranslationException
	 */
	private void processAssociation(CQLAssociatedObject association, StringBuilder hql, List<java.lang.Object> parameters, 
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList,
        CQLObject sourceQueryObject, String sourceAlias) throws QueryTranslationException {
        LOG.debug("Processing association " + sourceQueryObject.getClassName() + " to " + association.getClassName());
        
        // get the association's role name
		String roleName = association.getEndName();
		if (roleName == null) {
		    try {
		        roleName = typesInformationResolver.getRoleName(sourceQueryObject.getClassName(), association.getClassName());
		    } catch (TypesInformationException ex) {
		        throw new QueryTranslationException(ex.getMessage(), ex);
		    }
		}
		if (roleName == null) {
			// still null?? no association to the object!
		    throw new QueryTranslationException("No role name for an association between " + sourceQueryObject.getClassName() + 
		        " and " + association.getClassName() + " cound be determined.  Maybe the association doesn't exist?");
		}
        LOG.debug("Role name determined to be " + roleName);
        
        // determine the alias for this association
        String alias = getAssociationAlias(sourceQueryObject.getClassName(), association.getClassName(), roleName);
        LOG.debug("Association alias determined to be " + alias);
        
		// add this association to the stack
		associationStack.push(association);
		DatatypeFlavor flavor = null;
        try {
            flavor = DatatypeFlavor.getFlavorOfClass(Class.forName(stripGeneric(association.getClassName())));
        } catch (ClassNotFoundException ex) {
            throw new QueryTranslationException("Error determining datatype flavor of " 
                + association.getClassName() + ": " + ex.getMessage(), ex);
        }
		addTypeProcessingInformation(typesProcessingList, association.getClassName(), flavor == DatatypeFlavor.STANDARD ? alias : roleName);
        
		if (DatatypeFlavor.STANDARD.equals(flavor)) {
		    // flag indicates the query is only verifying the association is populated
		    boolean simpleNullCheck = true;
		    if (association.getCQLAssociatedObject() != null) {
                simpleNullCheck = false;
                hql.append(sourceAlias).append('.').append(roleName);
                hql.append(".id in (select ").append(alias).append(".id from ");
                hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
                processAttribute(association.getCQLAttribute(), hql, parameters, association, alias, associationStack, typesProcessingList);
                hql.append(") ");
            }
		    if (association.getCQLAssociatedObject() != null) {
		        simpleNullCheck = false;
		        // add clause to select things from this association
		        hql.append(sourceAlias).append('.').append(roleName);            
		        hql.append(".id in (select ").append(alias).append(".id from ");
		        hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
		        processAssociation(association.getCQLAssociatedObject(), hql, parameters, associationStack, typesProcessingList, association, alias);
		        hql.append(") ");
		    }
		    if (association.getCQLGroup() != null) {
		        simpleNullCheck = false;
		        hql.append(sourceAlias).append('.').append(roleName);            
		        hql.append(".id in (select ").append(alias).append(".id from ");
		        hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
		        processGroup(association.getCQLGroup(), hql, parameters, associationStack, typesProcessingList, association, alias);
		        hql.append(") ");
		    }
		    if (simpleNullCheck) {
		        // query is checking for the association to exist and be non-null
		        hql.append(sourceAlias).append('.').append(roleName).append(".id is not null ");
		    }
        } else {
            // complex datatype association (modeled as an attribute, so saying "Person.AD is not null" doesn't make sense...
            // "Person.AD.NullFlavor = NullFlavor.NI, however, is fine
            // FIXME: have to handle complex types here
            boolean simpleNullCheck = true;
            if (association.getCQLAssociatedObject() != null) {
                simpleNullCheck = false;
                // continue processing
                // TODO: does alias need to be role name?
                processAssociation(association.getCQLAssociatedObject(), hql, parameters, associationStack, typesProcessingList, association, alias);
            }
            if (association.getCQLGroup() != null) {
                simpleNullCheck = false;
                // continue processing
                // TODO: does alias need to be role name?
                processGroup(association.getCQLGroup(), hql, parameters, associationStack, typesProcessingList, association, alias);
            }
            if (association.getCQLAttribute() != null) {
                simpleNullCheck = false;
                // TODO: does sourceAlias need to be roleName??
                processAttribute(association.getCQLAttribute(), hql, parameters, sourceQueryObject, sourceAlias, associationStack, typesProcessingList);
            }
            if (simpleNullCheck) {
                // checking for the type not to be null, but .id doesn't work....
                // depending on the sequence of datatype flavors leading to this point 
                // we have to construct the HQL in different ways
                
                String path = getAssociationNavigationPath(typesProcessingList, 4);
                if (path.startsWith("join ")) {
                    // throw away the "where" part of the existing query
                    if (hql.toString().endsWith("where ")) {
                        removeLastWhereStatement(hql);
                    }
                }
                hql.append(path).append(" is not null");
            }
        }
        
		// pop this association off the stack
        associationStack.pop();
        clipTypeProcessingInformation(typesProcessingList);
        LOG.debug(associationStack.size() + " associations remain on the stack");
	}
	
	
	/**
	 * Processes a CQL group into HQL
	 * 
	 * @param group
	 * 		The CQL Group
	 * @param hql
	 * 		The HQL fragment which will be edited
     * @param parameters
     *      The positional HQL query parameters
	 * @param associationTrace
	 * 		The trace of associations
	 * @param sourceClassName
	 * 		The class to which this group belongs
	 * @throws QueryTranslationException
	 */
	private void processGroup(CQLGroup group, StringBuilder hql, List<java.lang.Object> parameters,
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList, 
        CQLObject sourceQueryObject, String sourceAlias) throws QueryTranslationException {
        LOG.debug("Processing group on " + sourceQueryObject.getClassName());
        
		String logic = convertLogicalOperator(group.getLogicalOperation());
		boolean mustAddLogic = false;
		
		// open the group
		hql.append('(');
		
		if (group.getCQLAttribute() != null) {
            for (int i = 0; i < group.getCQLAttribute().length; i++) {
                mustAddLogic = true;
                processAttribute(group.getCQLAttribute(i), hql, parameters, 
                    sourceQueryObject, sourceAlias, associationStack, typesProcessingList);
                if (i + 1 < group.getCQLAttribute().length) {
                    hql.append(' ').append(logic).append(' ');
                }
            }
        }
		if (group.getCQLAssociatedObject() != null) {
		    if (mustAddLogic) {
                hql.append(' ').append(logic).append(' ');
            }
			for (int i = 0; i < group.getCQLAssociatedObject().length; i++) {
				mustAddLogic = true;
				processAssociation(group.getCQLAssociatedObject(i), hql, parameters, 
				    associationStack, typesProcessingList, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getCQLAssociatedObject().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		if (group.getCQLGroup() != null) {
			if (mustAddLogic) {
				hql.append(' ').append(logic).append(' ');
			}
			for (int i = 0; i < group.getCQLGroup().length; i++) {
				processGroup(group.getCQLGroup(i), hql, parameters, associationStack, 
				    typesProcessingList, sourceQueryObject, sourceAlias);
				if (i + 1 < group.getCQLGroup().length) {
					hql.append(' ').append(logic).append(' ');
				}
			}
		}
		
		// close the group
		hql.append(')');
	}
	
	
	/**
	 * Converts a logical operator to its HQL string equivalent.
	 * 
	 * @param op
	 * 		The logical operator to convert
	 * @return
	 * 		The CQL logical operator as HQL
	 */
	private String convertLogicalOperator(GroupLogicalOperator op) throws QueryTranslationException {
	    if (GroupLogicalOperator.AND.equals(op)) {
			return "AND";
		} else if (GroupLogicalOperator.OR.equals(op)) {
			return "OR";
		}
		throw new QueryTranslationException("Logical operator '" + op.getValue() + "' is not recognized.");
	}
	
	
	private Object getAttributeValueObject(Class<?> fieldType, AttributeValue value) throws QueryTranslationException {
	    Object actualValue = null;
	    // handle a couple special cases
	    if (DatatypeFlavor.getFlavorOfClass(fieldType) == DatatypeFlavor.ENUMERATION) {
	        // 1) ISO 21090enumerations (NullFlavor, AddressPartType, etc)
	        String enumValue = value.getStringValue();
            LOG.debug("Field type is an Enumeration");
            try {
                Method factoryMethod = fieldType.getMethod("valueOf", String.class);
                actualValue = factoryMethod.invoke(null, enumValue);
            } catch (Exception ex) {
                throw new QueryTranslationException("Error converting " + enumValue 
                    + " to its enumeration value: " + ex.getMessage(), ex);
            }
	    } else if (Character.class.equals(fieldType)) {
	        // 2) Character
	        String stringVal = value.getStringValue();
	        if (stringVal.length() == 1) {
                actualValue = Character.valueOf(stringVal.charAt(0));
            } else {
                throw new QueryTranslationException("The string value \"" + value + "\" of length " 
                    + stringVal.length() + " is not a valid character (should be length 1)");
            }
	    } else if (URI.class.equals(fieldType)) {
	        // 3) URI
            actualValue = URI.create(value.getStringValue());
	    } else {
	        if (value.getStringValue() != null) {
	            actualValue = value.getStringValue();
	        } else if (value.getBooleanValue() != null) {
	            actualValue = value.getBooleanValue();
	        } else if (value.getDateValue() != null) {
	            actualValue = value.getDateValue();
	        } else if (value.getDoubleValue() != null) {
	            actualValue = value.getDoubleValue();
	        } else if (value.getIntegerValue() != null) {
	            actualValue = value.getIntegerValue();
	        } else if (value.getLongValue() != null) {
	            actualValue = value.getLongValue();
	        } else if (value.getTimeValue() != null) {
	            actualValue = value.getTimeValue().toString();
	        }
	    }
        return actualValue;
    }
    
    
    private String getAssociationAlias(String parentClassName, String associationClassName, String roleName) {
        int dotIndex = parentClassName.lastIndexOf('.');
        String parentShortName = dotIndex != -1 ? parentClassName.substring(dotIndex + 1) : parentClassName;
        parentShortName = compactGenericNameForAlias(parentShortName);
        dotIndex = associationClassName.lastIndexOf('.');
        String associationShortName = dotIndex != -1 ? associationClassName.substring(dotIndex + 1) : associationClassName;
        associationShortName = compactGenericNameForAlias(associationShortName);
        String alias = "__" + parentShortName + "_" + associationShortName + "_" + roleName;
        return alias;
    }
    
    
    private String compactGenericNameForAlias(String name) {
        name = name.replace("<", "_lt_");
        name = name.replace(">", "_gt_");
        return name;
    }
    
    
    private void addTypeProcessingInformation(List<CqlDataBucket> typesProcessingList, String className, String aliasOrRoleName) throws QueryTranslationException {
        DatatypeFlavor flavor = null;
        try {
            flavor = DatatypeFlavor.getFlavorOfClass(Class.forName(stripGeneric(className)));
        } catch (Exception ex) {
            throw new QueryTranslationException("Error determining datatype flavor of " + className + ": " + ex.getMessage(), ex);
        }
        CqlDataBucket bucket = new CqlDataBucket();
        bucket.clazz = className;
        bucket.aliasOrRoleName = aliasOrRoleName;
        bucket.datatypeFlavor = flavor;
        typesProcessingList.add(bucket);
    }
    
    
    private void clipTypeProcessingInformation(List<CqlDataBucket> typesProcessingList) {
        typesProcessingList.remove(typesProcessingList.size() - 1);
    }
    
    
    private void processStandardAttribute(CQLAttribute attribute, StringBuilder hql, 
        List<java.lang.Object> parameters, CQLObject queryObject, String queryObjectAlias)
        throws QueryTranslationException {
        LOG.debug("Processing standard attribute");
        
        // construct the query fragment
        if (caseInsensitive) {
            hql.append("lower(");
        }
        
        // append the path to the attribute itself
        hql.append(queryObjectAlias).append('.').append(attribute.getName());
        
        // close up case insensitivity
        if (caseInsensitive) {
            hql.append(')');
        }
        
        appendPredicateAndValue(attribute, hql, parameters, queryObject);
    }
    
    
    private void processComplexAttributeWithSimpleOrMixedContent(CQLAttribute attribute, 
        StringBuilder hql, List<java.lang.Object> parameters, 
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList) 
        throws QueryTranslationException {
        LOG.debug("Processing complex attribute with simple or mixed content");
        // determine the number of levels to back up to find a "standard" datatype
        int levels = determineLevelsRemovedFromStandardDatatype(typesProcessingList);
        
        // determine if this value is mapped to a constant
        java.lang.Object constantValue = constantValueResolver.getConstantValue(
            typesProcessingList.get(typesProcessingList.size() - levels).clazz, 
            getAttributePathList(typesProcessingList, attribute.getName(), levels - 1));
        if (constantValue == null) {
            LOG.debug("Attribute was not mapped to a constant");
            // append the path to the attribute itself
            if (caseInsensitive) {
                hql.append("lower(");
            }
            String path = getAttributePath(typesProcessingList, attribute.getName(), levels);
            if (path.startsWith("join ")) {
                // throw away the "where" part of the existing query
                if (hql.toString().endsWith("where ")) {
                    removeLastWhereStatement(hql);
                }
            }
            hql.append(path);
            if (caseInsensitive) {
                hql.append(')');
            }
        } else {
            // the value has been mapped to a constant.  Hibernate can't query
            // against these, but we can substitute the value into the query directly
            LOG.debug("Attribute mapped to a constant; appending that constant to the HQL parameters");
            hql.append('?');
            parameters.add(constantValue);
        }
        // get one level back's query object
        appendPredicateAndValue(attribute, hql, parameters, associationStack.peek());
    }
    
    
    private void processComplexAttributeWithCollectionOfComplexAttributesWithSimpleContent(
        CQLAttribute attribute, StringBuilder hql, List<java.lang.Object> parameters, 
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList) 
    throws QueryTranslationException {
        LOG.debug("Processing complex attribute collection of attributes with simple content");
        int levels = determineLevelsRemovedFromStandardDatatype(typesProcessingList);
        System.out.println("Levels: " + levels);
        if (levels == 2) {
            // we're probably processing an enum value of the most recent association class
            List<String> attribPath = getAttributePathList(typesProcessingList, attribute.getName(), 1);
            // determine if this value is mapped to a constant
            java.lang.Object constantValue = constantValueResolver.getConstantValue(
                typesProcessingList.get(typesProcessingList.size() - levels).clazz, 
                attribPath);
            if (constantValue == null) {
                LOG.debug("Attribute was not mapped to a constant");
                // append the path to the attribute itself
                if (caseInsensitive) {
                    hql.append("lower(");
                }
                String path = getAttributePath(typesProcessingList, attribute.getName(), levels);
                if (path.startsWith("join ")) {
                    // throw away the "where" part of the existing query
                    if (hql.toString().endsWith("where ")) {
                        removeLastWhereStatement(hql);
                    }
                }
                hql.append(path);
                if (caseInsensitive) {
                    hql.append(')');
                }
            } else {
                // the value has been mapped to a constant.  Hibernate can't query
                // against these, but we can substitute the value into the query directly
                LOG.debug("Attribute mapped to a constant; appending that constant to the HQL parameters");
                hql.append('?');
                parameters.add(constantValue);
            }
            // get one level back's query object
            appendPredicateAndValue(attribute, hql, parameters, associationStack.peek());
        } else {
            // some nested component path
            // build the query path with a place holder for the part names
            String componentNamePlaceholder = "---placeholder---";
            int listSize = typesProcessingList.size();
            int endIndex = listSize - 3;
            StringBuffer buf = new StringBuffer();
            for (int i = endIndex; i < listSize; i++) {
                if (i == listSize - 1) {
                    buf.append(componentNamePlaceholder);
                } else {
                    buf.append(typesProcessingList.get(i).aliasOrRoleName);
                }
                buf.append('.');
            }
            buf.append(attribute.getName());

            // get the part names out of the types information
            List<String> componentNames = typesInformationResolver.getInnerComponentNames(
                typesProcessingList.get(typesProcessingList.size() - 3).clazz, 
                typesProcessingList.get(typesProcessingList.size() - 2).aliasOrRoleName, 
                typesProcessingList.get(typesProcessingList.size() - 1).aliasOrRoleName);
            Iterator<String> nameIter = componentNames.iterator();

            // build the query fragment
            hql.append("(");
            while (nameIter.hasNext()) {
                if (caseInsensitive) {
                    hql.append("lower(");
                }
                String fragment = buf.toString().replace(componentNamePlaceholder, nameIter.next());
                hql.append(fragment);
                if (caseInsensitive) {
                    hql.append(")");
                }
                appendPredicateAndValue(attribute, hql, parameters, associationStack.peek());
                if (nameIter.hasNext()) {
                    hql.append(" or ");
                }
            }
            hql.append(")");
        }
    }
    
    
    private void processDsetOfComplexDatatypeWithCollectionOfComplexAttributesWithSimpleContent(
        CQLAttribute attribute, StringBuilder hql, List<java.lang.Object> parameters,
        Stack<CQLAssociatedObject> associationStack, List<CqlDataBucket> typesProcessingList) 
    throws QueryTranslationException {
        LOG.debug("Processing DSet of complex attribute with a collection of attributes with simple content");
        // strip the last where statement from the hql
        int whereStart = hql.lastIndexOf("where");
        hql.delete(whereStart, whereStart + "where".length());
        
        String topLevelAlias = typesProcessingList.get(typesProcessingList.size() - 4).aliasOrRoleName;
        hql.append("join ");
        hql.append(topLevelAlias).append(".");
        hql.append(typesProcessingList.get(typesProcessingList.size() - 3).aliasOrRoleName).append(".");
        hql.append(typesProcessingList.get(typesProcessingList.size() - 2).aliasOrRoleName).append(" ");
        // need a random alias
        String randAlias = "alias_" + System.currentTimeMillis();
        hql.append(randAlias).append(" where ");
        
        // get part names, "randalias.part_0.attributeName predicate value"
        // build the query path with a place holder for the part names
        String componentNamePlaceholder = "---placeholder---";
        StringBuffer buf = new StringBuffer();
        buf.append(randAlias).append(".");
        buf.append(componentNamePlaceholder).append(".");
        buf.append(attribute.getName());
        
        // get the part names out of the types information
        List<String> componentNames = typesInformationResolver.getNestedInnerComponentNames(
            typesProcessingList.get(typesProcessingList.size() - 4).clazz, 
            typesProcessingList.get(typesProcessingList.size() - 3).aliasOrRoleName, 
            typesProcessingList.get(typesProcessingList.size() - 2).aliasOrRoleName,
            typesProcessingList.get(typesProcessingList.size() - 1).aliasOrRoleName);
        Iterator<String> nameIter = componentNames.iterator();
        
        // build the query fragment
        hql.append("(");
        while (nameIter.hasNext()) {
            if (caseInsensitive) {
                hql.append("lower(");
            }
            String fragment = buf.toString().replace(componentNamePlaceholder, nameIter.next());
            hql.append(fragment);
            if (caseInsensitive) {
                hql.append(")");
            }
            appendPredicateAndValue(attribute, hql, parameters, associationStack.peek());
            if (nameIter.hasNext()) {
                hql.append(" or ");
            }
        }
        hql.append(")");
     }
    
    
    private void appendPredicateAndValue(CQLAttribute attribute, StringBuilder hql, 
        List<java.lang.Object> parameters, CQLObject queryObject) throws QueryTranslationException {
        LOG.debug("Appending predicate to HQL determining object value");
        
        // determine if the predicate is unary
        boolean unaryPredicate = attribute.getUnaryPredicate() != null;
        LOG.debug("Predicate is " + (unaryPredicate ? "not " : "") + "unary");
        // append the predicate
        hql.append(' ');
        String predicateAsString = null;
        if (unaryPredicate) {
            predicateAsString = predicateValues.get(attribute.getUnaryPredicate());
        } else {
            predicateAsString = predicateValues.get(attribute.getBinaryPredicate());
        }
        if (!unaryPredicate) {
            hql.append(predicateAsString).append(' ');

            // add a placeholder parameter to the HQL query
            hql.append('?');

            // convert the attribute value to the specific data type of the attribute
            Class<?> attributeType = null;
            try {
                attributeType = typesInformationResolver.getJavaDataType(stripGeneric(queryObject.getClassName()), attribute.getName());
            } catch (TypesInformationException ex) {
                LOG.error("Error determining type: " + ex.getMessage(), ex);
                throw new QueryTranslationException(ex.getMessage(), ex);
            }
            if (attributeType == null) {
                throw new QueryTranslationException("No type could be determined for attribute " 
                    + queryObject.getClassName() + "." + attribute.getName());
            }
            LOG.debug("Determined java type to be " + attribute.getName());
            java.lang.Object value = getAttributeValueObject(attributeType, attribute.getAttributeValue());

            // add a positional parameter value to the list            
            parameters.add(value);
        } else {
            // binary predicates just get appended w/o values associated with them
            hql.append(predicateAsString);
        }
    }
    
    
    private List<String> getAttributePathList(List<CqlDataBucket> typesProcessingList, String attribName, int levels) {
        List<String> path = new ArrayList<String>();
        int listSize = typesProcessingList.size();
        int endIndex = listSize - levels;
        for (int i = endIndex; i < listSize; i++) {
            path.add(typesProcessingList.get(i).aliasOrRoleName);
        }
        path.add(attribName);
        return path;
    }
    
    
    private String getAttributePath(List<CqlDataBucket> typesProcessingList, String attribName, int levels) {
        String path = getAssociationNavigationPath(typesProcessingList, levels);
        path += "." + attribName;
        return path;
    }
    
    
    private String getAssociationNavigationPath(List<CqlDataBucket> typesProcessing, int levels) {
        LOG.debug("Getting association navigation path");
        StringBuffer buf = new StringBuffer();
        int listSize = typesProcessing.size();
        int endIndex = listSize - levels;
        boolean useJoinSyntax = false;
        // figure out if I need to add the special join syntax
        if (typesProcessing.get(endIndex + 1).datatypeFlavor
                .equals(DatatypeFlavor.COLLECTION_OF_COMPLEX_WITH_COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT)
            || typesProcessing.get(endIndex + 1).datatypeFlavor
                .equals(DatatypeFlavor.COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT)) {
            useJoinSyntax = true;
        }
        if (useJoinSyntax) {
            LOG.debug("Association path requires a join");
            buf.append("join ");
            buf.append(typesProcessing.get(endIndex).aliasOrRoleName).append(".");
            buf.append(typesProcessing.get(endIndex + 1).aliasOrRoleName).append(".");
            buf.append(typesProcessing.get(endIndex + 2).aliasOrRoleName);
            // need a random alias here
            String randAlias = "alias_" + System.currentTimeMillis();
            buf.append(" as ").append(randAlias).append(" where ").append(randAlias);
            if (levels > 3) {
                buf.append(".");
                for (int i = endIndex + 3; i < listSize; i++) {
                    buf.append(typesProcessing.get(i).aliasOrRoleName);
                    if (i + 1 < listSize) {
                        buf.append('.');
                    }
                }
            }
        } else {
            LOG.debug("Association path doesn't need a join");
            // this is the easy case
            for (int i = endIndex; i < listSize; i++) {
                buf.append(typesProcessing.get(i).aliasOrRoleName);
                if (i + 1 < listSize) {
                    buf.append('.');
                }
            }
        }
        return buf.toString();
    }
    
    
    private int determineLevelsRemovedFromStandardDatatype(List<CqlDataBucket> typesProcessingList) {
        int count = 0;
        for (int i = typesProcessingList.size() - 1; i >= 0; i--) {
            count++;
            if (DatatypeFlavor.STANDARD.equals(typesProcessingList.get(i).datatypeFlavor)) {
                break;
            }
        }
        return count;
    }
    
    
    private String stripGeneric(String className) {
        String stripped = className;
        int index = className.indexOf("<");
        if (index != -1) {
            LOG.debug("Stripping generic portion off class name " + className);
            stripped = className.substring(0, index);
        }
        return stripped;
    }
    
    
    private void removeLastWhereStatement(StringBuilder hql) {
        LOG.debug("Removing last where statement from HQL");
        int index = hql.lastIndexOf("where ");
        hql.delete(index, index + "where ".length());
    }
}
