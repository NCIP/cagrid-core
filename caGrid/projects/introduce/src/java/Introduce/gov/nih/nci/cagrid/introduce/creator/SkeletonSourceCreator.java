package gov.nih.nci.cagrid.introduce.creator;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.services.security.info.AuthorizationTemplateInfoHolder;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.client.ClientConfigTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceITemplate;
import gov.nih.nci.cagrid.introduce.templates.service.ServiceImplBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.ServiceImplTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceAuthorizationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceConfigurationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceProviderImplTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ConfigurationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceHomeTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.SingletonResourceHomeTemplate;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.log4j.Logger;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SkeletonSourceCreator {
    private static final Logger logger = Logger.getLogger(SkeletonSourceCreator.class);

    public SkeletonSourceCreator() {
    }


    public void createSkeleton(File baseDirectory, ServiceInformation info, ServiceType service) throws Exception {
        logger.info("Creating new source code in : " + baseDirectory.getAbsolutePath() + File.separator + "src");

        File srcDir = new File(baseDirectory.getAbsolutePath() + File.separator + "src");
        srcDir.mkdir();

        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)).mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "client").mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "common").mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "service").mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "service" + File.separator + "globus").mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "service" + File.separator + "globus" + File.separator + "resource").mkdirs();
        new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service) + File.separator
            + "service" + File.separator + "globus" + File.separator + "resource").mkdirs();

        ServiceClientTemplate clientT = new ServiceClientTemplate();
        String clientS = clientT.generate(new SpecificServiceInformation(info, service));
        File clientF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "client" + File.separator + service.getName() + "Client.java");

        FileWriter clientFW = new FileWriter(clientF);
        clientFW.write(clientS);
        clientFW.close();
        
        ServiceClientBaseTemplate clientBaseT = new ServiceClientBaseTemplate();
        String clientBaseS = clientBaseT.generate(new SpecificServiceInformation(info, service));
        File clientBaseF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "client" + File.separator + service.getName() + "ClientBase.java");

        FileWriter clientBaseFW = new FileWriter(clientBaseF);
        clientBaseFW.write(clientBaseS);
        clientBaseFW.close();

        ClientConfigTemplate clientConfigT = new ClientConfigTemplate();
        String clientConfigS = clientConfigT.generate(new SpecificServiceInformation(info, service));
        File clientConfigF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "client" + File.separator + "client-config.wsdd");
        FileWriter clientConfigFW = new FileWriter(clientConfigF);
        clientConfigFW.write(clientConfigS);
        clientConfigFW.close();

        ServiceITemplate iT = new ServiceITemplate();
        String iS = iT.generate(new SpecificServiceInformation(info, service));
        File iF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "common" + File.separator + service.getName() + "I.java");

        FileWriter iFW = new FileWriter(iF);
        iFW.write(iS);
        iFW.close();

        ServiceImplBaseTemplate implBaseT = new ServiceImplBaseTemplate();
        String implBaseS = implBaseT.generate(new SpecificServiceInformation(info, service));
        File implBaseF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "service" + File.separator + service.getName() + "ImplBase.java");

        FileWriter implBaseFW = new FileWriter(implBaseF);
        implBaseFW.write(implBaseS);
        implBaseFW.close();

        ServiceImplTemplate implT = new ServiceImplTemplate();
        String implS = implT.generate(new SpecificServiceInformation(info, service));
        File implF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "service" + File.separator + service.getName() + "Impl.java");

        FileWriter implFW = new FileWriter(implF);
        implFW.write(implS);
        implFW.close();

        ServiceProviderImplTemplate providerImplT = new ServiceProviderImplTemplate();
        String providerImplS = providerImplT.generate(new SpecificServiceInformation(info, service));
        File providerImplF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "service" + File.separator + "globus" + File.separator + service.getName()
            + "ProviderImpl.java");

        FileWriter providerImplFW = new FileWriter(providerImplF);
        providerImplFW.write(providerImplS);
        providerImplFW.close();

        AuthorizationTemplateInfoHolder holder = new AuthorizationTemplateInfoHolder(new HashMap<String, String>(), new SpecificServiceInformation(info,service));
        ServiceAuthorizationTemplate authorizationT = new ServiceAuthorizationTemplate();
        String authorizationS = authorizationT.generate(holder);
        File authorizationF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
            + File.separator + "service" + File.separator + "globus" + File.separator + service.getName()
            + "Authorization.java");

        FileWriter authorizationFW = new FileWriter(authorizationF);
        authorizationFW.write(authorizationS);
        authorizationFW.close();

        if (service.getResourceFrameworkOptions().getMain()!=null) {
            ServiceConfigurationTemplate serviceConfT = new ServiceConfigurationTemplate();
            String serviceConfS = serviceConfT.generate(new SpecificServiceInformation(info, service));
            File serviceConfF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
                + File.separator + "service" + File.separator + service.getName() + "Configuration.java");
            FileWriter serviceConfFW = new FileWriter(serviceConfF);
            serviceConfFW.write(serviceConfS);
            serviceConfFW.close();
        }

        if (service.getResourceFrameworkOptions().getCustom()==null) {

            ConfigurationTemplate metadataConfigurationT = new ConfigurationTemplate();
            String metadataConfigurationS = metadataConfigurationT.generate(new SpecificServiceInformation(info,
                service));
            File metadataConfigurationF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                + File.separator + "resource" + File.separator + service.getName() + "ResourceConfiguration.java");

            FileWriter metadataConfigurationFW = new FileWriter(metadataConfigurationF);
            metadataConfigurationFW.write(metadataConfigurationS);
            metadataConfigurationFW.close();

            ServiceConstantsTemplate resourceContanstsT = new ServiceConstantsTemplate();
            String resourceContanstsS = resourceContanstsT.generate(new SpecificServiceInformation(info, service));
            File resourceContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName() + "Constants.java");

            FileWriter resourceContanstsFW = new FileWriter(resourceContanstsF);
            resourceContanstsFW.write(resourceContanstsS);
            resourceContanstsFW.close();
            
            ServiceConstantsBaseTemplate resourcebContanstsT = new ServiceConstantsBaseTemplate();
            String resourcebContanstsS = resourcebContanstsT.generate(new SpecificServiceInformation(info, service));
            File resourcebContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName() + "ConstantsBase.java");

            FileWriter resourcebContanstsFW = new FileWriter(resourcebContanstsF);
            resourcebContanstsFW.write(resourcebContanstsS);
            resourcebContanstsFW.close();

            ResourceBaseTemplate baseResourceBaseT = new ResourceBaseTemplate();
            String baseResourceBaseS = baseResourceBaseT.generate(new SpecificServiceInformation(info, service));
            File baseResourceBaseF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                + File.separator + "resource" + File.separator + service.getName() + "ResourceBase.java");

            FileWriter baseResourceBaseFW = new FileWriter(baseResourceBaseF);
            baseResourceBaseFW.write(baseResourceBaseS);
            baseResourceBaseFW.close();

            ResourceTemplate baseResourceT = new ResourceTemplate();
            String baseResourceS = baseResourceT.generate(new SpecificServiceInformation(info, service));
            File baseResourceF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                + File.separator + "resource" + File.separator + service.getName() + "Resource.java");

            FileWriter baseResourceFW = new FileWriter(baseResourceF);
            baseResourceFW.write(baseResourceS);
            baseResourceFW.close();

            if (service.getResourceFrameworkOptions().getSingleton()!=null) {
                SingletonResourceHomeTemplate baseResourceHomeT = new SingletonResourceHomeTemplate();
                String baseResourceHomeS = baseResourceHomeT.generate(new SpecificServiceInformation(info, service));
                File baseResourceHomeF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceHome.java");

                FileWriter baseResourceHomeFW = new FileWriter(baseResourceHomeF);
                baseResourceHomeFW.write(baseResourceHomeS);
                baseResourceHomeFW.close();

            } else {

                ResourceHomeTemplate baseResourceHomeT = new ResourceHomeTemplate();
                String baseResourceHomeS = baseResourceHomeT.generate(new SpecificServiceInformation(info, service));
                File baseResourceHomeF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceHome.java");

                FileWriter baseResourceHomeFW = new FileWriter(baseResourceHomeF);
                baseResourceHomeFW.write(baseResourceHomeS);
                baseResourceHomeFW.close();

            }
        }

    }

}