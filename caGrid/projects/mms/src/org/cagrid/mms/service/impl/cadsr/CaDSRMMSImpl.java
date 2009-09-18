package org.cagrid.mms.service.impl.cadsr;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cagrid.mms.domain.ModelSourceMetadata;
import org.cagrid.mms.domain.Property;
import org.cagrid.mms.domain.PropertyDescriptor;
import org.cagrid.mms.domain.SourceDescriptor;
import org.cagrid.mms.domain.SourceDescriptorSupportedProjectProperties;
import org.cagrid.mms.domain.UMLAssociationExclude;
import org.cagrid.mms.domain.UMLProjectIdentifer;
import org.cagrid.mms.service.impl.MMS;
import org.cagrid.mms.service.impl.MMSGeneralException;
import org.cagrid.mms.stubs.types.InvalidUMLProjectIndentifier;


public class CaDSRMMSImpl implements MMS {

    public static final String SOURCE_PROPERTY_PUBLIC_ID = "publicID";
    public static final String SOURCE_PROPERTY_LONG_NAME = "longName";
    public static final String SOURCE_PROPERTY_GME_NAMESPACE = "gmeNamespace";

    private ModelSourceMetadata metadata;
    private final Map<String, String> sourceToURLMap;


    public CaDSRMMSImpl(ModelSourceMetadata metadata, Map<String, String> sourceToURLMap) {
        this.sourceToURLMap = sourceToURLMap;
        this.metadata = metadata;
        // go through the supplied sources and add the properties we support
        for (SourceDescriptor source : this.metadata.getSupportedModelSources().getSource()) {
            SourceDescriptorSupportedProjectProperties supportedProjectProperties = new SourceDescriptorSupportedProjectProperties();
            source.setSupportedProjectProperties(supportedProjectProperties);

            PropertyDescriptor[] propertyDescriptors = new PropertyDescriptor[3];
            supportedProjectProperties.setPropertyDescriptor(propertyDescriptors);

            propertyDescriptors[0] = new PropertyDescriptor();
            propertyDescriptors[0].setName(SOURCE_PROPERTY_GME_NAMESPACE);
            propertyDescriptors[0].setDescription("The " + SOURCE_PROPERTY_GME_NAMESPACE
                + " attribute of the caDSR Project.");
            propertyDescriptors[0].setRequired(false);

            propertyDescriptors[1] = new PropertyDescriptor();
            propertyDescriptors[1].setName(SOURCE_PROPERTY_LONG_NAME);
            propertyDescriptors[1].setDescription("The " + SOURCE_PROPERTY_LONG_NAME
                + " attribute of the caDSR Project.");
            propertyDescriptors[1].setRequired(false);

            propertyDescriptors[2] = new PropertyDescriptor();
            propertyDescriptors[2].setName(SOURCE_PROPERTY_PUBLIC_ID);
            propertyDescriptors[2].setDescription("The " + SOURCE_PROPERTY_PUBLIC_ID
                + " attribute of the caDSR Project.");
            propertyDescriptors[2].setRequired(false);
        }
    }


    public ModelSourceMetadata getModelSourceMetadata() {
        return metadata;
    }


    public ServiceMetadata annotateServiceMetadata(ServiceMetadata serviceMetadata,
        Map<URI, UMLProjectIdentifer> namespaceToProjectMappings) throws MMSGeneralException,
        InvalidUMLProjectIndentifier {

        Map<String, QualifiedProject> uriToProjectMap = new HashMap<String, QualifiedProject>();
        if (namespaceToProjectMappings != null) {
            for (URI namespace : namespaceToProjectMappings.keySet()) {
                UMLProjectIdentifer projId = namespaceToProjectMappings.get(namespace);
                QualifiedProject proj = new QualifiedProject(getApplicationServiceForUMLProjectIdentifier(projId),
                    createProjectPrototypeFromIdentifier(projId));
                uriToProjectMap.put(namespace.toString(), proj);
            }
        }

        ServiceMetadataAnnotator annotator = new ServiceMetadataAnnotator(uriToProjectMap,
            getApplicationService(getModelSourceMetadata().getDefaultSourceIdentifier()));

        try {
            annotator.annotateServiceMetadata(serviceMetadata);
        } catch (CaDSRGeneralException e) {
            throw new MMSGeneralException("Problem from remote caDSR:" + e.getMessage(), e);
        }
        return serviceMetadata;
    }


    public DomainModel generateDomainModelForClasses(UMLProjectIdentifer umlProjectIdentifer,
        Collection<String> fullyQualifiedClassNames) throws MMSGeneralException, InvalidUMLProjectIndentifier {

        ApplicationService applicationService = getApplicationServiceForUMLProjectIdentifier(umlProjectIdentifer);
        DomainModelBuilder builder = new DomainModelBuilder(applicationService);

        try {
            String classes[] = new String[fullyQualifiedClassNames.size()];
            return builder.createDomainModelForClasses(createProjectPrototypeFromIdentifier(umlProjectIdentifer),
                fullyQualifiedClassNames.toArray(classes));
        } catch (DomainModelGenerationException e) {
            throw new MMSGeneralException("Problem from remote caDSR:" + e.getMessage(), e);
        }

    }


    public DomainModel generateDomainModelForClassesWithExcludes(UMLProjectIdentifer umlProjectIdentifer,
        Collection<String> fullyQualifiedClassNames, Collection<UMLAssociationExclude> umlAssociationExclude)
        throws InvalidUMLProjectIndentifier, MMSGeneralException {

        ApplicationService applicationService = getApplicationServiceForUMLProjectIdentifier(umlProjectIdentifer);
        DomainModelBuilder builder = new DomainModelBuilder(applicationService);

        try {
            String classes[] = new String[fullyQualifiedClassNames.size()];
            UMLAssociationExclude excludes[] = new UMLAssociationExclude[umlAssociationExclude.size()];

            return builder.createDomainModelForClassesWithExcludes(
                createProjectPrototypeFromIdentifier(umlProjectIdentifer), fullyQualifiedClassNames.toArray(classes),
                umlAssociationExclude.toArray(excludes));
        } catch (DomainModelGenerationException e) {
            throw new MMSGeneralException("Problem from remote caDSR:" + e.getMessage(), e);
        }
    }


