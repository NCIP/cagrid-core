package gov.nih.nci.cagrid.introduce.extensions.sdk.discovery;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author oster
 */
public class SDKExecutor {
    private static final String SDK_CONFIG_MODELS_DIR = "models";
    private static final String SDK_CONFIG_PROP_EXCLUDE_PACKAGE = "EXCLUDE_PACKAGE";
    private static final String SDK_CONFIG_PROP_INCLUDE_PACKAGE = "INCLUDE_PACKAGE";
    private static final String SDK_CONFIG_PROP_MODEL_FILENAME = "MODEL_FILE";
    private static final String SDK_CONFIG_PROP_NAMEPSACE = "NAMESPACE_PREFIX";
    private static final String SDK_CONFIG_PROP_PROJECT_NAME = "PROJECT_NAME";

    private static final String SDK_CONFIG_PROP_GENERATE_HIBERNATE_MAPPING = "GENERATE_HIBERNATE_MAPPING";
    private static final String SDK_CONFIG_PROP_GENERATE_BEANS = "GENERATE_BEANS";
    private static final String SDK_CONFIG_PROP_GENERATE_CASTOR_MAPPING = "GENERATE_CASTOR_MAPPING";
    private static final String SDK_CONFIG_PROP_GENERATE_XSD = "GENERATE_XSD";
    private static final String SDK_CONFIG_PROP_GENERATE_WSDD = "GENERATE_WSDD";

    private static final String SDK_CONFIG_FILE_DEPLOY_PROPERTIES = "conf/deploy.properties";

    private static final String ANT_TARGET = "codegen";

    protected static Log LOG = LogFactory.getLog(SDKExecutor.class.getName());


    // conf/deploy.properties:
    // ==========
    // MODEL_FILE = XMI file from user @MODEL_NAME@
    // NAMESPACE_PREFIX = the namespace prefix to use for generated schemas
    // INCLUDE_PACKAGE = package substring to include
    // EXCLUDE_PACKAGE = package substring to exclude

    public static SDKExecutionResult runSDK(File sdkDirectory, SDKGenerationInformation info,
        MultiEventProgressBar progress) throws SDKExecutionException {
        LOG.debug("SDK Directory:" + sdkDirectory.getAbsolutePath());
        LOG.debug("SDK Generation Info:" + info);

        File workDir = null;
        int currEventID = -1;
        try {
            currEventID = progress.startEvent("Creating SDK working area.");
            // create temporary working area
            workDir = File.createTempFile("SDKWorkArea", "");
            workDir.delete();
            workDir.mkdir();
            workDir.deleteOnExit();
            LOG.debug("SDK Work directory:" + workDir.getAbsolutePath());
            // copy sdk to it
            Utils.copyDirectory(sdkDirectory, workDir);
            progress.stopEvent(currEventID, "Creating working area.");
        } catch (IOException e) {
            String error = "Problem creating work area for SDK execution.";
            LOG.error(error, e);
            throw new SDKExecutionException(error, e);
        }

        // copy the model into the model dir
        File modelFile = new File(info.getXmiFile());
        File modelsDir = new File(workDir, SDK_CONFIG_MODELS_DIR);
        if (!(modelsDir.exists() && modelsDir.canWrite())) {
            String error = "Expected to find writable directory for models at:" + modelsDir.getAbsolutePath();
            LOG.error(error);
            throw new SDKExecutionException(error);
        }
        File destModelFile = null;
        try {
            destModelFile = new File(modelsDir, modelFile.getName());
            LOG.debug("Copying model file to: " + destModelFile);
            Utils.copyFile(modelFile, destModelFile);
        } catch (IOException e) {
            String error = "";
            if (destModelFile == null) {
                error = "Problem loading model file:" + modelFile.getName();
            } else {
                error = "Problem copying model file from: " + modelFile.getAbsolutePath() + " to:"
                    + destModelFile.getAbsolutePath();
            }
            LOG.error(error, e);
            throw new SDKExecutionException(error, e);

        }

        // template the SDK config with values from user
        LOG.debug("Applying configuration changes.");
        currEventID = progress.startEvent("Applying configuration changes.");
        applyConfigurationChanges(workDir, info);
        progress.stopEvent(currEventID, "Successfully configured.");

        // execute the SDK
        currEventID = progress.startEvent("Applying configuration changes.");
        String antTargets = ANT_TARGET;
        LOG.debug("Invoking ant targets (" + antTargets + ")");
        invokeAnt(antTargets, workDir);
        progress.stopEvent(currEventID, "Configuration Complete.");

        // create and validate the result
        SDKExecutionResult result = new SDKExecutionResult(workDir, info);
        LOG.debug("Validating results:" + result);
        result.validate();

        LOG.debug("Returning results.");
        return result;

    }


