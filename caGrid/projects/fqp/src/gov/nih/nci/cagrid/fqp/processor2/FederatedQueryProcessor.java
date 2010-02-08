package gov.nih.nci.cagrid.fqp.processor2;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.DistinctAttribute;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;
import org.cagrid.data.dcql.DCQLAssociatedObject;
import org.cagrid.data.dcql.DCQLGroup;
import org.cagrid.data.dcql.DCQLObject;
import org.cagrid.data.dcql.DataTransformation;
import org.cagrid.data.dcql.ForeignAssociatedObject;
import org.cagrid.data.dcql.JoinCondition;
import org.globus.gsi.GlobusCredential;


/**
 * FederatedQueryProcessor 
 * Decomposes DCQL 2 into individual CQL 2 queries.
 * Each rewritten CQL 2 query is then executed by 
 * the specified grid data service in the DCQL 2
 * Foreign Associated Object by the Cql2QueryExecutor
 * 
 * @author David Ervin
 * @author Srini Akkala
 * @author Scott Oster
 */
class FederatedQueryProcessor {
    protected static Log LOG = LogFactory.getLog(FederatedQueryProcessor.class.getName());

    protected GlobusCredential cred;


    public FederatedQueryProcessor() {
    }


    public FederatedQueryProcessor(GlobusCredential cred) {
        this.cred = cred;
    }


    /**
     * Begin processing a DCQL 2 query from the target object
     * 
     * @param targetObject
     *      The target object in the DCQL 2 query
     * @return The CQL query required to process the target
     * @throws FederatedQueryProcessingException
     */
    public CQLQuery processDCQLQuery(DCQLObject targetObject) throws FederatedQueryProcessingException {
        CQLQuery cqlQuery = new CQLQuery();

        // Create a new CQL 2 target object.
        // All the nested queries by Foreign Associated Object will
        // be resolved and eventually attached to this CQL object.
        CQLTargetObject cqlObject = new CQLTargetObject();
        cqlObject.setClassName(targetObject.getName());
        // FIXME: turn this on when we fix the DCQL language
        // cqlObject.set_instanceof(targetObject.get_instanceof());

        // process the DCQL 2 object, building up the CQL 2 target
        populateObjectFromDCQLObject(targetObject, cqlObject);
        // this CQL Object is our target
        cqlQuery.setCQLTargetObject(cqlObject);

        return cqlQuery;
    }


    /**
     * Recursively processes the given DCQLObject,
     * building up the CQLObject
     *  
     * @param dcqlObject
     *      The DCQL 2 object to be rewritten as CQL 2
     * @param cqlObject
     *      The CQL 2 object to be built up from the DCQL 2
     * @throws FederatedQueryProcessingException
     */

    private void populateObjectFromDCQLObject(DCQLObject dcqlObject, CQLObject cqlObject)
        throws FederatedQueryProcessingException {
        boolean foundChild = false;
        // attributes can be passed through directly
        if (dcqlObject.getAttribute() != null) {
            LOG.debug("Found an attribute in DCQL 2, passing through to CQL 2");
            cqlObject.setCQLAttribute(dcqlObject.getAttribute());
            foundChild = true;
        }

        // handle group
        if (dcqlObject.getGroup() != null) {
            LOG.debug("Found a group in DCQL 2, processing into CQL 2");
            if (foundChild) {
                throw new FederatedQueryProcessingException("Error in DCQL 2 query: multiple children of a DCQL object found!");
            }
            // convert group and attach group to CQL object
            CQLGroup cqlGroup = processGroup(dcqlObject.getGroup());
            cqlObject.setCQLGroup(cqlGroup);
            foundChild = true;
        }

        // handle association
        if (dcqlObject.getAssociatedObject() != null) {
            LOG.debug("Found association in DCQL 2, processing into CQL 2");
            if (foundChild) {
                throw new FederatedQueryProcessingException("Error in DCQL 2 query: multiple children of a DCQL object found!");
            }
            // Convert into CQLAssociatedObject
            CQLAssociatedObject cqlAssociation = processAssociation(dcqlObject.getAssociatedObject());
            cqlObject.setCQLAssociatedObject(cqlAssociation);
            foundChild = true;
        }

        // handle foreign associated object
        if (dcqlObject.getForeignAssociatedObject() != null) {
            LOG.debug("Found foreign associated object in DCQL 2, processing into CQL 2 attribute group");
            if (foundChild) {
                throw new FederatedQueryProcessingException("Error in DCQL 2 query: multiple children of a DCQL object found!");
            }
            CQLGroup foreignAttributeGroup = processForeignAssociation(
                dcqlObject.getForeignAssociatedObject());
            cqlObject.setCQLGroup(foreignAttributeGroup);
        }
    }


