package gov.nih.nci.cagrid.introduce.servicetasks.deployment;

import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Deployment;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Jar;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.Jars;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.globus.wsrf.encoding.ObjectSerializer;


public class DeploymentFileGeneratorTask extends Task {

    public static final String DEPLOYMENT_PERSISTENCE_FILE = "introduceDeployment.xml";
    public static final QName DEPLOYMENT_PERSISTENCE_QNAME = new QName(
        "gme://gov.nih.nci.cagrid.introduce/1/Deployment", "Deployment");


    public void execute() throws BuildException {
        super.execute();
        

        Properties properties = new Properties();
        properties.putAll(this.getProject().getProperties());
        
        String baseDir =  this.getProject().getBaseDir().getAbsolutePath();

        Deployment deployment = new Deployment();
        deployment.setServiceName(properties.getProperty("introduce.skeleton.service.name"));
        deployment.setDeploymentPrefix(properties.getProperty("service.deployment.prefix"));
        deployment.setServiceDeploymentDirName(properties.getProperty("service.deployment.dir.name"));
        deployment.setDeployDateTime(String.valueOf(System.currentTimeMillis()));
        
        // get the list of jars to be deployed and add them
        File libDir = new File(properties.getProperty("build.lib.dir"));
        String[] jars = libDir.list();

        Jars deployedJars = new Jars();

        Jar[] jarArr = new Jar[jars.length];
        for (int i = 0; i < jars.length; i++) {
            String jarFile = jars[i];
            Jar newJar = new Jar();
            newJar.setName(jarFile);
            newJar.setLocation(".");
            jarArr[i] = newJar;
        }
        deployedJars.setJar(jarArr);
        deployment.setJars(deployedJars);
        

        FileWriter fw = null;
        try {
            fw = new FileWriter(baseDir + File.separator +  "tmp" + File.separator + "etc" + File.separator + DEPLOYMENT_PERSISTENCE_FILE);
            ObjectSerializer.serialize(fw, deployment, DEPLOYMENT_PERSISTENCE_QNAME );
            fw.close();
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
