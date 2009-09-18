package org.cagrid.mms.service.impl.cadsr;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.service.CaDSRRegistration;
import gov.nih.nci.cagrid.metadata.service.InputParameter;
import gov.nih.nci.cagrid.metadata.service.Operation;
import gov.nih.nci.cagrid.metadata.service.Service;
import gov.nih.nci.cagrid.metadata.service.ServiceContext;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author oster
 */
public class ServiceMetadataAnnotator {
    protected static Log LOG = LogFactory.getLog(ServiceMetadataAnnotator.class.getName());
    private ApplicationService defaultcaDSR = null;

    private final Map<String, QualifiedProject> uri2ServiceMap;


    public ServiceMetadataAnnotator(Map<String, QualifiedProject> uri2ServiceMap, ApplicationService defaultCaDSR) {
        if (defaultCaDSR == null) {
            throw new IllegalArgumentException("Cannot supply a null default ApplicationService.");
        }
        this.uri2ServiceMap = uri2ServiceMap;
        this.defaultcaDSR = defaultCaDSR;
    }


    /**
     * Add caDSR information to model.
     * 
     * @param metadata
     * @throws CaDSRGeneralException
     */
    public void annotateServiceMetadata(ServiceMetadata metadata) throws CaDSRGeneralException {
        if (metadata == null || metadata.getServiceDescription() == null
            || metadata.getServiceDescription().getService() == null) {
            return;
        }

        Service service = metadata.getServiceDescription().getService();

        // TODO: how to set caDSR registration?
        CaDSRRegistration caDSRRegistration = service.getCaDSRRegistration();

        // TODO: set/edit service semantic metadata once service's are
        // registered in caDSR
        SemanticMetadata[] semanticMetadatas = service.getSemanticMetadata();

        if (service.getServiceContextCollection() == null
            || service.getServiceContextCollection().getServiceContext() == null) {
            return;
        }
        ServiceContext[] serviceContexts = service.getServiceContextCollection().getServiceContext();
        for (int i = 0; i < serviceContexts.length; i++) {
            annotateServiceContext(serviceContexts[i]);
        }

    }


    protected void annotateServiceContext(ServiceContext context) throws CaDSRGeneralException {
        if (context == null || context.getOperationCollection() == null
            || context.getOperationCollection().getOperation() == null) {
            return;
        }
        Operation[] operations = context.getOperationCollection().getOperation();
        for (int i = 0; i < operations.length; i++) {
            annotateOperation(operations[i]);
        }

    }


    protected void annotateOperation(Operation operation) throws CaDSRGeneralException {
        if (operation == null) {
            return;
        }

        // TODO: set/edit operation semantic metadata once services are
        // registered in caDSR
        SemanticMetadata[] semanticMetadatas = operation.getSemanticMetadata();

        // process input
        if (operation.getInputParameterCollection() != null
            && operation.getInputParameterCollection().getInputParameter() != null) {
            InputParameter[] inputParameters = operation.getInputParameterCollection().getInputParameter();
            for (int i = 0; i < inputParameters.length; i++) {
                InputParameter in = inputParameters[i];
                QName qname = in.getQName();
                UMLClass uml = getUMLClassForQName(qname);
                if (uml != null) {
                    LOG.debug("Successfully processed:" + qname);
                    in.setUMLClass(uml);
                }

            }
        }

        // process output
        if (operation.getOutput() != null) {
            QName qname = operation.getOutput().getQName();
            UMLClass uml = getUMLClassForQName(qname);
            if (uml != null) {
                LOG.debug("Successfully processed:" + qname);
                operation.getOutput().setUMLClass(uml);
            }
        }
    }


    /**
     * @param qname
     * @return The UML Class matching the QName
     * @throws CaDSRGeneralException
     */
    protected UMLClass getUMLClassForQName(QName qname) throws CaDSRGeneralException {
        // look up the UMLClassMetadata we are looking for, based on the QName
        UMLClassMetadata classMetadata = getUMLClassMetadataForQName(qname);
        if (classMetadata == null) {
            return null;
        }

        UMLClass result = null;
        try {
            String shortName = classMetadata.getProject().getShortName();
            String version = classMetadata.getProject().getVersion();

            ApplicationService cadsr = defaultcaDSR;
            QualifiedProject proj = uri2ServiceMap.get(qname.getNamespaceURI());
            if (proj != null) {
                cadsr = proj.getSourceAppServ();
            }

            result = CaDSRUtils.convertClassToUMLClass(cadsr, shortName, version, classMetadata);
        } catch (ApplicationException e) {
            LOG.error("Problem converting class to metadata", e);
        }
        return result;
    }


