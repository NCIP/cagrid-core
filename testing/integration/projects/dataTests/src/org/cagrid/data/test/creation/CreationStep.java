package org.cagrid.data.test.creation;


import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

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
    
    private static final Log logger = LogFactory.getLog(CreationStep.class);
    
    protected DataTestCaseInfo serviceInfo;
    protected String introduceDir;
    
	
	public CreationStep(DataTestCaseInfo serviceInfo, String introduceDir) {
		super();
        this.serviceInfo = serviceInfo;
		this.introduceDir = introduceDir;
	}
	

	public void runStep() throws Throwable {
		System.out.println("Creating service...");

		String cmd = AntTools.getAntSkeletonCreationCommand(introduceDir, serviceInfo.getName(), 
			serviceInfo.getDir(), serviceInfo.getPackageName(), serviceInfo.getNamespace(), 
            serviceInfo.getResourceFrameworkType(), serviceInfo.getExtensions());
        System.out.println("EXECUTING COMMAND: " + cmd);
		Process createSkeletonProcess = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(createSkeletonProcess.getInputStream(), 
            StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(createSkeletonProcess.getErrorStream(), 
            StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        createSkeletonProcess.waitFor();
		assertTrue("Creating new data service failed", createSkeletonProcess.exitValue() == 0);
        
        postSkeletonCreation();
		
		System.out.println("Invoking post creation processes...");
		cmd = AntTools.getAntSkeletonPostCreationCommand(introduceDir, serviceInfo.getName(),
			serviceInfo.getDir(), serviceInfo.getPackageName(), serviceInfo.getNamespace(), getServiceExtensions());
        System.out.println("EXECUTING COMMAND: " + cmd);
		Process postCreateProcess = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(postCreateProcess.getInputStream(), 
            StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(postCreateProcess.getErrorStream(), 
            StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        postCreateProcess.waitFor();
		assertTrue("Service post creation process failed", postCreateProcess.exitValue() == 0);
        
        postSkeletonPostCreation();

		System.out.println("Building created service...");
		cmd = AntTools.getAntAllCommand(serviceInfo.getDir());
        System.out.println("EXECUTING COMMAND: " + cmd);
		Process antAllProcess = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(antAllProcess.getInputStream(), 
            StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(antAllProcess.getErrorStream(), 
            StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        antAllProcess.waitFor();
		assertTrue("Build process failed", antAllProcess.exitValue() == 0);
	}
    
    
    protected void postSkeletonCreation() throws Throwable {
        // subclasses can hook in here to do things post-skeleton creation
    }
    
    
    protected void postSkeletonPostCreation() throws Throwable {
        // subclasses can hook in here to do things after post-creation process has run
    }
    
    
    private String getServiceExtensions() throws Exception {
        ServiceDescription description = (ServiceDescription) Utils.deserializeDocument(
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
