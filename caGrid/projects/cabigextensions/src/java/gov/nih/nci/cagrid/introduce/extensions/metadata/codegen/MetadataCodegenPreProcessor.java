package gov.nih.nci.cagrid.introduce.extensions.metadata.codegen;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extensions.metadata.common.MetadataExtensionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Just sets the metadata filelocation if its not set.
 * 
 * @author oster
 */
public class MetadataCodegenPreProcessor implements CodegenExtensionPreProcessor {
    protected static Log LOG = LogFactory.getLog(MetadataCodegenPreProcessor.class.getName());


    public void preCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CodegenExtensionException {
        MetadataExtensionHelper helper = new MetadataExtensionHelper(info);

        String filename = helper.setMetadataFilenameProperty();
        if (filename == null) {
            LOG.error("Unable to locate Service Metadata resource property.");
            return;
        } else {
            LOG.debug("Set service metadata file location to:" + filename);
        }
    }

}
