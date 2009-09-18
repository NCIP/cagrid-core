package gov.nih.nci.cagrid.introduce.extensions.metadata.common;

import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extensions.metadata.constants.MetadataConstants;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * MetadataExtensionBase
 * 
 * @author oster
 * @created Apr 9, 2007 11:59:14 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class MetadataExtensionHelper {
    protected static Log LOG = LogFactory.getLog(MetadataExtensionHelper.class.getName());

    protected static final String DEFAULT_FILENAME = "serviceMetadata.xml";
    protected ServiceInformation info = null;


    public MetadataExtensionHelper(ServiceInformation info) {
        this.info = info;
    }


    /**
     * @return
     *      The filename of the metadata
     */
    public String getMetadataFilenameProperty() {
        ResourcePropertyType rp = getMetadataResourceProperty();
        if (rp != null) {
            String fileLocation = rp.getFileLocation();
            if (fileLocation == null || fileLocation.trim().equals("")) {
                rp.setFileLocation(DEFAULT_FILENAME);
            }

            return rp.getFileLocation();
        }

        return null;
    }


    public ResourcePropertyType getMetadataResourceProperty() {
        ServiceType mainServ = this.info.getServiceDescriptor().getServices().getService()[0];
        ResourcePropertiesListType resourcePropertiesList = mainServ.getResourcePropertiesList();
        if (resourcePropertiesList != null && resourcePropertiesList.getResourceProperty() != null) {
            ResourcePropertyType[] resourceProperty = resourcePropertiesList.getResourceProperty();
            for (ResourcePropertyType rp : resourceProperty) {
                if (rp.getQName().equals(MetadataConstants.SERVICE_METADATA_QNAME)) {
                    return rp;
                }
            }
        }

        return null;
    }


    public String setMetadataFilenameProperty() {
        ResourcePropertyType rp = getMetadataResourceProperty();
        if (rp != null) {
            String fileLocation = rp.getFileLocation();
            if (fileLocation == null || fileLocation.trim().equals("")) {
                rp.setFileLocation(DEFAULT_FILENAME);
            }

            return rp.getFileLocation();
        }

        return null;
    }


    public File getMetadataAbsoluteFile() {
        String fileProp = getMetadataFilenameProperty();
        File localFile = new File(fileProp);
        if (localFile.isAbsolute()) {
            return localFile;
        } else {
            return new File(this.info.getBaseDirectory() + File.separator + "etc" + File.separator, fileProp);
        }

    }


    public boolean shouldCreateMetadata() {
        return getMetadataFilenameProperty() != null;
    }


    public ServiceMetadata getExistingServiceMetdata() {
        if (!shouldCreateMetadata()) {
            return null;
        }

        ServiceMetadata metadata = null;
        // look if the file already exists, and load it in, in case other
        // aspects of it (such as cancer center info) are set by something else
        File mdFile = getMetadataAbsoluteFile();
        if (mdFile.exists() && mdFile.canRead()) {
            try {
                FileReader reader = new FileReader(mdFile);
                metadata = MetadataUtils.deserializeServiceMetadata(reader);
                reader.close();
            } catch (Exception e) {
                LOG.error("Failed to deserialize existing metadata document!  A new one will be created.", e);
            }
        }
        return metadata;
    }


    public void writeServiceMetadata(ServiceMetadata md) throws Exception {
        FileWriter writer = new FileWriter(getMetadataAbsoluteFile());
        MetadataUtils.serializeServiceMetadata(md, writer);
        writer.close();
    }

}