    /**
     * NOTE: we used to qualify the Project with the ClassificationScheme
     * Context name, but there's not really a way to get that information now
     * (as Project has a unidirectional assoc to ClassificationScheme, so we
     * can't use the dataservice to query for the CS of a given Project); now
     * one would need to supply the publicID of the Project of interest if there
     * were multiple contexts using the same project name and version
     * 
     * @param qname
     * @return The UMLClassMetadata matching the qname
     * @throws CaDSRGeneralException
     */
    protected UMLClassMetadata getUMLClassMetadataForQName(QName qname) throws CaDSRGeneralException {

        ApplicationService cadsr = null;
        Project projPrototype = null;

        // if there are user supplied mappings, we need to try to use the
        // applicationservice and project identifiers from those
        if (this.uri2ServiceMap != null) {
            LOG.debug("Looking for suitable namespace mapping in supplied map.");
            QualifiedProject proj = uri2ServiceMap.get(qname.getNamespaceURI());
            // the user supplied a project to be used for this namespace, so get
            // the appserv from it, as well as the project prototype
            if (proj != null) {
                LOG.debug("Using supplied Project (" + proj.getProjectPrototype().getShortName() + ") version ("
                    + proj.getProjectPrototype().getVersion() + ") for Qname (" + qname + ").");
                cadsr = proj.getSourceAppServ();
                projPrototype = proj.getProjectPrototype();
            }
        }

        // we never found a suitable mapping, so use default
        if (cadsr == null) {
            LOG.debug("No suitable namespace mapping found; using default ApplicationService.");
            cadsr = this.defaultcaDSR;
        }

        // create a prototype class
        UMLClassMetadata prototype = new UMLClassMetadata();
        // the Project qualifier of the class may have been set above using the
        // supplied mappings, but if not, the QName is expected to be uniquely
        // used by a single Class
        if (projPrototype != null) {
            prototype.setProject(projPrototype);
        } else {
            LOG.debug("No suitable namespace to Project mapping found; issuing a non-project-qualified query.");
        }
        prototype.setGmeNamespace(qname.getNamespaceURI());
        prototype.setGmeXMLElement(qname.getLocalPart());

        List rList = null;
        try {
            rList = cadsr.search(UMLClassMetadata.class, prototype);
        } catch (ApplicationException e) {
            LOG.error(
                "Unable to locate UMLClassMetadata for QName (" + qname + "); skipping because:" + e.getMessage(), e);
            return null;
        }

        if (rList == null || rList.size() == 0) {
            LOG.error("Unable to locate UMLClassMetadata for QName (" + qname
                + "); skipping because no results were returned from ApplicationService.");
            return null;
        }

        if (rList.size() > 1) {
            LOG.info("Processing of UMLClassMetadata for QName (" + qname
                + ") returned more than 1 result, using first.");
        }

        return (UMLClassMetadata) rList.get(0);
    }


    public static void main(String[] args) {
        try {
            JFrame f = new JFrame();
            f.setVisible(true);

            JFileChooser fc = new JFileChooser(".");
            fc.showOpenDialog(f);

            File selectedFile = fc.getSelectedFile();
            ServiceMetadata model = MetadataUtils.deserializeServiceMetadata(new FileReader(selectedFile));
            ApplicationService appService = ApplicationServiceProvider
                .getApplicationServiceFromUrl("http://cadsrapi-prod2.nci.nih.gov/cadsrapi40/");
            ServiceMetadataAnnotator anno = new ServiceMetadataAnnotator(null, appService);
            anno.annotateServiceMetadata(model);

            File result = new File(".", selectedFile.getName() + "_annotated");
            MetadataUtils.serializeServiceMetadata(model, new FileWriter(result));
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}