package gov.nih.nci.cagrid.introduce.servicetasks.axis;

import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class FixSoapBindingStub extends Task {

    private static final Logger logger = Logger.getLogger(FixSoapBindingStub.class);


    public FixSoapBindingStub() {

    }


    public void execute() throws BuildException {

        try {

            File basedir = getProject().getBaseDir();

            Document doc = null;

            doc = XMLUtilities.fileNameToDocument(basedir.getAbsolutePath() + File.separator + "introduce.xml");

            Properties props = new Properties();
            String excludeArgs = null;

            props.load(new FileInputStream(
                new File(basedir.getAbsolutePath() + File.separator + "introduce.properties")));
            excludeArgs = (String) props.get("introduce.soap.binding.excludes");

            List services = doc.getRootElement().getChild("Services",
                Namespace.getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Services")).getChildren("Service",
                Namespace.getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Services"));
            String mainServiceName = ((Element) services.get(0)).getAttributeValue("name");
            String stubFileName = null;
            for (int i = 0; i < services.size(); i++) {
                logger.info("looking to fix soap binding for service "
                    + ((Element) services.get(i)).getAttributeValue("name"));
                stubFileName = basedir.getAbsolutePath() + File.separator + "build" + File.separator + "stubs-"
                    + mainServiceName + File.separator + "src" + File.separator
                    + ((Element) services.get(i)).getAttributeValue("packageName").replace(".", File.separator)
                    + File.separator + "stubs" + File.separator + "bindings" + File.separator
                    + ((Element) services.get(i)).getAttributeValue("name") + "PortTypeSOAPBindingStub.java";

                if (excludeArgs == null) {
                    logger.info("there are no custom serialized namespaces");
                    return;
                }

                StringTokenizer strtok = new StringTokenizer(excludeArgs, " ", false);

                StringBuffer oldContent = null;

                BufferedReader br = new BufferedReader(new FileReader(new File(stubFileName)));
                StringBuffer sb = new StringBuffer();
                try {
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        sb.append(s + "\n");
                    }
                } finally {
                    br.close();
                }

                oldContent = sb;

                while (strtok.hasMoreElements()) {
                    String namespace = strtok.nextToken();
                    logger.info("scanning for references to objects from the namespace: " + namespace);

                    StringBuffer newFileContent = new StringBuffer();

                    // find the method
                    br = new BufferedReader(new StringReader(oldContent.toString()));

                    String line = br.readLine();
                    while (line != null) {
                        if (line.indexOf(namespace) >= 0) {
                            for (int j = 0; j < 6; j++) {
                                br.readLine();
                            }
                        } else {
                            newFileContent.append(line + "\n");
                        }
                        line = br.readLine();
                    }

                    oldContent = newFileContent;

                }

                FileWriter fw = new FileWriter(new File(stubFileName));
                fw.write(oldContent.toString());
                fw.close();
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }


    public static void main(String[] args) {

    }
}
