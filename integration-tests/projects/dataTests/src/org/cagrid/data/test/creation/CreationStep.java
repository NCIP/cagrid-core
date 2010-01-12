package org.cagrid.data.test.creation;


import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  CreationStep
 *  Step to create a service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 22, 2006 
 * @version $Id: CreationStep.java,v 1.3 2008-06-02 20:34:18 dervin Exp $ 
 */
public class CreationStep extends Step {
    
    private static final Log log = LogFactory.getLog(CreationStep.class);
    
    protected DataTestCaseInfo serviceInfo;
    protected String introduceDir;
    
	
	public CreationStep(DataTestCaseInfo serviceInfo, String introduceDir) {
		super();
        this.serviceInfo = serviceInfo;
		this.introduceDir = introduceDir;
	}
	

	public void runStep() throws Throwable {
	    log.debug("Creating service");
        List<String> cmd = AntTools.getAntSkeletonCreationCommand(introduceDir,
            serviceInfo.getName(), serviceInfo.getDir(), serviceInfo.getPackageName(),
            serviceInfo.getNamespace(), serviceInfo.getResourceFrameworkType(), serviceInfo.getExtensions());
        debugCommand(cmd);

        Process p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Creation process exited abnormally", 0, p.exitValue());
        p.destroy();
        
        log.debug("Post skeleton creation");
        postSkeletonCreation();

        log.debug("Invoking post creation processes...");
        cmd = AntTools.getAntSkeletonPostCreationCommand(introduceDir,
            serviceInfo.getName(), serviceInfo.getDir(),
            serviceInfo.getPackageName(), serviceInfo.getNamespace(), getCurrentServiceExtensions());
        debugCommand(cmd);

        p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Post creation process exited abnormally", 0, p.exitValue());
        p.destroy();

        log.debug("Post skeleton post creation");
        postSkeletonPostCreation();

        log.debug("Building created service");
        cmd = AntTools.getAntAllCommand(serviceInfo.getDir());
        debugCommand(cmd);

        p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Build process exited abnormally", 0, p.exitValue());
        p.destroy();
	}
	
	
	protected void debugCommand(List<String> cmd) {
	    if (log.isDebugEnabled()) {
	        StringBuffer buf = new StringBuffer();
	        for (String s : cmd) {
	            buf.append(s).append(' ');
	        }
	        log.debug("COMMAND: " + buf.toString().trim());
	    }
	}
    
    
    protected void postSkeletonCreation() throws Throwable {
        // subclasses can hook in here to do things post-skeleton creation
    }
    
    
    protected void postSkeletonPostCreation() throws Throwable {
        // subclasses can hook in here to do things after post-creation process has run
    }
    
    
    /**
     * Gets the extensions of the service being created from the current introduce.xml
     * service descriptor.  Use this after initial creation to determine the service
     * extensions to run, since the data service extension can add other extensions 
     * to the service at runtime (enum, transfer, etc)
     * 
     * @return
     * @throws Exception
     */
    private String getCurrentServiceExtensions() throws Exception {
        ServiceDescription description = Utils.deserializeDocument(
            serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
            ServiceDescription.class);
        String ext = "";
        if (description.getExtensions() != null 
            && description.getExtensions().getExtension() != null) {
            ExtensionType[] extensions = description.getExtensions().getExtension();
            for (int i = 0; i < extensions.length; i++) {
                ext += extensions[i].getName();
                if (i + 1 < extensions.length) {
                    ext += ",";
                }
            }
        }
        return ext;
    }
}
