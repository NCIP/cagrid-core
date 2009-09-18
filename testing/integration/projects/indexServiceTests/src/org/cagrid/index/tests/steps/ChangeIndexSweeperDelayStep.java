package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;


/**
 * ChangeIndexSweeperDelayStep
 * This is a step that sets the sweeper delay in the jndi-config.xml of a 
 * Globus deployed Index Service.
 * 
 * @author Scott Oster
 * @author David
 */
public class ChangeIndexSweeperDelayStep extends Step {
    
    public static final long DEFAULT_SWEEPER_DELAY = 5000; // 5 seconds

    private static final String SWEEPER_DELAY = "entrySweeperInterval";
    
    private ServiceContainer indexServiceContainer;
    
    // default to 5 seconds
    private long sweeperDelay;

    public ChangeIndexSweeperDelayStep(ServiceContainer indexServiceContainer) {
        this(indexServiceContainer, DEFAULT_SWEEPER_DELAY);
    }


    public ChangeIndexSweeperDelayStep(ServiceContainer indeServiceContainer, long sweeperDelay) {
        super();
        this.indexServiceContainer = indeServiceContainer;
        this.sweeperDelay = sweeperDelay;
    }


    @Override
    public void runStep() throws IOException {
        editConfig(new File(indexServiceContainer.getProperties().getContainerDirectory(), 
            "webapps" + File.separator + "wsrf" + File.separator + "WEB-INF" + File.separator
            + "etc" + File.separator + "globus_wsrf_mds_bigindex"
            + File.separator + "jndi-config.xml"), this.sweeperDelay);
    }


    protected static void editConfig(File indexConfigFile, long delay) {
        Document jndiDoc = null;
        try {
            jndiDoc = XMLUtilities.fileNameToDocument(indexConfigFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problem loading Index Service config (" + indexConfigFile.getAbsolutePath() + "):" + e.getMessage());
        }

        boolean found=false;
        List serviceEls = jndiDoc.getRootElement().getChildren("service", jndiDoc.getRootElement().getNamespace());
        for (int serviceI = 0; serviceI < serviceEls.size(); serviceI++) {
            Element serviceEl = (Element) serviceEls.get(serviceI);
            String serviceName = serviceEl.getAttributeValue("name");
            if (serviceName.equals("DefaultIndexService")) {
                List resourceEls = serviceEl.getChildren("resource", serviceEl.getNamespace());
                for (int resourceI = 0; resourceI < resourceEls.size(); resourceI++) {
                    Element resourceEl = (Element) resourceEls.get(resourceI);
                    if (resourceEl.getAttributeValue("name").equals("configuration")) {
                        // get the params
                        Element params = resourceEl.getChild("resourceParams", resourceEl.getNamespace());
                        List parameters = params.getChildren("parameter", params.getNamespace());
                        Iterator parameterIterator = parameters.iterator();
                        while (parameterIterator.hasNext()) {
                            Element param = (Element) parameterIterator.next();
                            Element nameElement = param.getChild("name", param.getNamespace());
                            if (nameElement != null && nameElement.getText() != null && nameElement.getText().equals(SWEEPER_DELAY)) {
                                params.removeContent(param);
                                break;
                            }
                        }
                        // make a new param
                        Element param = new Element("parameter", params.getNamespace());
                        // make the name
                        Element paramName = new Element("name", params.getNamespace());
                        paramName.setText(SWEEPER_DELAY);
                        // make the value
                        Element paramValue = new Element("value", params.getNamespace());
                        paramValue.setText(String.valueOf(delay));
                        // add the name and value to the param
                        param.addContent(paramName);
                        param.addContent(paramValue);
                        // add the param to the params
                        params.addContent(param);
                        
                        found=true;
                    }
                }
            }
        }
        
        if(!found){
            fail("Unable to locate service in JNDI, so couldn't change sweeper delay!");
        }

        try {
            FileWriter fw = new FileWriter(indexConfigFile);
            fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(jndiDoc)));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problem writting out config:" + indexConfigFile.getAbsolutePath());
        }
    }
}
