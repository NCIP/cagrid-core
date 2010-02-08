package gov.nih.nci.cagrid.fqp.processor2;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import gov.nih.nci.cagrid.dcql.Association;
import gov.nih.nci.cagrid.dcql.ForeignAssociation;
import gov.nih.nci.cagrid.dcql.ForeignPredicate;
import gov.nih.nci.cagrid.dcql.Group;
import gov.nih.nci.cagrid.dcql.JoinCondition;
import gov.nih.nci.cagrid.dcql.Object;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;


/**
 * FederatedQueryProcessor decomposes the DCQL into individual CQLs. Each
 * individual CQL is executed by specified grid service in serviceURL by the
 * DataServiceQueryExecutor.
 * 
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
     * Process root element DCQLQuery Element
     * 
     * @param targetObject
     * @return The CQL query required to process the target
     * @throws FederatedQueryProcessingException
     */
    public CQLQuery processDCQLQuery(Object targetObject) throws FederatedQueryProcessingException {
        CQLQuery cqlQuery = new CQLQuery();

        // initialize CQLObject .all the nested Queries would get resolved and
        // attached to this CQL object .
        gov.nih.nci.cagrid.cqlquery.Object cqlObject = new gov.nih.nci.cagrid.cqlquery.Object();
        cqlObject.setName(targetObject.getName());

        // process the DCQL object, building up the CQL object
        populateObjectFromDCQLObject(targetObject, cqlObject);
        // this CQL Object is our target
        cqlQuery.setTarget(cqlObject);

        return cqlQuery;
    }


    /**
     * Recursively processes the given DCQLObject, building up the given
     * cqlObject
     * 
     * @param dcqlObject
     * @param cqlObject
     * @throws FederatedQueryProcessingException
     */

    private void populateObjectFromDCQLObject(Object dcqlObject, gov.nih.nci.cagrid.cqlquery.Object cqlObject)
        throws FederatedQueryProcessingException {
        // check for any attribute (PASS THRU)
        if (dcqlObject.getAttribute() != null) {
            cqlObject.setAttribute(dcqlObject.getAttribute());
        }

        // check for group
        if (dcqlObject.getGroup() != null) {
            // convert group and attach group to CQL object
            gov.nih.nci.cagrid.cqlquery.Group cqlGroup = processGroup(dcqlObject.getGroup());
            cqlObject.setGroup(cqlGroup);
        }

        // check for Association
        if (dcqlObject.getAssociation() != null) {
            // Convert into CQL Associoation
            gov.nih.nci.cagrid.cqlquery.Association cqlAssociation = processAssociation(dcqlObject.getAssociation());
            cqlObject.setAssociation(cqlAssociation);
        }

        // check for ForeignAssociation
        if (dcqlObject.getForeignAssociation() != null) {
            gov.nih.nci.cagrid.cqlquery.Group resultedGroup = processForeignAssociation(dcqlObject
                .getForeignAssociation());
            cqlObject.setGroup(resultedGroup);

        }
    }


    /**
     * Process Group, which builds CQL Group. DCQL Groups are processed exactly
     * as DCQL Object, except Groups can contain multiple children predicates,
     * so an array of each must be processed.
     * 
     * @param dcqlGroup
     * @return The CQL group
     * @throws FederatedQueryProcessingException
     */
    private gov.nih.nci.cagrid.cqlquery.Group processGroup(Group dcqlGroup) throws FederatedQueryProcessingException {
        // convert basic group information and attach group to CQL object
        gov.nih.nci.cagrid.cqlquery.Group cqlGroup = new gov.nih.nci.cagrid.cqlquery.Group();
        // attach logical relationship
        cqlGroup.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromValue(
            dcqlGroup.getLogicRelation().toString()));

        // attributes (PASS THRU)
        if (dcqlGroup.getAttribute() != null) {
            cqlGroup.setAttribute(dcqlGroup.getAttribute());
        }

        // associations
        if (dcqlGroup.getAssociation() != null && dcqlGroup.getAssociation().length > 0) {
            Association dcqlAssociationArray[] = dcqlGroup.getAssociation();
            gov.nih.nci.cagrid.cqlquery.Association[] cqlAssociationArray = 
                new gov.nih.nci.cagrid.cqlquery.Association[dcqlAssociationArray.length];
            for (int i = 0; i < dcqlAssociationArray.length; i++) {
                cqlAssociationArray[i] = processAssociation(dcqlAssociationArray[i]);
            }
            cqlGroup.setAssociation(cqlAssociationArray);
        }

        // groups
        if (dcqlGroup.getGroup() != null && dcqlGroup.getGroup().length > 0) {
            Group dcqlGroupArray[] = dcqlGroup.getGroup();
            gov.nih.nci.cagrid.cqlquery.Group[] cqlGroupArray = 
                new gov.nih.nci.cagrid.cqlquery.Group[dcqlGroupArray.length];
            for (int i = 0; i < dcqlGroupArray.length; i++) {
                gov.nih.nci.cagrid.cqlquery.Group cqlNestedGroup = processGroup(dcqlGroupArray[i]);
                cqlGroupArray[i] = cqlNestedGroup;
            }
            cqlGroup.setGroup(cqlGroupArray);
        }

        // foreign associations
        if (dcqlGroup.getForeignAssociation() != null && dcqlGroup.getForeignAssociation().length > 0) {
            ForeignAssociation[] foreignAssociationArray = dcqlGroup.getForeignAssociation();
            gov.nih.nci.cagrid.cqlquery.Group[] cqlGroupArray = 
                new gov.nih.nci.cagrid.cqlquery.Group[foreignAssociationArray.length];
            for (int i = 0; i < foreignAssociationArray.length; i++) {
                // need to attach the results as criteria ...
                gov.nih.nci.cagrid.cqlquery.Group resultedGroup = processForeignAssociation(foreignAssociationArray[i]);
                cqlGroupArray[i] = resultedGroup;
            }
            // merge in these groups with any that already exist (from group
            // processing above)
            cqlGroup.setGroup((gov.nih.nci.cagrid.cqlquery.Group[]) Utils.concatenateArrays(
                gov.nih.nci.cagrid.cqlquery.Group.class, cqlGroup.getGroup(), cqlGroupArray));
        }

        return cqlGroup;
    }


    /**
     * Process Association convert DCQL Association into CQL Association.
     * 
     * @param dcqlAssociation
     * @return The CQL Association
     * @throws QueryExecutionException
     */
    private gov.nih.nci.cagrid.cqlquery.Association processAssociation(Association dcqlAssociation)
        throws FederatedQueryProcessingException {

        // create a new CQL Association from the DCQL Association
        gov.nih.nci.cagrid.cqlquery.Association cqlAssociation = new gov.nih.nci.cagrid.cqlquery.Association();
        cqlAssociation.setRoleName(dcqlAssociation.getRoleName());
        cqlAssociation.setName(dcqlAssociation.getName());

        // process the association's Object
        populateObjectFromDCQLObject(dcqlAssociation, cqlAssociation);

        return cqlAssociation;
    }


    /**
     * process ForeignAssociation, which is basically a Query that can be
     * executed by a grid service mentioned in serviceURL attribute As
     * ForeignAssocitaion itself is a DCQL Object , the Object is processed and
     * CQL Query would be passed to DataServiceQueryExecutor obtained results
     * from services are then aggreated.
     * 
     * @param foreignAssociation
     * @return The CQL Group representing the foreign association
     * @throws FederatedQueryProcessingException
     */
    private gov.nih.nci.cagrid.cqlquery.Group processForeignAssociation(ForeignAssociation foreignAssociation)
        throws FederatedQueryProcessingException {
        // get Foreign Object
        Object dcqlObject = foreignAssociation.getForeignObject();

        // make a new query with the CQL Object created by processing the
        // foreign association
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object cqlObject = new gov.nih.nci.cagrid.cqlquery.Object();
        cqlObject.setName(dcqlObject.getName());
        populateObjectFromDCQLObject(dcqlObject, cqlObject);
        cqlQuery.setTarget(cqlObject);

        // build up a query result modifier to only return distinct values of
        // the attribute we need
        String foreignAttribute = foreignAssociation.getJoinCondition().getForeignAttributeName();
        QueryModifier queryModifier = new QueryModifier();
        queryModifier.setDistinctAttribute(foreignAttribute);
        cqlQuery.setQueryModifier(queryModifier);

        // Execute Foreign Query .....
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
                if (attribute == null || attribute.length != 1 || !attribute[0].getName().equals(foreignAttribute)) {
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

        gov.nih.nci.cagrid.cqlquery.Group criteriaGroup = 
            buildGroup(foreignAssociation.getJoinCondition(), remoteAttributeValues);
        return criteriaGroup;
    }


    /**
     * Build group of Attributes based on the List generated by processResults
     * method. Attribute name would be left join CDE Attribute value would be
     * value from the list generated by processResults method predicate is
     * "EQUAL_TO" Group with logical operator "OR". If no results are found, a
     * group that can never be true is created (currently IS_NULL AND
     * IS_NOT_NULL); ideally a "false" predicate would be created, but we have
     * no such construct in CQL.
     * 
     * @param list
     * @return A CQL Group of attributes
     * @throws FederatedQueryProcessingException
     */
    public static gov.nih.nci.cagrid.cqlquery.Group buildGroup(JoinCondition joinCondition, List<String> list)
        throws FederatedQueryProcessingException {
        gov.nih.nci.cagrid.cqlquery.Group cqlGroup = new gov.nih.nci.cagrid.cqlquery.Group();
        String property = joinCondition.getLocalAttributeName();

        // pre-process the results to deal with null values
        // we need to deal with these here, so the logic to handle the special
        // cases (of list size) is correct.
        // we don't need to process EQUAL_TO or NOT_EQUAL_TO, because we will
        // convert these to IS_NULL and IS_NOT_NULL (which won't affect list
        // size) but other predicates we will filter out (thus
        // changing the list size)
        // NOTE: the predicate is set in XSD to default to EQUAL_TO, but Axis 
        // won't do this for you and returns null if the client didn't fill it in
        ForeignPredicate predicate = joinCondition.getPredicate();
        if (predicate != null // default as EQUAL_TO
            && !ForeignPredicate.EQUAL_TO.equals(predicate)
            && !ForeignPredicate.NOT_EQUAL_TO.equals(predicate)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    // since these come from DISTINCT values, there should be
                    // more than one call to this, otherwise it would be
                    // inefficient; process the whole list just in case though
                    list.remove(i--);
                }
            }
        }

        // if the size of result set returned by sub query is zero we got no
        // results, and this group should never evaluate to true
        // add an impossible IS_NULL AND IS_NOT_NULL
        // this needs to be done because the client asked for a predicate that
        // evaluated to false (no remote results, so no join can be true), so we
        // need to propagate this evaluation (not just omit it).
        if (list.size() == 0) {
            cqlGroup.setLogicRelation(LogicalOperator.AND);
            gov.nih.nci.cagrid.cqlquery.Attribute[] attrArray = new gov.nih.nci.cagrid.cqlquery.Attribute[2];

            gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
            attr.setName(property);
            attr.setPredicate(Predicate.IS_NULL);
            attr.setValue("");
            attrArray[0] = attr;

            attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
            attr.setName(property);
            attr.setPredicate(Predicate.IS_NOT_NULL);
            attr.setValue("");
            attrArray[1] = attr;
            // attach the created attribute array
            cqlGroup.setAttribute(attrArray);
        } else if (list.size() == 1) {
            // create the property and apply twice (because a group needs two
            // entries and we only got one value).
            cqlGroup.setLogicRelation(LogicalOperator.OR);
            gov.nih.nci.cagrid.cqlquery.Attribute[] attrArray = new gov.nih.nci.cagrid.cqlquery.Attribute[2];
            java.lang.Object currRemoteValue = list.get(0);
            gov.nih.nci.cagrid.cqlquery.Attribute attr = createAttributeFromValue(joinCondition, property,
                currRemoteValue);
            attrArray[0] = attr;
            attrArray[1] = attr;
            // attach the created attribute array
            cqlGroup.setAttribute(attrArray);
        } else {
            gov.nih.nci.cagrid.cqlquery.Attribute[] attrArray = new gov.nih.nci.cagrid.cqlquery.Attribute[list.size()];
            cqlGroup.setLogicRelation(LogicalOperator.OR);
            for (int i = 0; i < list.size(); i++) {
                java.lang.Object currRemoteValue = list.get(i);
                gov.nih.nci.cagrid.cqlquery.Attribute attr = createAttributeFromValue(
                    joinCondition, property, currRemoteValue);
                attrArray[i] = attr;
            }
            // attach the created attribute array
            cqlGroup.setAttribute(attrArray);
        }

        return cqlGroup;
    }


    /**
     * @param joinCondition
     * @param property
     * @param value
     * @return A CQL Attribute
     * @throws FederatedQueryProcessingException
     */
    private static gov.nih.nci.cagrid.cqlquery.Attribute createAttributeFromValue(JoinCondition joinCondition,
        String property, java.lang.Object value) throws FederatedQueryProcessingException {
        gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
        // set the local property name
        attr.setName(property);
        ForeignPredicate predicate = joinCondition.getPredicate();
        Predicate cqlPredicate = null;
        if (value == null) {
            if (predicate == null || ForeignPredicate.EQUAL_TO.equals(predicate)) {
                // we got null, and are supposed to compare it as =, so that
                // means is_null
                cqlPredicate = Predicate.IS_NULL;
            } else if (ForeignPredicate.NOT_EQUAL_TO.equals(predicate)) {
                // we got null, and are supposed to compare it as !=, so that
                // means is_not_null
                cqlPredicate = Predicate.IS_NOT_NULL;                
            } else {
                // should not get here, nulls should have been filtered out
                throw new FederatedQueryProcessingException(
                    "Internal problem processing query. Got unexpected null values.");
            }
            attr.setValue("");
        } else {
            // set the predicate to the join predicate (this requires DCQL
            // to use the same representation as CQL)
            if (predicate == null) {
                cqlPredicate = Predicate.EQUAL_TO;
            } else {
                cqlPredicate = Predicate.fromValue(predicate.getValue());
            }
            // set the value to the string representation of the "foreign
            // result value"
            attr.setValue(value.toString());
        }
        attr.setPredicate(cqlPredicate);
        return attr;
    }


    /**
     * Returns true iff the passed result is not null AND contains some type of
     * result data.
     * 
     * @param cqlResults
     * @return true iff the passed result is not null AND contains some type of
     *         result data.
     */
    private static boolean hasResults(CQLQueryResults cqlResults) {
        return cqlResults != null
            && (cqlResults.getAttributeResult() != null && cqlResults.getCountResult() != null
                || cqlResults.getIdentifierResult() != null || cqlResults.getObjectResult() != null);
    }
}