    /**
     * Process the DCQL 2 Group, and build a CQL 2 Group.
     * DCQL 2 Groups are processed the same as DCQL 2 Objects are,
     * except Groups can contain multiple children,
     * so an array of each must be processed.
     * 
     * @param dcqlGroup
     * @return The CQL group
     * @throws FederatedQueryProcessingException
     */
    private CQLGroup processGroup(DCQLGroup dcqlGroup) throws FederatedQueryProcessingException {
        // convert basic group information and attach group to CQL 2 object
        CQLGroup cqlGroup = new CQLGroup();
        
        // attach logical relationship
        cqlGroup.setLogicalOperation(dcqlGroup.getLogicalOperation());

        // attributes can be passed through
        if (dcqlGroup.getAttribute() != null) {
            LOG.debug("Found attributes in DCQL 2 group, passing through to CQL 2");
            cqlGroup.setCQLAttribute(dcqlGroup.getAttribute());
        }

        // associations
        if (dcqlGroup.getAssociatedObject() != null && dcqlGroup.getAssociatedObject().length > 0) {
            LOG.debug("Found associations in DCQL 2 group, processing into CQL 2");
            DCQLAssociatedObject dcqlAssociationArray[] = dcqlGroup.getAssociatedObject();
            CQLAssociatedObject[] cqlAssociationArray = new CQLAssociatedObject[dcqlAssociationArray.length];
            for (int i = 0; i < dcqlAssociationArray.length; i++) {
                cqlAssociationArray[i] = processAssociation(dcqlAssociationArray[i]);
            }
            cqlGroup.setCQLAssociatedObject(cqlAssociationArray);
        }

        // groups
        if (dcqlGroup.getGroup() != null && dcqlGroup.getGroup().length > 0) {
            LOG.debug("Found a group(s) in DCQL 2 group, processing into CQL 2");
            DCQLGroup dcqlGroupArray[] = dcqlGroup.getGroup();
            CQLGroup[] cqlGroupArray = new CQLGroup[dcqlGroupArray.length];
            for (int i = 0; i < dcqlGroupArray.length; i++) {
                CQLGroup cqlNestedGroup = processGroup(dcqlGroupArray[i]);
                cqlGroupArray[i] = cqlNestedGroup;
            }
            cqlGroup.setCQLGroup(cqlGroupArray);
        }

        // foreign associations
        if (dcqlGroup.getForeignAssociatedObject() != null && dcqlGroup.getForeignAssociatedObject().length > 0) {
            LOG.debug("Found foreign associated object(s) in DCQL 2 group, processing into CQL 2 attribute group");
            ForeignAssociatedObject[] foreignAssociationArray = dcqlGroup.getForeignAssociatedObject();
            CQLGroup[] cqlGroupArray = new CQLGroup[foreignAssociationArray.length];
            for (int i = 0; i < foreignAssociationArray.length; i++) {
                // need to attach the results as criteria ...
                CQLGroup resultedGroup = processForeignAssociation(foreignAssociationArray[i]);
                cqlGroupArray[i] = resultedGroup;
            }
            // merge in these groups with any that already exist
            // from group processing above
            cqlGroup.setCQLGroup((CQLGroup[]) Utils.concatenateArrays(
                gov.nih.nci.cagrid.cqlquery.Group.class, cqlGroup.getCQLGroup(), cqlGroupArray));
        }

        return cqlGroup;
    }


