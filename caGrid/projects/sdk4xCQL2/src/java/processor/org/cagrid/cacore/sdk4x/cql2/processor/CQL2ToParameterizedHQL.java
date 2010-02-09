package org.cagrid.cacore.sdk4x.cql2.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.AssociationPopulationSpecification;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLObject;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.NamedAssociation;
import org.cagrid.cql2.UnaryPredicate;


/**
 * CQL2ToParameterizedHQL
 * Converter utility to turn CQL 2 into HQL using
 * positional parameters compatible with Hibernate 3.2.0ga.
 * Intended for use with caCORE SDK 4.x, but may be useful elsewhere.
 * 
 * @author David Ervin
 */
public class CQL2ToParameterizedHQL {
    // default alias for the target data type
    public static final String TARGET_ALIAS = "__TargetAlias__";

    private static Logger LOG = Logger.getLogger(CQL2ToParameterizedHQL.class);

    // maps a CQL 2 predicate to its HQL string representation
    private static Map<Object, String> predicateValues = null;
    static {
        predicateValues = new HashMap<Object, String>();
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
    
    private TypesInformationResolver typesInfoResolver = null;
    private boolean caseInsensitive = false;


    public CQL2ToParameterizedHQL(TypesInformationResolver typesInfoResolver, boolean caseInsensitive) {
        this.typesInfoResolver = typesInfoResolver;
        this.caseInsensitive = caseInsensitive;
    }


    /**
     * Converts CQL to parameterized HQL suitable for use with Hibernate
     * v3.2.0ga and the caCORE SDK version 4.x. This translation process 
     * <b>does not</b> include application of CQL 2 Query Modifiers;
     * this functionality should be implemented as a post-processing operation.
     * 
     * @param query
     *            The query to convert
     * @return A parameterized HQL Query representing the CQL query
     * @throws Exception
     */
    public ParameterizedHqlQuery convertToHql(CQLQuery query) throws QueryConversionException {
        // create a string builder to build up the HQL
        StringBuilder rawHql = new StringBuilder();

        // create the list in which parameters will be placed
        List<java.lang.Object> parameters = new LinkedList<java.lang.Object>();

        // determine if the target has subclasses
        boolean hasSubclasses;
        try {
            hasSubclasses = typesInfoResolver.classHasSubclasses(query.getCQLTargetObject().getClassName());
        } catch (TypesInformationException ex) {
            throw new QueryConversionException("Error determining if query target has subclasses: " + ex.getMessage(), ex);
        }
        LOG.debug(query.getCQLTargetObject().getClassName()
            + "has " + (hasSubclasses ? "" : "no ") + "subclasses");

        // begin processing at the target level
        processTarget(query.getCQLTargetObject(), query.getAssociationPopulationSpecification(),
            rawHql, parameters, hasSubclasses);
        
        // add any query modifiers
        if (query.getCQLQueryModifier() != null) {
            processQueryModifiers(rawHql, query.getCQLQueryModifier());
        } else {
            rawHql.insert(0, "Select distinct " + TARGET_ALIAS + " ");
        }

        // build the final query object
        ParameterizedHqlQuery hqlQuery = new ParameterizedHqlQuery(rawHql.toString(), parameters);
        return hqlQuery;
    }


    /**
     * Processes the target object of a CQL query
     * 
     * @param target
     *            The target of a CQL query
     * @param associtaionPopulation
     *            The (optional) association population specification
     *            information
     * @param hql
     *            The hql string builder to append to
     * @param parameters
     *            The list of positional parameter values
     * @param avoidSubclasses
     *            A flag to indicate the target has subclasses, which we should
     *            not return
     * @throws QueryProcessingException
     */
    private void processTarget(CQLTargetObject target, AssociationPopulationSpecification associtaionPopulation,
        StringBuilder hql, List<java.lang.Object> parameters, boolean avoidSubclasses) throws QueryConversionException {
        LOG.debug("Processing target " + target.getClassName());

        // the stack of associations processed at the current depth of the query
        Stack<CQLAssociatedObject> associationStack = new Stack<CQLAssociatedObject>();

        // start the query
        hql.append("From ").append(target.getClassName()).append(" as ").append(TARGET_ALIAS).append(' ');

        if (associtaionPopulation != null) {
            String fetchSpec = generateAssociationFetchClause(target.getClassName(), associtaionPopulation);
            hql.append(fetchSpec);
        }

        if (target.getCQLAssociatedObject() != null) {
            hql.append("where ");
            processAssociation(target.getCQLAssociatedObject(), hql, parameters, associationStack, target, TARGET_ALIAS);
        }
        if (target.getCQLAttribute() != null) {
            hql.append("where ");
            CQLAttribute attribute = target.getCQLAttribute();
            processAttribute(attribute, hql, parameters, target, TARGET_ALIAS);
        }
        if (target.getCQLGroup() != null) {
            hql.append("where ");
            processGroup(target.getCQLGroup(), hql, parameters, associationStack, target, TARGET_ALIAS);
        }

        if (avoidSubclasses) {
            boolean mustAddWhereClause = target.getCQLAssociatedObject() == null
                && target.getCQLAttribute() == null && target.getCQLGroup() == null;
            if (mustAddWhereClause) {
                hql.append(" where ");
            } else {
                hql.append(" and ");
            }
            hql.append(TARGET_ALIAS).append(".class = ?");
            
            // determine the class discriminator value
            String concreteClassName = target.getClassName();
            if (target.get_instanceof() != null) {
                concreteClassName = target.get_instanceof();
            }
            Object classDiscriminator;
            try {
                classDiscriminator = typesInfoResolver.getClassDiscriminatorValue(concreteClassName);
            } catch (TypesInformationException ex) {
                throw new QueryConversionException("Error determining class discriminator: " + ex.getMessage(), ex);
            }
            parameters.add(classDiscriminator);
        }
    }


    /**
     * Processes a CQL query attribute into HQL
     * 
     * @param attribute
     *            The CQL attribute
     * @param hql
     *            The HQL statement fragment
     * @param parameters
     *            The positional parameters list
     * @param associationTrace
     *            The trace of associations
     * @param objectClassName
     *            The class name of the object to which this association belongs
     * @throws QueryProcessingException
     */
    private void processAttribute(CQLAttribute attribute, StringBuilder hql, List<java.lang.Object> parameters,
        CQLObject queryObject, String queryObjectAlias) {
        LOG.debug("Processing attribute " + queryObject.getClassName() + "." + attribute.getName());

        // construct the attribute path
        String attributePath = queryObjectAlias + "." + attribute.getName();

        hql.append(' ');
        if (attribute.getUnaryPredicate() != null) {
            // unary predicates just get appended w/o values
            hql.append(attributePath).append(' ');
            // append the predicate
            String predicateAsString = predicateValues.get(attribute.getUnaryPredicate());
            hql.append(predicateAsString);
        } else {
            // binary predicates have a predicate string and a value
            // use lowercase for case insensitivity stuff
            if (caseInsensitive) {
                hql.append("lower(");
            }
            hql.append(attributePath);
            if (caseInsensitive) {
                hql.append(")");
            }
            
            String predicateAsString = predicateValues.get(attribute.getBinaryPredicate());
            AttributeValue rawValue = (attribute.getAttributeValue());
            boolean stringAttributeValue = rawValue.getStringValue() != null;

            hql.append(' ').append(predicateAsString).append(' ');

            if (caseInsensitive && stringAttributeValue) {
                hql.append("lower(");
            }
            // add a placeholder parameter to the HQL query
            hql.append('?');
            if (caseInsensitive && stringAttributeValue) {
                hql.append(')');
            }

            // add the parameter to the positional params list
            parameters.add(getAttributeValueObject(rawValue));
        }
    }


    /**
     * Processes CQL associations into HQL
     * 
     * @param association
     *            The CQL association
     * @param hql
     *            The HQL fragment which will be edited
     * @param parameters
     *            The positional HQL query parameters
     * @param associationTrace
     *            The trace of associations
     * @param sourceClassName
     *            The class name of the type to which this association belongs
     * @throws QueryProcessingException
     */
    private void processAssociation(CQLAssociatedObject association, StringBuilder hql,
        List<java.lang.Object> parameters, Stack<CQLAssociatedObject> associationStack, 
        CQLObject sourceQueryObject, String sourceAlias) throws QueryConversionException {
        LOG.debug("Processing association " + sourceQueryObject.getClassName() 
            + " to " + association.getClassName());

        // get the association's end name (role name)
        String roleName = association.getEndName();
        LOG.debug("Role name determined to be " + roleName);

        // determine the alias for this association
        String alias = getAssociationAlias(sourceQueryObject.getClassName(), 
            association.getClassName(), roleName);
        LOG.debug("Association alias determined to be " + alias);

        // add this association to the stack
        associationStack.push(association);

        // flag indicates the query is only verifying the association is populated
        boolean simpleNullCheck = true;
        if (association.getCQLAssociatedObject() != null) {
            simpleNullCheck = false;
            // add clause to select things from this association
            hql.append(sourceAlias).append('.').append(roleName);
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getClassName()).append(" as ")
                .append(alias).append(" where ");
            processAssociation(association.getCQLAssociatedObject(), hql, parameters,
                associationStack, association, alias);
            hql.append(") ");
        }
        if (association.getCQLAttribute() != null) {
            simpleNullCheck = false;
            CQLAttribute attrib = association.getCQLAttribute();
            hql.append(sourceAlias).append('.').append(roleName);
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getClassName()).append(" as ").append(alias).append(" where ");
            processAttribute(attrib, hql, parameters, association, alias);
            hql.append(")");
        }
        if (association.getCQLGroup() != null) {
            simpleNullCheck = false;
            hql.append(sourceAlias).append('.').append(roleName);
            hql.append(".id in (select ").append(alias).append(".id from ");
            hql.append(association.getClassName()).append(" as ")
                .append(alias).append(" where ");
            processGroup(association.getCQLGroup(), hql, parameters, 
                associationStack, association, alias);
            hql.append(") ");
        }

