package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.templates.RunToolsTemplate;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Upgrade a service created by introduce 1.3 to introduce 1.4.1 standards.
 */
public class Introduce_1_3__1_4_1_Upgrader extends IntroduceUpgraderBase {

	public Introduce_1_3__1_4_1_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath) throws Exception {
		super(status, serviceInformation, servicePath, "1.3", "1.4.1");
	}

	protected void upgrade() throws Exception {
		replaceOldBuildFilesWithNew();
		
		RunToolsTemplate runToolsT = new RunToolsTemplate();
		String runToolsS = runToolsT.generate(new SpecificServiceInformation(getServiceInformation(), getServiceInformation().getServices().getService(0)));
		File runToolsF = new File(getServicePath() + File.separator + "run-tools.xml");
		FileWriter runToolsFW = new FileWriter(runToolsF);
		runToolsFW.write(runToolsS);
		runToolsFW.close();
		getStatus().addDescriptionLine("replaced run-tools.xml, build.xml, and build-deploy.xml with new version");

		upgradeJars();
		fixDevBuildDeploy();
		fixSource();
		fixWSDD();
		fixSecurityOnMetadataAccessProviders();
		
        OrganizeImports oi = new OrganizeImports(new File(getServicePath()));
        oi.runStep();

		getStatus().setStatus(StatusBase.UPGRADE_OK);
	}

	protected void fixDevBuildDeploy() throws Exception {

	}

	protected void fixSecurityOnMetadataAccessProviders() {

	}

	protected void fixSource() throws Exception {

	}

	protected void fixWSDD() throws Exception {

	}

	private void upgradeJars() throws Exception {
		removeOldCagridGlobusAndMobiusJars();
		copySkeletonJarsToLib();

		// remove the old introduce tools jar
		File serviceToolsLibDir = new File(new File(getServicePath(), "tools"), "lib");
		File skeletonToolsLibDir = new File(new File(SKELETON_DIR, "tools"), "lib");
		File serviceTasksJar = new File(serviceToolsLibDir, "caGrid-Introduce-buildTools-1.3.jar");
		serviceTasksJar.delete();

		// remove the old core jar from 1.3
		File coreJar = new File(serviceToolsLibDir.getAbsolutePath(), "caGrid-core-1.3.jar");
		coreJar.delete();

		// copy new libraries into tools (every thing in skeleton/tool/lib)
		File[] skeletonToolsLibs = skeletonToolsLibDir.listFiles(JAR_FILTER);
		for (int i = 0; i < skeletonToolsLibs.length; i++) {
			File out = new File(serviceToolsLibDir.getAbsolutePath() + File.separator + skeletonToolsLibs[i].getName());
			try {
				Utils.copyFile(skeletonToolsLibs[i], out);
				getStatus().addDescriptionLine(skeletonToolsLibs[i].getName() + " added");
			} catch (IOException ex) {
				throw new Exception("Error copying library (" + skeletonToolsLibs[i] + ") to service: " + ex.getMessage(), ex);
			}
		}

	}

}
