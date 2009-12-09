package gov.nih.nci.cagrid.data.cql2.validation.walker;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.CQLExtension;

public class Cql2WalkerExtensionCompatibilityValidationHandler extends Cql2WalkerHandlerAdapter {
    
    private static Log LOG = LogFactory.getLog(Cql2WalkerExtensionCompatibilityValidationHandler.class);
    
    private Collection<QName> supportedExtensions = null;
    
    public Cql2WalkerExtensionCompatibilityValidationHandler(Collection<QName> supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }
    

    public void startExtension(CQLExtension ext) throws Cql2WalkerException {
        MessageElement extension = null;
        if (ext.get_any() == null || ext.get_any().length == 0) {
            throw new ExtensionValidationException("No extension element was defined");
        }
        extension = ext.get_any()[0];
        // must understand defaults to false
        if (ext.getMustUnderstand() == null || !ext.getMustUnderstand().booleanValue()) {
            QName type = extension.getType();
            if (!supportedExtensions.contains(type)) {
                throw new ExtensionValidationException("Extension type " + type.toString() + " is not supported");
            }
            LOG.debug("Extension type " + type + " is supported");
        } else {
            LOG.debug("Must understand set to false, skipping support check");
        }
    }
}