        if (simpleNullCheck) {
            // query is checking for the association to exist and be non-null
            hql.append(sourceAlias).append('.').append(roleName)
                .append(".id is not null ");
        }
        
        if (association.get_instanceof() != null) {
            hql.append("and ");
            if (simpleNullCheck) {
                hql.append(sourceAlias).append('.').append(roleName);
            } else {
                hql.append(alias);
            }
            hql.append(".class = ?");
            Object discriminator;
            try {
                discriminator = typesInfoResolver.getClassDiscriminatorValue(association.get_instanceof());
            } catch (TypesInformationException ex) {
                throw new QueryConversionException("Error determining class discriminator: " + ex.getMessage(), ex);
            }
            parameters.add(discriminator);
        }

        // pop this association off the stack
        associationStack.pop();
        LOG.debug(associationStack.size() + " associations remain on the stack");
    }


    /**
     * Processes a CQL group into HQL
     * 
     * @param group
     *            The CQL Group
     * @param hql
     *            The HQL fragment which will be edited
     * @param parameters
     *            The positional HQL query parameters
     * @param associationTrace
     *            The trace of associations
     * @param sourceClassName
     *            The class to which this group belongs
     * @throws QueryProcessingException
     */
    private void processGroup(CQLGroup group, StringBuilder hql, List<java.lang.Object> parameters,
        Stack<CQLAssociatedObject> associationStack, CQLObject sourceQueryObject, String sourceAlias) throws QueryConversionException {
        LOG.debug("Processing group on " + sourceQueryObject.getClassName());

        String logic = convertLogicalOperator(group.getLogicalOperation());
        boolean mustAddLogic = false;

        // open the group
        hql.append('(');

        if (group.getCQLAssociatedObject() != null) {
            for (int i = 0; i < group.getCQLAssociatedObject().length; i++) {
                mustAddLogic = true;
                processAssociation(group.getCQLAssociatedObject(i), hql, parameters, associationStack,
                    sourceQueryObject, sourceAlias);
                if (i + 1 < group.getCQLAssociatedObject().length) {
                    hql.append(' ').append(logic).append(' ');
                }
            }
        }
        if (group.getCQLAttribute() != null) {
            if (mustAddLogic) {
                hql.append(' ').append(logic).append(' ');
            }
            for (int i = 0; i < group.getCQLAttribute().length; i++) {
                mustAddLogic = true;
                processAttribute(group.getCQLAttribute(i), hql, parameters, 
                    sourceQueryObject, sourceAlias);
                if (i + 1 < group.getCQLAttribute().length) {
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
                    sourceQueryObject, sourceAlias);
                if (i + 1 < group.getCQLGroup().length) {
                    hql.append(' ').append(logic).append(' ');
                }
            }
        }

        // close the group
        hql.append(')');
    }


    /**
     * Converts a logical operator to its HQL string equiavalent.
     * 
     * @param op
     *            The logical operator to convert
     * @return The CQL logical operator as HQL
     */
    private String convertLogicalOperator(GroupLogicalOperator op) throws QueryConversionException {
        if (op.getValue().equals(GroupLogicalOperator._AND)) {
            return "AND";
        } else if (op.getValue().equals(GroupLogicalOperator._OR)) {
            return "OR";
        }
        throw new QueryConversionException("Logical operator '" + op.getValue() + "' is not recognized.");
    }


    private Object getAttributeValueObject(AttributeValue value) {
        if (value.getStringValue() != null) {
            return value.getStringValue();
        } else if (value.getBooleanValue() != null) {
            return value.getBooleanValue();
        } else if (value.getDateValue() != null) {
            return value.getDateValue();
        } else if (value.getIntegerValue() != null) {
            return value.getIntegerValue();
        } else if (value.getLongValue() != null) {
            return value.getLongValue();
        } else if (value.getTimeValue() != null) {
            return value.getTimeValue().getAsCalendar().getTime();
        }
        return null;
    }


    private String generateAssociationFetchClause(String targetClassName, AssociationPopulationSpecification spec) 
        throws QueryConversionException {
        StringBuffer clause = new StringBuffer();
        if (spec.getPopulationDepth() != null) {
            int maxDepth = spec.getPopulationDepth().getDepth();
            Set<String> joinedAssociations = new HashSet<String>();
            appendJoinsByDepth(clause, targetClassName, TARGET_ALIAS, 0, 0, maxDepth, joinedAssociations);
        } else {
            int aliasIndex = 0;
            NamedAssociation[] namedAssociations = spec.getNamedAssociationList().getNamedAssociation();
            for (NamedAssociation na : namedAssociations) {
                appendNamedJoins(clause, na, targetClassName, TARGET_ALIAS, aliasIndex);
            }
        }
        return clause.toString();
    }


    private void appendJoinsByDepth(StringBuffer buff, String parentClassName, String parentAlias, int aliasIndex,
        int currentDepth, int maxDepth, Set<String> joinedAssociations) throws QueryConversionException {
        LOG.debug("Populating associations of " + parentClassName + " to depth" + maxDepth 
            + "(currently at level " + currentDepth + ")");
        currentDepth++;
        if (currentDepth > maxDepth) {
            return;
        }
        List<ClassAssociation> associations;
        try {
            associations = typesInfoResolver.getAssociationsFromClass(parentClassName);
        } catch (TypesInformationException ex) {
            throw new QueryConversionException("Error determining associations: " + ex.getMessage(), ex);
        }
        for (ClassAssociation ca : associations) {
            String fetchName = ca.getClassName() + "." + ca.getEndName();
            if (!joinedAssociations.contains(fetchName)) {
                joinedAssociations.add(fetchName);
                String myAlias = "fetchAlias" + aliasIndex;
                aliasIndex++;
                buff.append("left join fetch ").append(parentAlias).append('.')
                    .append(ca.getEndName()).append(" as ").append(myAlias).append(' ');
                int newMaxDepth = maxDepth;
                int newCurrentDepth = currentDepth;
                appendJoinsByDepth(buff, ca.getClassName(), myAlias, aliasIndex, 
                    newCurrentDepth, newMaxDepth, joinedAssociations);
            }
        }
    }


    private void appendNamedJoins(StringBuffer buff, NamedAssociation na, 
        String parentClassName, String parentAlias, int aliasIndex) throws QueryConversionException {
        LOG.debug("Populating named associations");
        String myAlias = "fetchAlias" + aliasIndex;
        // get associations from the parent class, determine associated class goes with the named association's end name
        List<ClassAssociation> associations;
        try {
            associations = typesInfoResolver.getAssociationsFromClass(parentClassName);
        } catch (TypesInformationException ex) {
            throw new QueryConversionException("Error determining associations: " + ex.getMessage(), ex);
        }
        String associationClassName = null;
        for (ClassAssociation assoc : associations) {
            if (assoc.getEndName().equals(na.getEndName())) {
                associationClassName = assoc.getClassName();
                break;
            }
        }
        aliasIndex++;
        buff.append("left join fetch ").append(parentAlias).append('.').append(na.getEndName())
            .append(" as ").append(myAlias).append(' ');
        if (na.get_instanceof() != null) {
            buff.append("where ").append(myAlias).append(".class = ?");
            // FIXME: need to pass around parameters and append the class discriminator here
            Object classDiscriminator = null;
            try {
                classDiscriminator = typesInfoResolver.getClassDiscriminatorValue(na.get_instanceof());
            } catch (TypesInformationException ex) {
                throw new QueryConversionException("Error determining class discriminator: " + ex.getMessage(), ex);
            }
        }
        if (na.getNamedAssociationList() != null && na.getNamedAssociationList().getNamedAssociation() != null) {
            for (NamedAssociation subAssociation : na.getNamedAssociationList().getNamedAssociation()) {
                appendNamedJoins(buff, subAssociation, associationClassName, myAlias, aliasIndex);
            }
        } else if (na.getPopulationDepth() != null) {
            int depth = na.getPopulationDepth().getDepth();
            Set<String> joinedAssociations = new HashSet<String>();
            appendJoinsByDepth(buff, associationClassName, myAlias, aliasIndex, 0, depth, joinedAssociations);
        }
    }


    private String getAssociationAlias(String parentClassName, String associationClassName, String roleName) {
        int dotIndex = parentClassName.lastIndexOf('.');
        String parentShortName = dotIndex != -1 ? parentClassName.substring(dotIndex + 1) : parentClassName;
        dotIndex = associationClassName.lastIndexOf('.');
        String associationShortName = dotIndex != -1
            ? associationClassName.substring(dotIndex + 1)
            : associationClassName;
        String alias = "__" + parentShortName + "_" + associationShortName + "_" + roleName;
        return alias;
    }
    
    
    private void processQueryModifiers(StringBuilder hql, CQLQueryModifier mods) {
        StringBuilder modHql = new StringBuilder();
        if (mods.getCountOnly() != null && mods.getCountOnly().booleanValue()) {
            modHql.append("Select count(id)");
        } else if (mods.getDistinctAttribute() != null) {
            boolean reallyDistinct = false;
            modHql.append("Select ");
            Aggregation aggregation = mods.getDistinctAttribute().getAggregation();
            if (aggregation != null) {
                if (Aggregation.COUNT.equals(aggregation)) {
                    modHql.append("count(");
                    reallyDistinct = true;
                } else if (Aggregation.MAX.equals(aggregation)) {
                    modHql.append("max(");
                } else if (Aggregation.MIN.equals(aggregation)) {
                    modHql.append("min(");
                }
            }
            if (reallyDistinct) {
                modHql.append("distinct ");
            }
            modHql.append(mods.getDistinctAttribute().getAttributeName());
            if (aggregation != null) {
                modHql.append(')');
            }
        } else if (mods.getNamedAttribute() != null && mods.getNamedAttribute().length != 0) {
            /*
             * NOTE: this causes distinct TUPLES of attribute values to be returned from Hibernate.  
             * If you want attributes for each object, regardless of repeats, you have to ask for 
             * the ID attribute as well and post-process it out.
             */
            modHql.append("Select distinct "); 
            for (int i = 0; i < mods.getNamedAttribute().length; i++) {
                modHql.append(TARGET_ALIAS).append('.').append(mods.getNamedAttribute(i).getAttributeName());
                if (i + 1 < mods.getNamedAttribute().length) {
                    modHql.append(", ");
                }
            }
        }
        modHql.append(" ");
        hql.insert(0, modHql.toString());
    }
}
