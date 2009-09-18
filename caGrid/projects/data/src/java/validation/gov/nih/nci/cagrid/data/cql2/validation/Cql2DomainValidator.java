package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

/**
 * Cql2DomainValidator
 * Validates a CQL 2 query against a domain model
 * 
 * @author David
 */
public abstract class Cql2DomainValidator {
    
    protected DomainModel model = null;
    
    public Cql2DomainValidator(DomainModel model) {
        this.model = model;
    }
    
    
    public DomainModel getDomainModel() {
        return this.model;
    }
    

    public abstract void validateAgainstDomainModel(CQLQuery query) throws DomainValidationException;
}