    public DomainModel generateDomainModelForPackages(UMLProjectIdentifer umlProjectIdentifer,
        Collection<String> packageNames) throws InvalidUMLProjectIndentifier, MMSGeneralException {
        ApplicationService applicationService = getApplicationServiceForUMLProjectIdentifier(umlProjectIdentifer);
        DomainModelBuilder builder = new DomainModelBuilder(applicationService);

        try {
            String packages[] = new String[packageNames.size()];
            return builder.createDomainModelForPackages(createProjectPrototypeFromIdentifier(umlProjectIdentifer),
                packageNames.toArray(packages));
        } catch (DomainModelGenerationException e) {
            throw new MMSGeneralException("Problem from remote caDSR:" + e.getMessage(), e);
        }
    }


    public DomainModel generateDomainModelForProject(UMLProjectIdentifer umlProjectIdentifer)
        throws MMSGeneralException, InvalidUMLProjectIndentifier {

        ApplicationService applicationService = getApplicationServiceForUMLProjectIdentifier(umlProjectIdentifer);
        DomainModelBuilder builder = new DomainModelBuilder(applicationService);

        try {
            return builder.createDomainModel(createProjectPrototypeFromIdentifier(umlProjectIdentifer));
        } catch (DomainModelGenerationException e) {
            throw new MMSGeneralException("Problem from remote caDSR:" + e.getMessage(), e);
        }
    }


    private Project createProjectPrototypeFromIdentifier(UMLProjectIdentifer umlProjectIdentifer)
        throws InvalidUMLProjectIndentifier {
        // create the prototype
        Project prototype = new Project();
        prototype.setVersion(umlProjectIdentifer.getVersion());
        // TODO is this what I want to map identifier too (or could I map it to
        // either short or long name)
        prototype.setShortName(umlProjectIdentifer.getIdentifier());

        // process any additional source properties
        Property[] props = umlProjectIdentifer.getAdditionalSourceProperty();
        if (props != null) {
            for (Property prop : props) {
                String name = prop.getName();
                String value = prop.get_value();
                if (name.equals(SOURCE_PROPERTY_PUBLIC_ID)) {
                    prototype.setPublicID(Long.valueOf(value));
                } else if (name.equals(SOURCE_PROPERTY_LONG_NAME)) {
                    prototype.setLongName(value);
                } else if (name.equals(SOURCE_PROPERTY_GME_NAMESPACE)) {
                    prototype.setGmeNamespace(value);
                } else {
                    InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
                    FaultHelper helper = new FaultHelper(fault);
                    helper
                        .setDescription("The source property("
                            + name
                            + ") specified in the UMLProjectIdentifier is not supported.  View the ModelSourceMetadata for supported source property.");
                    throw (InvalidUMLProjectIndentifier) helper.getFault();
                }
            }
        }
        return prototype;
    }


    private ApplicationService getApplicationService(String sourceID) throws MMSGeneralException {
        ApplicationService appService = null;

        String url = this.sourceToURLMap.get(sourceID);
        if (url == null) {
            throw new MMSGeneralException("Unable to locate appropriate caDSR service for specified source ("
                + sourceID + ")");
        }
        try {
            appService = ApplicationServiceProvider.getApplicationServiceFromUrl(url);
        } catch (Exception e) {
            throw new MMSGeneralException("Problem loading caDSR ApplicationService", e);
        }
        return appService;
    }


    private SourceDescriptor getSourceDescriptorForUMLProjectIdentifier(UMLProjectIdentifer umlProjectIdentifer)
        throws InvalidUMLProjectIndentifier, MMSGeneralException {
        String sourceID = umlProjectIdentifer.getSourceIdentifier();

        boolean usingDefault = false;
        if (sourceID == null || sourceID.trim().equals("")) {
            sourceID = getModelSourceMetadata().getDefaultSourceIdentifier();
            usingDefault = true;
        }

        for (SourceDescriptor source : getModelSourceMetadata().getSupportedModelSources().getSource()) {
            if (source.getIdentifier().equals(sourceID)) {
                return source;
            }
        }

        if (usingDefault) {
            // if we got here the default was requested, but it's ID wasn't
            // found in the supported list, so the metadata is wrong
            throw new MMSGeneralException("Internal Error!  The default Model Source ("
                + getModelSourceMetadata().getDefaultSourceIdentifier()
                + ") was not found in the supported Model Sources");
        }

        return null;
    }


    private ApplicationService getApplicationServiceForUMLProjectIdentifier(UMLProjectIdentifer umlProjectIdentifer)
        throws InvalidUMLProjectIndentifier, MMSGeneralException {

        SourceDescriptor desc = getSourceDescriptorForUMLProjectIdentifier(umlProjectIdentifer);
        if (desc == null) {
            InvalidUMLProjectIndentifier fault = new InvalidUMLProjectIndentifier();
            FaultHelper helper = new FaultHelper(fault);
            helper
                .setDescription("The source ("
                    + umlProjectIdentifer.getSourceIdentifier()
                    + ") specified in the UMLProjectIdentifier is not supported.  View the ModelSourceMetadata for supported sources.");
            throw (InvalidUMLProjectIndentifier) helper.getFault();
        }

        return getApplicationService(desc.getIdentifier());
    }
}