    private static void applyConfigurationChanges(File workDir, SDKGenerationInformation info)
        throws SDKExecutionException {
        applyDeployConfigurationChanges(workDir, info);
    }


    /**
     * @param workDir
     * @throws SDKExecutionException
     */
    private static void applyDeployConfigurationChanges(File workDir, SDKGenerationInformation info)
        throws SDKExecutionException {
        // conf/deploy.properties:
        // ==========
        File deployPropertiesFile = new File(workDir, SDK_CONFIG_FILE_DEPLOY_PROPERTIES);
        if (!(deployPropertiesFile.exists() && deployPropertiesFile.canRead())) {
            String error = "Expected readible properties file at location: " + deployPropertiesFile.getAbsolutePath();
            LOG.error(error);
            throw new SDKExecutionException(error);
        }
        Properties deployProperties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(deployPropertiesFile);
            deployProperties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            String error = "Problem loading properties file at location: " + deployPropertiesFile.getAbsolutePath();
            LOG.error(error, e);
            throw new SDKExecutionException(error, e);
        }

        // configure the options
        deployProperties.setProperty(SDK_CONFIG_PROP_GENERATE_BEANS, "false");
        deployProperties.setProperty(SDK_CONFIG_PROP_GENERATE_CASTOR_MAPPING, "false");
        deployProperties.setProperty(SDK_CONFIG_PROP_GENERATE_HIBERNATE_MAPPING, "false");
        deployProperties.setProperty(SDK_CONFIG_PROP_GENERATE_WSDD, "false");
        deployProperties.setProperty(SDK_CONFIG_PROP_GENERATE_XSD, "true");

        // configure the settings
        deployProperties.setProperty(SDK_CONFIG_PROP_PROJECT_NAME, info.getProjectName());
        deployProperties.setProperty(SDK_CONFIG_PROP_NAMEPSACE, info.getNamespacePrefix());
        File modelfile = new File(info.getXmiFile());
        deployProperties.setProperty(SDK_CONFIG_PROP_MODEL_FILENAME, modelfile.getName());
        deployProperties.setProperty(SDK_CONFIG_PROP_INCLUDE_PACKAGE, info.getPackageIncludes());
        deployProperties.setProperty(SDK_CONFIG_PROP_EXCLUDE_PACKAGE, info.getPackageExcludes());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(deployPropertiesFile);
            deployProperties.store(fileOutputStream, "Generated by:" + SDKExecutor.class.getCanonicalName());
            fileOutputStream.close();
            LOG.debug("Saving properties:" + deployProperties.toString());
        } catch (IOException e) {
            String error = "Problem saving edited properties file at location: "
                + deployPropertiesFile.getAbsolutePath();
            LOG.error(error, e);
            throw new SDKExecutionException(error, e);
        }
    }


    private static void invokeAnt(String targets, File sdkDirectory) throws SDKExecutionException {
        Process p;
        try {
            String cmd = AntTools.getAntCommand(targets, sdkDirectory.getPath());
            LOG.debug("Running ant command:" + cmd);
            p = CommonTools.createAndOutputProcess(cmd);
            p.waitFor();
        } catch (Exception e) {
            String error = "Problem with SDK invocation:" + e.getMessage();
            LOG.error(error, e);
            throw new SDKExecutionException(error, e);
        }

        if (p.exitValue() != 0) {
            String error = "SDK invocation exited abnormally with exit code:" + p.exitValue()
                + ".  Check error log for details.";
            LOG.error(error);
            throw new SDKExecutionException(error);
        }
    }

}
