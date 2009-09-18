package gov.nih.nci.cagrid.introduce.creator;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class SkeletonPostCreator extends Task {
    
    private static final Logger logger = Logger.getLogger(SkeletonPostCreator.class);

    public SkeletonPostCreator() {
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator
            + "log4j.properties");
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
            logger.error(be);
            throw be;
        }

        ServiceInformation serviceInfo = new ServiceInformation(introService, properties, baseDirectory);

        // run any extensions that need to be ran
        if (introService.getExtensions() != null && introService.getExtensions().getExtension() != null) {
            ExtensionType[] extensions = introService.getExtensions().getExtension();
            for (int i = 0; i < extensions.length; i++) {
                CreationExtensionPostProcessor pp = null;
                ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance().getServiceExtension(
                    extensions[i].getName());
                try {
                    pp = ExtensionTools.getCreationPostProcessor(extensions[i].getName());
                } catch (Exception e1) {
                    BuildException be = new BuildException(e1.getMessage());
                    be.setStackTrace(e1.getStackTrace());
                    logger.error(be);
                    throw be;
                }
                try {
                    if (pp != null) {
                        pp.postCreate(desc, serviceInfo);
                    }
                } catch (CreationExtensionException e) {
                    BuildException be = new BuildException(e.getMessage());
                    be.setStackTrace(e.getStackTrace());
                    logger.error(be);
                    throw be;
                }
            }
        }

        try {
            Utils.serializeDocument(baseDirectory + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
                introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        } catch (Exception e) {
            BuildException be = new BuildException(e.getMessage());
            be.setStackTrace(e.getStackTrace());
            logger.error(be);
            throw be;
        }
    }
}