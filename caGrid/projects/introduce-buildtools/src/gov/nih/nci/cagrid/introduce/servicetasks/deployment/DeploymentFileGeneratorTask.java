package gov.nih.nci.cagrid.introduce.servicetasks.deployment;

import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.File;
import java.io.FileWriter;

import javax.xml.namespace.QName;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * DeploymentFileGeneratorTask
 *  * 
 * <note>This class used to serialize a bean to handle this, however that causes
 * problems when the code is executed out of the caGrid installer under
 * Windows XP sp 3.  For more details, see https://jira.citih.osumc.edu/browse/CAGRID-404</note>
 * 
 * @author David
 */
public class DeploymentFileGeneratorTask extends Task {

    public static final String DEPLOYMENT_PERSISTENCE_FILE = "introduceDeployment.xml";
    public static final QName DEPLOYMENT_PERSISTENCE_QNAME = new QName(
        "gme://gov.nih.nci.cagrid.introduce/1/Deployment", "Deployment");
    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    
    public void execute() throws BuildException {
        super.execute();
        
        // get the base directory
        String baseDir =  this.getProject().getBaseDir().getAbsolutePath();
        
        // create the Deployment element
        Namespace xsiNamespace = Namespace.getNamespace("xsi", XSI_NAMESPACE);
        Namespace depNamespace = Namespace.getNamespace("ns1", DEPLOYMENT_PERSISTENCE_QNAME.getNamespaceURI());
        Element deployment = new Element("Deployment");
        deployment.addNamespaceDeclaration(xsiNamespace);
        deployment.setNamespace(depNamespace);
        deployment.setAttribute("type", depNamespace.getPrefix() + ":Deployment", xsiNamespace);
        deployment.setAttribute("serviceName", getProject().getProperty("introduce.skeleton.service.name"));
        deployment.setAttribute("deploymentPrefix", getProject().getProperty("service.deployment.prefix"));
        deployment.setAttribute("serviceDeploymentDirName", getProject().getProperty("service.deployment.dir.name"));
        deployment.setAttribute("deployDateTime", String.valueOf(System.currentTimeMillis()));
        
        // get the list of jars to be deployed and add them
        File libDir = new File(getProject().getProperty("build.lib.dir"));
        String[] jars = libDir.list();
        
        // create the Jars element
        Element deployedJars = new Element("Jars");
        //deployedJars.setNamespace(depNamespace);
        deployedJars.setAttribute("type", depNamespace.getPrefix() + ":Jars", xsiNamespace);
        for (int i = 0; i < jars.length; i++) {
            String jarFile = jars[i];
            Element newJar = new Element("Jar");
            //newJar.setNamespace(depNamespace);
            newJar.setAttribute("type", depNamespace.getPrefix() + ":Jar", xsiNamespace);
            newJar.setAttribute("name", jarFile);
            newJar.setAttribute("location", ".");
            deployedJars.addContent(newJar);
        }
        
        // add the jars element to the deployment element
        deployment.addContent(deployedJars);

        // write it out
        FileWriter fw = null;
        try {
            File out = new File(baseDir, "tmp" + File.separator + "etc" + File.separator + DEPLOYMENT_PERSISTENCE_FILE);
            fw = new FileWriter(out);
            String xml = XMLUtilities.elementToString(deployment);
            xml = XMLUtilities.formatXML(xml);
            System.out.println(xml);
            fw.write(xml);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
}
