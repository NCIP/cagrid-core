package org.cagrid.mms.service;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mms.domain.ModelSourceMetadata;
import org.cagrid.mms.domain.NamespaceToProjectMapping;
import org.cagrid.mms.domain.UMLAssociationExclude;
import org.cagrid.mms.domain.UMLProjectIdentifer;
import org.cagrid.mms.service.impl.MMS;
import org.cagrid.mms.service.impl.MMSGeneralException;
import org.cagrid.mms.stubs.MetadataModelServiceResourceProperties;
import org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier;
import org.globus.wsrf.config.ContainerConfig;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;


/**
 * MMS Grid Service Implementation
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class MetadataModelServiceImpl extends MetadataModelServiceImplBase {

    protected static Log LOG = LogFactory.getLog(MetadataModelServiceImpl.class.getName());

    private static final String MMS_BEAN_NAME = "mms";
    private MMS mms;


    public MetadataModelServiceImpl() throws RemoteException {
        super();

        String mmsConfigurationFile = null;
        try {

            // load the configuration file from our extended resource
            // configuration
            mmsConfigurationFile = getConfiguration().getMmsConfigurationFile();

            FileSystemResource confResource = new FileSystemResource(mmsConfigurationFile);

            XmlBeanFactory factory = new XmlBeanFactory(confResource);
            // PropertyPlaceholderConfigurer cfg = new
            // PropertyPlaceholderConfigurer();
            // cfg.setLocation(mmsPropertiesResource);
            // cfg.postProcessBeanFactory(factory);

            this.mms = (MMS) factory.getBean(MMS_BEAN_NAME, MMS.class);

        } catch (Exception e) {
            throw new RemoteException("Problem loading configuration file:" + e.getMessage(), e);
        }

        // set the resource's ModelSourceMetadata from the Spring-loaded
        // implementation
        try {
            ((MetadataModelServiceResourceProperties) getResourceHome().getAddressedResource().getResourceBean())
                .setModelSourceMetadata((ModelSourceMetadata) this.mms.getModelSourceMetadata());
        } catch (Exception e) {
            String message = "Unable to set Model Source Metadata!";
            LOG.error(message, e);
            throw new RemoteException(message, e);
        }
    }


    protected MMS getMms() {
        return this.mms;
    }


    public gov.nih.nci.cagrid.metadata.dataservice.DomainModel generateDomainModelForProject(
        org.cagrid.mms.domain.UMLProjectIdentifer umlProjectIdentifer) throws RemoteException,
        org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier {
        if (umlProjectIdentifer == null) {
            InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
            FaultHelper helper = new FaultHelper(fault);
            helper.setDescription("A null UMLProjectIdentifier cannot be used!");
            throw (InvalidUMLProjectIndentifier) helper.getFault();
        }

        try {
            return getMms().generateDomainModelForProject(umlProjectIdentifer);
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }


    public gov.nih.nci.cagrid.metadata.dataservice.DomainModel generateDomainModelForPackages(
        org.cagrid.mms.domain.UMLProjectIdentifer umlProjectIdentifer, java.lang.String[] packageNames)
        throws RemoteException, org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier {

        if (umlProjectIdentifer == null) {
            InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
            FaultHelper helper = new FaultHelper(fault);
            helper.setDescription("A null UMLProjectIdentifier cannot be used!");
            throw (InvalidUMLProjectIndentifier) helper.getFault();
        }

        Collection<String> packages = new ArrayList<String>();
        if (packageNames != null) {
            for (String pkg : packageNames) {
                packages.add(pkg);
            }
        }

        try {
            return getMms().generateDomainModelForPackages(umlProjectIdentifer, packages);
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }


    public gov.nih.nci.cagrid.metadata.dataservice.DomainModel generateDomainModelForClasses(
        org.cagrid.mms.domain.UMLProjectIdentifer umlProjectIdentifer, java.lang.String[] fullyQualifiedClassNames)
        throws RemoteException, org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier {

        if (umlProjectIdentifer == null) {
            InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
            FaultHelper helper = new FaultHelper(fault);
            helper.setDescription("A null UMLProjectIdentifier cannot be used!");
            throw (InvalidUMLProjectIndentifier) helper.getFault();
        }

        Collection<String> classes = new ArrayList<String>();
        if (fullyQualifiedClassNames != null) {
            for (String className : fullyQualifiedClassNames) {
                classes.add(className);
            }
        }

        try {
            return getMms().generateDomainModelForClasses(umlProjectIdentifer, classes);
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }


    public gov.nih.nci.cagrid.metadata.dataservice.DomainModel generateDomainModelForClassesWithExcludes(
        org.cagrid.mms.domain.UMLProjectIdentifer umlProjectIdentifer, java.lang.String[] fullyQualifiedClassNames,
        org.cagrid.mms.domain.UMLAssociationExclude[] umlAssociationExclude) throws RemoteException,
        org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier {

        if (umlProjectIdentifer == null) {
            InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
            FaultHelper helper = new FaultHelper(fault);
            helper.setDescription("A null UMLProjectIdentifier cannot be used!");
            throw (InvalidUMLProjectIndentifier) helper.getFault();
        }

        Collection<String> classes = new ArrayList<String>();
        if (fullyQualifiedClassNames != null) {
            for (String className : fullyQualifiedClassNames) {
                classes.add(className);
            }
        }

        Collection<UMLAssociationExclude> excludes = new ArrayList<UMLAssociationExclude>();
        if (umlAssociationExclude != null) {
            for (UMLAssociationExclude exclude : umlAssociationExclude) {
                excludes.add(exclude);
            }
        }

        try {
            return getMms().generateDomainModelForClassesWithExcludes(umlProjectIdentifer, classes, excludes);
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }


    public gov.nih.nci.cagrid.metadata.ServiceMetadata annotateServiceMetadata(
        gov.nih.nci.cagrid.metadata.ServiceMetadata serviceMetadata,
        org.cagrid.mms.domain.NamespaceToProjectMapping[] namespaceToProjectMappings) throws RemoteException,
        org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier {

        Map<URI, UMLProjectIdentifer> mappings = new HashMap<URI, UMLProjectIdentifer>();
        if (namespaceToProjectMappings != null) {
            for (NamespaceToProjectMapping mapping : namespaceToProjectMappings) {
                try {
                    if (mapping.getUMLProjectIdentifer() == null) {
                        InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
                        FaultHelper helper = new FaultHelper(fault);
                        helper
                            .setDescription("A null UMLProjectIdentifier cannot be used for the mapping from namespace ("
                                + mapping.getNamespaceURI() + ").");
                        throw (InvalidUMLProjectIndentifier) helper.getFault();

                    }
                    mappings.put(new URI(mapping.getNamespaceURI().toString()), mapping.getUMLProjectIdentifer());
                } catch (URISyntaxException e) {
                    String message = "Problem parsing specified URI:" + e.getMessage();
                    LOG.error(message, e);
                    throw new RemoteException(message);
                }
            }
        }

        try {
            return getMms().annotateServiceMetadata(serviceMetadata, mappings);
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }


    public org.cagrid.mms.domain.ModelSourceMetadata getModelSourceMetadata() throws RemoteException {

        try {
            return getMms().getModelSourceMetadata();
        } catch (MMSGeneralException e) {
            // TODO: replace with typed exception?
            throw new RemoteException(e.getMessage(), e);
        }
    }

}
