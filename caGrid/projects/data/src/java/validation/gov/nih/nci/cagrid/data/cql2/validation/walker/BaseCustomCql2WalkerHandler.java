/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
