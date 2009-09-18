package gov.nih.nci.cagrid.introduce.codegen.services.resources;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceConfigurationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ConfigurationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceBaseTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * SyncMethodsOnDeployment
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncResource extends SyncTool {

    private File srcDir;

    private ServiceType service;


    public SyncResource(File baseDirectory, ServiceInformation info, ServiceType service) {
        super(baseDirectory, info);
        srcDir = new File(baseDirectory.getAbsolutePath() + File.separator + "src");
        this.service = service;
    }


    public void sync() throws SynchronizationException {
        try {
            
            if (service.getResourceFrameworkOptions().getMain()!=null) {

                ServiceConfigurationTemplate serviceConfT = new ServiceConfigurationTemplate();
                String serviceConfS = serviceConfT.generate(new SpecificServiceInformation(getServiceInformation(),
                    service));
                File serviceConfF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator
                    + service.getName() + "Configuration.java");
                FileWriter serviceConfFW = new FileWriter(serviceConfF);
                serviceConfFW.write(serviceConfS);
                serviceConfFW.close();

            }

            if (service.getResourceFrameworkOptions().getCustom()==null) {

                ConfigurationTemplate metadataConfigurationT = new ConfigurationTemplate();
                String metadataConfigurationS = metadataConfigurationT.generate(new SpecificServiceInformation(
                    getServiceInformation(), service));
                File metadataConfigurationF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceConfiguration.java");

                FileWriter metadataConfigurationFW = new FileWriter(metadataConfigurationF);
                metadataConfigurationFW.write(metadataConfigurationS);
                metadataConfigurationFW.close();

                ServiceConstantsBaseTemplate resourceContanstsT = new ServiceConstantsBaseTemplate();
                String resourceContanstsS = resourceContanstsT.generate(new SpecificServiceInformation(
                    getServiceInformation(), service));
                File resourceContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName() +"ConstantsBase.java");

                FileWriter resourceContanstsFW = new FileWriter(resourceContanstsF);
                resourceContanstsFW.write(resourceContanstsS);
                resourceContanstsFW.close();

                ResourceBaseTemplate baseResourceBaseT = new ResourceBaseTemplate();
                String baseResourceBaseS = baseResourceBaseT.generate(new SpecificServiceInformation(
                    getServiceInformation(), service));
                File baseResourceBaseF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceBase.java");

                FileWriter baseResourceBaseFW = new FileWriter(baseResourceBaseF);
                baseResourceBaseFW.write(baseResourceBaseS);
                baseResourceBaseFW.close();

            }
        } catch (IOException e) {
            throw new SynchronizationException("Error writing file:" + e.getMessage(), e);
        }
    }

}
