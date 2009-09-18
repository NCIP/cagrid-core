package gov.nih.nci.cagrid.introduce.portal.modification.discovery;

import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


/**
 * Classes which extend this class should maintain the proper policy for
 * downloading and storing schemas. There are 3 policies: replace ignore and
 * error Replace: if there are any schema/namespace to bring in that are already
 * existing in the services current namespace list then just replace them
 * Ignore: if there are any schema/namespace to bring in that are already
 * existing in the services current namespace list then just ignore them, i.e do
 * not overwrite the schema that already exists and do not create a new
 * namespace type. Error: if there are any schema/namespace to bring in that are
 * already existing in the services current namespace list then set the error
 * messages and return null. In this case the developer should make sure the
 * class does not pull any schemas down and does not return any new namespace
 * types.
 * 
 * @author hastings
 */
public abstract class NamespaceTypeDiscoveryComponent extends JPanel {
    public static final String REPLACE_POLICY = "replace";
    public static final String IGNORE_POLICY = "ignore";
    public static final String ERROR_POLICY = "error";

    private DiscoveryExtensionDescriptionType descriptor;
    private NamespacesType currentNamespaces;
    protected List<String> errors;
    private Throwable errorCauseThrowable;


    public NamespaceTypeDiscoveryComponent(DiscoveryExtensionDescriptionType descriptor,
        NamespacesType currentNamespaces) {
        this.descriptor = descriptor;
        this.currentNamespaces = currentNamespaces;
        this.errors = new ArrayList<String>();
    }


    public DiscoveryExtensionDescriptionType getDescriptor() {
        return this.descriptor;
    }


    public NamespacesType getCurrentNamespaces() {
        return this.currentNamespaces;
    }
    
    
    public void setCurrentNamespaces(NamespacesType types){
        this.currentNamespaces = types;
    }


    public boolean namespaceAlreadyExists(String namespaceURI) {
        if (CommonTools.getNamespaceType(this.currentNamespaces, namespaceURI) != null) {
            return true;
        }
        return false;
    }


    protected void addError(String error) {
        this.errors.add(error);
    }


    protected void setErrorCauseThrowable(Throwable t) {
        this.errorCauseThrowable = t;
    }


    public Throwable getErrorCauseThrowable() {
        Throwable t = this.errorCauseThrowable;
        this.errorCauseThrowable = null;
        return t;
    }


    public String[] getErrorMessage() {
        String[] messages = this.errors.toArray(new String[this.errors.size()]);
        this.errors = new ArrayList<String>();
        return messages;
    }


    public abstract NamespaceType[] createNamespaceType(File schemaDestinationDir, NamespaceReplacementPolicy replacementPolicy,
        MultiEventProgressBar progress);

}
