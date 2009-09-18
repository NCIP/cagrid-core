package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;

/**
 * Cql2StructureValidator
 * Validates the syntax of a CQL 2 query
 * 
 * @author David
 */
public interface Cql2StructureValidator {

    public void validateQuerySyntax(CQLQuery query) throws StructureValidationException;
}
