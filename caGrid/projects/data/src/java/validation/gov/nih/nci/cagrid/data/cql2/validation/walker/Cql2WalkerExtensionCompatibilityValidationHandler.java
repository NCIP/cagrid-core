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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.CQLExtension;
import org.exolab.castor.types.AnyNode;

public class Cql2WalkerExtensionCompatibilityValidationHandler extends Cql2WalkerHandlerAdapter {
    
    private static Log LOG = LogFactory.getLog(Cql2WalkerExtensionCompatibilityValidationHandler.class);
    
    private Collection<QName> supportedExtensions = null;
    
    public Cql2WalkerExtensionCompatibilityValidationHandler(Collection<QName> supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }
    

    public void startExtension(CQLExtension ext) throws Exception {
        AnyNode extension = null;
        if (ext.get_any() == null) {
            throw new ExtensionValidationException("No extension element was defined");
        }
        extension = (AnyNode) ext.get_any();
        // must understand defaults to false
        if (ext.getMustUnderstand() == null || !ext.getMustUnderstand().booleanValue()) {
            QName type = new QName(extension.getNamespaceURI(), extension.getLocalName());
            if (!supportedExtensions.contains(type)) {
                throw new ExtensionValidationException("Extension type " + type.toString() + " is not supported");
            }
            LOG.debug("Extension type " + type + " is supported");
        } else {
            LOG.debug("Must understand set to false, skipping support check");
        }
    }
}
