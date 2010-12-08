package gov.nih.nci.cagrid.introduce.creator;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.service.Identifiable;
import gov.nih.nci.cagrid.introduce.beans.service.Lifetime;
import gov.nih.nci.cagrid.introduce.beans.service.Main;
import gov.nih.nci.cagrid.introduce.beans.service.Notification;
import gov.nih.nci.cagrid.introduce.beans.service.Persistent;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ResourcePropertyManagement;
import gov.nih.nci.cagrid.introduce.beans.service.Secure;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.Singleton;
import gov.nih.nci.cagrid.introduce.codegen.provider.ProviderTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SkeletonCreator extends Task {

    private static Log logger = LogFactory.getLog(SkeletonCreator.class.getName());


    public SkeletonCreator() {
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "log4j.properties");

    }


    public void execute() throws BuildException {
        super.execute();

        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        Properties properties = new Properties();
        properties.putAll(this.getProject().getProperties());

        File baseDirectory = new File(properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR));

        ServiceDescription introService = null;
        try {
            introService = (ServiceDescription) Utils.deserializeDocument(baseDirectory + File.separator
                + IntroduceConstants.INTRODUCE_XML_FILE, ServiceDescription.class);
        } catch (Exception e1) {
            BuildException be = new BuildException(e1.getMessage());
            be.setStackTrace(e1.getStackTrace());
            logger.error("Deserialization Error",be);
            throw be;
        }

        // need to add the base service....
        ServiceType serviceType = new ServiceType();
        serviceType.setName(properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        serviceType.setNamespace(properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_NAMESPACE_DOMAIN));
        serviceType.setPackageName(properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_PACKAGE));

        serviceType.setResourceFrameworkOptions(new ResourceFrameworkOptions());
        // for each resource propertyOption set it on the service
        String resourceOptionsList = properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_RESOURCE_OPTIONS);
        StringTokenizer strtok = new StringTokenizer(resourceOptionsList, ",", false);
        while (strtok.hasMoreElements()) {
            String option = strtok.nextToken();
            if (option.equals(IntroduceConstants.INTRODUCE_MAIN_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setMain(new Main());
            } else if (option.equals(IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setSingleton(new Singleton());
            } else if (option.equals(IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setIdentifiable(new Identifiable());
            } else if (option.equals(IntroduceConstants.INTRODUCE_LIFETIME_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setLifetime(new Lifetime());
            } else if (option.equals(IntroduceConstants.INTRODUCE_PERSISTENT_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setPersistent(new Persistent());
            } else if (option.equals(IntroduceConstants.INTRODUCE_NOTIFICATION_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setNotification(new Notification());
            } else if (option.equals(IntroduceConstants.INTRODUCE_SECURE_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setSecure(new Secure());
            } else if (option.equals(IntroduceConstants.INTRODUCE_RESOURCEPROPETIES_RESOURCE)) {
                serviceType.getResourceFrameworkOptions().setResourcePropertyManagement(
                    new ResourcePropertyManagement());
            }
        }

        // add new service to the services
        // add new method to array in bean
        // this seems to be a wierd way be adding things....
        ServiceType[] newServices;
        int newLength = 0;
        if (introService.getServices() != null && introService.getServices().getService() != null) {
            newLength = introService.getServices().getService().length + 1;
            newServices = new ServiceType[newLength];
            System.arraycopy(introService.getServices().getService(), 0, newServices, 0, introService.getServices()
                .getService().length);
        } else {
            newLength = 1;
            newServices = new ServiceType[newLength];
        }
        newServices[newLength - 1] = serviceType;
        introService.getServices().setService(newServices);

        // write the modified document back out....
        try {
            Utils.serializeDocument(baseDirectory + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
                introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        } catch (Exception e1) {
            BuildException be = new BuildException(e1.getMessage());
            be.setStackTrace(e1.getStackTrace());
            logger.error("Serialization Error", be);
            throw be;
        }

        // for each extension in the properties make sure to add the xml to the
        // introduce model for them to use.....
        String extensionsList = properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
        strtok = new StringTokenizer(extensionsList, ",", false);
        ExtensionType[] types = new ExtensionType[strtok.countTokens()];
        int count = 0;
        while (strtok.hasMoreTokens()) {
            String token = strtok.nextToken();
            ExtensionDescription desc = ExtensionsLoader.getInstance().getExtension(token);
            if (desc != null) {
                ExtensionType type = new ExtensionType();
                type.setName(token);
                type.setVersion(desc.getVersion());
                types[count++] = type;
            } else {
                System.err.println("Extension was requested but does not exist: "
                    + token);
                return;
            }
        }
        ExtensionsType exts = new ExtensionsType();
        exts.setExtension(types);
        introService.setExtensions(exts);

        String service = properties.getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        if (!service.matches("[A-Z]++[A-Za-z0-9\\_\\$]*")) {
            System.err.println("Service Name can only contain [A-Z]++[A-Za-z0-9\\_\\$]*");
            return;
        }
        if (service.substring(0, 1).toLowerCase().equals(service.substring(0, 1))) {
            System.err.println("Service Name cannnot start with lower case letters.");
            return;
        }

        // create the dirs to the basedir if needed
        baseDirectory.mkdirs();

        ServiceInformation info = new ServiceInformation(introService, properties, baseDirectory);

        SkeletonBaseCreator sbc = new SkeletonBaseCreator();
        SkeletonSourceCreator ssc = new SkeletonSourceCreator();
        SkeletonSchemaCreator sscc = new SkeletonSchemaCreator();
        SkeletonEtcCreator sec = new SkeletonEtcCreator();
        SkeletonDocsCreator sdc = new SkeletonDocsCreator();
        SkeletonSecurityOperationProviderCreator ssopc = new SkeletonSecurityOperationProviderCreator();

        // add the providers that might be needed for particular resource
        // options
        if (serviceType.getResourceFrameworkOptions().getLifetime() != null) {
            ProviderTools.addLifetimeResourceProvider(info.getServices().getService(0), info);
        }
        if (serviceType.getResourceFrameworkOptions().getResourcePropertyManagement() != null) {
            ProviderTools
                .addResourcePropertiesManagementResourceFrameworkOption(info.getServices().getService(0), info);
        }
        if (serviceType.getResourceFrameworkOptions().getNotification() != null) {
            ProviderTools.addSubscribeResourceProvider(info.getServices().getService(0), info);
        }

        // Generate the source
        try {
            if (info.getServices() != null && info.getServices().getService() != null) {
                for (int i = 0; i < info.getServices().getService().length; i++) {
                    ssc.createSkeleton(baseDirectory, info, info.getServices().getService(i));
                    sscc.createSkeleton(baseDirectory, info, info.getServices().getService(i));
                    ssopc.createSkeleton(new SpecificServiceInformation(info, info.getServices().getService(i)));
                    sec.createSkeleton(info, info.getServices().getService(i));
                }
            }

            sdc.createSkeleton(info);
            sbc.createSkeleton(info);
        } catch (Exception e) {
            BuildException be = new BuildException(e.getMessage());
            be.setStackTrace(e.getStackTrace());
            logger.error("Template Error",be);
            throw be;
        }

        try {
            Utils.serializeDocument(baseDirectory + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
                introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        } catch (Exception e) {
            BuildException be = new BuildException(e.getMessage());
            be.setStackTrace(e.getStackTrace());
            logger.error("Serialization Error", be);
            throw be;
        }

        // process the extensions
    }
}