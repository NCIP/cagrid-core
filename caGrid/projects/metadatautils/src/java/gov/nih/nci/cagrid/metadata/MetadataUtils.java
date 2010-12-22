package gov.nih.nci.cagrid.metadata;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.metadata.version.CaGridVersion;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;


public class MetadataUtils {
    
    private static Log LOG = LogFactory.getLog(MetadataUtils.class);
    
    /**
     * Obtain the service metadata from the specified service.
     * 
     * @param serviceEPR
     * @return The service metadata from the targeted service
     * @throws InvalidResourcePropertyException
     * @throws RemoteResourcePropertyRetrievalException
     * @throws ResourcePropertyRetrievalException
     */
    public static ServiceMetadata getServiceMetadata(EndpointReferenceType serviceEPR)
        throws InvalidResourcePropertyException, RemoteResourcePropertyRetrievalException,
        ResourcePropertyRetrievalException {
        Element resourceProperty = ResourcePropertyHelper.getResourceProperty(serviceEPR,
            MetadataConstants.CAGRID_MD_QNAME);
        ServiceMetadata result;
        try {
            result = deserializeServiceMetadata(new StringReader(XmlUtils.toString(resourceProperty)));
        } catch (Exception e) {
            throw new ResourcePropertyRetrievalException("Unable to deserailize ServiceMetadata: " + e.getMessage(), e);
        }
        return result;
    }


    /**
     * Obtain the data service metadata from the specified service.
     * 
     * @param serviceEPR
     * @return The domain model from the targeted data service
     * @throws InvalidResourcePropertyException
     * @throws RemoteResourcePropertyRetrievalException
     * @throws ResourcePropertyRetrievalException
     */
    public static DomainModel getDomainModel(EndpointReferenceType serviceEPR) throws InvalidResourcePropertyException,
        RemoteResourcePropertyRetrievalException, ResourcePropertyRetrievalException {
        Element resourceProperty = ResourcePropertyHelper.getResourceProperty(serviceEPR,
            MetadataConstants.CAGRID_DATA_MD_QNAME);

        DomainModel result;
        try {
            result = deserializeDomainModel(new StringReader(XmlUtils.toString(resourceProperty)));
        } catch (Exception e) {
            throw new ResourcePropertyRetrievalException("Unable to deserailize DomainModel: " + e.getMessage(), e);
        }
        return result;
    }
    
    
    public static CaGridVersion getCaGridVersion(EndpointReferenceType serviceEPR) throws InvalidResourcePropertyException, 
        RemoteResourcePropertyRetrievalException, ResourcePropertyRetrievalException {
        CaGridVersion result = null;
        try {
            Element resourceProperty = ResourcePropertyHelper.getResourceProperty(serviceEPR,
                MetadataConstants.CAGRID_VERSION_QNAME);
            result = deserializeCaGridVersion(new StringReader(XmlUtils.toString(resourceProperty)));
        } catch (InvalidResourcePropertyException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resource property for CaGridVersion not found on " + serviceEPR.getAddress().toString());
                LOG.debug("Assuming the service is from an older version of caGrid which doesn' have this property");
                LOG.debug(e);
            }
        } catch (Exception ex) {
            throw new ResourcePropertyRetrievalException("Unable to deserailize CaGridVersion: " + ex.getMessage(), ex);
        }
        return result;
    }


    /**
     * Determines if the given service is a data service
     * 
     * @param serviceEPR
     * @return true iff the service is a data service
     * @throws RemoteResourcePropertyRetrievalException
     * @throws ResourcePropertyRetrievalException
     */
    public static boolean isDataService(EndpointReferenceType serviceEPR) throws InvalidResourcePropertyException,
        RemoteResourcePropertyRetrievalException, ResourcePropertyRetrievalException {

        DomainModel domainModel = null;
        ServiceMetadata serviceMetadata = null;
        try {
            domainModel = getDomainModel(serviceEPR);
            serviceMetadata = getServiceMetadata(serviceEPR);
        } catch (InvalidResourcePropertyException e) {
            // if it complains about not having the necessary metadata, its not
            // a data service
            return false;
        }

        return domainModel != null && serviceMetadata != null;
    }


    /**
     * Determines if the given service is an analytical service (not a data
     * service)
     * 
     * @param serviceEPR
     * @return true iff the service is a data service
     * @throws RemoteResourcePropertyRetrievalException
     * @throws ResourcePropertyRetrievalException
     */
    public static boolean isAnalyticalService(EndpointReferenceType serviceEPR)
        throws InvalidResourcePropertyException, RemoteResourcePropertyRetrievalException,
        ResourcePropertyRetrievalException {

        DomainModel domainModel = null;
        ServiceMetadata serviceMetadata = null;
        try {
            serviceMetadata = getServiceMetadata(serviceEPR);
        } catch (InvalidResourcePropertyException e) {
            // if it complains about not having the necessary metadata, its not
            // an analytical service
            return false;
        }
        try {
            domainModel = getDomainModel(serviceEPR);
        } catch (InvalidResourcePropertyException e) {
            // this is actually expected; the service should not be providing
            // data service metadata
        }

        return domainModel == null && serviceMetadata != null;
    }


    /**
     * Write the XML representation of the specified metadata to the specified
     * writer. If either are null, an IllegalArgumentException will be thown.
     * 
     * @param metadata
     * @param writer
     * @throws Exception
     */
    public static void serializeServiceMetadata(ServiceMetadata metadata, Writer writer) throws Exception {
        if (metadata == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(metadata, MetadataConstants.CAGRID_MD_QNAME, writer);
    }


    /**
     * Create an instance of the service metadata from the specified reader. The
     * reader must point to a stream that contains an XML representation of the
     * metadata. If the reader is null, an IllegalArgumentException will be
     * thown.
     * 
     * @param xmlReader
     * @return The deserialized service metadata
     * @throws Exception
     */
    public static ServiceMetadata deserializeServiceMetadata(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return Utils.deserializeObject(xmlReader, ServiceMetadata.class);
    }


    /**
     * Write the XML representation of the specified metadata to the specified
     * writer. If either are null, an IllegalArgumentException will be thown.
     * 
     * @param domainModel
     * @param writer
     * @throws Exception
     */
    public static void serializeDomainModel(DomainModel domainModel, Writer writer) throws Exception {
        if (domainModel == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(domainModel, MetadataConstants.CAGRID_DATA_MD_QNAME, writer);
    }


    /**
     * Create an instance of the data service metadata from the specified
     * reader. The reader must point to a stream that contains an XML
     * representation of the metadata. If the reader is null, an
     * IllegalArgumentException will be thown.
     * 
     * @param xmlReader
     * @return The deserialized domain model
     * @throws Exception
     */
    public static DomainModel deserializeDomainModel(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return  Utils.deserializeObject(xmlReader, DomainModel.class);
    }
    
    
    public static void serializeCaGridVersion(CaGridVersion version, Writer writer) throws Exception {
        if (version == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(version, MetadataConstants.CAGRID_VERSION_QNAME, writer);
    }
    
    
    public static CaGridVersion deserializeCaGridVersion(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return Utils.deserializeObject(xmlReader, CaGridVersion.class);
    }
}
