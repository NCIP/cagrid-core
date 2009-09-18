package gov.nih.nci.cagrid.introduce.servicetasks.deployment.validator;

import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.validator.DeploymentValidatorDescriptor;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.validator.ValidatorDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;


public class DeploymentValidatorTask extends Task {

    public static final String DEPLOYMENT_VALIDATOR_FILE = "deploymentValidator.xml";
    public static final QName DEPLOYMENT_VALIDATOR_QNAME = new QName(
        "gme://gov.nih.nci.cagrid.introduce/1/DeploymentValidator", "DeploymentValidatorDescriptor");


    public void execute() throws BuildException {
        super.execute();

        try {

            Properties properties = new Properties();
            properties.putAll(this.getProject().getProperties());

            String baseDir = this.getProject().getBaseDir().getAbsolutePath();

            File deploymentDescriptor = new File(baseDir + File.separator + "tools" + File.separator
                + DeploymentValidatorTask.DEPLOYMENT_VALIDATOR_FILE);
            if (!deploymentDescriptor.exists() || !deploymentDescriptor.canRead()) {
                throw new Exception(
                    "Service does not seem to be an Introduce generated service or file system permissions are preventing access.");
            }

            InputSource is = null;
            try {
                is = new InputSource(new FileInputStream(deploymentDescriptor));
            } catch (FileNotFoundException e) {
                throw new Exception("Connot find deployment validator descriptor: " + deploymentDescriptor, e);
            }

            DeploymentValidatorDescriptor deploymentValidatorDescriptor = null;
            try {
                deploymentValidatorDescriptor = (DeploymentValidatorDescriptor) ObjectDeserializer.deserialize(is,
                    DeploymentValidatorDescriptor.class);

            } catch (DeserializationException e) {
                throw new Exception("Cannot deserialize deployment validator descriptor: " + deploymentDescriptor, e);
            }

            if (deploymentValidatorDescriptor != null) {
                if (deploymentValidatorDescriptor != null
                    && deploymentValidatorDescriptor.getValidatorDescriptor() != null) {
                    for (int i = 0; i < deploymentValidatorDescriptor.getValidatorDescriptor().length; i++) {
                        ValidatorDescriptor desc = deploymentValidatorDescriptor.getValidatorDescriptor(i);
                        if (desc.getValidationClass() != null) {
                            Class clazz = Class.forName(desc.getValidationClass());
                            Constructor con = clazz.getConstructor(new Class[]{String.class});
                            DeploymentValidator validator = (DeploymentValidator) con
                                .newInstance(new Object[]{baseDir});
                            validator.validate();
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new BuildException(e.getMessage(), e);
        }

    }
}