    /**
     * Process DCQL 2 Association into a CQL 2 association
     * 
     * @param dcqlAssociation
     * @return The CQL Association
     * @throws QueryExecutionException
     */
    private CQLAssociatedObject processAssociation(DCQLAssociatedObject dcqlAssociation)
        throws FederatedQueryProcessingException {

        // create a new CQL 2 Association from the DCQL 2 Association
        CQLAssociatedObject cqlAssociation = new CQLAssociatedObject();
        cqlAssociation.setEndName(dcqlAssociation.getEndName());
        cqlAssociation.setClassName(dcqlAssociation.getName());

        // process the association's Object
        populateObjectFromDCQLObject(dcqlAssociation, cqlAssociation);

        return cqlAssociation;
    }


    /**
     * Process DCQL 2 foreign associated object.  This is essentially a sub-query
     * that can be executed by a grid data service defined in the foreign associated 
     * object definition.  Since the ForeignAssociatedObject extends from 
     * DCQLAssociatedObject and DCQLObject, the Object is processed and
     * the generated CQL 2 Query is executed by the Cql2QueryExecutor.
     * 
     * @param foreignAssociation
     * @return The CQL 2 Group of attributes resulting from processing the foreign associated object
     * @throws FederatedQueryProcessingException
     */
    private CQLGroup processForeignAssociation(ForeignAssociatedObject foreignAssociation)
        throws FederatedQueryProcessingException {
        // get foreign associated object
        DCQLAssociatedObject dcqlAssociatedObject = foreignAssociation.getAssociatedObject();

        // make a new query with the CQL Target Object created by processing the
        // foreign associated object
        CQLQuery cqlQuery = new CQLQuery();
        CQLTargetObject cqlTargetObject = new CQLTargetObject();
        cqlTargetObject.setClassName(dcqlAssociatedObject.getName());
        // FIXME: cqlTargetObject.set_instanceof(dcqlAssociatedObject.get_instanceof());
        populateObjectFromDCQLObject(dcqlAssociatedObject, cqlTargetObject);
        cqlQuery.setCQLTargetObject(cqlTargetObject);

        // build up a query result modifier to only return distinct values of
        // the attribute we need
        String foreignAttributeName = foreignAssociation.getJoinCondition().getForeignAttributeName();
        CQLQueryModifier queryModifier = new CQLQueryModifier();
        DistinctAttribute distinctAttribute = new DistinctAttribute();
        distinctAttribute.setAttributeName(foreignAttributeName);
        queryModifier.setDistinctAttribute(distinctAttribute);
        cqlQuery.setCQLQueryModifier(queryModifier);

        // execute the subquery for the foreign object
        String targetServiceURL = foreignAssociation.getTargetServiceURL();
        CQLQueryResults cqlResults = Cql2QueryExecutor.queryDataService(cqlQuery, targetServiceURL, this.cred);

        // process the resulting values
        List<String> remoteAttributeValues = new ArrayList<String>();
        if (cqlResults != null && cqlResults.getAttributeResult() != null) {
            CQLAttributeResult[] attributeResult = cqlResults.getAttributeResult();
            for (int i = 0; i < attributeResult.length; i++) {
                CQLAttributeResult attResult = attributeResult[i];
                TargetAttribute[] attribute = attResult.getAttribute();
                // make sure there is a valid result of only the specific
                // attribute we asked for
                if (attribute == null || attribute.length != 1 || !attribute[0].getName().equals(foreignAttributeName)) {
                    throw new RemoteDataServiceException("Data Service (" + targetServiceURL
                        + ") returned an invalid attribute result.");
                }
                remoteAttributeValues.add(attribute[0].getValue());
            }
            // process the array
        } else {
            // make sure there are NO RESULTS (of other types), and raise an
            // error if there are
            if (hasResults(cqlResults)) {
                throw new RemoteDataServiceException("Data Service (" + targetServiceURL
                    + ") returned invalid results when queried for Attributes.");
            }
        }

        CQLGroup criteriaGroup = buildGroup(
            foreignAssociation.getJoinCondition(), foreignAssociation.getDataTransformation(), remoteAttributeValues);
        return criteriaGroup;
    }


