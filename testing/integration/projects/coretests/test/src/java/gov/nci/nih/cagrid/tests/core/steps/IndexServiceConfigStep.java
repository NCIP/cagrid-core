/*
 * Created on Jun 11, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.projectmobius.common.MobiusException;
import org.projectmobius.common.XMLUtilities;


/**
 * This is a step that sets the sweeper delay in the jndi-config.xml of a Globus
 * deployed Index Service.
 * 
 * @author Scott Oster
 */
public class IndexServiceConfigStep extends Step {

    private static final String SWEEPER_DELAY = "entrySweeperInterval";
    private GlobusHelper globusHelper;
    // default to 10 seconds
    private long sweeperDelay = 10000;


    public IndexServiceConfigStep(GlobusHelper globusHelper) {
        super();
        this.globusHelper = globusHelper;
    }


    public IndexServiceConfigStep(GlobusHelper globusHelper, long sweeperDelay) {
        super();
        this.globusHelper = globusHelper;
        this.sweeperDelay = sweeperDelay;
    }


    @Override
    public void runStep() throws IOException {
        editConfig(new File(this.globusHelper.getTempGlobusLocation(), "etc" + File.separator + "globus_wsrf_mds_index"
            + File.separator + "jndi-config.xml"), this.sweeperDelay);
    }


    protected static void editConfig(File indexConfigFile, long delay) {

        Document jndiDoc = null;
        try {
            jndiDoc = XMLUtilities.fileNameToDocument(indexConfigFile.getAbsolutePath());
        } catch (MobiusException e) {
            e.printStackTrace();
            fail("Problem loading Index Service config (" + indexConfigFile.getAbsolutePath() + "):" + e.getMessage());
        }

        List serviceEls = jndiDoc.getRootElement().getChildren("service", jndiDoc.getRootElement().getNamespace());
        for (int serviceI = 0; serviceI < serviceEls.size(); serviceI++) {
            Element serviceEl = (Element) serviceEls.get(serviceI);
            String serviceName = serviceEl.getAttributeValue("name");
            if (serviceName.equals("IndexService")) {
                List resourceEls = serviceEl.getChildren("resource", serviceEl.getNamespace());
                for (int resourceI = 0; resourceI < resourceEls.size(); resourceI++) {
                    Element resourceEl = (Element) resourceEls.get(resourceI);
                    if (resourceEl.getAttributeValue("name").equals("configuration")) {
                        // get the params
                        Element params = resourceEl.getChild("resourceParams", resourceEl.getNamespace());
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
                    }
                }
            }
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
