package gov.nih.nci.cagrid.introduce.codegen.services;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncMethods;
import gov.nih.nci.cagrid.introduce.codegen.services.resources.SyncResource;
import gov.nih.nci.cagrid.introduce.codegen.services.security.SyncSecurity;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.ServiceImplBaseTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;

import org.apache.log4j.Logger;


/**
 * SyncMethodsOnDeployment
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncServices extends SyncTool {
    
    private static final Logger logger = Logger.getLogger(SyncServices.class);

    public SyncServices(File baseDirectory, ServiceInformation info) {
        super(baseDirectory, info);
    }


    public void sync() throws SynchronizationException {
        // sync each sub service
        if ((getServiceInformation().getServices() != null)
            && (getServiceInformation().getServices().getService() != null)) {
            for (ServiceType service : getServiceInformation().getServices().getService()) {

                try {
                    SpecificServiceInformation ssi = new SpecificServiceInformation(getServiceInformation(), service);

                    // regenerate the services client layer "ClientBase"
                    ServiceClientBaseTemplate clientBaseT = new ServiceClientBaseTemplate();
                    String clientBaseS = clientBaseT.generate(new SpecificServiceInformation(getServiceInformation(), service));
                    File clientBaseF = new File(getBaseDirectory() + File.separator + "src" + File.separator + CommonTools.getPackageDir(service)
                        + File.separator + "client" + File.separator + service.getName() + "ClientBase.java");

                    FileWriter clientBaseFW = new FileWriter(clientBaseF);
                    clientBaseFW.write(clientBaseS);
                    clientBaseFW.close();
                    
                    // regenerate the services globus layer "ImplBase"
                    ServiceImplBaseTemplate implBaseT = new ServiceImplBaseTemplate();
                    String implBaseS = implBaseT.generate(new SpecificServiceInformation(getServiceInformation(), ssi
                        .getService()));
                    File implBaseF = new File(getBaseDirectory() + File.separator + "src" + File.separator
                        + CommonTools.getPackageDir(ssi.getService()) + File.separator + "service" + File.separator
                        + ssi.getService().getName() + "ImplBase.java");
                    FileWriter implBaseFW = new FileWriter(implBaseF);
                    implBaseFW.write(implBaseS);
                    implBaseFW.close();

                    // execute the source synchronization to make any necessary
                    // changes to source
                    // code for new/modified/removed methods
                    SyncMethods methodSync = new SyncMethods(getBaseDirectory(), getServiceInformation(), service);
                    methodSync.sync();
                    SyncResource resourceSync = new SyncResource(getBaseDirectory(), getServiceInformation(), service);
                    resourceSync.sync();

                    // resync the security configuration files and authorization
                    // callbacks
                    SyncSecurity securitySync = new SyncSecurity(getBaseDirectory(), getServiceInformation(), service);
                    securitySync.sync();

                    // if there is a description on the service then add it to
                    // the interfaces javadoc
                    if ((ssi.getService().getDescription() != null) && (ssi.getService().getDescription().length() > 0)) {
                        StringBuffer fileContent = null;
                        String serviceInf = null;
                        serviceInf = getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator
                            + CommonTools.getPackageDir(ssi.getService()) + File.separator + "common" + File.separator
                            + ssi.getService().getName() + "I.java";
                        fileContent = Utils.fileToStringBuffer(new File(serviceInf));

                        BufferedReader reader = new BufferedReader(new StringReader(ssi.getService().getDescription()));
                        String line = reader.readLine();
                        String descriptionString = "\n";
                        while (line != null) {
                            descriptionString += " * " + line + "\n";
                            line = reader.readLine();
                        }
                        descriptionString += " *\n * ";
                        int startofjavadoc = fileContent.indexOf("/**");
                        int startofoldfirstline = fileContent.indexOf("This class is autogenerated, DO NOT EDIT.");
                        if (startofoldfirstline >= 0) {
                            fileContent.replace(startofjavadoc + 3, startofoldfirstline, descriptionString);
                            String fileContentString = fileContent.toString();
                            FileWriter fw = new FileWriter(new File(serviceInf));
                            fw.write(fileContentString);
                            fw.close();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                    throw new SynchronizationException(e.getMessage(), e);
                }
            }
        }
    }
}
