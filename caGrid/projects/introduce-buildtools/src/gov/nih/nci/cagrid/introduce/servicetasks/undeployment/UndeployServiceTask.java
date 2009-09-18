package gov.nih.nci.cagrid.introduce.servicetasks.undeployment;

import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Deployment;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Jar;
import gov.nih.nci.cagrid.introduce.servicetasks.deployment.DeploymentFileGeneratorTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;


public class UndeployServiceTask extends Task {

    private String webAppDeployLocation;

    private String webAppDeployLibLocation;

    private String webAppDeploySchemaLocation;

    private String webAppDeployEtcLocation;

    private String serviceDeploymentDirectoryName;

    private String servicePrefix;

    private String serviceName;

    private Deployment undeployService = null;

    private Map otherDeployedServices = new HashMap();


    public void execute() throws BuildException {
        super.execute();

        Properties properties = new Properties();
        properties.putAll(this.getProject().getProperties());

        // 1.get some basic properties about the deployment
        webAppDeployLocation = properties.getProperty("webapp.deploy.dir");
        webAppDeployLibLocation = properties.getProperty("webapp.deploy.lib.dir");
        webAppDeploySchemaLocation = properties.getProperty("webapp.deploy.schema.dir");
        webAppDeployEtcLocation = properties.getProperty("webapp.deploy.etc.dir");
        serviceDeploymentDirectoryName = properties.getProperty("service.deployment.dir.name");
        servicePrefix = properties.getProperty("service.deployment.prefix");
        serviceName = properties.getProperty("service.name");

        UndeployServiceHelper helper = new UndeployServiceHelper(webAppDeployLocation, webAppDeployLibLocation,
            webAppDeploySchemaLocation, webAppDeployEtcLocation, serviceDeploymentDirectoryName, servicePrefix,
            serviceName);
        try {
            helper.execute();
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }
}
