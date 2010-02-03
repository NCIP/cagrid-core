package gov.nih.nci.cagrid.data.cql2.validation.walker;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

/**
 * Abstract base class for custom CQL 2 walker handlers
 * that a service developer might want to add to their service
 * 
 * @author David
 */
public abstract class BaseCustomCql2WalkerHandler extends Cql2WalkerHandlerAdapter {

    private DomainModel domainModel = null;
    
    public BaseCustomCql2WalkerHandler(DomainModel model) {
        super();
        this.domainModel = model;
    }
    
    
    public DomainModel getDomainModel() {
        return this.domainModel;
    }
}