    /**
     * Builds a group of CQLAttributes from a list of values.
     * The attribute name is determined from the Join Criteria of 
     * a ForeignAssociatedObject.  Every attribute predicate is "EQUAL_TO"
     * and the group logical operation is "OR". If the list of values is empty or null,
     * a group that can never evaluate to true is created using 
     * IS_NULL and IS_NOT_NULL together.
     * 
     * @param values
     * @return A CQL Group of attributes
     * @throws FederatedQueryProcessingException
     */
    public static CQLGroup buildGroup(JoinCondition joinCondition, DataTransformation transformation, List<String> values)
        throws FederatedQueryProcessingException {
        CQLGroup cqlGroup = new CQLGroup();
        String localAttributeName = joinCondition.getLocalAttributeName();
        
        // if the predicate is something other than EQUAL_TO or NOT_EQUAL_TO, throw away any null values
        BinaryPredicate joinPredicate = joinCondition.getPredicate();
        if (!BinaryPredicate.EQUAL_TO.equals(joinPredicate) && !BinaryPredicate.NOT_EQUAL_TO.equals(joinPredicate)) {
            Iterator<String> valueIter = values.iterator();
            while (valueIter.hasNext()) {
                if (valueIter.next() == null) {
                    valueIter.remove();
                }
            }
        }

        CQLAttribute[] attributes = null;
        // handle the case of no values
        if (values == null || values.size() == 0) {
            cqlGroup.setLogicalOperation(GroupLogicalOperator.AND);
            attributes = new CQLAttribute[2];
            attributes[0] = new CQLAttribute();
            attributes[0].setName(localAttributeName);
            attributes[0].setUnaryPredicate(UnaryPredicate.IS_NULL);
            attributes[1] = new CQLAttribute();
            attributes[1].setName(localAttributeName);
            attributes[1].setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
        } else if (values.size() == 1) {
            // create the property and apply twice because a group needs two
            // entries and we only got one value.
            cqlGroup.setLogicalOperation(GroupLogicalOperator.OR);
            attributes = new CQLAttribute[2];
            attributes[0] = createAttributeFromValue(joinCondition, localAttributeName, values.get(0));
            attributes[1] = attributes[0];
        } else {
            // more than 1 value
            attributes = new CQLAttribute[values.size()];
            cqlGroup.setLogicalOperation(GroupLogicalOperator.OR);
            for (int i = 0; i < values.size(); i++) {
                String currRemoteValue = values.get(i);
                attributes[i] = createAttributeFromValue(
                    joinCondition, localAttributeName, currRemoteValue);
            }
        }
        cqlGroup.setCQLAttribute(attributes);
        
        return cqlGroup;
    }


    /**
     * @param joinCondition
     * @param property
     * @param value
     * @return A CQL Attribute
     * @throws FederatedQueryProcessingException
     */
    private static CQLAttribute createAttributeFromValue(JoinCondition joinCondition,
        String property, String value) throws FederatedQueryProcessingException {
        CQLAttribute attr = new CQLAttribute();
        // set the local property name
        attr.setName(property);
        BinaryPredicate predicate = joinCondition.getPredicate();
        if (value == null) {
            if (BinaryPredicate.EQUAL_TO.equals(predicate)) {
                // we got null, and are supposed to compare it as =, so that
                // means is_null
                attr.setUnaryPredicate(UnaryPredicate.IS_NULL);
            } else if (BinaryPredicate.NOT_EQUAL_TO.equals(predicate)) {
                // we got null, and are supposed to compare it as !=, so that
                // means is_not_null
                attr.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);                
            } else {
                // should not get here, nulls should have been filtered out
                throw new FederatedQueryProcessingException(
                    "Internal problem processing query. Got unexpected null values.");
            }
        } else {
            // copy the join predicate into the attribute
            attr.setBinaryPredicate(predicate);
            // set the value to the string representation of 
            // the "foreign result value"
            // TODO: value types??
            AttributeValue attrValue = new AttributeValue();
            attrValue.setStringValue(value);
            attr.setAttributeValue(attrValue);
        }
        return attr;
    }
    
    
    private static String applyTransformation(DataTransformation transformation, String value) {
        // TODO: implement me
        return value;
    }


    /**
     * Returns true if the passed result is not null AND contains some type of
     * result data.
     * 
     * @param cqlResults
     * @return true if the passed result is not null AND contains some type of
     *         result data.
     */
    private static boolean hasResults(CQLQueryResults cqlResults) {
        return cqlResults != null
            && (cqlResults.getAttributeResult() != null && cqlResults.getAggregationResult() != null
                || cqlResults.getObjectResult() != null || cqlResults.getExtendedResult() != null);
    }
}