package gov.nih.nci.cagrid.introduce.extensions.metadata.codegen;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extensions.metadata.common.MetadataExtensionHelper;
import gov.nih.nci.cagrid.introduce.extensions.metadata.constants.MetadataConstants;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataHostingResearchCenter;
import gov.nih.nci.cagrid.metadata.ServiceMetadataServiceDescription;
import gov.nih.nci.cagrid.metadata.common.Address;
import gov.nih.nci.cagrid.metadata.common.PointOfContact;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;
import gov.nih.nci.cagrid.metadata.common.ResearchCenterPointOfContactCollection;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.service.ContextProperty;
import gov.nih.nci.cagrid.metadata.service.Fault;
import gov.nih.nci.cagrid.metadata.service.InputParameter;
import gov.nih.nci.cagrid.metadata.service.Operation;
import gov.nih.nci.cagrid.metadata.service.OperationFaultCollection;
import gov.nih.nci.cagrid.metadata.service.OperationInputParameterCollection;
import gov.nih.nci.cagrid.metadata.service.Output;
import gov.nih.nci.cagrid.metadata.service.Service;
import gov.nih.nci.cagrid.metadata.service.ServiceContext;
import gov.nih.nci.cagrid.metadata.service.ServiceContextContextPropertyCollection;
import gov.nih.nci.cagrid.metadata.service.ServiceContextOperationCollection;
import gov.nih.nci.cagrid.metadata.service.ServicePointOfContactCollection;
import gov.nih.nci.cagrid.metadata.service.ServiceServiceContextCollection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mms.client.MetadataModelServiceClient;
import org.cagrid.mms.common.MetadataModelServiceConstants;
import org.cagrid.mms.common.MetadataModelServiceI;
import org.cagrid.mms.domain.NamespaceToProjectMapping;
import org.cagrid.mms.domain.UMLProjectIdentifer;


/**
 * Creates a metadata instance file. Only creates/modifies the Service portion,
 * will leave any existing other aspects (ResearchCenter) intact.
 * 
 * @author oster
 */
public class MetadataCodegenPostProcessor implements CodegenExtensionPostProcessor {
    // private static final String MAIN_RF_TYPE = "main";
    private static final String SEMANTIC_METADATA_DEFAULTS_DATA_SERVICE = "default-Service-SemanticMetadata-data.xml";
    private static final String SEMANTIC_METADATA_DEFAULTS_ANALYTICAL_SERVICE = "default-Service-SemanticMetadata-analytical.xml";

    protected static Log LOG = LogFactory.getLog(MetadataCodegenPostProcessor.class.getName());


