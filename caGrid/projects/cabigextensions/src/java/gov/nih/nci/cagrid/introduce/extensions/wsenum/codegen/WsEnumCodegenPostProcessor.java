package gov.nih.nci.cagrid.introduce.extensions.wsenum.codegen;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

import org.jdom.Element;


/**
 * WsEnumCreationPostProcessor Post-creation extension to Introduce to add
 * WS-Enumeration support to a Grid Service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 16, 2006
 * @version $Id: WsEnumCodegenPostProcessor.java,v 1.5 2007/05/08 16:58:09
 *          dervin Exp $
 */
public class WsEnumCodegenPostProcessor implements CodegenExtensionPostProcessor {

    public WsEnumCodegenPostProcessor() {

    }


    public void postCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CodegenExtensionException {
        editJNDI(info);
    }


    private void editJNDI(ServiceInformation info) throws CodegenExtensionException {
        boolean jndiEdited = false;
        File serviceJndiFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "jndi-config.xml");
        Element jndiRoot = null;
        try {
            jndiRoot = XMLUtilities.fileNameToDocument(serviceJndiFile.getAbsolutePath()).getRootElement();
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error loading service's JNDI file: " + ex.getMessage(), ex);
        }
        // locate the enumeration service context's stuff in the JNDI
        Iterator serviceElementIter = jndiRoot.getChildren("service", jndiRoot.getNamespace()).iterator();
        String serviceName = "SERVICE-INSTANCE-PREFIX/" + info.getServices().getService(0).getName() + "Enumeration";
        while (serviceElementIter.hasNext()) {
            Element serviceElement = (Element) serviceElementIter.next();
            if (serviceElement.getAttributeValue("name").equals(serviceName)) {
                // see if the service already has the resource definition
                if (serviceElement.getChild("resource", jndiRoot.getNamespace()) == null) {
                    // add the enumeration resource description
                    serviceElement.addContent(getEnumerationResourceDescription());
                    jndiEdited = true;
                }
                break;
            }
        }
        if (jndiEdited) {
            // write the JNDI with edits back out
            try {
                FileWriter jndiWriter = new FileWriter(serviceJndiFile);
                String xml = XMLUtilities.formatXML(XMLUtilities.elementToString(jndiRoot));
                jndiWriter.write(xml);
                jndiWriter.flush();
                jndiWriter.close();
            } catch (Exception ex) {
                throw new CodegenExtensionException("Error writing edited JNDI file: " + ex.getMessage(), ex);
            }
        }
    }


    private Element getEnumerationResourceDescription() throws CodegenExtensionException {
        String globusLocation = CommonTools.getGlobusLocation();
        File coreJndiConfigFile = new File(new File(globusLocation).getAbsolutePath() + File.separator + "etc"
            + File.separator + "globus_wsrf_core" + File.separator + "jndi-config.xml");
        Element coreJndiRoot = null;
        try {
            coreJndiRoot = XMLUtilities.fileNameToDocument(coreJndiConfigFile.getAbsolutePath()).getRootElement();
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error loading Globus core JNDI file: " + ex.getMessage(), ex);
        }
        Element globalElement = coreJndiRoot.getChild("global", coreJndiRoot.getNamespace());
        Iterator resourceElementIter = globalElement.getChildren("resource", coreJndiRoot.getNamespace()).iterator();
        while (resourceElementIter.hasNext()) {
            Element resourceElement = (Element) resourceElementIter.next();
            if (resourceElement.getAttributeValue("name").equals("enumeration/EnumerationHome")) {
                Element resourceDescription = (Element) resourceElement.clone();
                return (Element) resourceDescription.detach();
            }
        }
        return null;
    }
}
