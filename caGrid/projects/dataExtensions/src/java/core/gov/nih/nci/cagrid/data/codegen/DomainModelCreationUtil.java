package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.mms.client.MetadataModelServiceClient;
import org.cagrid.mms.common.MetadataModelServiceI;
import org.cagrid.mms.domain.UMLProjectIdentifer;


/**
 * DomainModelCreationUtil
 * Utility to create a domain model given the ModelInformation
 * kept in the service's extension data
 * 
 * @author David Ervin
 * @created Apr 2, 2007 1:35:01 PM
 * @version $Id: DomainModelCreationUtil.java,v 1.3 2007/12/18 19:10:26 dervin
 *          Exp $
 */
public class DomainModelCreationUtil {

    private static final Log LOG = LogFactory.getLog(DomainModelCreationUtil.class);


    private DomainModelCreationUtil() {
        // no instantiation of this class, all methods are static
    }


    public static DomainModel createDomainModel(ModelInformation modelInfo) 
        throws MalformedURIException, RemoteException, Exception {
        // get a handle to the MMS client
        PropertiesProperty mmsUrlProp = null;
        try {
            mmsUrlProp = ConfigurationUtil.getGlobalExtensionProperty(DataServiceConstants.MMS_URL);
        } catch (Exception ex) {
            LOG.error("Unable to obtain the MMS url", ex);
            throw ex;
        }
        String mmsUrl = mmsUrlProp.getValue();
        LOG.info("Initializing MMS client (URL = " + mmsUrl + ")");
        MetadataModelServiceI mmsClient = new MetadataModelServiceClient(mmsUrl);

        // grab the prototype project
        UMLProjectIdentifer proj = modelInfo.getUMLProjectIdentifer();

        // Set of fully qualified class names
        Set<String> selectedClasses = new HashSet<String>();

        // Set of targetable class names
        Set<String> targetableClasses = new HashSet<String>();

        // walk through the selected packages
        if (modelInfo.getModelPackage() != null) {
            for (ModelPackage packageInfo : modelInfo.getModelPackage()) {
                String packName = packageInfo.getPackageName();
                // get selected classes from the package
                if (packageInfo.getModelClass() != null) {
                    for (ModelClass currentClass : packageInfo.getModelClass()) {
                        String fullClassName = packName + "." + currentClass.getShortClassName();
                        selectedClasses.add(fullClassName);
                        if (currentClass.isTargetable()) {
                            targetableClasses.add(fullClassName);
                        }                    
                    }
                } else {
                    LOG.warn("Empty package found in model: " + packName);
                }
            }
        }
        
        // build the domain model
        String buildingMessage = "Contacting MMS to build domain model.  This might take a while...";
        LOG.info(buildingMessage);
        System.out.println(buildingMessage);

        String classNames[] = new String[selectedClasses.size()];
        selectedClasses.toArray(classNames);
        DomainModel model = mmsClient.generateDomainModelForClasses(proj, classNames);

        LOG.info("Setting targetability in the domain model");
        System.out.println("Setting targetability in the domain model");
        UMLClass[] exposedClasses = model.getExposedUMLClassCollection().getUMLClass();
        for (int i = 0; exposedClasses != null && i < exposedClasses.length; i++) {
            String fullClassName = exposedClasses[i].getPackageName() + "." + exposedClasses[i].getClassName();
            exposedClasses[i].setAllowableAsTarget(targetableClasses.contains(fullClassName));
        }
        return model;
    }
}