    public void postCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CodegenExtensionException {
        MetadataExtensionHelper helper = new MetadataExtensionHelper(info);
        if (!helper.shouldCreateMetadata()) {
            LOG.error("Unable to locate Service Metadata resource property, skipping metadata instance creation.");
            return;
        }

        ServiceMetadata metadata = helper.getExistingServiceMetdata();
        // create a new model if need be, and initialize it
        if (metadata == null) {
            metadata = new ServiceMetadata();
        }
        initializeModel(metadata);

        // shell it without UML informaiton
        populateService(metadata.getServiceDescription().getService(), info);

        // try to annotate the metadata with cadsr extract
        try {
            MetadataModelServiceI mmsService = new MetadataModelServiceClient(getMMSURL());

            // extract project mappings from extension data on the
            // namespaces if they exist
            List<NamespaceToProjectMapping> mappings = new ArrayList<NamespaceToProjectMapping>();
            NamespacesType namespaces = info.getNamespaces();
            if (namespaces != null && namespaces.getNamespace() != null) {
                // walk the service's namespaces and extract any extension data
                for (NamespaceType ns : namespaces.getNamespace()) {
                    // if there are extensions
                    if (ns.getExtensions() != null && ns.getExtensions().getExtension() != null) {
                        // walk them and look for a UMLProjectIdentifier
                        for (ExtensionType ext : ns.getExtensions().getExtension()) {
                            if (ext.getName().equals(
                                MetadataModelServiceConstants.UML_PROJECT_IDENTIFIER_EXTENSION_NAME)
                                && ext.getVersion().equals(
                                    MetadataModelServiceConstants.UML_PROJECT_IDENTIFIER_EXTENSION_VERSION)) {

                                try {
                                    // if we find one, load it
                                    StringReader reader = new StringReader(ext.getExtensionData().get_any()[0]
                                        .getAsString());
                                    UMLProjectIdentifer projID = (UMLProjectIdentifer) Utils.deserializeObject(reader,
                                        UMLProjectIdentifer.class);

                                    // create a mapping from the current
                                    // namespace
                                    // to it
                                    NamespaceToProjectMapping nsMap = new NamespaceToProjectMapping();
                                    nsMap.setNamespaceURI(new org.apache.axis.types.URI(ns.getNamespace()));
                                    nsMap.setUMLProjectIdentifer(projID);

                                    // add it to a list of mappings
                                    mappings.add(nsMap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LOG.error("Problem using uml project annotation of namespace (" + ns.getNamespace()
                                        + "); ignoring the annotation.", e);
                                }
                            }
                        }
                    }
                }
            }

            // if we found mappings, pass the mappings to the
            NamespaceToProjectMapping[] mappingArr = new NamespaceToProjectMapping[mappings.size()];
            metadata=mmsService.annotateServiceMetadata(metadata, mappings.toArray(mappingArr));
        } catch (Exception e) {
            LOG.error("Problem annotating ServiceMetadata; using unannotated model.", e);
        }

        // serialize the model
        try {
            helper.writeServiceMetadata(metadata);
        } catch (Exception e) {
            throw new CodegenExtensionException("Error serializing metadata document.", e);
        }

    }


    private String getMMSURL() {
        try {
            return ConfigurationUtil.getGlobalExtensionProperty(MetadataConstants.MMS_URL_PROPERTY).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Problem loading MMS URL from preerences:" + e.getMessage(), e);
        }
        return null;
    }


    /**
     * Reads the service model, and builds the metadata
     * 
     * @param metadata
     * @param info
     * @throws CodegenExtensionException
     */
    private void populateService(Service service, ServiceInformation info) throws CodegenExtensionException {
        service.setDescription(info.getServiceDescriptor().getDescription());
        if (service.getDescription() == null) {
            service.setDescription("");
        }
        ServiceType services[] = info.getServiceDescriptor().getServices().getService();

        // we won't set a caDSR registration status

        // initialize the service's semantic metadata
        editServiceSemanticMetadata(service, info);

        ServiceContext[] newServContexts = new ServiceContext[services.length];

        // build a map based on context names (only for lookup)
        Map<String, ServiceContext> contextMap = new HashMap<String, ServiceContext>();
        ServiceContext[] existingServiceContexts = service.getServiceContextCollection().getServiceContext();
        if (existingServiceContexts != null) {
            for (ServiceContext context : existingServiceContexts) {
                contextMap.put(context.getName(), context);
            }
        }

        // create/edit a service context for each service
        for (int i = 0; i < services.length; i++) {
            ServiceType serv = services[i];

            // find the existing context
            ServiceContext currContext = contextMap.get(serv.getName());
            // create a new one if necessary
            if (currContext == null) {
                currContext = new ServiceContext();
            }

            // now edit it
            editServiceContext(serv, currContext);
            newServContexts[i] = currContext;

            // use the main service to set some higher level items
            if (serv.getResourceFrameworkOptions().getMain() != null) {
                service.setName(serv.getName());
                // set a version
                if (service.getVersion() == null || service.getVersion().trim().equals("")) {
                    // version is introduce's version... should be set elsewhere
                    service.setVersion(info.getServiceDescriptor().getIntroduceVersion());
                }
            }
        }

        // replace the old with the new
        service.getServiceContextCollection().setServiceContext(newServContexts);
    }


    /**
     * TODO: should we move this to the annotateServiceMetadata call?
     * 
     * @param service
     * @param info
     */
    private void editServiceSemanticMetadata(Service service, ServiceInformation info) {
        // determine if its a data service or analytical service
        // TODO: how are we classifying data services that add other operations?
        // (just data or data and analytical)
        boolean isDataService = isDataService(info);
        LOG.debug("Service " + (isDataService ? "is" : "is not") + " a data service.");
        // deserialize the codes based on the type
        InputStream inputStream = null;
        if (isDataService) {
            inputStream = ClassUtils.getResourceAsStream(getClass(), SEMANTIC_METADATA_DEFAULTS_DATA_SERVICE);
        } else {
            inputStream = ClassUtils.getResourceAsStream(getClass(), SEMANTIC_METADATA_DEFAULTS_ANALYTICAL_SERVICE);
        }

        try {
            // load the appropriate template
            InputStreamReader reader = new InputStreamReader(inputStream);
            ServiceMetadata metadata = MetadataUtils.deserializeServiceMetadata(reader);
            reader.close();

            // set the codes
            // TODO: should only insert of nothing is there?
            service.setSemanticMetadata(metadata.getServiceDescription().getService().getSemanticMetadata());
        } catch (Exception e) {
            LOG.error("Problem setting service semantic metdata; skipping!", e);
        }
    }


    /**
     * @param info
     * @return True if the service information is that of a Data Service, false
     *         otherwise
     */
    private boolean isDataService(ServiceInformation info) {
        ServicesType services = info.getServices();
        if (services != null && services.getService() != null) {
            ServiceType mainService = null;
            // find the main service
            for (int i = 0; i < services.getService().length; i++) {
                ServiceType serv = services.getService(i);
                if (serv.getResourceFrameworkOptions().getMain() != null) {
                    mainService = serv;
                    break;
                }
            }
            if (mainService != null) {
                MethodsType methods = mainService.getMethods();
                if (methods != null) {
                    MethodType[] methodArr = methods.getMethod();
                    if (methodArr != null) {
                        // walk its methods
                        for (MethodType method : methodArr) {
                            // if the method name is data service query
                            if (method.getName().equals(DataServiceConstants.QUERY_METHOD_NAME)) {
                                MethodTypeInputs inputs = method.getInputs();
                                if (inputs != null && inputs.getInput().length == 1) {
                                    // if it has the right input
                                    if (inputs.getInput(0).getQName().equals(DataServiceConstants.CQL_QUERY_QNAME)) {
                                        // if it has the right output
                                        if (method.getOutput() != null
                                            && method.getOutput().getQName().equals(
                                                DataServiceConstants.CQL_RESULT_COLLECTION_QNAME)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private void editServiceContext(ServiceType service, ServiceContext serviceContext) {
        serviceContext.setName(service.getName());

        // set a description (for xsd validation reasons)
        if (serviceContext.getDescription() == null || serviceContext.getDescription().trim().equals("")) {
            serviceContext.setDescription("");
        }

        // make context properties for RPs
        ResourcePropertyType[] resourceProperty = null;
        if (service.getResourcePropertiesList() != null) {
            resourceProperty = service.getResourcePropertiesList().getResourceProperty();
        }
        editServiceContextProperties(resourceProperty, serviceContext);

        // make operations
        editOperations(service.getMethods().getMethod(), serviceContext);

    }


    private void editOperations(MethodType methods[], ServiceContext serviceContext) {
        if (methods == null || methods.length == 0) {
            serviceContext.setOperationCollection(new ServiceContextOperationCollection());
            return;
        }

        Operation[] newOperations = new Operation[methods.length];

        if (serviceContext.getOperationCollection() == null) {
            serviceContext.setOperationCollection(new ServiceContextOperationCollection());
        }

        // build a map based on operation names (only for lookup)
        Map<String, Operation> opMap = new HashMap<String, Operation>();
        Operation[] existingOperations = serviceContext.getOperationCollection().getOperation();
        if (existingOperations != null) {
            for (Operation op : existingOperations) {
                opMap.put(op.getName(), op);
            }
        }

        for (int i = 0; i < methods.length; i++) {
            MethodType method = methods[i];
            // find the existing op
            Operation currOp = opMap.get(method.getName());
            // create a new one if necessary
            if (currOp == null) {
                currOp = new Operation();
            }

            // edit the operation
            currOp.setDescription(method.getDescription());
            editOperation(method, currOp);
            newOperations[i] = currOp;

        }
        serviceContext.getOperationCollection().setOperation(newOperations);

    }


    private void editOperation(MethodType method, Operation operation) {
        operation.setName(method.getName());
        // set a description (for xsd validation reasons)
        if (operation.getDescription() == null || operation.getDescription().trim().equals("")) {
            operation.setDescription("");
        }

        // OUTPUT
        MethodTypeOutput methOut = method.getOutput();
        if (methOut.getQName().toString().equals("void")) {
            operation.setOutput(null);
        } else {
            // we create a new one, because we don't currently make the UML
            // info, and can't know if its up to date or not
            Output output = new Output();
            output.setIsArray(methOut.isIsArray());
            // this is here for expansion, but we only support 1 dim arrays
            output.setDimensionality(1);
            output.setQName(methOut.getQName());
            operation.setOutput(output);
        }

        // FAULTS
        if (method.getExceptions() == null) {
            editFaults(null, operation);
        } else {
            editFaults(method.getExceptions().getException(), operation);
        }

        // INPUTS
        if (method.getInputs() == null) {
            editOperationInputs(null, operation);
        } else {
            editOperationInputs(method.getInputs().getInput(), operation);
        }

        // SEMANTIC METADATA
        if (operation.getSemanticMetadata() == null) {
            operation.setSemanticMetadata(new SemanticMetadata[]{});
        }

    }


    private void editOperationInputs(MethodTypeInputsInput[] inputs, Operation operation) {
        if (inputs == null || inputs.length == 0) {
            operation.setInputParameterCollection(new OperationInputParameterCollection());
            return;
        }

        InputParameter[] newInputs = new InputParameter[inputs.length];
        if (operation.getInputParameterCollection() == null) {
            operation.setInputParameterCollection(new OperationInputParameterCollection());
        }

        // build a map based on fault names (only for lookup)
        Map<String, InputParameter> inputMap = new HashMap<String, InputParameter>();
        InputParameter[] existingInputs = operation.getInputParameterCollection().getInputParameter();
        if (existingInputs != null) {
            for (InputParameter input : existingInputs) {
                inputMap.put(input.getName(), input);
            }
        }

        // for each input, build/edit the param
        for (int i = 0; i < inputs.length; i++) {
            MethodTypeInputsInput input = inputs[i];
            String name = input.getName();

            // find the existing
            InputParameter param = inputMap.get(name);
            // create a new one if necessary
            if (param == null) {
                param = new InputParameter();
            }
            param.setName(name);
            // we only support 1 dim arrays
            param.setDimensionality(1);
            param.setIndex(i);
            param.setIsArray(input.isIsArray());
            // param.setIsRequired(???)
            param.setQName(input.getQName());

            // clear this out, because we don't set it, and don't know if its
            // still valid
            param.setUMLClass(null);
            newInputs[i] = param;
        }

        operation.getInputParameterCollection().setInputParameter(newInputs);
    }


    private void editFaults(MethodTypeExceptionsException[] exceptions, Operation operation) {
        if (exceptions == null || exceptions.length == 0) {
            operation.setFaultCollection(new OperationFaultCollection());
            return;
        }

        if (operation.getFaultCollection() == null) {
            operation.setFaultCollection(new OperationFaultCollection());
        }
        Fault[] newFaults = new Fault[exceptions.length];

        // build a map based on fault names (only for lookup)
        Map<String, Fault> faultMap = new HashMap<String, Fault>();
        Fault[] existingFaults = operation.getFaultCollection().getFault();
        if (existingFaults != null) {
            for (Fault fault : existingFaults) {
                faultMap.put(fault.getName(), fault);
            }
        }

        // for each exception, build/edit the fault

        for (int i = 0; i < exceptions.length; i++) {
            MethodTypeExceptionsException exception = exceptions[i];
            String name = exception.getName();

            // find the existing context
            Fault currFault = faultMap.get(name);
            // create a new one if necessary
            if (currFault == null) {
                currFault = new Fault();
            }
            currFault.setName(name);
            currFault.setDescription(exception.getDescription());
            // set a description (for xsd validation reasons)
            if (currFault.getDescription() == null || currFault.getDescription().trim().equals("")) {
                currFault.setDescription("");
            }
            newFaults[i] = currFault;
        }

        operation.getFaultCollection().setFault(newFaults);
    }


    private void editServiceContextProperties(ResourcePropertyType[] resourcePropertys, ServiceContext serviceContext) {
        if (resourcePropertys == null || resourcePropertys.length == 0) {
            serviceContext.setContextPropertyCollection(new ServiceContextContextPropertyCollection());
            return;
        }

        ContextProperty[] newProps = new ContextProperty[resourcePropertys.length];

        if (serviceContext.getContextPropertyCollection() == null) {
            serviceContext.setContextPropertyCollection(new ServiceContextContextPropertyCollection());
        }

        // build a map based on rps names (only for lookup)
        Map<String, ContextProperty> propMap = new HashMap<String, ContextProperty>();
        ContextProperty[] existingProps = serviceContext.getContextPropertyCollection().getContextProperty();
        if (existingProps != null) {
            for (ContextProperty prop : existingProps) {
                propMap.put(prop.getName(), prop);
            }
        }

        // for each RP, build/edit the contextprop

        for (int i = 0; i < resourcePropertys.length; i++) {
            ResourcePropertyType rp = resourcePropertys[i];
            QName name = rp.getQName();

            // find the existing context
            ContextProperty currProp = propMap.get(name.toString());
            // create a new one if necessary
            if (currProp == null) {
                currProp = new ContextProperty();
            }
            currProp.setName(name.toString());
            // set a description (for xsd validation reasons)
            currProp.setDescription(rp.getDescription());
            if (currProp.getDescription() == null || currProp.getDescription().trim().equals("")) {
                currProp.setDescription("");
            }

            newProps[i] = currProp;

        }
        serviceContext.getContextPropertyCollection().setContextProperty(newProps);
    }


    /**
     * bootstrap the necessary fields as needed, to avoid null checks
     * everywhere.
     * 
     * @param metadata
     */
    private static void initializeModel(ServiceMetadata metadata) {
        // every model needs a service desc
        ServiceMetadataServiceDescription desc = metadata.getServiceDescription();
        if (desc == null) {
            desc = new ServiceMetadataServiceDescription();
            metadata.setServiceDescription(desc);
        }

        // every model needs a hosting center (container)
        ServiceMetadataHostingResearchCenter hostingResearchCenter = metadata.getHostingResearchCenter();
        if (hostingResearchCenter == null) {
            hostingResearchCenter = new ServiceMetadataHostingResearchCenter();
            ResearchCenter researchCenter = new ResearchCenter();
            Address address = new Address("", "", "", "", "", "");
            researchCenter.setAddress(address);
            researchCenter.setDisplayName("");
            researchCenter.setShortName("");
            ResearchCenterPointOfContactCollection pointOfContactCollection = new ResearchCenterPointOfContactCollection();
            PointOfContact[] pointOfContact = new PointOfContact[1];
            pointOfContact[0] = createEmptyPointOfContact();
            pointOfContactCollection.setPointOfContact(pointOfContact);
            researchCenter.setPointOfContactCollection(pointOfContactCollection);
            hostingResearchCenter.setResearchCenter(researchCenter);
            metadata.setHostingResearchCenter(hostingResearchCenter);
        }

        // every service desc needs a service
        Service serv = desc.getService();
        if (serv == null) {
            serv = new Service();
            desc.setService(serv);
        }

        // every service needs a context collection
        ServicePointOfContactCollection pointOfContactCollection = serv.getPointOfContactCollection();
        if (pointOfContactCollection == null) {
            pointOfContactCollection = new ServicePointOfContactCollection();
            PointOfContact[] pointOfContact = new PointOfContact[1];
            pointOfContact[0] = createEmptyPointOfContact();
            pointOfContactCollection.setPointOfContact(pointOfContact);
            serv.setPointOfContactCollection(pointOfContactCollection);
        }

        // every service needs a context coll
        ServiceServiceContextCollection contCol = serv.getServiceContextCollection();
        if (contCol == null) {
            contCol = new ServiceServiceContextCollection();
            serv.setServiceContextCollection(contCol);
        }
    }


    private static PointOfContact createEmptyPointOfContact() {
        return new PointOfContact("", "", "", "", "", "");
    }
}
