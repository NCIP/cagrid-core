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
package gov.nih.nci.cagrid.introduce.extensions.metadata.codegen;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extensions.metadata.common.MetadataExtensionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Just sets the metadata file location if its not set.
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
        
        String versionFilename = helper.setVersionFilenameProperty();
        if (versionFilename == null) {
            LOG.error("Unable to locate caGrid Version resource property.");
            return;
        } else {
            LOG.debug("Set service metadata file location to:" + versionFilename);
        }
    }
}
